package com.unach.api_pp_sc_rp.service.impl;

import com.unach.api_pp_sc_rp.exception.EntityNotFoundException;
import com.unach.api_pp_sc_rp.model.Programa;
import com.unach.api_pp_sc_rp.repository.ProgramaRepository;
import com.unach.api_pp_sc_rp.service.EmailService;
import com.unach.api_pp_sc_rp.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*#3
*Función: Implementa el servicio que envía las notificaciones por correo electrónico a
* los usuarios, utilizando el EmailService para enviar los correos.
* */

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private EmailService emailService;
    @Autowired
    private ProgramaRepository programaRepo;


    @Override
    public void notifyEventCreation(String email, String eventTitle) {
        String subject = "Nuevo Evento Creado";
        String text = "Se ha creado un nuevo evento: " + eventTitle;
        emailService.sendEmail(email, subject, text);
    }

    @Override
    public void notifyEventUpdate(String email, String eventTitle) {
        String subject = "Evento Actualizado";
        String text = "El evento ha sido actualizado: " + eventTitle;
        emailService.sendEmail(email, subject, text);
    }

    @Override
    public void notifyEventDeletion(String email, String eventTitle) {
        String subject = "Evento Eliminado";
        String text = "El evento ha sido eliminado: " + eventTitle;
        emailService.sendEmail(email, subject, text);
    }

    @Override
    public void notifyProgramCreation(String email, String eventTitle) {
        String subject = "Nuevo Programa Creado";
        String text = "Se ha creado un nuevo Programa: " + eventTitle;
        emailService.sendEmail(email, subject, text);
    }

    @Override
    public void notifyEventProgramUpdate(String email, String eventTitle) {
        String subject = "Programa Actualizado";
        String text = "Hola, El programa ha sido actualizado: " + eventTitle;
        emailService.sendEmail(email, subject, text);
    }
    @Override
    public void notifyEventProgramUpdateMarckAsFinished(String email, String eventTitle) {
        String subject = "El Programa Actualizado";
        String text = "Hola, El status del programa ha sido actualizado: " + eventTitle;
        emailService.sendEmail(email, subject, text);
    }

    @Override
    public void notifyProgramDeletion(String email, String eventTitle) {
        String subject = "Evento Eliminado";
        String text = "El evento ha sido eliminado: " + eventTitle;
        emailService.sendEmail(email, subject, text);
    }

    @Override
    public void notifyEvent(String email, String subject, String text) {
        emailService.sendEmail(email, subject, text);
    }

    @Override
    public void notifyStudentOfProgram(Long programaId, String notificationMessage) {
        Programa programa = programaRepo.findById(programaId)
                .orElseThrow(() -> new EntityNotFoundException("Programa no encontrado con ID: " + programaId));

        if (programa.getEstudiante() != null && programa.getEstudiante().getCorreoInstitucional() != null) {
            String estudianteEmail = programa.getEstudiante().getCorreoInstitucional();
            emailService.sendEmail(estudianteEmail, "Notificación del Programa", notificationMessage);
        } else {
            throw new RuntimeException("El estudiante asignado al programa no tiene un correo electrónico.");
        }
    }
}