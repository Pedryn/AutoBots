package com.autobots.automanager.controles;

import java.util.List;
import java.util.Optional;

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
import org.springframework.web.server.ResponseStatusException;

import com.autobots.automanager.entitades.Documento;
import com.autobots.automanager.entitades.Empresa;
import com.autobots.automanager.entitades.Telefone;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.modelos.AdicionadorLinkDocumento;
import com.autobots.automanager.repositorios.DocumentoRepositorio;
import com.autobots.automanager.repositorios.UsuarioRepositorio;

@RestController
public class DocumentoControle {
	@Autowired
	private DocumentoRepositorio documentoRepositorio;
	@Autowired
	private AdicionadorLinkDocumento adicionadorLinkDocumento;
	@Autowired
    private UsuarioRepositorio usuarioRepositorio;
	
	@GetMapping("/documentos")
    public List<Documento> obterDocumentos() {
        List<Documento> documento = documentoRepositorio.findAll();
        adicionadorLinkDocumento.adicionarLink(documento);
        return documento;
    }
	
	@GetMapping("/documento/{id}")
    public Documento obterDocumentoPorId(@PathVariable Long id) {
        Documento documento = documentoRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        adicionadorLinkDocumento.adicionarLink(documento);
        return documento;
    }
	
	@PutMapping("/documento/atualizar/{id}")
    public ResponseEntity<Documento> atualizarDocumento(@PathVariable Long id, @RequestBody Documento documento) {
        Documento documentoExistente = documentoRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        documentoExistente.setTipo(documento.getTipo());
        documentoExistente.setDataEmissao(documento.getDataEmissao());
        documentoExistente.setNumero(documento.getNumero());
        
        documentoExistente = documentoRepositorio.save(documentoExistente);

        adicionadorLinkDocumento.adicionarLink(documentoExistente);

        return new ResponseEntity<>(documentoExistente, HttpStatus.OK);
    }
    
    @PostMapping("/documento/usuario/{idUsuario}")
    @Transactional 
    public ResponseEntity<Usuario> cadastrarDocumentoUsuario(@PathVariable Long idUsuario, @RequestBody Documento documento) {
        Optional<Usuario> usuarioOptional = usuarioRepositorio.findById(idUsuario);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();

            documento = documentoRepositorio.save(documento); 

            adicionadorLinkDocumento.adicionarLink(documento);

            usuario.getDocumentos().add(documento);

            usuario = usuarioRepositorio.save(usuario);  

            return new ResponseEntity<>(usuario, HttpStatus.CREATED);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não Encontrado.");
        }
    }
    
    @DeleteMapping("/documento/deletar/{id}")
    @Transactional
    public ResponseEntity<Void> deletarDocumento(@PathVariable Long id) {
        Optional<Documento> documentoOptional = documentoRepositorio.findById(id);
        if (documentoOptional.isPresent()) {
            Documento documento = documentoOptional.get();
            adicionadorLinkDocumento.adicionarLink(documento);
            List<Usuario> usuarios = usuarioRepositorio.findByDocumentos(documento); 

            for (Usuario usuario : usuarios) {
                usuario.getDocumentos().remove(documento);
                usuarioRepositorio.save(usuario); 
            }
            
            documentoRepositorio.delete(documento);
            return ResponseEntity.noContent().build(); 
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "documento não encontrado.");
        }
    }
}
