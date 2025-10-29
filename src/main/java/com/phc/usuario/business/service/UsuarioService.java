package com.phc.usuario.business.service;

import com.phc.usuario.business.converter.UsuarioConverter;
import com.phc.usuario.business.converter.UsuarioConverterDTO;
import com.phc.usuario.business.dto.EnderecoDTO;
import com.phc.usuario.business.dto.TelefoneDTO;
import com.phc.usuario.business.dto.UsuarioDTO;
import com.phc.usuario.infrastructure.entity.Endereco;
import com.phc.usuario.infrastructure.entity.Telefone;
import com.phc.usuario.infrastructure.entity.Usuario;
import com.phc.usuario.infrastructure.exceptions.ConflictException;
import com.phc.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.phc.usuario.infrastructure.exceptions.UnauthorizedException;
import com.phc.usuario.infrastructure.repository.EnderecoRepository;
import com.phc.usuario.infrastructure.repository.TelefoneRepository;
import com.phc.usuario.infrastructure.repository.UsuarioRepository;
import com.phc.usuario.infrastructure.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final UsuarioConverterDTO usuarioConverterDTO;
    private final EnderecoRepository enderecoRepository;
    private final TelefoneRepository telefoneRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioConverter usuarioConverter, UsuarioConverterDTO usuarioConverterDTO, EnderecoRepository enderecoRepository, TelefoneRepository telefoneRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioConverter = usuarioConverter;
        this.usuarioConverterDTO = usuarioConverterDTO;
        this.enderecoRepository = enderecoRepository;
        this.telefoneRepository = telefoneRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public void deletarUsuarioPorEmail(String email) {
        if (!verificaEmailExistente(email)) {
            throw new ResourceNotFoundException("Email não encontrado " + email);
        }
        usuarioRepository.deleteByEmail(email);
    }

    public UsuarioDTO buscarUsuarioPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email não encontrado"));
        return usuarioConverterDTO.converteParaUsuarioDTO(usuario);
    }

    @Transactional
    public UsuarioDTO salvarUsuario(UsuarioDTO usuarioDTO) {
        emailExiste(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.converteParaUsuario(usuarioDTO);
        usuario = usuarioRepository.save(usuario);
        return usuarioConverterDTO.converteParaUsuarioDTO(usuario);
    }

    public String autenticarUsuario(UsuarioDTO usuarioDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(usuarioDTO.getEmail(), usuarioDTO.getSenha())
            );
            return "Bearer " + jwtUtil.generateToken(authentication.getName());
        } catch (BadCredentialsException | UsernameNotFoundException | AuthorizationDeniedException e) {
            throw new UnauthorizedException("Credenciais inválidas: ", e);
        }
    }

    public void emailExiste(String email) {
        if (verificaEmailExistente(email)) {
            throw new ConflictException("Email já cadastrado " + email);
        }
    }

    public boolean verificaEmailExistente(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Transactional
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

    public EnderecoDTO atualizarDadosEndereco(Long idEndereco, EnderecoDTO enderecoDTO) {
        Endereco enderecoEntity = enderecoRepository.findById(idEndereco)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado, pelo id " + idEndereco));

        Endereco endereco = usuarioConverter.updateEndereco(enderecoDTO, enderecoEntity);
        enderecoRepository.save(endereco);
        return usuarioConverterDTO.paraEnderecoDTO(endereco);
    }

    public TelefoneDTO atualizarDadosTelefone(Long idTelefone, TelefoneDTO telefoneDTO) {
        Telefone telefoneEntity = telefoneRepository.findById(idTelefone)
                .orElseThrow(() -> new ResourceNotFoundException("Telefone não encontrado, pelo id " + idTelefone));

        Telefone telefone = usuarioConverter.updateTelefone(telefoneDTO, telefoneEntity);
        telefoneRepository.save(telefone);
        return usuarioConverterDTO.paraTelefoneDTO(telefone);
    }

    public EnderecoDTO cadastroEndereco(String token, EnderecoDTO enderecoDTO) {
        // Primeiro remove "Bearer ", depois extrai o email
        String tokenLimpo = token.substring(7); // Remove "Bearer "
        String email = jwtUtil.extractUsername(tokenLimpo);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email não encontrado: " + email));

        Endereco endereco = usuarioConverter.paraEnderecoEntity(enderecoDTO, usuario.getId());
        enderecoRepository.save(endereco);

        return usuarioConverterDTO.paraEnderecoDTO(endereco);
    }

    public TelefoneDTO cadastroTelefone(String token, TelefoneDTO telefoneDTO) {
        // Primeiro remove "Bearer ", depois extrai o email
        String tokenLimpo = token.substring(7); // Remove "Bearer "
        String email = jwtUtil.extractUsername(tokenLimpo);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email não encontrado: " + email));

        Telefone telefone = usuarioConverter.paraTelefoneEntity(telefoneDTO, usuario.getId());
        telefoneRepository.save(telefone);

        return usuarioConverterDTO.paraTelefoneDTO(telefone);
    }
}
