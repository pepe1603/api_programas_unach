package com.unach.api_pp_sc_rp.utils;

import org.springframework.stereotype.Component;

@Component
public class GenerateUtils {

    /**
     * Genera la abreviatura de una cadena dada, tomando la primera letra de cada palabra.
     *
     * @param anyString la cadena de entrada de la que se generará la abreviatura.
     * @return la abreviatura en mayúsculas.
     * @throws IllegalArgumentException si la cadena de entrada es nula o vacía.
     */

    public String generateAbbr(String anyString){
        if (anyString == null || anyString.isEmpty()){
            throw new IllegalArgumentException("La cadena de entrada para getAbreviation no puedee ser nula o vacia.");
        }
        String [] words = anyString.split(" ");

        StringBuilder abbreviation = new StringBuilder();

        for (String word : words){
            if (!word.isEmpty()){
                abbreviation.append(
                    word.charAt(0) //Tomar la primera letra de cada palñabra
                );
            }
        }
        return abbreviation.toString().toUpperCase();
    }

    /**
     * Valida y formatea el identificador del administrador.
     *
     * @param idAdmin El identificador a validar y formatear.
     * @return El identificador formateado con el prefijo especificado.
     * @throws IllegalArgumentException Si el identificador es inválido.
     */
    public String validarYFormatearIdentificador(String idAdmin) {
        if (idAdmin == null || idAdmin.trim().isEmpty()) {
            throw new IllegalArgumentException("El identificador no puede ser nulo o vacío.");
        }

        // Elimina espacios en blanco
        String idAdminInput = idAdmin.trim();

        // Verifica que el identificador contenga solo letras, números y guiones bajos
        if (!idAdminInput.matches("[A-Za-z0-9_]+")) {
            throw new IllegalArgumentException("El identificador solo puede contener letras, números y guiones bajos.");
        }

        // Formatea el identificador con el prefijo
        return "ID-" + idAdminInput;
    }

}
