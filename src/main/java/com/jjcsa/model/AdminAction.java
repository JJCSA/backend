package com.jjcsa.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jjcsa.model.enumModel.Action;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;
import javax.persistence.*;

@Entity
@Table(name = "admin_action")
@NoArgsConstructor
public @Data class AdminAction{

    @Id
    @Column(name="action_id", columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID actionId;

    @Column(name="from_user_id", columnDefinition = "uuid")
    private UUID fromUserId;

    @Column(name="to_user_id", columnDefinition = "uuid")
    private UUID toUserId;

	@JsonFormat(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "TIMESTAMPTZ")
    private Date dateOfAction;

	@Column(columnDefinition = "varchar(45) default ''")
    private Action action;

	@Column(columnDefinition = "varchar(512) default ''")
    private String descrip;

}