package com.autobots.automanager.controles;

import java.util.List;

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

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.modelos.AdicionadorLinkEndereco;
import com.autobots.automanager.modelos.EnderecoAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.EnderecoRepositorio;

@RestController
public class EnderecoControle {
    @Autowired
    private EnderecoRepositorio repositorio;
    
    @Autowired
    private EnderecoRepositorio repositorioEndereco;

    @Autowired
    private AdicionadorLinkEndereco adicionadorLink;

    @Autowired
    private ClienteRepositorio repositorioCliente; 
    
    @Transactional
    @PostMapping("/cliente/{clienteId}/endereco")
    public ResponseEntity<?> criarEnderecoParaCliente(@PathVariable long clienteId, @RequestBody Endereco novoEndereco) {
        Cliente cliente = repositorioCliente.findById(clienteId).orElse(null);
        if (cliente != null) {
            if (cliente.getEndereco() != null) {
                return ResponseEntity.badRequest().body("Cliente com o ID " + clienteId + " já possui um endereço.");
            } else {
                repositorio.save(novoEndereco); 

                cliente.setEndereco(novoEndereco);
                repositorioCliente.save(cliente);
                adicionadorLink.adicionarLink(novoEndereco);

                return new ResponseEntity<>(cliente, HttpStatus.CREATED);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/endereco")
    public ResponseEntity<List<Endereco>> obterEnderecos() {
        List<Endereco> enderecos = repositorio.findAll();
        if (enderecos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            adicionadorLink.adicionarLink(enderecos);
            return new ResponseEntity<>(enderecos, HttpStatus.FOUND);
        }
    }

    @GetMapping("/endereco/{id}")
    public ResponseEntity<Endereco> obterEndereco(@PathVariable long id) {
        Endereco endereco = repositorio.findById(id).orElse(null);
        if (endereco == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            adicionadorLink.adicionarLink(endereco);
            return new ResponseEntity<>(endereco, HttpStatus.FOUND);
        }
    }

    @PutMapping("/atualizar/endereco/{id}")
    public ResponseEntity<?> atualizarEndereco(@PathVariable long id, @RequestBody Endereco atualizacao) {
        Endereco endereco = repositorio.findById(id).orElse(null);

        if (endereco != null) {
            EnderecoAtualizador atualizador = new EnderecoAtualizador();
            atualizador.atualizar(endereco, atualizacao);
            repositorio.save(endereco);
            adicionadorLink.adicionarLink(endereco);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @Transactional
    @DeleteMapping("/excluir/endereco/{enderecoId}")
    public ResponseEntity<?> excluirEnderecoPorId(@PathVariable long enderecoId) {
        Endereco endereco = repositorio.findById(enderecoId).orElse(null);

        if (endereco != null) {
            Cliente cliente = repositorioCliente.findByEndereco(endereco); 

            if (cliente != null) {
                cliente.setEndereco(null); 
                repositorioCliente.save(cliente); 
            }

            repositorio.delete(endereco); 
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Endereço com o ID " + enderecoId + " não encontrado.", HttpStatus.NOT_FOUND);
        }
    }
    
}