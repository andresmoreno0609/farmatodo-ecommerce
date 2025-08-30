package com.farmatodo.ecommerce.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.MimeMessagePreparator;
import jakarta.mail.internet.MimeMessage;

@TestConfiguration
public class MailTestConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        return new JavaMailSender() {
            @Override
            public void send(SimpleMailMessage simpleMessage) throws MailException { /* no-op */ }

            @Override
            public void send(SimpleMailMessage... simpleMessages) throws MailException { /* no-op */ }

            @Override
            public MimeMessage createMimeMessage() {
                return new MimeMessage((jakarta.mail.Session) null);
            }

            @Override
            public MimeMessage createMimeMessage(java.io.InputStream contentStream) {
                throw new UnsupportedOperationException("Not used in unit tests");
            }

            @Override
            public void send(MimeMessage mimeMessage) { /* no-op */ }

            @Override
            public void send(MimeMessage... mimeMessages) { /* no-op */ }

            @Override
            public void send(MimeMessagePreparator mimeMessagePreparator) { /* no-op */ }

            @Override
            public void send(MimeMessagePreparator... mimeMessagePreparators) { /* no-op */ }
        };
    }
}
