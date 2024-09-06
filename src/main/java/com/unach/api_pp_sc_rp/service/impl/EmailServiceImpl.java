package com.unach.api_pp_sc_rp.service.impl;

import com.unach.api_pp_sc_rp.exception.EmailException;
import com.unach.api_pp_sc_rp.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;



@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;


    @Override
    public void sendPasswordResetEmail(String to, String resetUrl) {
        try {
            Context context = new Context();
            context.setVariable("resetUrl", resetUrl);

            String htmlBody = templateEngine.process("password-reset-email", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Restablecimiento de Contraseña");
            helper.setText(htmlBody, true);

            mailSender.send(mimeMessage);
        } catch (MailException | MessagingException ex) {
            throw new EmailException("Ocurrió un error al enviar el correo electrónico de restablecimiento: " + ex.getMessage());
        }
    }
    @Override
    public void sendPasswordResetConfirmEmail(String email) {

            String subject = "Contraseña Restablecida";
            String message = "Tu contraseña ha sido restablecida exitosamente.";
            sendEmail(email, subject, message);

    }
    @Override
    public void sendEmail(String to, String subject, String text) {
        // enviar correos sin plantillas
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);

        }catch (MailException ex) {
            throw new EmailException("Ocurrió un error al enviar el correo electrónico : " + ex.getMessage());
        }

    }
}

