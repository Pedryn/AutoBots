package com.autobots.automanager.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autobots.automanager.entitades.Empresa;
import com.autobots.automanager.entitades.Endereco;
import com.autobots.automanager.entitades.Mercadoria;
import com.autobots.automanager.entitades.Servico;
import com.autobots.automanager.entitades.Telefone;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.entitades.Venda;


public interface RepositorioEmpresa extends JpaRepository<Empresa, Long> {
	//public Empresa findByRazaoSocial(String nome); // Remove este comentário
	Optional<Empresa> findByRazaoSocial(String razaoSocial); // Adicione este método
	List<Empresa> findByTelefones(Telefone telefone);
	List<Empresa> findByMercadorias(Mercadoria mercadoria);
	List<Empresa> findByServicos(Servico servico);
	Empresa findByEndereco(Endereco endereco);
	List<Empresa> findByUsuarios(Usuario usuario);
	Empresa findByUsuariosNome(String nome); 
	List<Empresa> findByVendas(Venda venda);

}