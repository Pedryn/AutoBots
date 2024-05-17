package com.autobots.automanager.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.autobots.automanager.entidades.Endereco;

@Repository
public interface EnderecoRepositorio extends JpaRepository<Endereco, Long>{

}
