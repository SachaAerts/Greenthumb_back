package com.GreenThumb.api.notification.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.mail.primary")
    public MailProperties primaryMailProperties() {
        return new MailProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.mail.noreply")
    public MailProperties noReplyMailProperties() {
        return new MailProperties();
    }

    @Bean
    @Primary
    @Qualifier("primarySender")
    public JavaMailSender primarySender() {
        return createMailSender(primaryMailProperties());
    }

    @Bean
    @Qualifier("noReplySender")
    public JavaMailSender noReplySender() {
        return createMailSender(noReplyMailProperties());
    }

    private JavaMailSender createMailSender(MailProperties mailProperties) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailProperties.getHost());
        mailSender.setPort(mailProperties.getPort());
        mailSender.setUsername(mailProperties.getUsername());
        mailSender.setPassword(mailProperties.getPassword());
        mailSender.setDefaultEncoding(mailProperties.getDefaultEncoding().name());

        Properties props = mailSender.getJavaMailProperties();
        mailProperties.getProperties().forEach(props::put);

        return mailSender;
    }
}