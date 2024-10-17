package com.jjcsa.model.events;

import com.jjcsa.model.enumModel.EventResponse;
import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.persistence.*;

@Entity
@Table(name = "user_event_responses")
@Data
public class UserEventResponses {

    @Id
    private String id;

    @Column(name="event_id")
    private String eventId;

    @Column(name="user_id")
    private String userId;

    @Enumerated(EnumType.STRING)
    private EventResponse response;

    @Column(name="created_by_user")
    private String createdByUserId;

    @Column(name = "created_at")
    private DateTime createdAt; // UTC time

    @Column(name = "updated_at")
    private DateTime updatedAt; // UTC time

    @PrePersist
    protected void onCreate() {
        createdAt = DateTime.now(DateTimeZone.UTC);
        updatedAt = DateTime.now(DateTimeZone.UTC);
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = DateTime.now(DateTimeZone.UTC);
    }
}
