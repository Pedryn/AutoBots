package com.autobots.automanager.repositorios;

import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.entidades.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface DocumentoRepositorio extends JpaRepository<Documento, Long> {

}
