package com.autobots.automanager.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autobots.automanager.entitades.Mercadoria;
import com.autobots.automanager.entitades.Servico;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.entitades.Veiculo;
import com.autobots.automanager.entitades.Venda;

public interface VendaRepositorio extends JpaRepository<Venda, Long>{
	List<Venda> findByMercadorias(Mercadoria mercadoria);
	List<Venda> findByServicos(Servico servico);
	List<Venda> findByCliente(Usuario cliente);
	List<Venda> findByFuncionario(Usuario funcionario);
	List<Venda> findByVeiculo(Veiculo veiculo);
	boolean existsByMercadoriasId(Long mercadoriaId);
}
