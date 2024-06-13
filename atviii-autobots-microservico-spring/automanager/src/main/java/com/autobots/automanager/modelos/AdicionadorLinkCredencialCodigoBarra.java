package com.autobots.automanager.modelos;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.CredencialCodigoBarraControle;
import com.autobots.automanager.entitades.CredencialCodigoBarra;

@Component
public class AdicionadorLinkCredencialCodigoBarra implements AdicionadorLink<CredencialCodigoBarra>{
	 
	@Override
	    public void adicionarLink(List<CredencialCodigoBarra> lista) {
	        for (CredencialCodigoBarra credencial : lista) {
	            long id = credencial.getId();
	            Link linkProprio = WebMvcLinkBuilder
	                    .linkTo(WebMvcLinkBuilder
	                            .methodOn(CredencialCodigoBarraControle.class)
	                            .obterCredencialCodigoBarraPorId(id))
	                    .withSelfRel();
	            credencial.add(linkProprio);
	        }
	    }

	    @Override
	    public void adicionarLink(CredencialCodigoBarra objeto) {
	        long id = objeto.getId();
	        Link linkLista = WebMvcLinkBuilder
	                .linkTo(WebMvcLinkBuilder
	                        .methodOn(CredencialCodigoBarraControle.class)
	                        .obterTodasCredenciaisCodigoBarra())
	                .withRel("obterCredenciaisCodigoBarra");
	        Link linkDel = WebMvcLinkBuilder
	                .linkTo(WebMvcLinkBuilder
	                        .methodOn(CredencialCodigoBarraControle.class)
	                        .deletarCredencialCodigoBarra(id))
	                .withRel("deletarCredencialCodigoBarra");
	        Link linkPut = WebMvcLinkBuilder
	                .linkTo(WebMvcLinkBuilder
	                        .methodOn(CredencialCodigoBarraControle.class)
	                        .atualizarCredencialCodigoBarra(id, objeto))
	                .withRel("atualizarCredencialCodigoBarra");

	        objeto.add(linkPut);
	        objeto.add(linkDel);
	        objeto.add(linkLista);
	    }
	}