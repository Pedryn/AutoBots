package com.autobots.automanager.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServicoDTO {
	private Long id;
	private String nome;
	private double valor;
	private String descricao;
	
	@JsonBackReference 
    private EmpresaDTO empresa;
}
