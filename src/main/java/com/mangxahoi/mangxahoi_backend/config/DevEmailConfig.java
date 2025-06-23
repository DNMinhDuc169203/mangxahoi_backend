package com.mangxahoi.mangxahoi_backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import jakarta.mail.internet.MimeMessage;
import java.io.InputStream;

@Configuration
@Slf4j
@Profile("dev")
public class DevEmailConfig {

    @Bean
    @Primary
    public JavaMailSender mockMailSender() {
        return new JavaMailSender() {
            @Override
            public MimeMessage createMimeMessage() {
                return null;
            }

            @Override
            public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
                return null;
            }

            @Override
            public void send(MimeMessage mimeMessage) throws MailException {
                // Không làm gì cả
            }

            @Override
            public void send(MimeMessage... mimeMessages) throws MailException {
                // Không làm gì cả
            }

            @Override
            public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {
                // Không làm gì cả
            }

            @Override
            public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {
                // Không làm gì cả
            }

            @Override
            public void send(SimpleMailMessage simpleMessage) throws MailException {
                // Log thông tin email thay vì gửi
                log.info("==================== FAKE EMAIL ====================");
                log.info("To: {}", String.join(", ", simpleMessage.getTo()));
                log.info("Subject: {}", simpleMessage.getSubject());
                log.info("Content: {}", simpleMessage.getText());
                log.info("===================================================");
            }

            @Override
            public void send(SimpleMailMessage... simpleMessages) throws MailException {
                for (SimpleMailMessage message : simpleMessages) {
                    send(message);
                }
            }
        };
    }
}