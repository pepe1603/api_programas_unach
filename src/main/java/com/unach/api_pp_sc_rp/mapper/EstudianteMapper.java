package com.unach.api_pp_sc_rp.mapper;

import com.unach.api_pp_sc_rp.dto.EstudianteDTO;
import com.unach.api_pp_sc_rp.model.Estudiante;
import com.unach.api_pp_sc_rp.model.enums.Grupo;
import com.unach.api_pp_sc_rp.model.enums.Sexo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper( componentModel =  "spring")
public interface EstudianteMapper {
    EstudianteMapper INSTANCE = Mappers.getMapper(EstudianteMapper.class);


    @Mapping(source = "carrera.id", target = "idCarrera")
    @Mapping(source = "usuario.id", target = "idUsuario")
    @Mapping(source = "sexo", target = "sexo", qualifiedByName = "mapSexoToString")
    @Mapping(source = "grupo", target = "grupo", qualifiedByName = "mapGrupoToString")
    EstudianteDTO toDTO(Estudiante estudiante);

    @Mapping(source = "idCarrera", target = "carrera.id")
    @Mapping(source = "idUsuario", target = "usuario.id")
    @Mapping(source = "grupo", target = "grupo", qualifiedByName = "mapStringToGrupo")
    @Mapping(source = "sexo", target = "sexo", qualifiedByName = "mapStringToSexo")
    Estudiante toEntity(EstudianteDTO estudianteDTO);


    @Named("mapGrupoToString")
    default String mapGrupoToString(Grupo grupo) {
        return grupo != null ? grupo.name() : null;
    }

    @Named("mapStringToGrupo")
    default Grupo mapStringToGrupo(String grupo) {
        return grupo != null ? Grupo.valueOf(grupo.toUpperCase()) : null;
    }

    @Named("mapSexoToString")
    default String mapSexoToString(Sexo sexo) {
        return sexo != null ? sexo.name() : null;
    }

    @Named("mapStringToSexo")
    default Sexo mapStringToSexo(String sexo) {
        return sexo != null ? Sexo.valueOf(sexo.toUpperCase()) : null;
    }
}
