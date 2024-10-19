package com.jjcsa.model.events;

import com.jjcsa.model.enumModel.EventStatus;
import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;

@Entity
@Table(name = "events")
@Data
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String title;

    @Column(name = "short_description")
    private String shortDescription;

    @Column(name = "long_description")
    private String longDescription;
    private String location;

    @Column(name = "start_time")
    private LocalDateTime startTime; // UTC time

    @Column(name = "end_time")
    private LocalDateTime endTime; // UTC time

    @Column(name = "is_published")
    private Boolean isPublished = false; // default false

    @Column(name = "registration_deadline")
    private LocalDateTime registrationDeadline; // UTC time

    @Column(name = "meeting_link")
    private String meetingLink;

    @Enumerated(EnumType.STRING)
    private EventStatus status = null; // should this be in db??

    @Column(name="created_by_user")
    private String createdByUserId;

    @Column(name = "created_at")
    private LocalDateTime createdAt; // UTC time

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // UTC time

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now(ZoneOffset.UTC);;
        updatedAt = LocalDateTime.now(ZoneOffset.UTC);;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now(ZoneOffset.UTC);;
    }

    // *********************************************** //
    // Relations
    // *********************************************** //

    @OneToMany(mappedBy = "event")
    private Set<EventSpeaker> eventSpeakers;

    @OneToMany(mappedBy = "event")
    private Set<EventResources> eventResources;
}
