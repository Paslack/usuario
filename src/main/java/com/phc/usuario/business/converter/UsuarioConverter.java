package com.phc.usuario.business.converter;

import com.phc.usuario.business.dto.EnderecoDTO;
import com.phc.usuario.business.dto.TelefoneDTO;
import com.phc.usuario.business.dto.UsuarioDTO;
import com.phc.usuario.infrastructure.entity.Endereco;
import com.phc.usuario.infrastructure.entity.Telefone;
import com.phc.usuario.infrastructure.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UsuarioConverter {

     public Usuario converteParaUsuario(UsuarioDTO usuarioDTO) {
         return Usuario.builder()
                 .nome(usuarioDTO.getNome())
                 .email(usuarioDTO.getEmail())
                 .senha(usuarioDTO.getSenha())
                 .enderecos(usuarioDTO.getEnderecos() != null ? paraListaEndereco(usuarioDTO.getEnderecos()) : null)
                 .telefones(usuarioDTO.getTelefones() != null ? paraListaTelefone(usuarioDTO.getTelefones()) : null)
                 .build();
     }

     // Converte os dados da Lista DTO para uma Lista da Entidade Endereços
     public List<Endereco> paraListaEndereco(List<EnderecoDTO> enderecoDTOS) {
         return enderecoDTOS.stream().map(this::paraEndereco)
                 .collect(Collectors.toList());
     }


     // Seta os atributos DTO para Entidade Endereço
     public Endereco paraEndereco(EnderecoDTO enderecoDTO) {
         return Endereco.builder()
                 .rua(enderecoDTO.getRua())
                 .numero(enderecoDTO.getNumero())
                 .cidade(enderecoDTO.getCidade())
                 .complemento(enderecoDTO.getComplemento())
                 .cep(enderecoDTO.getCep())
                 .estado(enderecoDTO.getEstado())
                 .build();
     }


     // Converte os dados da Lista DTO para uma Lista da Entidade Telefone
     public List<Telefone> paraListaTelefone(List<TelefoneDTO> telefoneDTOS) {
         return telefoneDTOS.stream().map(this::paraTelefone)
                 .collect(Collectors.toList());
     }

     // Seta os atributos de DTO para Entidade Telefone
     public Telefone paraTelefone(TelefoneDTO telefoneDTO) {
         return Telefone.builder()
                 .numero(telefoneDTO.getNumero())
                 .ddd(telefoneDTO.getDdd())
                 .build();
     }

     public Usuario updateUsuario(UsuarioDTO usuarioDTO, Usuario entity) {
         return Usuario.builder()
                 .id(entity.getId())
                 .nome(usuarioDTO.getNome() != null ? usuarioDTO.getNome() : entity.getNome())
                 .email(usuarioDTO.getEmail() != null ? usuarioDTO.getEmail() : entity.getEmail())
                 .senha(usuarioDTO.getSenha() != null ? usuarioDTO.getSenha() : entity.getSenha())
                 .enderecos(entity.getEnderecos())
                 .telefones(entity.getTelefones())
                 .build();
     }

     public Endereco updateEndereco(EnderecoDTO enderecoDTO, Endereco enderecoEntity) {
         return Endereco.builder()
                 .id(enderecoEntity.getId())
                 .cidade(enderecoDTO.getCidade() != null ? enderecoDTO.getCidade() : enderecoEntity.getCidade())
                 .estado(enderecoDTO.getEstado() != null ? enderecoDTO.getEstado() : enderecoEntity.getEstado())
                 .cep(enderecoDTO.getCep() != null ? enderecoDTO.getCep() : enderecoEntity.getCep())
                 .complemento(enderecoDTO.getComplemento() != null ? enderecoDTO.getComplemento() : enderecoEntity.getComplemento())
                 .rua(enderecoDTO.getRua() != null ? enderecoDTO.getRua() : enderecoEntity.getRua())
                 .numero(enderecoDTO.getNumero() != null ? enderecoDTO.getNumero() : enderecoEntity.getNumero())
                 .build();
     }

     public Telefone updateTelefone(TelefoneDTO telefoneDTO, Telefone telefoneEntity) {
         return Telefone.builder()
                 .id(telefoneEntity.getId())
                 .ddd(telefoneDTO.getDdd() != null ? telefoneDTO.getDdd() : telefoneEntity.getDdd())
                 .numero(telefoneDTO.getNumero() != null ? telefoneDTO.getNumero() : telefoneEntity.getNumero())
                 .build();
     }

    public Endereco paraEnderecoEntity(EnderecoDTO enderecoDTO, Long idUsuario) {
        return Endereco.builder()
                .usuario_id(idUsuario)
                .rua(enderecoDTO.getRua())
                .numero(enderecoDTO.getNumero())
                .cidade(enderecoDTO.getCidade())
                .complemento(enderecoDTO.getComplemento())
                .cep(enderecoDTO.getCep())
                .estado(enderecoDTO.getEstado())
                .build();
    }

    public Telefone paraTelefoneEntity(TelefoneDTO telefoneDTO, Long idUsuario) {
         return Telefone.builder()
                 .usuario_id(idUsuario)
                 .ddd(telefoneDTO.getDdd())
                 .numero(telefoneDTO.getNumero())
                 .build();
    }
}
