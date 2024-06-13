package com.autobots.automanager.modelos;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.EmpresaControle;
import com.autobots.automanager.dto.EmpresaDTO;
import com.autobots.automanager.entitades.Empresa;

@Component
public class AdicionadorLinkEmpresa implements AdicionadorLink<Empresa> {
    @Override
    public void adicionarLink(List<Empresa> lista) {
        for (Empresa empresa : lista) {
            long id = empresa.getId();
            Link linkProprio = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder
                            .methodOn(EmpresaControle.class)
                            .obterEmpresaPorId(id))
                    .withSelfRel();
            empresa.add(linkProprio);
        }
    }

    @Override
    public void adicionarLink(Empresa objeto) {
        long id = objeto.getId();
        Link linkLista = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(EmpresaControle.class)
                        .obterEmpresa())
                .withRel("obterEmpresas");
        Link linkDel = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(EmpresaControle.class)
                        .excluirEmpresa(id))
                .withRel("deletarEmpresas");

        
        objeto.add(linkDel);
        objeto.add(linkLista);
    }
}
