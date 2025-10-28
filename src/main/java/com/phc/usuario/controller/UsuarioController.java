package com.phc.usuario.controller;

import com.phc.usuario.business.dto.EnderecoDTO;
import com.phc.usuario.business.dto.TelefoneDTO;
import com.phc.usuario.business.dto.UsuarioDTO;
import com.phc.usuario.business.service.UsuarioService;
import com.phc.usuario.business.service.ViaCepService;
import com.phc.usuario.infrastructure.client.ViaCepDTO;
import com.phc.usuario.infrastructure.security.JwtUtil;
import com.phc.usuario.infrastructure.security.SecurityConfig;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")
@Tag(name = "Tarefas", description = "Agendador de Tarefas")
@SecurityRequirement(name = SecurityConfig.SECURITY_SCHEME)
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ViaCepService viaCepService;

    public UsuarioController(UsuarioService usuarioService, AuthenticationManager authenticationManager, JwtUtil jwtUtil, ViaCepService viaCepService) {
        this.usuarioService = usuarioService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.viaCepService = viaCepService;
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> salvarUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        return ResponseEntity.ok(usuarioService.salvarUsuario(usuarioDTO));
    }

    @PostMapping("/login")
    public String login(@RequestBody UsuarioDTO usuarioDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usuarioDTO.getEmail(), usuarioDTO.getSenha())
        );
        return "Bearer " + jwtUtil.generateToken(authentication.getName());
    }

    @GetMapping
    public ResponseEntity<UsuarioDTO> buscarUsuarioPorEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok(usuarioService.buscarUsuarioPorEmail(email));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deletarUsuarioPorEmail(@PathVariable String email) {
        usuarioService.deletarUsuarioPorEmail(email);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<UsuarioDTO> atualizaDadosUsuario(@RequestHeader("Authorization") String token, @RequestBody UsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.atualizaDadosUsuario(token, dto));
    }

    @PutMapping("/endereco")
    public ResponseEntity<EnderecoDTO> atualizaDadosEndereco(@RequestParam Long id, @RequestBody EnderecoDTO enderecoDTO) {
        return ResponseEntity.ok(usuarioService.atualizarDadosEndereco(id, enderecoDTO));
    }

    @PutMapping("/telefone")
    public ResponseEntity<TelefoneDTO> atualizaDadosTelefone(@RequestParam Long id, @RequestBody TelefoneDTO telefoneDTO) {
        return ResponseEntity.ok(usuarioService.atualizarDadosTelefone(id, telefoneDTO));
    }

    @PostMapping("/endereco")
    public ResponseEntity<EnderecoDTO> cadastroEndereco(@RequestHeader("Authorization") String token, @RequestBody EnderecoDTO enderecoDTO) {
        return ResponseEntity.ok(usuarioService.cadastroEndereco(token, enderecoDTO));
    }

    @PostMapping("/telefone")
    public ResponseEntity<TelefoneDTO> cadastroTelefone(@RequestHeader("Authorization") String token, @RequestBody TelefoneDTO telefoneDTO) {
        return ResponseEntity.ok(usuarioService.cadastroTelefone(token, telefoneDTO));
    }

    @GetMapping("/endereco/{cep}")
    public ResponseEntity<ViaCepDTO> buscarDadosCep(@PathVariable("cep") String cep) {
        return ResponseEntity.ok(viaCepService.buscarEnderecoPorCep(cep));
    }
}
