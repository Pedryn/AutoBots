package com.autobots.automanager.controles;

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
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.modelo.EnderecoAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.EnderecoRepositorio;

@RestController
@RequestMapping("/endereco")
public class EnderecoControle {
	@Autowired
	private EnderecoAtualizador atualizador;
	
    @Autowired
    private EnderecoRepositorio repositorioEndereco;
    @Autowired
    private ClienteRepositorio repositorioCliente;

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Endereco> obterEnderecoDoCliente(@PathVariable long clienteId) {
        Cliente cliente = repositorioCliente.getById(clienteId);
        if (cliente != null && cliente.getEndereco() != null) {
            return ResponseEntity.ok(cliente.getEndereco());
        } else {
            System.out.println("Endereço não encontrado para o cliente com o ID " + clienteId);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/cliente/{clienteId}/cadastrar")
    public ResponseEntity<Cliente> criarEnderecoParaCliente(@PathVariable long clienteId, @RequestBody Endereco novoEndereco) {
        Cliente cliente = repositorioCliente.getById(clienteId);
        if (cliente != null) {
            if (cliente.getEndereco() != null) {
                System.out.println("Cliente com o ID " + clienteId + " já possui um endereço. Não é possível criar outro.");
                return ResponseEntity.badRequest().build(); // Retorna erro 400 (Bad Request)
            } else {
                cliente.setEndereco(novoEndereco);
                return ResponseEntity.ok(repositorioCliente.save(cliente));
            }
        } else {
            System.out.println("Cliente com o ID " + clienteId + " não encontrado. Não foi possível criar o endereço.");
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Void> atualizarEndereco(@PathVariable long id, @RequestBody Endereco atualizacao) {
        Endereco endereco = repositorioEndereco.getById(id);
        if (endereco != null) {
            atualizador.atualizar(endereco, atualizacao); 
            repositorioEndereco.save(endereco);
            return ResponseEntity.ok().build();
        } else {
            System.out.println("Cliente com o ID " + id + " não encontrado. Não foi possível atualizar.");
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/cliente/{clienteId}/atualizar")
    public ResponseEntity<Cliente> atualizarEnderecoParaCliente(@PathVariable long clienteId, @RequestBody Endereco novoEndereco) {
        Cliente cliente = repositorioCliente.getById(clienteId);
        if (cliente != null) {
            Endereco enderecoExistente = cliente.getEndereco();
            if (enderecoExistente != null) {
                enderecoExistente.setEstado(novoEndereco.getEstado());
                enderecoExistente.setCidade(novoEndereco.getCidade());
                enderecoExistente.setBairro(novoEndereco.getBairro());
                enderecoExistente.setRua(novoEndereco.getRua());
                enderecoExistente.setNumero(novoEndereco.getRua());
                enderecoExistente.setCodigoPostal(novoEndereco.getCodigoPostal());
                enderecoExistente.setInformacoesAdicionais(novoEndereco.getInformacoesAdicionais());
            } else {
                cliente.setEndereco(novoEndereco);
            }
            return ResponseEntity.ok(repositorioCliente.save(cliente));
        } else {
            System.out.println("Cliente com o ID " + clienteId + " não encontrado. Não foi possível criar/atualizar o endereço.");
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/cliente/{clienteId}/excluir")
    public ResponseEntity<Void> excluirEnderecoDoCliente(@PathVariable long clienteId) {
        Cliente cliente = repositorioCliente.getById(clienteId);
        if (cliente != null) {
            cliente.setEndereco(null); // Remove o endereço associado ao cliente
            repositorioCliente.save(cliente);
            return ResponseEntity.ok().build();
        } else {
            System.out.println("Cliente com o ID " + clienteId + " não encontrado. Não foi possível excluir o endereço.");
            return ResponseEntity.notFound().build();
        }
    }

}