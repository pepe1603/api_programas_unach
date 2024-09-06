package com.unach.api_pp_sc_rp.controller;

import com.unach.api_pp_sc_rp.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("api/v1/auth/public/rescue-account")
public class PasswordResetController {

    private final AuthService authService;

    @Autowired
    public PasswordResetController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/password-reset/confirm")
    public String showResetPasswordPage(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "password-reset-confirm";
    }

    //Cambniar metodo por devlver un mesaje de eroror o de exito y que vuejs maneje las ¿visats de error
    @PostMapping("/password-reset/confirm")
    public String confirmPasswordReset(@RequestParam String token, @RequestParam String newPassword, Model model) {
        try {
            authService.resetPassword(token, newPassword);
            model.addAttribute("message", "Contraseña restablecida con éxito. Puedes volver a iniciar sesión.");
            return "password-reset-success";
        } catch (RuntimeException ex) {
            model.addAttribute("error", "Error al restablecer la contraseña.");
            return "password-reset-error";
        }
    }
}
