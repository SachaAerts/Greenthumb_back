package com.GreenThumb.api.forum.application.service;

import com.GreenThumb.api.forum.application.dto.ChatMessageDto;
import com.GreenThumb.api.forum.domain.repository.MessageRepository;
import com.GreenThumb.api.forum.infrastructure.entity.MessageEntity;

import com.GreenThumb.api.user.domain.repository.UserRepository;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ForumMessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ForumMessageService(
            MessageRepository messageRepository,
            UserRepository userRepository,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public ChatMessageDto createAndBroadcastMessage(ChatMessageDto dto, String username) {
        UserEntity user = userRepository.getUserEntityByName(username);

        MessageEntity messageSave = messageRepository.save(dto.idThread(), dto.text(), user);

        ChatMessageDto message = new ChatMessageDto(
                messageSave.getId(),
                dto.idThread(),
                username,
                dto.text(),
                messageSave.getCreatedAt()
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
                .map(messageEntity -> new ChatMessageDto(
                        messageEntity.getId(),
                        messageEntity.getThread().getId(),
                        messageEntity.getUser().getUsername(),
                        messageEntity.getText(),
                        messageEntity.getCreatedAt()
                ))
                .toList();
    }
}
