package com.autobots.automanager.dto;

import java.util.Date;

import org.springframework.hateoas.RepresentationModel;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class CredencialDTO  extends RepresentationModel<CredencialDTO>{
	private Long id;
	private Date criacao;
	private Date ultimoAcesso;
	private boolean inativo;
	private Long codigo;
    private String tipo; 
    private String nomeUsuario;
    private String senha;
}
