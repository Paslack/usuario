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
                 .enderecos(paraListaEndereco(usuarioDTO.getEnderecos()))
                 .telefones(paraListaTelefone(usuarioDTO.getTelefones()))
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
}
