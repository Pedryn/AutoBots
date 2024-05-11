package com.autobots.automanager.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Telefone;

@Repository
public interface TelefoneRepositorio extends JpaRepository<Telefone, Long> {
    List<Telefone> findByCliente(Cliente cliente);

}
