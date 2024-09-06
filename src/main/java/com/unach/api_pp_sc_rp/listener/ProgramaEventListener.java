package com.unach.api_pp_sc_rp.listener;

import com.unach.api_pp_sc_rp.events.EventoActualizadoEvent;
import com.unach.api_pp_sc_rp.events.EventoCreadoEvent;
import com.unach.api_pp_sc_rp.events.EventoEliminadoEvent;
import com.unach.api_pp_sc_rp.service.NotificationService;
import com.unach.api_pp_sc_rp.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProgramaEventListener implements ApplicationListener<ApplicationEvent> {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof EventoCreadoEvent) {
            handleEventoCreado((EventoCreadoEvent) event);
        } else if (event instanceof EventoActualizadoEvent) {
            handleEventoActualizado((EventoActualizadoEvent) event);
        } else if (event instanceof EventoEliminadoEvent) {
            handleEventoEliminado((EventoEliminadoEvent) event);
        }
    }

    private void handleEventoCreado(EventoCreadoEvent event) {
        List<String> emails = usuarioService.getAllAdminEmails(); // Obtenemos todos los emails de los usuarios
        for (String email : emails) {
            notificationService.notifyEventCreation(email, event.getEventTitle());
        }
    }

    private void handleEventoActualizado(EventoActualizadoEvent event) {
        List<String> emails = usuarioService.getAllAdminEmails();
        for (String email : emails) {
            notificationService.notifyEventUpdate(email, event.getEventTitle());
        }
    }

    private void handleEventoEliminado(EventoEliminadoEvent event) {
        List<String> emails = usuarioService.getAllAdminEmails();
        for (String email : emails) {
            notificationService.notifyEventDeletion(email, event.getEventTitle());
        }
    }

}
