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

import com.autobots.automanager.dto.VeiculoDTO;
import com.autobots.automanager.entitades.Empresa;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.entitades.Veiculo;
import com.autobots.automanager.entitades.Venda;
import com.autobots.automanager.modelos.AdicionadorLinkEmpresa;
import com.autobots.automanager.modelos.AdicionadorLinkVeiculo;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import com.autobots.automanager.repositorios.VeiculoRepositorio;
import com.autobots.automanager.repositorios.VendaRepositorio;

@RestController
public class VeiculoControle {
	@Autowired
	private VeiculoRepositorio veiculoRepositorio;
	@Autowired
    private UsuarioRepositorio usuarioRepositorio;
	@Autowired
	private VendaRepositorio vendaRepositorio;
	@Autowired
	private AdicionadorLinkVeiculo adicionadorLink;
	
	@GetMapping("/veiculo/{id}")
	public Veiculo obterVeiculoPorId(@PathVariable Long id) {
		Veiculo veiculo = veiculoRepositorio.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		adicionadorLink.adicionarLink(veiculo);
		return veiculo;
	}
	
	@GetMapping("/veiculos")
	public List<Veiculo> obterVeiculos() {
		List<Veiculo> veiculo = veiculoRepositorio.findAll();
		adicionadorLink.adicionarLink(veiculo);
		return veiculo;
	}
	
	@PutMapping("/veiculo/atualizar/{id}")
	public ResponseEntity<Veiculo> atualizarVeiculo(@PathVariable Long id, @RequestBody VeiculoDTO veiculoDTO) {
	    Veiculo veiculoExistente = veiculoRepositorio.findById(id)
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

	    veiculoExistente.setModelo(veiculoDTO.getModelo());
	    veiculoExistente.setPlaca(veiculoDTO.getPlaca());
	    veiculoExistente.setTipo(veiculoDTO.getTipo());
	    
	    veiculoExistente = veiculoRepositorio.save(veiculoExistente);
	    
	    // Adicionando links HATEOAS ao veículo atualizado
	    adicionadorLink.adicionarLink(veiculoExistente);

	    return new ResponseEntity<>(veiculoExistente, HttpStatus.OK);
	}
	 
	@PostMapping("/veiculo/{idUsuario}")
	public ResponseEntity<Veiculo> criarVeiculo(@PathVariable Long idUsuario, @RequestBody VeiculoDTO veiculoDTO) {

	    Usuario usuario = usuarioRepositorio.findById(idUsuario)
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

	    Veiculo novoVeiculo = new Veiculo();
	    novoVeiculo.setModelo(veiculoDTO.getModelo());
	    novoVeiculo.setPlaca(veiculoDTO.getPlaca());
	    novoVeiculo.setTipo(veiculoDTO.getTipo());
	    novoVeiculo.setProprietario(usuario); 

	    Veiculo veiculoSalvo = veiculoRepositorio.save(novoVeiculo);
	    
	    // Adicionando links HATEOAS ao veículo recém-criado
	    adicionadorLink.adicionarLink(veiculoSalvo);

	    return new ResponseEntity<>(veiculoSalvo, HttpStatus.CREATED);
	}
	 
	 @DeleteMapping("/veiculo/deletar/{id}")
	    @Transactional
	    public ResponseEntity<Void> deletarVeiculo(@PathVariable Long id) {
	        Optional<Veiculo> veiculoOptional = veiculoRepositorio.findById(id);
	        if (veiculoOptional.isPresent()) {
	            Veiculo veiculo = veiculoOptional.get();

	            List<Usuario> usuarios = usuarioRepositorio.findByVeiculos(veiculo); 
	            for (Usuario usuario : usuarios) {
	                usuario.getVeiculos().remove(veiculo);
	                usuarioRepositorio.save(usuario); 
	            }

	            for (Venda venda : veiculo.getVendas()) {
	                venda.setVeiculo(null); 
	                vendaRepositorio.save(venda); 
	            }

	            veiculoRepositorio.delete(veiculo);
	            return ResponseEntity.noContent().build(); 
	        } else {
	            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Veiculo não encontrado.");
	        }
	    }
}
