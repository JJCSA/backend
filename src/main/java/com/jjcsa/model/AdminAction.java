package com.jjcsa.model;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.DateTimeFormat;


@Entity
@Table(name = "admin_action")
public class AdminAction{

    @Id
    @Column(name="action_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int actionId;

    @Column(name="from_user_id")
    private String fromUserId;

    @Column(name="to_user_id")
    private String toUserId;

    @DateTimeFormat(pattern = "dd/MM/yyyy") 
    private Date dateOfAction;
    private String action;
    private String descrip;

	public int getActionId() {
		return this.actionId;
	}

	public void setActionId(int actionId) {
		this.actionId = actionId;
	}

	public String getFrom_user_id() {
		return this.from_user_id;
	}

	public void setFrom_user_id(String from_user_id) {
		this.from_user_id = from_user_id;
	}

	public String getTo_user_id() {
		return this.to_user_id;
	}

	public void setTo_user_id(String to_user_id) {
		this.to_user_id = to_user_id;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getDescrip() {
		return this.descrip;
	}

	public void setDescrip(String descrip) {
		this.descrip = descrip;
	}


}