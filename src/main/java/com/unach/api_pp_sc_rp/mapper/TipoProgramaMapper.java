package com.unach.api_pp_sc_rp.mapper;

import com.unach.api_pp_sc_rp.dto.TipoProgramaDTO;
import com.unach.api_pp_sc_rp.model.TipoPrograma;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel =  "spring")
public interface TipoProgramaMapper {
    TipoProgramaMapper INSTANCE = Mappers.getMapper(TipoProgramaMapper.class);

    TipoProgramaDTO toDTO(TipoPrograma tipoPrograma);


    TipoPrograma toEntity(TipoProgramaDTO tipoProgramaDTO);
}
