package com.unach.api_pp_sc_rp.mapper;

import com.unach.api_pp_sc_rp.dto.AdministradorDTO;
import com.unach.api_pp_sc_rp.model.Administrador;
import com.unach.api_pp_sc_rp.model.enums.Grupo;
import com.unach.api_pp_sc_rp.model.enums.Sexo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AdministradorMapper {
    AdministradorMapper INSTANCE = Mappers.getMapper(AdministradorMapper.class);

    @Mapping(source = "usuario.id", target = "idUsuario")
    @Mapping(source = "sexo", target = "sexo", qualifiedByName = "mapSexoToString")
    AdministradorDTO toDTO(Administrador administrador);

    @Mapping(source = "idUsuario", target = "usuario.id")
    @Mapping(source = "sexo", target = "sexo", qualifiedByName = "mapStringToSexo")
    Administrador toEntity(AdministradorDTO administradorDTO);

    @Named("mapSexoToString")
    default String mapSexoToString(Sexo sexo) {
        return sexo != null ? sexo.name() : null;
    }

    @Named("mapStringToSexo")
    default Sexo mapStringToSexo(String sexo) {
        return sexo != null ? Sexo.valueOf(sexo.toUpperCase()) : null;
    }
}
