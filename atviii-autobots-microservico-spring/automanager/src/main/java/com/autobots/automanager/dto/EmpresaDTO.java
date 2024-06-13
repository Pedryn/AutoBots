package com.autobots.automanager.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.autobots.automanager.entitades.Empresa;
import com.autobots.automanager.entitades.Endereco;
import com.autobots.automanager.entitades.Mercadoria;
import com.autobots.automanager.entitades.Servico;
import com.autobots.automanager.entitades.Telefone;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.entitades.Venda;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class EmpresaDTO {
	private Long id;
	private String razaoSocial;
	private String nomeFantasia;
	private Set<Telefone> telefones = new HashSet<>();
//	private Endereco endereco;
	private Date cadastro;
//	private Set<Usuario> usuarios = new HashSet<>();
//	private Set<Mercadoria> mercadorias = new HashSet<>();
//	private Set<Servico> servicos = new HashSet<>();
//	private Set<Venda> vendas = new HashSet<>();
	

}