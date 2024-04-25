package com.jjcsa.service;

import com.jjcsa.dto.EmailTemplateDto;
import com.jjcsa.model.User;
import com.jjcsa.model.enumModel.EmailEvent;
import io.awspring.cloud.ses.SimpleEmailServiceJavaMailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewEmailService {

    @Value("${email.ses.from-email:newsletter@jjcsausa.com}")
    private String fromEmailAddress;

    private final JavaMailSender mailSender;
    private final EmailTemplateService emailTemplateService;

    public void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(fromEmailAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException ex) {
            log.info("Email sent failed for destination:{} with error:{}, stacktrace:{}", to, ex.getMessage(), ex.getStackTrace());
        }
    }

    public void sendEmail(EmailEvent emailEvent, User user, String rejectReason) {
        log.info("Triggering Email Event:{} for user:{}(Id:{})", emailEvent, user.getEmail(), user.getId());

        EmailTemplateDto emailTemplateDto = emailTemplateService.resolveEmailContent(emailEvent, user, rejectReason);
        sendEmail(user.getEmail(), emailTemplateDto.getSubject(), emailTemplateDto.getBody());
    }

}
