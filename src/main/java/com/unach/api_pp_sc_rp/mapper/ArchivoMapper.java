package com.unach.api_pp_sc_rp.mapper;

import com.unach.api_pp_sc_rp.dto.ArchivoDTO;
import com.unach.api_pp_sc_rp.model.Archivo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ArchivoMapper {
    ArchivoMapper INSTANCE = Mappers.getMapper(ArchivoMapper.class);

    @Mapping(source = "programa.id", target = "idPrograma")
    ArchivoDTO toDTO(Archivo archivo);

    @Mapping(source = "idPrograma", target = "programa.id")
    Archivo toEntity(ArchivoDTO archivoDTO);
}
