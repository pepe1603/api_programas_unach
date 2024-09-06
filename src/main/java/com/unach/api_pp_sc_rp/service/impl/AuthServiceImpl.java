package com.unach.api_pp_sc_rp.service.impl;

import com.unach.api_pp_sc_rp.config.JwtService;
import com.unach.api_pp_sc_rp.dto.auth.LoginRequest;
import com.unach.api_pp_sc_rp.dto.auth.PasswordResetRequest;
import com.unach.api_pp_sc_rp.dto.auth.SignupRequest;
import com.unach.api_pp_sc_rp.events.UserLoginEvent;
import com.unach.api_pp_sc_rp.events.UserRegistrationEvent;
import com.unach.api_pp_sc_rp.exception.BadCredentialsException;
import com.unach.api_pp_sc_rp.exception.EntityNotFoundException;
import com.unach.api_pp_sc_rp.exception.TokenExpiredException;
import com.unach.api_pp_sc_rp.model.Administrador;
import com.unach.api_pp_sc_rp.model.Estudiante;
import com.unach.api_pp_sc_rp.model.PasswordResetToken;
import com.unach.api_pp_sc_rp.model.Usuario;
import com.unach.api_pp_sc_rp.model.enums.Role;
import com.unach.api_pp_sc_rp.repository.AdminRepository;
import com.unach.api_pp_sc_rp.repository.EstudianteRepository;
import com.unach.api_pp_sc_rp.repository.PasswordResetTokenRepository;
import com.unach.api_pp_sc_rp.repository.UsuarioRepository;
import com.unach.api_pp_sc_rp.service.auth.AuthService;
import com.unach.api_pp_sc_rp.service.EmailService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);


    @Autowired
    private UsuarioRepository usuarioRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private AdminRepository adminRepo;
    @Autowired
    private EstudianteRepository estudianteRepo;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepo;
    @Autowired
    private ApplicationEventPublisher eventPublisher;


    @Override
    @Transactional
    public void registerUser(SignupRequest signupRequest) {
        validateSignupRequest(signupRequest);

        Usuario usuario = new Usuario();
        usuario.setUsername(signupRequest.getUsername());
        usuario.setEmail(signupRequest.getEmail());
        usuario.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        usuarioRepo.save(usuario);
        assignRoleAndSaveUser(signupRequest, usuario);

        logger.info("User registered successfully: {}", signupRequest.getUsername());

        // Enviar correo de notificación
        eventPublisher.publishEvent(new UserRegistrationEvent(this, usuario.getUsername(), usuario.getEmail()));
    }

    private void validateSignupRequest(SignupRequest signupRequest) {
        logger.debug("Validating signup request for username: {}", signupRequest.getUsername());

        if (usuarioRepo.existsByUsername(signupRequest.getUsername())) {
            logger.error("Username already exists: {}", signupRequest.getUsername());
            throw new IllegalArgumentException("El nombre de usuario ya existe, elige otro");
        }

        if (signupRequest.getEmail() == null || signupRequest.getEmail().isEmpty()) {
            throw new IllegalArgumentException("El campo email está vacío, se requiere para restablecimiento de contraseña");
        }
    }

    private void assignRoleAndSaveUser(SignupRequest signupRequest, Usuario usuario) {
        if (signupRequest.getMatricula() != null) {
            usuario.getRoles().add(Role.STUDENT);
            Estudiante estudiante = estudianteRepo.findByMatricula(signupRequest.getMatricula())
                    .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado con Matrícula: " + signupRequest.getMatricula()));
            estudiante.setUsuario(usuario);
            estudianteRepo.save(estudiante);
        } else if (signupRequest.getIdAdmin() != null) {
            usuario.getRoles().add(Role.ADMIN);
            Administrador administrador = adminRepo.findByIdAdmin(signupRequest.getIdAdmin())
                    .orElseThrow(() -> new EntityNotFoundException("Administrador no encontrado con ID: " + signupRequest.getIdAdmin()));
            administrador.setUsuario(usuario);
            adminRepo.save(administrador);
        } else {
            logger.error("Neither matricula nor IDAdmin provided");
            throw new RuntimeException("Debe proporcionar una matrícula o un ID de administrador");
        }
    }

    @Override
    public LoginRequest.LoginResponse authenticateUser(LoginRequest loginRequest) {
        logger.debug("Attempting to authenticate user: {}", loginRequest.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            Usuario user = (Usuario) authentication.getPrincipal();
            String token = jwtService.generateToken(user);
            String role = user.getRoles().iterator().next().name(); // Obtener el primer rol del usuario

            // Publicar evento de inicio de sesión exitoso
            eventPublisher.publishEvent(new UserLoginEvent(this, user.getUsername(), user.getEmail()));

            return new LoginRequest.LoginResponse(token, role);

        } catch (AuthenticationException e) {
            logger.error("Authentication failed for user: {}", loginRequest.getUsername(), e);
            throw new BadCredentialsException("Nombre de usuario o contraseña incorrectos");
        } catch (TokenExpiredException ex) {
            throw new TokenExpiredException("Token JWT expirado. Por favor, inicie sesión nuevamente.");
        }
    }

    @Override
    public void requestPasswordReset(PasswordResetRequest request) {
        String email = getEmailFromRequest(request);

        logger.info("Generating temporary token for password reset");

        try {
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setEmail(email);
            resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
            logger.info("Token generated: {}", resetToken);

            passwordResetTokenRepo.save(resetToken);

            logger.info("Token saved in the database");

            // En el método `requestPasswordReset` en AuthServiceImpl
            String resetUrl = "http://localhost:4200/api/v1/auth/public/rescue-account/password-reset/confirm?token=" + token;


            emailService.sendPasswordResetEmail(email, resetUrl);

            logger.info("Password reset token sent to email: {}", email);

        } catch (Exception ex) {
            logger.error("Error generating temporary token: {} \nCause: {}", ex.getMessage(), ex.getCause());
            throw new RuntimeException("Error inesperado: " + ex.getMessage());
        }
    }

    private String getEmailFromRequest(PasswordResetRequest request) {
        if (request.getMatricula() != null) {
            Estudiante estudiante = estudianteRepo.findByMatricula(request.getMatricula())
                    .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado con Matrícula: " + request.getMatricula()));
            return estudiante.getCorreoInstitucional();///enviamos corroe de notificacion al estudiante en su correo uanch
        } else if (request.getIdAdmin() != null) {
            Administrador administrador = adminRepo.findByIdAdmin(request.getIdAdmin())
                    .orElseThrow(() -> new EntityNotFoundException("Administrador no encontrado con ID: " + request.getIdAdmin()));
            return administrador.getUsuario().getEmail(); //enviamos correo de notiifcacion al administrador en su correo de usuario
        } else {
            throw new IllegalArgumentException("Debe proporcionar una matrícula o un ID de administrador.");
        }
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepo.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Token no válido o expirado"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expirado");
        }

        Usuario usuario = usuarioRepo.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con correo: " + resetToken.getEmail()));

        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepo.save(usuario);

        // Eliminar el token después de usarlo
        passwordResetTokenRepo.delete(resetToken);
        logger.warn("Token de Restablecimeinto Elimnado");

        // Enviar correo de notificación
        emailService.sendPasswordResetConfirmEmail(usuario.getEmail());
        logger.info("Correo de Notificacion Password Reset succefull Enviado..");
    }


}
