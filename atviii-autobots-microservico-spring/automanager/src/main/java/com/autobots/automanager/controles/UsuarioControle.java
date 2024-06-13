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

import com.autobots.automanager.dto.UsuarioDTO;
import com.autobots.automanager.entitades.Empresa;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.entitades.Veiculo;
import com.autobots.automanager.entitades.Venda;
import com.autobots.automanager.modelos.AdicionadorLinkUsuario;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import com.autobots.automanager.repositorios.VeiculoRepositorio;
import com.autobots.automanager.repositorios.VendaRepositorio;

@RestController
public class UsuarioControle {
	@Autowired
	private UsuarioRepositorio usuarioRepositorio;
	@Autowired
	private AdicionadorLinkUsuario adicionadorLinkUsuario;
	@Autowired
	private RepositorioEmpresa empresaRepositorio;
	@Autowired
	private VendaRepositorio vendaRepositorio;
	@Autowired
	private VeiculoRepositorio veiculoRepositorio;
	
	@GetMapping("/usuarios")
    public List<Usuario> obterUsuarios() {
		List<Usuario> usuario = usuarioRepositorio.findAll();
        adicionadorLinkUsuario.adicionarLink(usuario);
        return usuarioRepositorio.findAll();
    }
	
	@GetMapping("/usuario/{id}")
	public Usuario obterUsuarioPorId(@PathVariable Long id) {
		Usuario usuario = usuarioRepositorio.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		adicionadorLinkUsuario.adicionarLink(usuario);
		return usuario;
	}
	
	@DeleteMapping("/usuario/deletar/{id}")
	@Transactional
	public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
	    Optional<Usuario> usuarioOptional = usuarioRepositorio.findById(id);
	    if (usuarioOptional.isPresent()) {
	        Usuario usuario = usuarioOptional.get();

	        // Desassocia o usuário das empresas
	        List<Empresa> empresas = empresaRepositorio.findByUsuarios(usuario); 
	        for (Empresa empresa : empresas) {
	            empresa.getUsuarios().remove(usuario);
	            empresaRepositorio.save(empresa); 
	        }

	        // Desassocia o usuário dos veículos
	        List<Veiculo> veiculos = veiculoRepositorio.findByProprietario(usuario); 
	        for (Veiculo veiculo : veiculos) {
	            veiculo.setProprietario(null); 
	            veiculoRepositorio.save(veiculo);
	        }

	        // Desassocia o usuário das vendas como cliente
	        List<Venda> vendasCliente = vendaRepositorio.findByCliente(usuario); 
	        for (Venda venda : vendasCliente) {
	            venda.setCliente(null); 
	            vendaRepositorio.save(venda);
	        }

	        // Desassocia o usuário das vendas como funcionário
	        List<Venda> vendasFuncionario = vendaRepositorio.findByFuncionario(usuario); 
	        for (Venda venda : vendasFuncionario) {
	            venda.setFuncionario(null);
	            vendaRepositorio.save(venda);
	        }

	        // Remove o usuário do banco de dados
	        usuarioRepositorio.delete(usuario);
	        return ResponseEntity.noContent().build(); 
	    } else {
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.");
	    }
	}
	
	@PutMapping("/usuario/atualizar/{id}")
	public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @RequestBody UsuarioDTO usuarioDTO) {
	    Usuario usuario = usuarioRepositorio.findById(id)
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

	    usuario.setNome(usuarioDTO.getNome());
        usuario.setNomeSocial(usuarioDTO.getNomeSocial());

	    usuario = usuarioRepositorio.save(usuario);
	    adicionadorLinkUsuario.adicionarLink(usuario);
	    return new ResponseEntity<>(usuario, HttpStatus.OK);
	}
	 
	 @PostMapping("/usuario/empresa/{empresaId}")
	 public ResponseEntity<Usuario> criarUsuario(@PathVariable Long empresaId, @RequestBody UsuarioDTO usuarioDTO) {
	     Usuario usuario = new Usuario(); 
	     usuario.setNome(usuarioDTO.getNome());
	     usuario.setNomeSocial(usuarioDTO.getNomeSocial());
	     // ... preencha os outros atributos do usuário ...

	     Optional<Empresa> empresaOptional = empresaRepositorio.findById(empresaId);

	     if (empresaOptional.isPresent()) {
	         Empresa empresa = empresaOptional.get();

	         // Adiciona o usuário à empresa (não precisa mais definir a empresa do usuário)
	         empresa.getUsuarios().add(usuario); 

	         // Salva o usuário no banco de dados
	         usuario = usuarioRepositorio.save(usuario);

	         // Adiciona links HATEOAS
	         adicionadorLinkUsuario.adicionarLink(usuario);

	         // Retorna o usuário criado
	         return new ResponseEntity<>(usuario, HttpStatus.CREATED); 
	     } else {
	         throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada.");
	     }
	 }
}
