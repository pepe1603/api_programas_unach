package com.unach.api_pp_sc_rp.config.security;


import com.unach.api_pp_sc_rp.config.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.cors.CorsConfigurationSource;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final AuthenticationProvider authProvider;

    @Autowired
    public SecurityConfig(JwtFilter jwtFilter, AuthenticationProvider authProvider) {
        this.jwtFilter = jwtFilter;
        this.authProvider = authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, CorsConfigurationSource corsConfigSource ) throws Exception
    {

        httpSecurity

                .cors( cors -> cors
                        .configurationSource(corsConfigSource) )
                .csrf( 	csrf -> csrf
                        .disable() )

                .authorizeHttpRequests( auth -> auth
                        .requestMatchers(publicEndPoints()).permitAll()
                        .anyRequest().authenticated() )

                .sessionManagement( sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authenticationProvider(authProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    @Bean
    public RequestMatcher publicEndPoints() {
        return new OrRequestMatcher(
                new AntPathRequestMatcher("/api/v1/auth/**"),
                new AntPathRequestMatcher("/api/v1/public/**"),
                new AntPathRequestMatcher("/api/v1/coordinacion_programas/files/**"),
               // Rutas de Swagger UI y OpenAPI
                new AntPathRequestMatcher("/doc/swagger-ui.html"),
                new AntPathRequestMatcher("/doc/v3/api-docs/**"),
                new AntPathRequestMatcher("/doc/swagger-ui.html"),
                new AntPathRequestMatcher("/doc/swagger-ui/**"),


                // Permitir acceder a los archivos estáticos
                new AntPathRequestMatcher("/static/css/**"),  // Ruta para archivos CSS
                new AntPathRequestMatcher("/static/js/**"),   // Ruta para archivos JS
                new AntPathRequestMatcher("/static/images/**"), // Ruta para imágenes
                new AntPathRequestMatcher("/resources/**")  // Ruta para recursos adicionales si es necesario
        );

    }
}
