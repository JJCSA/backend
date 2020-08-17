package com.jjcsa.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "user_profile")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    String message;

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public User(){}

    public User(String message) {
        this.message = message;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
