package com.jjcsa.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;
import javax.persistence.*;


@Entity
@Table(name = "admin_action")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public @Data class Action {

    @Id
    @Column(name="action_id", columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID actionId;

    @Column(name="from_user_id", columnDefinition = "varchar(45) default ''")
    private String fromUserId;

    @Column(name="to_user_id", columnDefinition = "varchar(45) default ''")
    private String toUserId;

	@JsonFormat(pattern = "MM/dd/yyyy")
    @Column(columnDefinition = "TIMESTAMPTZ")
    private Date dateOfAction;

	@Column(columnDefinition = "varchar(45) default ''")
    private String action;

}