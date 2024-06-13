package com.autobots.automanager.controles;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.autobots.automanager.entitades.Empresa;
import com.autobots.automanager.entitades.Telefone;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.modelos.AdicionadorLinkTelefone;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.TelefoneRepositorio;
import com.autobots.automanager.repositorios.UsuarioRepositorio;

@RestController
public class TelefoneControle {
	private static final Logger logger = LoggerFactory.getLogger(TelefoneControle.class);
    @Autowired
    private TelefoneRepositorio telefoneRepositorio;
    @Autowired
    private RepositorioEmpresa empresaRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private AdicionadorLinkTelefone adicionadorLink;
    
    @GetMapping("/telefone/{id}")
	public Telefone obterTelefonePorId(@PathVariable Long id) {
		Telefone telefone = telefoneRepositorio.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		adicionadorLink.adicionarLink(telefone);
		return telefone;
	}
	
	@GetMapping("/telefones")
	public List<Telefone> obterTelefones() {
		List<Telefone> telefone = telefoneRepositorio.findAll();
		adicionadorLink.adicionarLink(telefone);
		return telefone;
	}
    
	@PutMapping("/telefone/atualizar/{id}")
	public ResponseEntity<Telefone> atualizarTelefone(@PathVariable Long id, @RequestBody Telefone telefone) {
		Telefone telefoneExistente = telefoneRepositorio.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

		telefoneExistente.setDdd(telefone.getDdd());
		telefoneExistente.setNumero(telefone.getNumero());

		telefoneExistente = telefoneRepositorio.save(telefoneExistente);
		
		// Adicionando links HATEOAS ao telefone atualizado
		adicionadorLink.adicionarLink(telefoneExistente);

		return new ResponseEntity<>(telefoneExistente, HttpStatus.OK);
	}
    
    
	@PostMapping("/telefone/empresa/{idEmpresa}")
    @Transactional 
    public ResponseEntity<Empresa> cadastrarTelefoneEmpresa(@PathVariable Long idEmpresa, @RequestBody Telefone telefone) {
        Optional<Empresa> empresaOptional = empresaRepositorio.findById(idEmpresa);
        if (empresaOptional.isPresent()) {
            Empresa empresa = empresaOptional.get();
            logger.info("Adicionando telefone {} à empresa {}", telefone.getNumero(), empresa.getRazaoSocial());
            
            telefone = telefoneRepositorio.save(telefone); 
            
            // Adiciona links ao telefone recém-criado
            adicionadorLink.adicionarLink(telefone);

            empresa.getTelefones().add(telefone);

            empresa = empresaRepositorio.save(empresa); 

            return new ResponseEntity<>(empresa, HttpStatus.CREATED);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada.");
        }
    }
    
    @PostMapping("/telefone/usuario/{idUsuario}")
    @Transactional 
    public ResponseEntity<Usuario> cadastrarTelefoneUsuario(@PathVariable Long idUsuario, @RequestBody Telefone telefone) {
        Optional<Usuario> usuarioOptional = usuarioRepositorio.findById(idUsuario);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();

            telefone = telefoneRepositorio.save(telefone); 

            // Adiciona links ao telefone recém-criado
            adicionadorLink.adicionarLink(telefone);

            usuario.getTelefones().add(telefone);

            usuario = usuarioRepositorio.save(usuario);  

            return new ResponseEntity<>(usuario, HttpStatus.CREATED);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não Encontrado.");
        }
    }
    
    @DeleteMapping("/telefone/deletar/{id}")
    @Transactional
    public ResponseEntity<Void> deletarTelefone(@PathVariable Long id) {
        Optional<Telefone> telefoneOptional = telefoneRepositorio.findById(id);
        if (telefoneOptional.isPresent()) {
            Telefone telefone = telefoneOptional.get();

            List<Empresa> empresas = empresaRepositorio.findByTelefones(telefone); 

            for (Empresa empresa : empresas) {
                empresa.getTelefones().remove(telefone);
                empresaRepositorio.save(empresa); 
            }
            
            List<Usuario> usuarios = usuarioRepositorio.findByTelefones(telefone); 

            for (Usuario usuario : usuarios) {
                usuario.getTelefones().remove(telefone);
                usuarioRepositorio.save(usuario); 
            }
            
            telefoneRepositorio.delete(telefone);
            return ResponseEntity.noContent().build(); 
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Telefone não encontrado.");
        }
    }
}