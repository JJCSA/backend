package com.jjcsa.util;

import com.jjcsa.model.enumModel.EventStatus;
import org.joda.time.DateTime;

public class EventUtil {
    public static EventStatus getEventStatus(DateTime startTime, DateTime endTime) {
        if (endTime.isBeforeNow()) {
            return EventStatus.past;
        }

        if (startTime.isAfterNow()) {
            return EventStatus.upcoming;
        }

        // startTime is before now && endTime is after now
        return EventStatus.ongoing;
    }
}
