package com.GreenThumb.api.notification;

import com.GreenThumb.api.notification.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("MailService - Tests unitaires")
class MailServiceTest {

    private MailService mailService;
    private JavaMailSender mailSender;
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        mailService = new MailService(mailSender);

        // Injection du fromEmail via ReflectionTestUtils
        ReflectionTestUtils.setField(mailService, "fromEmail", "noreply@greenthumb.com");

        // Mock MimeMessage
        mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    @DisplayName("send - Doit créer et envoyer un MimeMessage")
    void shouldCreateAndSendMimeMessage() throws Exception {
        // Given
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "<h1>Ceci est un test d'email</h1>";

        // When
        mailService.send(to, subject, body);

        // Then
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("send - Doit configurer le destinataire et le sujet")
    void shouldSetRecipientAndSubject() throws Exception {
        // Given
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test body";

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);

        // When
        mailService.send(to, subject, body);

        // Then
        verify(mailSender).send(captor.capture());
        MimeMessage sentMessage = captor.getValue();

        assertThat(sentMessage.getAllRecipients()).isNotNull();
        assertThat(sentMessage.getAllRecipients()[0].toString()).isEqualTo(to);
        assertThat(sentMessage.getSubject()).isEqualTo(subject);
    }

    @Test
    @DisplayName("send - Doit lever une RuntimeException si MessagingException")
    void shouldThrowRuntimeExceptionOnMessagingException() throws Exception {
        // Given
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test body";

        // Recréer le mock pour permettre l'exception
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Configure pour lancer MessagingException lors de la configuration du helper
        // Cela simule une erreur lors de la configuration du message
        doAnswer(invocation -> {
            throw new MessagingException("SMTP connection failed");
        }).when(mailSender).send(any(MimeMessage.class));

        // When & Then
        assertThatThrownBy(() -> mailService.send(to, subject, body))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Impossible d'envoyer l'email")
                .hasCauseInstanceOf(MessagingException.class);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("send - Doit configurer le fromEmail correctement")
    void shouldSetFromEmailCorrectly() throws Exception {
        // Given
        String to = "test@example.com";
        String subject = "Test";
        String body = "Body";

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);

        // When
        mailService.send(to, subject, body);

        // Then
        verify(mailSender).send(captor.capture());
        MimeMessage sentMessage = captor.getValue();
        assertThat(sentMessage.getFrom()).isNotNull();
        assertThat(sentMessage.getFrom()[0].toString()).isEqualTo("noreply@greenthumb.com");
    }

    @Test
    @DisplayName("send - Doit appeler createMimeMessage avant l'envoi")
    void shouldCallCreateMimeMessageBeforeSending() {
        // Given
        String to = "test@example.com";
        String subject = "Test";
        String body = "Body";

        // When
        mailService.send(to, subject, body);

        // Then - Vérifier l'ordre d'appel
        var inOrder = inOrder(mailSender);
        inOrder.verify(mailSender).createMimeMessage();
        inOrder.verify(mailSender).send(any(MimeMessage.class));
    }
}
