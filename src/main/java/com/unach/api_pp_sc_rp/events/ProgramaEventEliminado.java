package com.unach.api_pp_sc_rp.events;

import lombok.*;
import org.springframework.context.ApplicationEvent;


public class ProgramaEventEliminado extends ApplicationEvent {
    private String eventTittle;

    public ProgramaEventEliminado(Object source, String eventTittle) {
        super(source);
        this.eventTittle = eventTittle;
    }

    public String getEventTittle() {
        return eventTittle;
    }
}
