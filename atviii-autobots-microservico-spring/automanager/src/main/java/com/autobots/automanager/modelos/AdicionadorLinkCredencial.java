package com.autobots.automanager.modelos;

import java.util.List;

import com.autobots.automanager.entitades.Credencial;

public interface AdicionadorLinkCredencial {

    void adicionarLink(List<Credencial> lista);
    void adicionarLink(Credencial objeto);
}