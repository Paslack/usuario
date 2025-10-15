package com.phc.usuario.business.sevice;

import com.phc.usuario.business.converter.UsuarioConverter;
import com.phc.usuario.business.converter.UsuarioConverterDTO;
import com.phc.usuario.business.dto.UsuarioDTO;
import com.phc.usuario.infrastructure.entity.Usuario;
import com.phc.usuario.infrastructure.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final UsuarioConverterDTO usuarioConverterDTO;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioConverter usuarioConverter, UsuarioConverterDTO usuarioConverterDTO) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioConverter = usuarioConverter;
        this.usuarioConverterDTO = usuarioConverterDTO;
    }

    public UsuarioDTO salvarUsuario(UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioConverter.converteParaUsuario(usuarioDTO);
        usuario = usuarioRepository.save(usuario);
        return usuarioConverterDTO.converteParaUsuarioDTO(usuario);
    }
}
