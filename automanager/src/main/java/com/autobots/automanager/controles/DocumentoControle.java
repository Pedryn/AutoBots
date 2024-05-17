package com.autobots.automanager.controles;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.modelos.AdicionadorLinkDocumento;
import com.autobots.automanager.modelos.DocumentoAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.DocumentoRepositorio;

@RestController
public class DocumentoControle {
	
    @Autowired
    private ClienteRepositorio repositorioCliente;

    @Autowired
    private DocumentoRepositorio repositorioDocumento;
	
	@Autowired
	private DocumentoRepositorio repositorio;

	@Autowired
	private AdicionadorLinkDocumento adicionadorLink;
	
	// cadastro documento
	@Transactional
	@PostMapping("/cliente/{clienteId}/documento")
	public ResponseEntity<?> adicionarDocumentoAoCliente(@PathVariable long clienteId, @RequestBody Documento novoDocumento) {
	    Cliente cliente = repositorioCliente.findById(clienteId).orElse(null);

	    if (cliente != null) {
	        repositorioDocumento.save(novoDocumento);
	        cliente.addDocumentos(novoDocumento); 
	        repositorioCliente.save(cliente);

	        return new ResponseEntity<>(cliente, HttpStatus.CREATED);
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}
	
	// listagem dos documentos
	@GetMapping("/documentos")
	public ResponseEntity<List<Documento>> obterDocumento() {
		List<Documento> documentos = repositorio.findAll();
		if (documentos.isEmpty()) {
			ResponseEntity<List<Documento>> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return resposta;
		} else {
			adicionadorLink.adicionarLink(documentos);
			ResponseEntity<List<Documento>> resposta = new ResponseEntity<>(documentos, HttpStatus.FOUND);
			return resposta;
		}
	}
	
	// atualizar documentos
	@PutMapping("/atualizar/documento")
	public ResponseEntity<?> atualizarDocumento(@RequestBody Documento atualizacao) {
		HttpStatus status = HttpStatus.CONFLICT;
		Documento documento = repositorio.getById(atualizacao.getId());
		if (documento != null) {
			DocumentoAtualizador atualizador = new DocumentoAtualizador();
			atualizador.atualizar(documento, atualizacao);
			repositorio.save(documento);
			status = HttpStatus.OK;
		} else {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(status);
	}
	
	// excluir documento
	@DeleteMapping("/documento/{documentoId}")
	public ResponseEntity<Void> excluirDocumento(@PathVariable long documentoId) {
	    Documento documento = repositorioDocumento.findById(documentoId).orElse(null);

	    if (documento != null) {
	        Cliente cliente = repositorioCliente.findByDocumentosId(documentoId); 
	        
	        if (cliente != null) {
	            cliente.getDocumentos().remove(documento);
	            repositorioCliente.save(cliente);
	        }

	        repositorioDocumento.delete(documento); 
	        return ResponseEntity.noContent().build(); 
	    } else {
	        return ResponseEntity.notFound().build(); 
	    }
	}

}
