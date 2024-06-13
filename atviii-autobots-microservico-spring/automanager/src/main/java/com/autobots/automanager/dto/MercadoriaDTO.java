package com.autobots.automanager.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MercadoriaDTO {
	private Long id;
	private Date validade;
	private Date fabricao;
	private Date cadastro;
	private String nome;
	private long quantidade;
	private double valor;
	private String descricao;
	
	@JsonBackReference 
    private EmpresaDTO empresa;
}
