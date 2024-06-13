package com.autobots.automanager.modelos;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.DocumentoControle;
import com.autobots.automanager.controles.EmailControle;
import com.autobots.automanager.controles.EmpresaControle;
import com.autobots.automanager.entitades.Email;
import com.autobots.automanager.entitades.Empresa;

@Component
public class AdicionadorLinkEmail implements AdicionadorLink<Email>{
    @Override
    public void adicionarLink(List<Email> lista) {
        for (Email email : lista) {
            long id = email.getId();
            Link linkProprio = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder
                            .methodOn(EmailControle.class)
                            .obterEmailPorId(id))
                    .withSelfRel();
            email.add(linkProprio);
        }
    }

    @Override
    public void adicionarLink(Email objeto) {
        long id = objeto.getId();
        Link linkLista = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(EmailControle.class)
                        .obterEmails())
                .withRel("obterEmails");
        Link linkDel = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(EmailControle.class)
                        .deletarEmail(id))
                .withRel("deletarEmails");
        Link linkPut = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(EmailControle.class)
                        .atualizarEmail(id, objeto))
                .withRel("atualizarEmails");
        
        objeto.add(linkPut);
        objeto.add(linkDel);
        objeto.add(linkLista);
    }
}
