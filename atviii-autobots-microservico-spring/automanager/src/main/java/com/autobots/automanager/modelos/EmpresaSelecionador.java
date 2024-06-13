package com.autobots.automanager.modelos;

import org.springframework.stereotype.Component;

import com.autobots.automanager.entitades.Empresa;

import java.util.List;

@Component
public class EmpresaSelecionador {
	public Empresa selecionar(List<Empresa> empresas, long id) {
		Empresa selecionado = null;
		for (Empresa empresa : empresas) {
			if (empresa.getId() == id) {
				selecionado = empresa;
			}
		}
		return selecionado;
	}
}
