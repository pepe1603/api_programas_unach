package com.unach.api_pp_sc_rp.events;

import org.springframework.context.ApplicationEvent;

public class ProgramaEventUpdate extends ApplicationEvent {
    private final String eventTitle;

    public ProgramaEventUpdate(Object source, String eventTitle) {
        super(source);
        this.eventTitle = eventTitle;
    }

    public String getEventTitle() {
        return eventTitle;
    }
}
