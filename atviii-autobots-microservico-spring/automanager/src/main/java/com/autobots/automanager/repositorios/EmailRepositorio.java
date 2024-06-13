package com.autobots.automanager.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autobots.automanager.entitades.Email;

public interface EmailRepositorio extends JpaRepository<Email, Long>{

}
