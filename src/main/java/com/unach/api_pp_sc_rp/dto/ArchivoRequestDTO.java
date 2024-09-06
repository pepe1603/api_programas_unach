package com.unach.api_pp_sc_rp.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ArchivoRequestDTO {
    private String nombreArchivo;   //opcional
    private MultipartFile file;     //archivo a subir
    private Long idPrograma;        //ID_programa asociado
}
