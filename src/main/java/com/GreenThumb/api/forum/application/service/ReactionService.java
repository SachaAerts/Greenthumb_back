package com.GreenThumb.api.forum.application.service;

import com.GreenThumb.api.forum.application.dto.ReactionActionDto;
import com.GreenThumb.api.forum.application.dto.ReactionBroadcastDto;
import com.GreenThumb.api.forum.application.dto.ReactionDto;
import com.GreenThumb.api.forum.domain.repository.MessageRepository;
import com.GreenThumb.api.forum.domain.repository.ReactionRepository;
import com.GreenThumb.api.forum.infrastructure.entity.MessageEntity;
import com.GreenThumb.api.forum.infrastructure.entity.ReactionEntity;
import com.GreenThumb.api.user.domain.repository.UserRepository;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ReactionService(
            ReactionRepository reactionRepository,
            MessageRepository messageRepository,
            UserRepository userRepository,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.reactionRepository = reactionRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public void toggleReaction(ReactionActionDto dto, String username) {
        if (!messageRepository.existsById(dto.idMessage())) {
            log.error("Message {} not found", dto.idMessage());
            throw new IllegalArgumentException("Message non trouvé");
        }

        UserEntity user = userRepository.getUserEntityByName(username);

        List<MessageEntity> messages = messageRepository.findByUser(user);
        MessageEntity messageEntity = messages.stream()
                .filter(message -> message.getId().equals(dto.idMessage()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Message non trouvé"));

        Optional<ReactionEntity> existingReaction = reactionRepository.findByMessageAndUserAndEmoji(messageEntity, user, dto.emoji());

        if (existingReaction.isPresent()) {
            reactionRepository.delete(existingReaction.get());

            ReactionDto reactionDto = new ReactionDto(
                    existingReaction.get().getIdReaction(),
                    existingReaction.get().getEmoji(),
                    username,
                    existingReaction.get().getCreatedAt()
            );

            broadcastReactionChange(messageEntity.getThread().getId(), "REMOVE", dto.idMessage(), reactionDto);
        } else {
            ReactionEntity newReaction = ReactionEntity.builder()
                    .emoji(dto.emoji())
                    .message(messageEntity)
                    .user(user)
                    .build();

            ReactionEntity reaction = reactionRepository.save(newReaction);

            ReactionDto reactionDto = new ReactionDto(
                    reaction.getIdReaction(),
                    reaction.getEmoji(),
                    username,
                    reaction.getCreatedAt()
            );

            broadcastReactionChange(messageEntity.getThread().getId(), "ADD", dto.idMessage(), reactionDto);
        }
    }

    public List<ReactionDto> getReactionByMessage(Long idMessage, String username ) {
        UserEntity user = userRepository.getUserEntityByName(username);

        List<MessageEntity> messages = messageRepository.findByUser(user);
        MessageEntity message = messages.stream()
                .filter(m -> m.getId().equals(idMessage))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Message non trouvé"));

        List<ReactionEntity> reactions = reactionRepository.findByMessage(message);

        return reactions.stream()
                .map(r -> new ReactionDto(
                        r.getIdReaction(),
                        r.getEmoji(),
                        r.getUser().getUsername(),
                        r.getCreatedAt()
                ))
                .toList();
    }

    private void broadcastReactionChange(
            Long threadId,
            String action,
            Long messageId,
            ReactionDto reactionDto
    ) {
        ReactionBroadcastDto broadcast = new ReactionBroadcastDto(
                action,
                messageId,
                reactionDto
        );

        messagingTemplate.convertAndSend(
                "/topic/forum/" + threadId + "/reactions",
                broadcast
        );


    }
}
