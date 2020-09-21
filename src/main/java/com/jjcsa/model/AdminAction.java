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
    @Column(name="action_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int actionId;

    @Column(name="from_user_id")
    private String fromUserId;

    @Column(name="to_user_id")
    private String toUserId;

	@JsonFormat(pattern = "dd/MM/yyyy")
    private Date dateOfAction;
    private String action;
    private String descrip;

}