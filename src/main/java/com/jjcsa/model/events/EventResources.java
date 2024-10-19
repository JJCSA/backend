package com.jjcsa.model.events;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "event_resources")
@Data
public class EventResources {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name="resource_type")
    private String resourceType;

    @Column(name="resource_s3_name")
    private String resourceS3Name;
}
