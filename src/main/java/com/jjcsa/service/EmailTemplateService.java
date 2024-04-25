package com.jjcsa.service;

import com.jjcsa.dto.EmailTemplateDto;
import com.jjcsa.model.EmailTemplate;
import com.jjcsa.model.User;
import com.jjcsa.model.enumModel.EmailEvent;
import com.jjcsa.repository.EmailTemplateRepository;
import liquibase.pro.packaged.C;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
@Slf4j
@Data
public class EmailTemplateService {

    private final SpringTemplateEngine templateEngine;
    private final EmailTemplateRepository emailTemplateRepository;
    private final String baseTemplateString;

    public EmailTemplateService(SpringTemplateEngine templateEngine, EmailTemplateRepository emailTemplateRepository) {
        this.templateEngine = templateEngine;
        this.emailTemplateRepository = emailTemplateRepository;
        baseTemplateString = getBaseTemplateString();
    }

    private String getBaseTemplateString() {
        Resource resource = new ClassPathResource("templates/baseTemplate.html");
        try {
            InputStream ip = resource.getInputStream();
            byte[] bytes = new byte[ip.available()];
            ip.read(bytes);
            ip.close();
            return new String(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String resolveEmailBody(String template, Context context) {
        String content = templateEngine.process(template, context);

        // resolve base template
        Context baseContext = new Context();
        context.setVariable("content", content);
        return templateEngine.process(baseTemplateString, baseContext);
    }

    public String resolveTemplate(Context context, String template){
        return templateEngine.process(template, context);
    }

    public EmailTemplateDto resolveEmailContent(EmailEvent emailEvent, User user, String rejectReason) {

        EmailTemplate template = emailTemplateRepository.findByTemplateName(emailEvent.getName());

        Context context = new Context();
        context.setLocale(Locale.ENGLISH);

        switch (emailEvent) {
            case REJECTED:
                context.setVariable("firstName", user.getFirstName());
                context.setVariable("rejectReason", rejectReason);
                break;
            case APPROVED:
                context.setVariable("firstName", user.getFirstName());
                context.setVariable("lastName", user.getLastName());
                break;
            case PW_UPDATE:
            case PROFILE_UPDATE:
            case REGISTRATION:
                context.setVariable("firstName", user.getFirstName());
                break;
        }
        String resolvedBody = resolveEmailBody(template.getEmailBody(), context);

        return EmailTemplateDto.builder()
                .body(resolvedBody)
                .subject(template.getEmailSubject())
                .build();
    }

    // TODO create converter and use EmailTemplate enum while retrieving the template from DB.
    public EmailTemplateDto resolveTemplate(User user,
                                            String templateName){
        EmailTemplate template = emailTemplateRepository.findByTemplateName(templateName);
        String templateBody = template.getEmailBody();

        Map<String, Object> params = new HashMap<>();
        params.put("firstName", user.getFirstName());
        params.put("lastName", user.getLastName());
        params.put("random", "Random");

        Context context = new Context();
        context.setLocale(Locale.ENGLISH);
        context.setVariables(params);
        String resolvedBody = this.resolveTemplate(context, templateBody);

        log.info("User:{} , Resolved Body:{}, params:{}", user.getFirstName(), resolvedBody, params);
        return EmailTemplateDto.builder()
                .body(resolvedBody)
                .subject(template.getEmailSubject())
                .build();
    }

    public EmailTemplateDto resolveTemplateForForgotPasswordEmail(String email,
                                                                  String link,
                                                                  String tempPassword) {
        EmailTemplate template = emailTemplateRepository.findByTemplateName(EmailEvent.FORGOT_PW.getName());

        Context context = new Context();
        context.setLocale(Locale.ENGLISH);
        context.setVariable("email", email);
        context.setVariable("link", link);
        context.setVariable("tempPassword", tempPassword);

        String resolvedBody = resolveEmailBody(template.getEmailBody(), context);

        return EmailTemplateDto.builder()
                .body(resolvedBody)
                .subject(template.getEmailSubject())
                .build();
    }

    public EmailTemplateDto resolveTemplateForContactUs(String userName,
                                                        String message,
                                                        String userEmail) {
        EmailTemplate template = emailTemplateRepository.findByTemplateName(EmailEvent.CONTACT_US.getName());

        Context context = new Context();
        context.setLocale(Locale.ENGLISH);
        context.setVariable("name", userName);
        context.setVariable("message", message);
        context.setVariable("userEmail", userEmail);

        String resolvedBody = resolveEmailBody(template.getEmailBody(), context);

        return EmailTemplateDto.builder()
                .body(resolvedBody)
                .subject(template.getEmailSubject())
                .build();
    }
}
