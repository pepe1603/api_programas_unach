package com.unach.api_pp_sc_rp.config;

import com.unach.api_pp_sc_rp.service.BlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final BlacklistService blacklistService;

    @Autowired
    public JwtFilter(UserDetailsService userDetailsService, JwtService jwtService, BlacklistService blacklistService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.blacklistService = blacklistService;
        logger.info("JwtFilter initialized");
    }




    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        logger.debug("Processing request : " + request.getRequestURI());


        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader != null) {
            logger.info("Authorization header found: {}", authHeader);
        } else {
            logger.warn("Authorization header missing");
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("No JWT token found, passing request through.");
            filterChain.doFilter(request, response);
            return;
        }


        jwt = authHeader.substring(7);

        //verificamos que el token no esta en listanegra
        if (blacklistService.isTokenBlacklisted(jwt)) {//validamos el token
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has been blacklisted");
            return;
        }


        username = jwtService.getUserName(jwt);




        logger.debug("JWT token found, username: {}", username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtService.validarToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                logger.debug("User authenticated successfully: {}", username);
            }else{
                logger.warn("Invalid JWT token for user: {}", username);
            }
        }

        filterChain.doFilter(request, response);
        // Logging after processing
        logger.info("Request processing completed: {} {}", request.getMethod(), request.getRequestURI());

    }//fin_doFilter



}
