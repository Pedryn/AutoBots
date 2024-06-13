package com.autobots.automanager.dto;

import java.util.HashSet;
import java.util.Set;

import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.entitades.Venda;
import com.autobots.automanager.enumeracoes.TipoVeiculo;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VeiculoDTO {
	private Long id;
	private TipoVeiculo tipo;
	private String modelo;
	private String placa;
	//private Usuario proprietario;
	//private Set<Venda> vendas = new HashSet<>();
	
}
