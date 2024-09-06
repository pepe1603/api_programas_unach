package com.unach.api_pp_sc_rp.events;

import org.springframework.context.ApplicationEvent;
/**#1
 * Función: Estas clases representan los diferentes tipos de eventos que pueden ocurrir en tu aplicación
 * (creación, actualización, eliminación de un evento). Cada una extiende de ApplicationEvent.
 * */

public class EventoEliminadoEvent extends ApplicationEvent {
    private final String eventTitle;

    public EventoEliminadoEvent(Object source, String eventTitle) {
        super(source);
        this.eventTitle = eventTitle;
    }

    public String getEventTitle() {
        return eventTitle;
    }
}
