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
import com.autobots.automanager.entitades.Servico;
import com.autobots.automanager.entitades.Venda;
import com.autobots.automanager.modelos.AdicionadorLinkServico;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.ServicoRepositorio;
import com.autobots.automanager.repositorios.VendaRepositorio;

@RestController
public class ServicoControle {
	@Autowired
	private ServicoRepositorio servicoRepositorio;
	@Autowired
	private RepositorioEmpresa empresaRepositorio;
	@Autowired
	private VendaRepositorio vendaRepositorio;
	@Autowired
	private AdicionadorLinkServico adicionadorLinkServico;
	
	@GetMapping("/servicos")
    public List<Servico> obterServicos() {
		List<Servico> servico = servicoRepositorio.findAll();
        adicionadorLinkServico.adicionarLink(servico);
        return servico;
    }
	
	@GetMapping("/servico/{id}")
    public Servico obterServicoPorId(@PathVariable Long id) {
		Servico servico = servicoRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        adicionadorLinkServico.adicionarLink(servico);
        return servico;
	}
	
	@PutMapping("/servico/atualizar/{id}")
    public ResponseEntity<Servico> atualizarServico(@PathVariable Long id, @RequestBody Servico servico) {
		Servico servicoExistente = servicoRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
		servicoExistente.setNome(servico.getNome());
		servicoExistente.setValor(servico.getValor());
		servicoExistente.setDescricao(servico.getDescricao());
		
		servicoExistente = servicoRepositorio.save(servicoExistente);
        adicionadorLinkServico.adicionarLink(servicoExistente);
        return new ResponseEntity<>(servicoExistente, HttpStatus.OK);
    }
	
	@PostMapping("/servico/empresa/{idEmpresa}")
    public ResponseEntity<Empresa> cadastrarServicoEmpresa(@PathVariable Long idEmpresa, @RequestBody Servico servico) {
        Optional<Empresa> empresaOptional = empresaRepositorio.findById(idEmpresa);
        if (empresaOptional.isPresent()) {
            Empresa empresa = empresaOptional.get();

            servico = servicoRepositorio.save(servico); 
            adicionadorLinkServico.adicionarLink(servico);
            
            empresa.getServicos().add(servico); 

            empresa = empresaRepositorio.save(empresa);  

            return new ResponseEntity<>(empresa, HttpStatus.CREATED);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Servico não Encontrado.");
        }
    }
	
	@DeleteMapping("/servico/deletar/{id}")
    @Transactional
    public ResponseEntity<Void> deletarServico(@PathVariable Long id) {
        Optional<Servico> servicoOptional = servicoRepositorio.findById(id);
        if (servicoOptional.isPresent()) {
            Servico servico = servicoOptional.get();

            List<Empresa> empresas = empresaRepositorio.findByServicos(servico); 

            for (Empresa empresa : empresas) {
                empresa.getServicos().remove(servico);
                empresaRepositorio.save(empresa); 
            }
            
            List<Venda> vendas = vendaRepositorio.findByServicos(servico); 

            for (Venda venda : vendas) {
                venda.getServicos().remove(servico);
                vendaRepositorio.save(venda); 
            }
            
            servicoRepositorio.delete(servico);
            return ResponseEntity.noContent().build(); 
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Servico não encontrado.");
        }
    }
}
