package com.autobots.automanager.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.entitades.Veiculo;
import com.autobots.automanager.entitades.Venda;

public interface VeiculoRepositorio extends JpaRepository<Veiculo, Long>{
	List<Veiculo> findByProprietario(Usuario proprietario);
	List<Veiculo> findByVendas(Venda venda);

}