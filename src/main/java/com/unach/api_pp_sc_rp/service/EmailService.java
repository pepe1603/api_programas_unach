package com.unach.api_pp_sc_rp.service;

public interface EmailService {
    void sendPasswordResetEmail(String email, String token);

    void sendPasswordResetConfirmEmail(String email);

    void sendEmail(String to, String subject, String text);
}
