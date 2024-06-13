package com.autobots.automanager.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.autobots.automanager.entitades.Mercadoria;
import com.autobots.automanager.entitades.Servico;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.entitades.Veiculo;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VendaDTO {
	private Long id;
    private Date cadastro; // Adicione a data aqui
    private Date dataCadastroMercadoria; 
    private String identificacao;
    private Long idCliente; // ID do Cliente
    private Long idFuncionario; // ID do Funcionário
    private Set<Long> mercadorias = new HashSet<>(); // IDs das Mercadorias
    private Set<Long> servicos = new HashSet<>(); // IDs dos Serviços
    private Long idVeiculo; // ID do Veículo
	
    @JsonBackReference 
    private EmpresaDTO empresa;
	
	
}
