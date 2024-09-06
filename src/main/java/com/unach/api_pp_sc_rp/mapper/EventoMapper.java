package com.unach.api_pp_sc_rp.mapper;

import com.unach.api_pp_sc_rp.dto.EventoDTO;
import com.unach.api_pp_sc_rp.model.Evento;
import com.unach.api_pp_sc_rp.model.enums.EstadoEvento;
import com.unach.api_pp_sc_rp.model.enums.Grupo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EventoMapper {
    EventoMapper INSTANCE = Mappers.getMapper(EventoMapper.class);

    @Mapping(source = "estadoEvento", target = "estadoEvento", qualifiedByName = "mapEstadoEventoToString")
    @Mapping(source = "tipoPrograma.id" , target = "idTipoPrograma")
    EventoDTO toDTO (Evento evento);

    @Mapping(source = "estadoEvento", target = "estadoEvento",  qualifiedByName = "mapStringToEstadoEvento")
    @Mapping(source = "idTipoPrograma", target = "tipoPrograma.id")
    Evento toEntity (EventoDTO eventoDTO);


    @Named("mapEstadoEventoToString")
    default String mapEstadoEventoToString(EstadoEvento estadoEvento) {
        return estadoEvento != null ? estadoEvento.name() : null;
    }

    @Named("mapStringToEstadoEvento")
    default EstadoEvento mapStringToEstadoEvento(String estadoEvento) {
        return estadoEvento != null ? EstadoEvento.valueOf(estadoEvento.toUpperCase()) : null;
    }

}
