package com.unach.api_pp_sc_rp.events;

import org.springframework.context.ApplicationEvent;

public class ProgramaCreadoEvent extends ApplicationEvent {

    private final  String eventTitle;

    public ProgramaCreadoEvent(Object source, String eventTitle) {
        super(source);
        this.eventTitle = eventTitle;
    }

    public String getEventTitle() {
        return eventTitle;
    }
}
