package com.autobots.automanager.modelos;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.MercadoriaControle;
import com.autobots.automanager.entitades.Mercadoria;

@Component
public class AdicionadorLinkMercadoria implements AdicionadorLink<Mercadoria>{
	@Override
    public void adicionarLink(List<Mercadoria> lista) {
        for (Mercadoria mercadoria : lista) {
            long id = mercadoria.getId();
            Link linkProprio = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder
                            .methodOn(MercadoriaControle.class)
                            .obterMercadoriaPorId(id))
                    .withSelfRel();
            mercadoria.add(linkProprio);
        }
    }

    @Override
    public void adicionarLink(Mercadoria objeto) {
        long id = objeto.getId();
        Link linkLista = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(MercadoriaControle.class)
                        .obterMercadorias())
                .withRel("obterMercadorias");
        Link linkDel = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(MercadoriaControle.class)
                        .deletarMercadoria(id))
                .withRel("deletarMercadorias");
        Link linkPut = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(MercadoriaControle.class)
                        .atualizarMercadoria(id, objeto))
                .withRel("atualizarMercadorias");
        
        objeto.add(linkPut);
        objeto.add(linkDel);
        objeto.add(linkLista);
    }
}
