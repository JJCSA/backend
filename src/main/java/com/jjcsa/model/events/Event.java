package com.jjcsa.model.events;

import com.jjcsa.model.enumModel.EventStatus;
import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "events")
@Data
public class Event {

    @Id
    private String id;

    private String title;

    @Column(name = "short_description")
    private String shortDescription;

    @Column(name = "long_description")
    private String longDescription;
    private String location;

    @Column(name = "start_time")
    private DateTime startTime; // UTC time

    @Column(name = "end_time")
    private DateTime end_time; // UTC time

    @Column(name = "is_published")
    private Boolean isPublished = false; // default false

    @Column(name = "registration_deadline")
    private DateTime registrationDeadline; // UTC time

    @Column(name = "meeting_link")
    private String meetingLink;

    @Enumerated(EnumType.STRING)
    private EventStatus status; // should this be in db??

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

    // *********************************************** //
    // Relations
    // *********************************************** //

    @OneToMany(mappedBy = "event")
    private Set<EventSpeaker> eventSpeakers;

    @OneToMany(mappedBy = "event")
    private Set<EventResources> eventResources;
}
