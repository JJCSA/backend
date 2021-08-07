package com.jjcsa.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

@Configuration
public class EmailTemplateConfig {

    @Bean
    public SpringTemplateEngine springTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(htmlStringTemplateResolver());
        return templateEngine;
    }

    @Bean
    public StringTemplateResolver htmlStringTemplateResolver(){
        StringTemplateResolver emailTemplateResolver = new StringTemplateResolver();
        emailTemplateResolver.setTemplateMode(TemplateMode.HTML);
        return emailTemplateResolver;
    }

}
