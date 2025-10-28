package com.phc.usuario.business.service;

import com.phc.usuario.infrastructure.client.ViaCepClient;
import com.phc.usuario.infrastructure.client.ViaCepDTO;
import org.springframework.stereotype.Service;

@Service
public class ViaCepService {
    private final ViaCepClient viaCepClient;

    public ViaCepService(ViaCepClient viaCepClient) {
        this.viaCepClient = viaCepClient;
    }

    public ViaCepDTO buscarEnderecoPorCep(String cep) {
        return viaCepClient.buscarEnderecoPorCep(processarCep(cep));
    }

    private String processarCep(String cep) {
        String cepFormatado = cep
                .trim()
                .replace("-", "")
                .replace(" ", "");

        if(!cepFormatado.matches("^\\d{8}$")) {
            throw new IllegalArgumentException("O CEP está inválido, informe o CEP correto");
        }
        return cepFormatado;
    }
}
