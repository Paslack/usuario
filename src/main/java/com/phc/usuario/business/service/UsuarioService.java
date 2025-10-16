package com.phc.usuario.business.service;

import com.phc.usuario.business.converter.UsuarioConverter;
import com.phc.usuario.business.converter.UsuarioConverterDTO;
import com.phc.usuario.business.dto.UsuarioDTO;
import com.phc.usuario.infrastructure.entity.Usuario;
import com.phc.usuario.infrastructure.exceptions.ConflictException;
import com.phc.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.phc.usuario.infrastructure.repository.UsuarioRepository;
import com.phc.usuario.infrastructure.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final UsuarioConverterDTO usuarioConverterDTO;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioConverter usuarioConverter, UsuarioConverterDTO usuarioConverterDTO, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioConverter = usuarioConverter;
        this.usuarioConverterDTO = usuarioConverterDTO;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
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

    public UsuarioDTO atualizaDadosUsuario(String token, UsuarioDTO dto) {
        // retirando o "Bearer " do token, e extraindo o email passando o token
        String email = jwtUtil.extractUsername(token.substring(7));

        // criptografia de senha
        dto.setSenha(dto.getSenha() != null ? passwordEncoder.encode(dto.getSenha()) : null);

        // busca usuario utilizando email no banco de dados
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email não encontrando"));
        // atualiza os dados do usuario
        Usuario usuarioAtualizado = usuarioConverter.updateUsuario(dto, usuario);
        // salva os dados atualizados no banco de dados
        Usuario usuarioSalvo = usuarioRepository.save(usuarioAtualizado);

        // retorna apenas o dto com os dados atualizados
        return usuarioConverterDTO.converteParaUsuarioDTO(usuarioSalvo);
    }
}
