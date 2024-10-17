package com.jjcsa.model.events;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "event_speaker")
@Data
public class EventSpeaker {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "speaker_name")
    private String speakerName;

    @Column(name = "speaker_description")
    private String speakerDescription;
}
