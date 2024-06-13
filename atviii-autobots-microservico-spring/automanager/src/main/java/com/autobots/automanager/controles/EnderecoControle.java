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

import com.autobots.automanager.entitades.Email;
import com.autobots.automanager.entitades.Empresa;
import com.autobots.automanager.entitades.Endereco;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.modelos.AdicionarLinkEndereco;
import com.autobots.automanager.repositorios.EnderecoRepositorio;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.UsuarioRepositorio;

@RestController
public class EnderecoControle {
	@Autowired
	private EnderecoRepositorio enderecoRepositorio;
	@Autowired
	private UsuarioRepositorio usuarioRepositorio;
	@Autowired
    private RepositorioEmpresa empresaRepositorio;
	@Autowired
	private AdicionarLinkEndereco adicionadorLinkEndereco;
	
	
	@GetMapping("/enderecos")
    public List<Endereco> obterEnderecos() {
		List<Endereco> endereco = enderecoRepositorio.findAll();
        adicionadorLinkEndereco.adicionarLink(endereco);
        return enderecoRepositorio.findAll();
    }
	
	@GetMapping("/endereco/{id}")
	public Endereco obterEnderecoPorId(@PathVariable Long id) {
		Endereco endereco = enderecoRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        adicionadorLinkEndereco.adicionarLink(endereco);
        return endereco;
    }
	
	 @PutMapping("/endereco/atualizar/{id}")
	    public ResponseEntity<Endereco> atualizarEndereco(@PathVariable Long id, @RequestBody Endereco endereco) {
	        Endereco enderecoExistente = enderecoRepositorio.findById(id)
	                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

	        enderecoExistente.setEstado(endereco.getEstado());
	        enderecoExistente.setCidade(endereco.getCidade());
	        enderecoExistente.setBairro(endereco.getBairro());
	        enderecoExistente.setRua(endereco.getRua());
	        enderecoExistente.setNumero(endereco.getNumero());
	        enderecoExistente.setCodigoPostal(endereco.getCodigoPostal());
	        enderecoExistente.setInformacoesAdicionais(endereco.getInformacoesAdicionais());
	        
	        enderecoExistente = enderecoRepositorio.save(enderecoExistente);
	        adicionadorLinkEndereco.adicionarLink(enderecoExistente);
	        return new ResponseEntity<>(enderecoExistente, HttpStatus.OK);
	 }

	 @Transactional
	    @PostMapping("/usuario/{usuarioId}/endereco")
	    public ResponseEntity<?> criarEnderecoParaCliente(@PathVariable long usuarioId, @RequestBody Endereco novoEndereco) {
	        Usuario usuario = usuarioRepositorio.findById(usuarioId).orElse(null);

	        if (usuario != null) {
	            if (usuario.getEndereco() != null) {
	                return ResponseEntity.badRequest().body("Cliente com o ID " + usuarioId + " já possui um endereço.");
	            } else {
	                enderecoRepositorio.save(novoEndereco); 
	                adicionadorLinkEndereco.adicionarLink(novoEndereco);
	                
	                usuario.setEndereco(novoEndereco);
	                usuarioRepositorio.save(usuario);

	                return new ResponseEntity<>(usuario, HttpStatus.CREATED);
	            }
	        } else {
	            return ResponseEntity.notFound().build();
	        }
	    }

	 @Transactional
	    @PostMapping("/empresa/{empresaId}/endereco")
	    public ResponseEntity<?> criarEnderecoParaEmpresa(@PathVariable long empresaId, @RequestBody Endereco novoEndereco) {
	        Empresa empresa = empresaRepositorio.findById(empresaId).orElse(null);

	        if (empresa != null) {
	            if (empresa.getEndereco() != null) {
	                return ResponseEntity.badRequest().body("Cliente com o ID " + empresaId + " já possui um endereço.");
	            } else {
	                enderecoRepositorio.save(novoEndereco); 
	                adicionadorLinkEndereco.adicionarLink(novoEndereco);

	                empresa.setEndereco(novoEndereco);
	                empresaRepositorio.save(empresa);

	                return new ResponseEntity<>(empresa, HttpStatus.CREATED);
	            }
	        } else {
	            return ResponseEntity.notFound().build();
	        }
	    }

	 
	    @Transactional
	    @DeleteMapping("/endereco/{id}") // Rota para deletar pelo ID do Endereco
	    public ResponseEntity<Void> deletarEnderecos(@PathVariable Long id) {
	        Optional<Endereco> enderecoOptional = enderecoRepositorio.findById(id);
	        if (enderecoOptional.isPresent()) {
	            Endereco endereco = enderecoOptional.get();

	            // Remove a associação do endereço com o usuário ou empresa (se houver)
	            Usuario usuario = usuarioRepositorio.findByEndereco(endereco);
	            if (usuario != null) {
	                usuario.setEndereco(null);
	                usuarioRepositorio.save(usuario);
	            }

	            Empresa empresa = empresaRepositorio.findByEndereco(endereco);
	            if (empresa != null) {
	                empresa.setEndereco(null);
	                empresaRepositorio.save(empresa);
	            }

	            enderecoRepositorio.delete(endereco);
	            return ResponseEntity.noContent().build();
	        } else {
	            return ResponseEntity.notFound().build();
	        }
	    }
}