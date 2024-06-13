package com.autobots.automanager.modelos;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.CredencialUsuarioSenhaControle;
import com.autobots.automanager.entitades.CredencialUsuarioSenha;

@Component
public class AdicionadorLinkCredencialUsuarioSenha implements AdicionadorLink<CredencialUsuarioSenha> {
	@Override
    public void adicionarLink(List<CredencialUsuarioSenha> lista) {
        for (CredencialUsuarioSenha credencial : lista) {
            long id = credencial.getId();
            Link linkProprio = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder
                            .methodOn(CredencialUsuarioSenhaControle.class)
                            .obterCredencialUsuarioSenhaPorId(id))
                    .withSelfRel();
            credencial.add(linkProprio);
        }
    }

    @Override
    public void adicionarLink(CredencialUsuarioSenha objeto) {
        long id = objeto.getId();
        Link linkLista = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(CredencialUsuarioSenhaControle.class)
                        .obterTodasCredenciaisUsuarioSenha())
                .withRel("obterCredenciaisUsuarioSenha"); // Corrigido
        Link linkDel = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(CredencialUsuarioSenhaControle.class)
                        .deletarCredencialUsuarioSenha(id))
                .withRel("deletarCredencialUsuarioSenha"); // Corrigido
        Link linkPut = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(CredencialUsuarioSenhaControle.class)
                        .atualizarCredencialUsuarioSenha(id, objeto))
                .withRel("atualizarCredencialUsuarioSenha"); // Corrigido

        objeto.add(linkPut);
        objeto.add(linkDel);
        objeto.add(linkLista);
    }
}