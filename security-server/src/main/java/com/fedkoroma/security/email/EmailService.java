package com.fedkoroma.security.email;

import com.fedkoroma.security.service.EmailTemplateReader;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@AllArgsConstructor
@Slf4j
public class EmailService implements EmailSender{

    private final JavaMailSender mailSender;
    @Override
    @Async // todo : add a RabbitMQ
    public void send(String to, String name, String link ) {
        try {
            String htmlTemplate = EmailTemplateReader.readEmailTemplate("templates/email_template.html", name, link);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(htmlTemplate, true);
            helper.setTo(to);
            helper.setSubject("Confirm your email");
            helper.setFrom("hello@email.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException | IOException e){
            log.error("failed to send email", e);
            throw new IllegalStateException("failed to send email");
        }
    }
}
