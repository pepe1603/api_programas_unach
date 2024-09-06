package com.unach.api_pp_sc_rp.mapper;

import com.unach.api_pp_sc_rp.dto.EmpresaDTO;
import com.unach.api_pp_sc_rp.model.Empresa;
import com.unach.api_pp_sc_rp.model.enums.Sexo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EmpresaMapper {
    EmpresaMapper INSTANCE = Mappers.getMapper(EmpresaMapper.class);

    @Mapping(source = "sexoResponsable", target = "sexoResponsable", qualifiedByName = "mapSexoToString")
    EmpresaDTO toDTO(Empresa empresa);

    @Mapping(source = "sexoResponsable", target = "sexoResponsable", qualifiedByName = "mapStringToSexo")
    Empresa toEntity(EmpresaDTO empresaDTO);


    @Named("mapSexoToString")
    default String mapSexoToString(Sexo sexo) {
        return sexo != null ? sexo.name() : null;
    }

    @Named("mapStringToSexo")
    default Sexo mapStringToSexo(String sexo) {
        return sexo != null ? Sexo.valueOf(sexo.toUpperCase()) : null;
    }
}
