package com.autobots.automanager.controles;

import java.util.List;

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

import com.autobots.automanager.dto.EmpresaDTO;
import com.autobots.automanager.entitades.Empresa;
import com.autobots.automanager.modelos.AdicionadorLinkEmpresa;
import com.autobots.automanager.modelos.EmpresaSelecionador;
import com.autobots.automanager.repositorios.EnderecoRepositorio;
import com.autobots.automanager.repositorios.MercadoriaRepositorio;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.ServicoRepositorio;
import com.autobots.automanager.repositorios.TelefoneRepositorio;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import com.autobots.automanager.repositorios.VendaRepositorio;

@RestController
public class EmpresaControle {
	@Autowired
	private RepositorioEmpresa empresaRepositorio;
	@Autowired
	private EmpresaSelecionador selecionador;
	@Autowired
	private AdicionadorLinkEmpresa adicionadorLink;
	@Autowired
	private UsuarioRepositorio usuarioRepositorio;
	@Autowired
	private MercadoriaRepositorio mercadoriaRepositorio; 
	@Autowired
	public TelefoneRepositorio telefoneRepositorio;
	
	@Autowired
	public EnderecoRepositorio enderecoRepositorio;
	
	@Autowired
	public ServicoRepositorio servicoRepositorio;
	
	@Autowired
	public VendaRepositorio vendaRepositorio;
	
	
	@GetMapping("/empresa/{id}")
	public Empresa obterEmpresaPorId(@PathVariable Long id) {
		Empresa empresa = empresaRepositorio.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		adicionadorLink.adicionarLink(empresa);
		return empresa;
	}
	
	@GetMapping("/empresa")
	public List<Empresa> obterEmpresa() {
		List<Empresa> empresa = empresaRepositorio.findAll();
		adicionadorLink.adicionarLink(empresa);
		return empresa;
	}

	
	 @DeleteMapping("/empresa/excluir/{id}")
	    public ResponseEntity<?> excluirEmpresa(@PathVariable Long id) {
	        Empresa empresa = empresaRepositorio.findById(id)
	                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

	        empresaRepositorio.delete(empresa);

	        // Retorna apenas o status 200 OK 
	        return ResponseEntity.noContent().build();
	    }

	

	@PostMapping("/empresa/cadastro")
    public ResponseEntity<Empresa> criarEmpresa(@RequestBody EmpresaDTO empresaDTO) {
        Empresa novaEmpresa = new Empresa();
        novaEmpresa.setRazaoSocial(empresaDTO.getRazaoSocial());
        novaEmpresa.setNomeFantasia(empresaDTO.getNomeFantasia());
        novaEmpresa.setCadastro(empresaDTO.getCadastro());
        novaEmpresa.getTelefones().addAll(empresaDTO.getTelefones());
        novaEmpresa = empresaRepositorio.save(novaEmpresa);
        adicionadorLink.adicionarLink(novaEmpresa);
        return new ResponseEntity<>(novaEmpresa, HttpStatus.CREATED);
    }
	
	@PutMapping("/empresa/atualizar/{id}")
	public ResponseEntity<Empresa> atualizarEmpresa(@PathVariable Long id, @RequestBody EmpresaDTO empresaDTO) {
	    Empresa empresa = empresaRepositorio.findById(id)
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

	    empresa.setRazaoSocial(empresaDTO.getRazaoSocial());
	    empresa.setNomeFantasia(empresaDTO.getNomeFantasia());
	    empresa.setCadastro(empresaDTO.getCadastro());
	    // Atribuir outros campos da entidade Empresa com base no DTO

	    empresa = empresaRepositorio.save(empresa);
	    adicionadorLink.adicionarLink(empresa);
	    return new ResponseEntity<>(empresa, HttpStatus.OK);
	}
}