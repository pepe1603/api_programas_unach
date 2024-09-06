package com.unach.api_pp_sc_rp.service;

public interface NotificationService {


    void notifyEventCreation(String email, String eventTitle);

    void notifyEventUpdate(String email, String eventTitle);

    void notifyEventDeletion(String email, String eventTitle);

    void notifyProgramCreation(String email, String eventTitle);

    void notifyEventProgramUpdate(String email, String eventTitle);

    void notifyEventProgramUpdateMarckAsFinished(String email, String eventTitle);

    void notifyProgramDeletion(String email, String eventTitle);

    void notifyEvent(String email, String subject, String text);

    void notifyStudentOfProgram(Long programaId, String notificationMessage);
}
