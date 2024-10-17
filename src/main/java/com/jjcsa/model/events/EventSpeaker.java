package com.jjcsa.model.events;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "event_speaker")
@Data
public class EventSpeaker {

    @Id
    private String id;

    @Column(name="event_id")
    private String eventId;

    @Column(name = "speaker_name")
    private String speakerName;

    @Column(name = "speaker_description")
    private String speakerDescription;
}
