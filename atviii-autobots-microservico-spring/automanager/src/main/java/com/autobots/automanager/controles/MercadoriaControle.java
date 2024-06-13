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

import com.autobots.automanager.entitades.Empresa;
import com.autobots.automanager.entitades.Endereco;
import com.autobots.automanager.entitades.Mercadoria;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.entitades.Venda;
import com.autobots.automanager.modelos.AdicionadorLinkMercadoria;
import com.autobots.automanager.repositorios.MercadoriaRepositorio;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import com.autobots.automanager.repositorios.VendaRepositorio;

@RestController
public class MercadoriaControle {
	
	@Autowired
	private MercadoriaRepositorio mercadoriaRepositorio;
	@Autowired
	private UsuarioRepositorio usuarioRepositorio;
	@Autowired
	private RepositorioEmpresa empresaRepositorio;
	@Autowired
	private VendaRepositorio vendaRepositorio;
	@Autowired
	private AdicionadorLinkMercadoria adicionadorLinkMercadoria;
	
	@GetMapping("/mercadorias")
    public List<Mercadoria> obterMercadorias() {
		List<Mercadoria> mercadoria = mercadoriaRepositorio.findAll();
        adicionadorLinkMercadoria.adicionarLink(mercadoria);
        return mercadoriaRepositorio.findAll();
    }
	
	@GetMapping("/mercadoria/{id}")
    public Mercadoria obterMercadoriaPorId(@PathVariable Long id) {
		Mercadoria mercadoria = mercadoriaRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        adicionadorLinkMercadoria.adicionarLink(mercadoria);
        return mercadoria;
    }
	
	@PutMapping("/mercadoria/atualizar/{id}")
    public ResponseEntity<Mercadoria> atualizarMercadoria(@PathVariable Long id, @RequestBody Mercadoria mercadoria) {
		Mercadoria mercadoriaExistente = mercadoriaRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
		mercadoriaExistente.setValidade(mercadoria.getValidade());
		mercadoriaExistente.setFabricao(mercadoria.getFabricao());
		mercadoriaExistente.setCadastro(mercadoria.getCadastro());
		mercadoriaExistente.setNome(mercadoria.getNome());
		mercadoriaExistente.setQuantidade(mercadoria.getQuantidade());
		mercadoriaExistente.setValor(mercadoria.getValor());
		mercadoriaExistente.setDescricao(mercadoria.getDescricao());

		mercadoriaExistente = mercadoriaRepositorio.save(mercadoriaExistente);
        adicionadorLinkMercadoria.adicionarLink(mercadoriaExistente);
        return new ResponseEntity<>(mercadoriaExistente, HttpStatus.OK);
    }
	
	@PostMapping("/mercadoria/usuario/{idUsuario}")
    public ResponseEntity<Usuario> cadastrarMercadoriaUsuario(@PathVariable Long idUsuario, @RequestBody Mercadoria mercadoria) {
        Optional<Usuario> usuarioOptional = usuarioRepositorio.findById(idUsuario);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();

            mercadoria = mercadoriaRepositorio.save(mercadoria); 

            adicionadorLinkMercadoria.adicionarLink(mercadoria);

            usuario.getMercadorias().add(mercadoria); 

            usuario = usuarioRepositorio.save(usuario);  

            return new ResponseEntity<>(usuario, HttpStatus.CREATED);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não Encontrado.");
        }
    }
	
	@PostMapping("/mercadoria/empresa/{idEmpresa}")
    public ResponseEntity<Empresa> cadastrarMercadoriaEmpresa(@PathVariable Long idEmpresa, @RequestBody Mercadoria mercadoria) {
        Optional<Empresa> empresaOptional = empresaRepositorio.findById(idEmpresa);
        if (empresaOptional.isPresent()) {
            Empresa empresa = empresaOptional.get();

            mercadoria = mercadoriaRepositorio.save(mercadoria); 

            adicionadorLinkMercadoria.adicionarLink(mercadoria);

            empresa.getMercadorias().add(mercadoria); 

            empresa = empresaRepositorio.save(empresa);  

            return new ResponseEntity<>(empresa, HttpStatus.CREATED);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não Encontrado.");
        }
    }
	
	@DeleteMapping("/mercadoria/deletar/{id}")
    @Transactional
    public ResponseEntity<Void> deletarMercadoria(@PathVariable Long id) {
        Optional<Mercadoria> mercadoriaOptional = mercadoriaRepositorio.findById(id);
        if (mercadoriaOptional.isPresent()) {
            Mercadoria mercadoria = mercadoriaOptional.get();

            List<Empresa> empresas = empresaRepositorio.findByMercadorias(mercadoria); 

            for (Empresa empresa : empresas) {
                empresa.getMercadorias().remove(mercadoria);
                empresaRepositorio.save(empresa); 
            }
            
            List<Usuario> usuarios = usuarioRepositorio.findByMercadorias(mercadoria); 

            for (Usuario usuario : usuarios) {
                usuario.getMercadorias().remove(mercadoria);
                usuarioRepositorio.save(usuario); 
            }
            
            List<Venda> vendas = vendaRepositorio.findByMercadorias(mercadoria); 
            for (Venda venda : vendas) {
                venda.getMercadorias().remove(mercadoria);
                vendaRepositorio.save(venda); 
            }
            
            mercadoriaRepositorio.delete(mercadoria);
            return ResponseEntity.noContent().build(); 
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mercadoria não encontrado.");
        }
    }
}
