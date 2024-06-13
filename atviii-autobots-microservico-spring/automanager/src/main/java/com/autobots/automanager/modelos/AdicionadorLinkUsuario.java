package com.autobots.automanager.modelos;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.UsuarioControle;
import com.autobots.automanager.entitades.Usuario;

@Component
public class AdicionadorLinkUsuario implements AdicionadorLink<Usuario>{
    @Override
    public void adicionarLink(List<Usuario> lista) {
        for (Usuario usuario : lista) {
            long id = usuario.getId();
            Link linkProprio = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder
                            .methodOn(UsuarioControle.class)
                            .obterUsuarioPorId(id))
                    .withSelfRel();
            usuario.add(linkProprio);
        }
    }

    @Override
    public void adicionarLink(Usuario objeto) {
        long id = objeto.getId();
        Link linkLista = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(UsuarioControle.class)
                        .obterUsuarios())
                .withRel("obterUsuarios");
        Link linkDel = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(UsuarioControle.class)
                        .deletarUsuario(id))
                .withRel("deletarUsuarios");

        
        objeto.add(linkDel);
        objeto.add(linkLista);
    }
}
