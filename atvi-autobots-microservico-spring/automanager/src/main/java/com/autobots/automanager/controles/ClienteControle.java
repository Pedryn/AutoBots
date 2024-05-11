package com.autobots.automanager.controles;

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
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.modelo.ClienteAtualizador;
import com.autobots.automanager.modelo.ClienteSelecionador;
import com.autobots.automanager.repositorios.ClienteRepositorio;

@RestController
@RequestMapping("/cliente")
public class ClienteControle {
	@Autowired
	private ClienteRepositorio repositorio;
	@Autowired
	private ClienteSelecionador selecionador;
	@Autowired
	private ClienteAtualizador atualizador;

	@GetMapping("/cliente/{id}")
	public Cliente obterCliente(@PathVariable long id) {
		List<Cliente> clientes = repositorio.findAll();
		return selecionador.selecionar(clientes, id);
	}

	@GetMapping("/clientes")
	public List<Cliente> obterClientes() {
		List<Cliente> clientes = repositorio.findAll();
		return clientes;
	}

	@PostMapping("/cadastro")
	public void cadastrarCliente(@RequestBody Cliente cliente) {
		repositorio.save(cliente);
	}

	@PutMapping("/atualizar/{id}")
    public ResponseEntity<Void> atualizarCliente(@PathVariable long id, @RequestBody Cliente atualizacao) {
        Cliente cliente = repositorio.getById(id);
        if (cliente != null) {
            atualizador.atualizar(cliente, atualizacao); 
            repositorio.save(cliente);
            return ResponseEntity.ok().build();
        } else {
            System.out.println("Cliente com o ID " + id + " não encontrado. Não foi possível atualizar.");
            return ResponseEntity.notFound().build();
        }
    }
	
	@PutMapping("/atualizarCompleto/{id}")
	public ResponseEntity<Void> atualizarClienteCompleto(@PathVariable long id, @RequestBody Cliente atualizacao) {
	    Cliente cliente = repositorio.getById(id);
	    if (cliente != null) {
	    	
	    	cliente.setNome(atualizacao.getNome());
	        cliente.setDataNascimento(atualizacao.getDataNascimento());
	        cliente.setDataCadastro(atualizacao.getDataCadastro());
	        cliente.setNomeSocial(atualizacao.getNomeSocial());

	        List<Telefone> telefonesOriginais = cliente.getTelefones();
	        List<Telefone> telefonesAtualizacao = atualizacao.getTelefones();
	        for (int i = 0; i < telefonesOriginais.size(); i++) {
	            Telefone telefoneAtualizacao = telefonesAtualizacao.get(i);
	            if (i < telefonesOriginais.size()) {
	                Telefone telefoneOriginal = telefonesOriginais.get(i);
	                telefoneOriginal.setDdd(telefoneAtualizacao.getDdd());
	                telefoneOriginal.setNumero(telefoneAtualizacao.getNumero());
	            } else {
	                cliente.getTelefones().add(telefoneAtualizacao);
	            }
	        }

	        Endereco endereco = atualizacao.getEndereco();
	        if (endereco != null) {
	            Endereco enderecoCliente = cliente.getEndereco();
	            enderecoCliente.setEstado(endereco.getEstado());
	            enderecoCliente.setCidade(endereco.getCidade());
	            enderecoCliente.setBairro(endereco.getBairro());
	            enderecoCliente.setRua(endereco.getRua());
	            enderecoCliente.setNumero(endereco.getNumero());
	            enderecoCliente.setCodigoPostal(endereco.getCodigoPostal());
	            enderecoCliente.setInformacoesAdicionais(endereco.getInformacoesAdicionais());
	        }

	        List<Documento> documentosOriginais = cliente.getDocumentos();
	        List<Documento> documentosAtualizacao = atualizacao.getDocumentos();
	        for (int i = 0; i < documentosOriginais.size(); i++) {
	            Documento documentoAtualizacao = documentosAtualizacao.get(i);
	            if (i < documentosOriginais.size()) {
	                Documento documentoOriginal = documentosOriginais.get(i);
	                documentoOriginal.setTipo(documentoAtualizacao.getTipo());
	                documentoOriginal.setNumero(documentoAtualizacao.getNumero());
	            } else {
	                cliente.getDocumentos().add(documentoAtualizacao);
	            }
	        }

	        repositorio.save(cliente);
	        return ResponseEntity.ok().build();
	    } else {
	        System.out.println("Cliente com o ID " + id + " não encontrado. Não foi possível atualizar.");
	        return ResponseEntity.notFound().build();
	    }
	}
	
	@DeleteMapping("/excluir/{id}")
	public void excluirCliente(@PathVariable long id) {
	    Cliente cliente = repositorio.getById(id);
	    if (cliente != null) {
	        repositorio.delete(cliente);
	    } else {
	        System.out.println("Cliente com o ID " + id + " não encontrado. Não foi possível excluir.");
	    }
	}
}