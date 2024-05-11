package com.autobots.automanager.entidades;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
public class Documento {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column
	private String tipo;
	@Column(unique = true)
	private String numero;
	@JsonIgnore // Evita a serialização deste campo
	@ManyToOne
    private Cliente cliente; 
	
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
}