package com.jjcsa.model.events;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "event_resources")
@Data
public class EventResources {

    @Id
    private String id;

    @Column(name="event_id")
    private String eventId;

    @Column(name="resource_type")
    private String resourceType;

    @Column(name="resource_s3_name")
    private String resourceS3Name;
}
