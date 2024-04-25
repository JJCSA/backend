package com.jjcsa.util;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import org.apache.james.mime4j.codec.EncoderUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;


@Configuration
public class BackendConfig {

    @Value("${cloud.aws.ses.region.static:us-east-2}")
    private String region;

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

    @Bean
    public AmazonSimpleEmailService getAmazonSimpleEmailServiceClient() {
        return AmazonSimpleEmailServiceClientBuilder.standard()
                .withRegion(region).build();
    }

    @Bean
    public SendEmailRequest getSendEmailRequest() {
        return new SendEmailRequest();
    }
}
