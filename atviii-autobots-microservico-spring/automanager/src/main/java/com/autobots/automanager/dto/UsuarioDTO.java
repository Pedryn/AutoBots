package com.autobots.automanager.dto;

import java.util.HashSet;
import java.util.Set;

import com.autobots.automanager.enumeracoes.PerfilUsuario;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class UsuarioDTO {
	private Long id;
    private String nome;
    private String nomeSocial;
    private Set<PerfilUsuario> perfis = new HashSet<>();
    private Set<TelefoneDTO> telefones = new HashSet<>();
    private EnderecoDTO endereco;
    private Set<DocumentoDTO> documentos = new HashSet<>();
    private Set<EmailDTO> emails = new HashSet<>();
    private Set<CredencialDTO> credenciais = new HashSet<>();
    
    
    @JsonBackReference 
    private EmpresaDTO empresa;
}
