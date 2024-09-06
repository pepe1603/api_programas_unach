package com.unach.api_pp_sc_rp.mapper;

import com.unach.api_pp_sc_rp.dto.CarreraDTO;
import com.unach.api_pp_sc_rp.model.Carrera;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CarreraMapper {
    CarreraMapper INSTANCE = Mappers.getMapper(CarreraMapper.class);

    CarreraDTO toDTO(Carrera carrera);

    Carrera toEntity(CarreraDTO carreraDTO);
}
