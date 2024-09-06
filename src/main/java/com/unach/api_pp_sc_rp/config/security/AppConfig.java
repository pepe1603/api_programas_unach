package com.unach.api_pp_sc_rp.config.security;

import com.unach.api_pp_sc_rp.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);


    @Autowired
    private UsuarioRepository usuarioRepo;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            logger.debug("Loading user by username: {}", username);
            return usuarioRepo.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User Not Found in Repository"));
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        logger.debug("Creating AuthenticationProvider bean");
        DaoAuthenticationProvider daoAuthProvier = new DaoAuthenticationProvider();
        daoAuthProvier.setUserDetailsService(userDetailsService());
        daoAuthProvier.setPasswordEncoder(passwordEncoder());
        return daoAuthProvier;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // TODO Auto-generated method stub
        logger.debug("Creating PasswordEncoder bean");
        return new BCryptPasswordEncoder();
    }
}
