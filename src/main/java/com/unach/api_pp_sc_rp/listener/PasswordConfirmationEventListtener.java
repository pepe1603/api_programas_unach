package com.unach.api_pp_sc_rp.listener;

import com.unach.api_pp_sc_rp.events.PasswordConfirmationEvent;
import com.unach.api_pp_sc_rp.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PasswordConfirmationEventListtener {

    @Autowired
    private NotificationService notificationService;

    @EventListener
    public void handlePasswordConfirmationEvent(PasswordConfirmationEvent event) {
        String subject = "Confirmación de Cambio de Contraseña";
        String text = "Hola mi estimado" + event.getUsername() + ",\n\n" +
                "Tu contraseña ha sido confirmada y actualizada exitosamente. Si no fuiste tú quien realizó este cambio, por favor, contacta al soporte inmediatamente.";
        notificationService.notifyEvent(event.getEmail(), subject, text);
    }
}
