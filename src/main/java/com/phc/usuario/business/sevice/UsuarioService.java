package com.phc.usuario.business.sevice;

import com.phc.usuario.business.converter.UsuarioConverter;
import com.phc.usuario.business.converter.UsuarioConverterDTO;
import com.phc.usuario.business.dto.UsuarioDTO;
import com.phc.usuario.infrastructure.entity.Usuario;
import com.phc.usuario.infrastructure.exceptions.ConflictException;
import com.phc.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.phc.usuario.infrastructure.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final UsuarioConverterDTO usuarioConverterDTO;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioConverter usuarioConverter, UsuarioConverterDTO usuarioConverterDTO, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioConverter = usuarioConverter;
        this.usuarioConverterDTO = usuarioConverterDTO;
        this.passwordEncoder = passwordEncoder;
    }

    public void deletarUsuarioPorEmail(String email) {
        if (!verificaEmailExistente(email)) {
            throw new ResourceNotFoundException("Email não encontrado " + email);
        }
        usuarioRepository.deleteByEmail(email);
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Email não encontrado. " + email)
        );
    }

    public UsuarioDTO salvarUsuario(UsuarioDTO usuarioDTO) {
        emailExiste(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.converteParaUsuario(usuarioDTO);
        usuario = usuarioRepository.save(usuario);
        return usuarioConverterDTO.converteParaUsuarioDTO(usuario);
    }

    public void emailExiste(String email) {
        if (verificaEmailExistente(email)) {
            throw new ConflictException("Email já cadastrado " + email);
        }
    }

    public boolean verificaEmailExistente(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}
