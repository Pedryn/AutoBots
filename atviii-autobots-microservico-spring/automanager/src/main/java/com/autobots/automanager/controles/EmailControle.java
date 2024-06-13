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
import com.autobots.automanager.entitades.Email;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.modelos.AdicionadorLinkEmail;
import com.autobots.automanager.repositorios.EmailRepositorio;
import com.autobots.automanager.repositorios.UsuarioRepositorio;

@RestController
public class EmailControle {
	@Autowired
	private EmailRepositorio emailRepositorio;
	@Autowired
	private UsuarioRepositorio usuarioRepositorio;
	@Autowired
	private AdicionadorLinkEmail adicionadorLinkEmail;
	
	
	@GetMapping("/emails")
    public List<Email> obterEmails() {
        List<Email> email = emailRepositorio.findAll();
        adicionadorLinkEmail.adicionarLink(email);
        return email;
    }
	
	@GetMapping("/email/{id}")
    public Email obterEmailPorId(@PathVariable Long id) {
        Email email = emailRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        adicionadorLinkEmail.adicionarLink(email);
        return email;
    }
	
	@PutMapping("/email/atualizar/{id}")
    public ResponseEntity<Email> atualizarEmail(@PathVariable Long id, @RequestBody Email email) {
        Email emailExistente = emailRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        emailExistente.setEndereco(email.getEndereco());

        emailExistente = emailRepositorio.save(emailExistente);
        adicionadorLinkEmail.adicionarLink(emailExistente);

        return new ResponseEntity<>(emailExistente, HttpStatus.OK);
    }
	
	@PostMapping("/email/usuario/{idUsuario}")
    public ResponseEntity<Usuario> cadastrarEmailUsuario(@PathVariable Long idUsuario, @RequestBody Email email) {
        Optional<Usuario> usuarioOptional = usuarioRepositorio.findById(idUsuario);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();

            email = emailRepositorio.save(email); 
            
            adicionadorLinkEmail.adicionarLink(email);

            usuario.getEmails().add(email); 

            usuario = usuarioRepositorio.save(usuario);  

            return new ResponseEntity<>(usuario, HttpStatus.CREATED);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não Encontrado.");
        }
    }
	
	@DeleteMapping("/email/deletar/{id}")
    @Transactional
    public ResponseEntity<Void> deletarEmail(@PathVariable Long id) {
        Optional<Email> emailOptional = emailRepositorio.findById(id);
        if (emailOptional.isPresent()) {
            Email email = emailOptional.get();

            List<Usuario> usuarios = usuarioRepositorio.findByEmails(email); 

            for (Usuario usuario : usuarios) {
            	usuario.getEmails().remove(email);
                usuarioRepositorio.save(usuario); 
            }
            
            emailRepositorio.delete(email);
            return ResponseEntity.noContent().build(); 
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Telefone não encontrado.");
        }
    }
}
