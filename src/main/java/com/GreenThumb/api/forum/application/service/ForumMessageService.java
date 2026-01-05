package com.GreenThumb.api.forum.application.service;

import com.GreenThumb.api.forum.application.dto.ReactionDto;
import com.GreenThumb.api.infrastructure.service.RedisService;
import com.GreenThumb.api.forum.application.dto.ChatMessageDto;
import com.GreenThumb.api.forum.domain.entity.Message;
import com.GreenThumb.api.forum.domain.repository.MessageRepository;
import com.GreenThumb.api.forum.infrastructure.entity.MediaEntity;
import com.GreenThumb.api.forum.infrastructure.entity.MessageEntity;
import com.GreenThumb.api.forum.infrastructure.mapper.MessageMapper;

import com.GreenThumb.api.user.application.dto.TierDto;
import com.GreenThumb.api.user.application.dto.UserDto;
import com.GreenThumb.api.user.application.dto.UserStatsUpdateDto;
import com.GreenThumb.api.user.application.service.TierProgressionService;
import com.GreenThumb.api.user.application.service.UserService;
import com.GreenThumb.api.user.domain.repository.UserRepository;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.Object;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ForumMessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;
    private final RedisService redisService;
    private final MessageModerationFilterService moderationFilterService;
    private final TierProgressionService tierProgressionService;

    public ForumMessageService(
            MessageRepository messageRepository,
            UserRepository userRepository,
            SimpMessagingTemplate messagingTemplate,
            UserService userService,
            RedisService redisService,
            MessageModerationFilterService moderationFilterService,
            TierProgressionService tierProgressionService
    ) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
        this.redisService = redisService;
        this.moderationFilterService = moderationFilterService;
        this.tierProgressionService = tierProgressionService;
    }

    @Transactional
    public ChatMessageDto createAndBroadcastMessage(ChatMessageDto dto, String username) {
        UserEntity user = userRepository.getUserEntityByName(username);

        MessageEntity messageSave = messageRepository.save(dto.idThread(), dto.text(), user);

        if (dto.mediaUrls() != null && !dto.mediaUrls().isEmpty()) {
            List<MediaEntity> medias = dto.mediaUrls().stream()
                    .map(url -> MediaEntity.builder()
                            .url(url)
                            .message(messageSave)
                            .build()
                    ).toList();

            messageSave.getMedias().addAll(medias);
            messageRepository.save(messageSave);
        }

        userRepository.incrementCountMessage(user.getId());

        TierDto newTier = tierProgressionService.checkAndUpgradeTier(
                user.getId(),
                user.getCountMessage() + 1,
                user.getTier().getIdTier()
        );

        if (newTier != null) {
            broadcastTierUpgrade(username, newTier);
        }

        redisService.delete(username);

        UserDto updatedUserDto = userService.getUserByUsername(username);

        UserStatsUpdateDto statsUpdate = new UserStatsUpdateDto(
                updatedUserDto.username(),
                updatedUserDto.messageCount(),
                updatedUserDto.tier(),
                updatedUserDto.countCreatedThread()
        );

        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/stats",
                statsUpdate
        );

        log.info("User stats sent to user {} via /user/queue/stats - messageCount: {}",
                username, updatedUserDto.messageCount());

        ChatMessageDto message = new ChatMessageDto(
                messageSave.getId(),
                dto.idThread(),
                username,
                dto.text(),
                messageSave.getCreatedAt(),
                messageSave.getMedias().stream()
                        .map(MediaEntity::getUrl)
                        .toList(),
                messageSave.getReactions().stream()
                        .map(r -> new ReactionDto(
                                r.getIdReaction(),
                                r.getEmoji(),
                                r.getUser().getUsername(),
                                r.getCreatedAt()
                        ))
                        .toList()
        );

        messagingTemplate.convertAndSend(
                "/topic/forum/" + dto.idThread(),
                message
        );

        log.info("Message broadcasted to thread {}", dto.idThread());

        return message;
    }

    public List<ChatMessageDto> getMessagesByThread(Long threadId) {
        List<MessageEntity> messageEntities = messageRepository.findByThreadId(threadId);

        return messageEntities.stream()
                .filter(entity -> {
                    Message message = MessageMapper.toDomain(entity);
                    return moderationFilterService.isMessageVisible(message);
                })
                .map(entity -> new ChatMessageDto(
                        entity.getId(),
                        threadId,
                        entity.getUser().getUsername(),
                        entity.getText(),
                        entity.getCreatedAt(),
                        entity.getMedias().stream()
                                .map(MediaEntity::getUrl)
                                .toList(),
                        entity.getReactions().stream()
                                .map(r -> new ReactionDto(
                                        r.getIdReaction(),
                                        r.getEmoji(),
                                        r.getUser().getUsername(),
                                        r.getCreatedAt()
                                ))
                                .toList()
                ))
                .toList();
    }

    private void broadcastTierUpgrade(String username, TierDto newTier) {
        Map<String, Object> tierUpgradeNotification = Map.of(
            "type", "TIER_UPGRADE",
            "tierName", newTier.name(),
            "threadsUnlocked", newTier.threadUnlocked(),
            "messageRequired", newTier.messageRequired(),
            "timestamp", Instant.now().toString()
        );

        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/tier-upgrade",
                tierUpgradeNotification
        );
    }
}
