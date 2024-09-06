package com.unach.api_pp_sc_rp.mapper;

import com.unach.api_pp_sc_rp.dto.ProgramaDTO;
import com.unach.api_pp_sc_rp.model.Programa;
import com.unach.api_pp_sc_rp.model.enums.EstadoAvance;
import com.unach.api_pp_sc_rp.model.enums.EstadoEvento;
import com.unach.api_pp_sc_rp.model.enums.Grupo;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProgramaMapper {
    ProgramaMapper INSTANCE = Mappers.getMapper(ProgramaMapper.class);

    @Mapping(source = "estadoAvance", target = "estadoAvance", qualifiedByName = "mapEstadoAvanceToString")
    @Mapping(source = "estudiante.id", target = "idEstudiante")
    @Mapping(source = "empresa.id", target = "idEmpresa")
    @Mapping(source = "tipoPrograma.id", target = "idTipoPrograma")
    @Mapping(source = "fecha_inicio", target = "fechaInicio")

    @Mapping(source = "fecha_fin", target = "fechaFin")
    ProgramaDTO toDTO(Programa programa);

    @Mapping(source = "estadoAvance", target = "estadoAvance", qualifiedByName = "mapStringToEstadoAvance")
    @Mapping(source = "idEstudiante", target = "estudiante.id")
    @Mapping(source = "idEmpresa", target = "empresa.id")
    @Mapping(source = "idTipoPrograma", target = "tipoPrograma.id")
    @Mapping(source = "fechaInicio", target = "fecha_inicio")
    @Mapping(source = "fechaFin", target = "fecha_fin")
    Programa toEntity(ProgramaDTO programaDTO);

    @Named("mapEstadoAvanceToString")
    default String mapEstadoAvanceToString(EstadoAvance estadoAvance) {
        return estadoAvance != null ? estadoAvance.name() : null;
    }

    @Named("mapStringToEstadoAvance")
    default EstadoAvance mapStringToEstadoAvance(String estadoAvance) {
        return estadoAvance != null ? EstadoAvance.valueOf(estadoAvance.toUpperCase()) : null;
    }

}
