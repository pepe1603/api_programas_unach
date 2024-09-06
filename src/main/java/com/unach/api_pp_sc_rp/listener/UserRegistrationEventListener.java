package com.unach.api_pp_sc_rp.listener;

import com.unach.api_pp_sc_rp.events.UserRegistrationEvent;
import com.unach.api_pp_sc_rp.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationEventListener {

    @Autowired
    private NotificationService notificationService;

    @EventListener
    public void handleUserRegistrationEvent(UserRegistrationEvent event) {
        String subject = "Bienvenido a la Plataforma UNACH-Gestion de programas (pp,sc,rp)";
        String text = "Hola " + event.getUsername() + ",\n\n" +
                "Te has registrado exitosamente en nuestra plataforma. ¡Estamos emocionados de tenerte con nosotros!  :]" +
                "\n\nAhora ya puedes Login en nuestra plataforma";

        notificationService.notifyEvent(event.getEmail(), subject, text);
    }
}
