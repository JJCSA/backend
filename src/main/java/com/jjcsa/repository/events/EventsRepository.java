package com.jjcsa.repository.events;

import com.jjcsa.model.events.Event;
import org.springframework.data.repository.CrudRepository;

public interface EventsRepository extends CrudRepository<Event, String> {
}
