package com.jjcsa.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.UUID;
import javax.persistence.*;


@Entity
@Table(name = "admin_action")
public @Data class AdminAction{

    @Id
    @Column(name="action_id", columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int actionId;

    @Column(name="from_user_id", columnDefinition = "uuid")
    private String fromUserId;

    @Column(name="to_user_id", columnDefinition = "uuid")
    private String toUserId;

	@JsonFormat(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "TIMESTAMPTZ")
    private Date dateOfAction;

	@Column(columnDefinition = "varchar(45) default ''")
    private String action;

	@Column(columnDefinition = "varchar(512) default ''")
    private String descrip;

}