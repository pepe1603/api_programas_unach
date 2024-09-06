package com.unach.api_pp_sc_rp.mapper;

import com.unach.api_pp_sc_rp.dto.UsuarioDTO;
import com.unach.api_pp_sc_rp.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);

    UsuarioDTO toDTO(Usuario usuario);


    Usuario toEntity(UsuarioDTO usuarioDTO);
}
