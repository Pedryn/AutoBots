package com.autobots.automanager.modelos;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.VeiculoControle;
import com.autobots.automanager.entitades.Veiculo;

@Component
public class AdicionadorLinkVeiculo implements AdicionadorLink<Veiculo>{
	@Override
    public void adicionarLink(List<Veiculo> lista) {
        for (Veiculo veiculo : lista) {
            long id = veiculo.getId();
            Link linkProprio = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder
                            .methodOn(VeiculoControle.class)
                            .obterVeiculoPorId(id))
                    .withSelfRel();
            veiculo.add(linkProprio);
        }
    }

    @Override
    public void adicionarLink(Veiculo objeto) {
        long id = objeto.getId();
        Link linkLista = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(VeiculoControle.class)
                        .obterVeiculos())
                .withRel("obterVeiculos");
        Link linkDel = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(VeiculoControle.class)
                        .deletarVeiculo(id))
                .withRel("deletarVeiculos");
        
        objeto.add(linkDel);
        objeto.add(linkLista);
    }
}
