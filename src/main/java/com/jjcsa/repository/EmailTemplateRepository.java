package com.jjcsa.repository;

import com.jjcsa.model.EmailTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmailTemplateRepository extends CrudRepository<EmailTemplate, UUID> {
    EmailTemplate findByTemplateName(String templateName);
}
