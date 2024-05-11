package com.autobots.automanager.controles; // Defina o pacote correto

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.modelo.DocumentoAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.DocumentoRepositorio;

@RestController
@RequestMapping("/documento")
public class DocumentoControle {
	@Autowired
	private DocumentoAtualizador atualizador;
	
    @Autowired
    private DocumentoRepositorio repositorioDocumento;

    @Autowired
    private ClienteRepositorio repositorioCliente;

    @GetMapping("/cliente/{clienteId}/listar")
    public ResponseEntity<List<Documento>> obterDocumentosDoCliente(@PathVariable long clienteId) {
        Cliente cliente = repositorioCliente.getById(clienteId);
        if (cliente != null) {
            return ResponseEntity.ok(cliente.getDocumentos());
        } else {
            System.out.println("Cliente com o ID " + clienteId + " não encontrado.");
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/cliente/{clienteId}/criar")
    public ResponseEntity<Cliente> criarDocumentoParaCliente(@PathVariable long clienteId, @RequestBody Documento novoDocumento) {
        Cliente cliente = repositorioCliente.getById(clienteId);
        if (cliente != null) {
            cliente.addDocumentos(novoDocumento);
            return ResponseEntity.ok(repositorioCliente.save(cliente));
        } else {
            System.out.println("Cliente com o ID " + clienteId + " não encontrado. Não foi possível criar o documento.");
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/cliente/{clienteId}/excluir/{documentoId}")
    public ResponseEntity<Void> excluirDocumento(@PathVariable long clienteId, @PathVariable long documentoId) {
        Cliente cliente = repositorioCliente.getById(clienteId);
        if (cliente != null) {
            List<Documento> documentos = cliente.getDocumentos();
            for (int i = 0; i < documentos.size(); i++) {
                if (documentos.get(i).getId() == documentoId) {
                    documentos.remove(i);
                    repositorioCliente.save(cliente);
                    return ResponseEntity.ok().build();
                }
            }
            System.out.println("Documento com o ID " + documentoId + " não encontrado para o cliente " + clienteId);
            return ResponseEntity.notFound().build();
        } else {
            System.out.println("Cliente com o ID " + clienteId + " não encontrado.");
            return ResponseEntity.notFound().build();
        }
    }
    

	@PutMapping("/atualizar/{id}")
    public ResponseEntity<Void> atualizarDocumento(@PathVariable long id, @RequestBody Documento atualizacao) {
        Documento documento = repositorioDocumento.getById(id);
        if (documento != null) {
            atualizador.atualizar(documento, atualizacao); 
            repositorioDocumento.save(documento);
            return ResponseEntity.ok().build();
        } else {
            System.out.println("Cliente com o ID " + id + " não encontrado. Não foi possível atualizar.");
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/cliente/{clienteId}/atualizar/{documentoId}")
    public ResponseEntity<Cliente> atualizarDocumentoDoCliente(@PathVariable long clienteId, @PathVariable long documentoId, @RequestBody Documento documentoAtualizado) {
        Cliente cliente = repositorioCliente.getById(clienteId);
        if (cliente != null) {
            List<Documento> documentos = cliente.getDocumentos();
            for (Documento documento : documentos) {
                if (documento.getId() == documentoId) {
                    documento.setTipo(documentoAtualizado.getTipo());
                    documento.setNumero(documentoAtualizado.getNumero());
                    break; // Sai do loop após encontrar e atualizar o documento
                }
            }
            return ResponseEntity.ok(repositorioCliente.save(cliente));
        } else {
            System.out.println("Cliente com o ID " + clienteId + " não encontrado. Não foi possível atualizar o documento.");
            return ResponseEntity.notFound().build();
        }
    }
}
