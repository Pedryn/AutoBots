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
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.modelos.AdicionadorLinkTelefone;
import com.autobots.automanager.modelos.TelefoneAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.TelefoneRepositorio;

@RestController
public class TelefoneControle {
    
    @Autowired
    private ClienteRepositorio repositorioCliente;

    @Autowired
    private TelefoneRepositorio repositorioTelefone;
    
    @Autowired
    private AdicionadorLinkTelefone adicionadorLink;
    
    // Cadastrar Telefone
    @Transactional
    @PostMapping("/cliente/{clienteId}/telefone")
    public ResponseEntity<?> adicionarTelefoneAoCliente(@PathVariable long clienteId, @RequestBody Telefone novoTelefone) {
        Cliente cliente = repositorioCliente.findById(clienteId).orElse(null);

        if (cliente != null) {
            repositorioTelefone.save(novoTelefone);

            cliente.addTelefone(novoTelefone); 
            repositorioCliente.save(cliente);

            return new ResponseEntity<>(cliente, HttpStatus.CREATED);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Listagem dos Telefones
    @GetMapping("/telefone")
    public ResponseEntity<List<Telefone>> obterTelefones() {
        List<Telefone> telefones = repositorioTelefone.findAll();
        if (telefones.isEmpty()) {
            ResponseEntity<List<Telefone>> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return resposta;
        } else {
            adicionadorLink.adicionarLink(telefones);
            ResponseEntity<List<Telefone>> resposta = new ResponseEntity<>(telefones, HttpStatus.FOUND);
            return resposta;
        }
    }
    
    // Atualizar Telefone
    @PutMapping("/atualizar/telefone")
    public ResponseEntity<?> atualizarTelefone(@RequestBody Telefone atualizacao) {
        HttpStatus status = HttpStatus.CONFLICT;
        Telefone telefone = repositorioTelefone.getById(atualizacao.getId());
        if (telefone != null) {
            TelefoneAtualizador atualizador = new TelefoneAtualizador();
            atualizador.atualizar(telefone, atualizacao);
            repositorioTelefone.save(telefone);
            status = HttpStatus.OK;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(status);
    }
    
    // Excluir Telefone
    @DeleteMapping("/telefone/{telefoneId}")
    public ResponseEntity<Void> excluirTelefone(@PathVariable long telefoneId) {
        Telefone telefone = repositorioTelefone.findById(telefoneId).orElse(null);

        if (telefone != null) {
            Cliente cliente = repositorioCliente.findByTelefonesId(telefoneId); 

            if (cliente != null) {
                cliente.getTelefones().remove(telefone);
                repositorioCliente.save(cliente);
            }

            repositorioTelefone.delete(telefone);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}