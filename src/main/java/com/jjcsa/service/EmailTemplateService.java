package com.jjcsa.service;

import com.jjcsa.model.EmailTemplate;
import com.jjcsa.model.User;
import com.jjcsa.repository.EmailTemplateRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class EmailTemplateService {

    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private EmailTemplateRepository emailTemplateRepository;

    public String resolveTemplate(Context context, String template){
        return templateEngine.process(template, context);
    }

    public String resolveTemplate(User user, String templateName){
        if(emailTemplateRepository == null) return "Template repository null";
        EmailTemplate template = emailTemplateRepository.findByTemplateName(templateName);
        String templateBody = template.getEmailBody();

        Map<String, Object> params = new HashMap<>();
        params.put("firstName", user.getFirstName());
        params.put("lastName", user.getLastName());
        params.put("random", "Random");

        Context context = new Context();
        context.setVariables(params);

        return resolveTemplate(context, templateBody);
    }
}
