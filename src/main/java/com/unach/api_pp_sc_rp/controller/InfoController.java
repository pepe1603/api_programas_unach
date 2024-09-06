package com.unach.api_pp_sc_rp.controller;

import com.unach.api_pp_sc_rp.dto.AdministradorDTO;
import com.unach.api_pp_sc_rp.service.AdministradorService;
import com.unach.api_pp_sc_rp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/public/info")
public class InfoController {

    /**
     * Endpoint para obtener la versión de la API.
     *
     * @return Información de la versión.
     */
    @GetMapping("/version")
    public ResponseEntity<String> getVersion() {
        String versionInfo = "API Version 1.0.0";
        return ResponseEntity.ok(versionInfo);
    }

    /**
     * Endpoint para obtener información sobre los colaboradores.
     *
     * @return Información de los colaboradores.
     */
    @GetMapping("/contributors")
    public ResponseEntity<String> getContributors() {
        String contributorsInfo = "Contributors: Braulio Coutiño , Jhonatan , Jose Colombio, Mtro. Rene Servando, entre otros,etc.";
        return ResponseEntity.ok(contributorsInfo);
    }

    /**
     * Endpoint para obtener información sobre las herramientas de desarrollo.
     *
     * @return Información sobre herramientas.
     */
    @GetMapping("/tools")
    public ResponseEntity<String> getDevelopmentTools() {
        String toolsInfo = "Development Tools: Spring Boot, Java, Maven, PostgreSQL,  JWT-TOKEN, GoogleApi, Thymeleaft,, GitHub, etc.";
        return ResponseEntity.ok(toolsInfo);
    }

    /**
     * Endpoint para obtener información adicional sobre la API.
     *
     * @return Información adicional.
     */
    @GetMapping("/info")
    public ResponseEntity<String> getAdditionalInfo() {
        String additionalInfo = "Esta API provee la administracion y gestion de programas para alumnos de la UNACH Campus IV, administrada por la Coordinacion de de Programas..";
        return ResponseEntity.ok(additionalInfo);
    }

}