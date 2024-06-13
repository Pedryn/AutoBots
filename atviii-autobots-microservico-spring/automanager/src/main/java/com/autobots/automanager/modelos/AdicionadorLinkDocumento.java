package com.autobots.automanager.modelos;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.DocumentoControle;
import com.autobots.automanager.controles.EnderecoControle;
import com.autobots.automanager.entitades.Documento;

@Component
public class AdicionadorLinkDocumento implements AdicionadorLink<Documento> {

    @Override
    public void adicionarLink(List<Documento> lista) {
        for (Documento documento : lista) {
            long id = documento.getId();
            Link linkProprio = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder
                            .methodOn(DocumentoControle.class)
                            .obterDocumentoPorId(id))
                    .withSelfRel();
            Link linkAtualizar = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder
                            .methodOn(DocumentoControle.class)
                            .atualizarDocumento(id, null))
                    .withRel("atualizar");

            documento.add(linkProprio);
            documento.add(linkAtualizar);
        }
    }

    @Override
    public void adicionarLink(Documento objeto) {
        long id = objeto.getId();
        Link linkLista = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(DocumentoControle.class)
                        .obterDocumentos())
                .withRel("obterDocumentos");
        Link linkDel = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(DocumentoControle.class)
                        .deletarDocumento(id))
                .withRel("deletarDocumentos");
        Link linkPut = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(DocumentoControle.class)
                        .atualizarDocumento(id, objeto))
                .withRel("atualizarDocumentos");
        
        objeto.add(linkPut);
        objeto.add(linkDel);
        objeto.add(linkLista);
    }
}