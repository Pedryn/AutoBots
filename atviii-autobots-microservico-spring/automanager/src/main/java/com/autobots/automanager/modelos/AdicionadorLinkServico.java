package com.autobots.automanager.modelos;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.ServicoControle;
import com.autobots.automanager.entitades.Servico;

@Component
public class AdicionadorLinkServico implements AdicionadorLink<Servico>{
	@Override
    public void adicionarLink(List<Servico> lista) {
        for (Servico servico : lista) {
            long id = servico.getId();
            Link linkProprio = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder
                            .methodOn(ServicoControle.class)
                            .obterServicoPorId(id))
                    .withSelfRel();
            servico.add(linkProprio);
        }
    }

    @Override
    public void adicionarLink(Servico objeto) {
        long id = objeto.getId();
        Link linkLista = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(ServicoControle.class)
                        .obterServicos())
                .withRel("obterServicos");
        Link linkDel = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(ServicoControle.class)
                        .deletarServico(id))
                .withRel("deletarServicos");
        Link linkPut = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(ServicoControle.class)
                        .atualizarServico(id, objeto))
                .withRel("deletarServicos");
        
        objeto.add(linkPut);
        objeto.add(linkDel);
        objeto.add(linkLista);
    }
}
