package com.jjcsa.service;

import com.jjcsa.dto.EmailTemplateDto;
import com.jjcsa.model.EmailTemplate;
import com.jjcsa.model.User;
import com.jjcsa.model.enumModel.EmailEvent;
import com.jjcsa.repository.EmailTemplateRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
@Slf4j
@Data
public class EmailTemplateService {

    private final SpringTemplateEngine templateEngine;
    private final EmailTemplateRepository emailTemplateRepository;

    public String resolveTemplate(Context context, String template){
        return templateEngine.process(template, context);
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
        String templateBody = template.getEmailBody();

        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("link", link);
        params.put("tempPassword", tempPassword);

        Context context = new Context();
        context.setLocale(Locale.ENGLISH);
        context.setVariables(params);
        String resolvedBody = this.resolveTemplate(context, templateBody);

        log.info("User:{}, Resolved Body:{}", email, resolvedBody);

        return EmailTemplateDto.builder()
                .body(resolvedBody)
                .subject(template.getEmailSubject())
                .build();
    }

    // TODO generalize template resolution for all methods in this class
    public EmailTemplateDto resolveTemplateForContactUs(String userName,
                                                        String message,
                                                        String userEmail) {
        EmailTemplate template = emailTemplateRepository.findByTemplateName(EmailEvent.CONTACT_US.getName());
        String templateBody = template.getEmailBody();

        Map<String, Object> params = new HashMap<>();
        params.put("name", userName);
        params.put("message", message);
        params.put("userEmail", userEmail);

        Context context = new Context();
        context.setLocale(Locale.ENGLISH);
        context.setVariables(params);
        String resolvedBody = this.resolveTemplate(context, templateBody);

        log.info("Contact Us resolution for userEmail:{}, Resolved Body:{}", userEmail, resolvedBody);

        return
                EmailTemplateDto
                        .builder()
                        .body(resolvedBody)
                        .subject(template.getEmailSubject())
                        .build();
    }
}
