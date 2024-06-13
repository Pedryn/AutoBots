package com.autobots.automanager.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.autobots.automanager.entitades.Documento;
import com.autobots.automanager.entitades.Email;
import com.autobots.automanager.entitades.Endereco;
import com.autobots.automanager.entitades.Mercadoria;
import com.autobots.automanager.entitades.Telefone;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.entitades.Veiculo;
import com.autobots.automanager.entitades.Venda;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long>{
	List<Usuario> findByTelefones(Telefone telefone);
	List<Usuario> findByEmails(Email email);
	List<Usuario> findByMercadorias(Mercadoria mercadoria);
	List<Usuario> findByVeiculos(Veiculo veiculo);
	Usuario findByEndereco(Endereco endereco);
	List<Usuario> findByDocumentos(Documento documento);
	List<Usuario> findByVendas(Venda venda);
	@Query("SELECT u FROM Usuario u JOIN u.credenciais c WHERE c.id = :credencialId")
    Optional<Usuario> findByCredenciaisId(@Param("credencialId") Long credencialId);
}
