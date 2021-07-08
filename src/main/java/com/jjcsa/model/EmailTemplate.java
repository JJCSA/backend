package com.jjcsa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "email_templates")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EmailTemplate {
    @Id
    @Column(name="template_id", columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int templateId;

    @Column(name="template_name", columnDefinition = "varchar(255) default ''", unique=true)
    @NotNull
    private String templateName;

    @Column(name="email_subject", columnDefinition = "varchar(255) default ''")
    @NotNull
    private String emailSubject;

    @Column(name="email_body", columnDefinition = "text default ''")
    @NotNull
    private String emailBody;
}
