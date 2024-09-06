package com.unach.api_pp_sc_rp.listener;

import com.unach.api_pp_sc_rp.events.UserLoginEvent;
import com.unach.api_pp_sc_rp.service.EmailService;
import com.unach.api_pp_sc_rp.service.NotificationService;
import com.unach.api_pp_sc_rp.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserLoginEventListener {

    @Autowired
    private NotificationService notificationService;

    @EventListener
    public void handleUserLoginEvent(UserLoginEvent event) {
        String subject = "Notificación de Inicio de Sesión";
        String text = "Hola " + event.getUsername() + ",\n\n" +
                "Se ha detectado un inicio de sesión en tu cuenta. Si no fuiste tú, te recomendamos cambiar tu contraseña inmediatamente.";

        notificationService.notifyEvent(event.getEmail(), subject, text);
    }



}
