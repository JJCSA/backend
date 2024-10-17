package com.jjcsa.repository.events;

import com.jjcsa.model.events.EventSpeaker;
import org.springframework.data.repository.CrudRepository;

public interface EventSpeakerRepository extends CrudRepository<EventSpeaker, String> {
}
