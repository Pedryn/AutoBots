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
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.modelo.TelefoneAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.TelefoneRepositorio;

@RestController
@RequestMapping("/telefone")
public class TelefoneControle {
	
	@Autowired
    private TelefoneAtualizador atualizador;
	
    @Autowired
    private TelefoneRepositorio repositorioTelefone;

    @Autowired
    private ClienteRepositorio repositorioCliente;

    @GetMapping("/cliente/{clienteId}/listar")
    public ResponseEntity<List<Telefone>> obterTelefoneDoCliente(@PathVariable long clienteId) {
        Cliente cliente = repositorioCliente.getById(clienteId);
        if (cliente != null) {
            return ResponseEntity.ok(cliente.getTelefones());
        } else {
            System.out.println("Cliente com o ID " + clienteId + " não encontrado.");
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/cliente/{clienteId}/criar")
    public ResponseEntity<Cliente> criarTelefoneParaCliente(@PathVariable long clienteId, @RequestBody Telefone novoTelefone) {
        Cliente cliente = repositorioCliente.getById(clienteId);
        if (cliente != null) {
            cliente.addTelefone(novoTelefone);
            return ResponseEntity.ok(repositorioCliente.save(cliente));
        } else {
            System.out.println("Cliente com o ID " + clienteId + " não encontrado. Não foi possível criar o documento.");
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/cliente/{clienteId}/excluir/{telefoneId}")
    public ResponseEntity<Void> excluirTelefone(@PathVariable long clienteId, @PathVariable long telefoneId) {
        Cliente cliente = repositorioCliente.getById(clienteId);
        if (cliente != null) {
            List<Telefone> telefones = cliente.getTelefones();
            for (int i = 0; i < telefones.size(); i++) {
                if (telefones.get(i).getId() == telefoneId) {
                    telefones.remove(i);
                    repositorioCliente.save(cliente);
                    return ResponseEntity.ok().build();
                }
            }
            System.out.println("Documento com o ID " + telefoneId + " não encontrado para o cliente " + clienteId);
            return ResponseEntity.notFound().build();
        } else {
            System.out.println("Cliente com o ID " + clienteId + " não encontrado.");
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Void> atualizarTelefone(@PathVariable long id, @RequestBody Telefone atualizacao) {
        Telefone telefone = repositorioTelefone.getById(id);
        if (telefone != null) {
            atualizador.atualizar(telefone, atualizacao); 
            repositorioTelefone.save(telefone);
            return ResponseEntity.ok().build();
        } else {
            System.out.println("Cliente com o ID " + id + " não encontrado. Não foi possível atualizar.");
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/cliente/{clienteId}/atualizar/{telefoneId}")
    public ResponseEntity<Cliente> atualizarTelefoneDoCliente(@PathVariable long clienteId, @PathVariable long telefoneId, @RequestBody Telefone telefoneAtualizado) {
        Cliente cliente = repositorioCliente.getById(clienteId);
        if (cliente != null) {
            List<Telefone> telefones = cliente.getTelefones();
            for (Telefone telefone : telefones) {
                if (telefone.getId() == telefoneId) {
                    telefone.setDdd(telefoneAtualizado.getDdd());
                    telefone.setNumero(telefoneAtualizado.getNumero());
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
