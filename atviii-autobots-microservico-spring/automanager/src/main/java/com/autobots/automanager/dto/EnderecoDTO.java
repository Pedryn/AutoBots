package com.autobots.automanager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnderecoDTO {
	private Long id;
	private String rua;
	private String numeroEndereco;
	private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
}
