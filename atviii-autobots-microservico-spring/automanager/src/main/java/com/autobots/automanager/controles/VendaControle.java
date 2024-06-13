package com.autobots.automanager.controles;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

import com.autobots.automanager.dto.VendaDTO;
import com.autobots.automanager.entitades.Empresa;
import com.autobots.automanager.entitades.Mercadoria;
import com.autobots.automanager.entitades.Servico;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.entitades.Veiculo;
import com.autobots.automanager.entitades.Venda;
import com.autobots.automanager.modelos.AdicionadorLinkVenda;
import com.autobots.automanager.repositorios.MercadoriaRepositorio;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.ServicoRepositorio;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import com.autobots.automanager.repositorios.VeiculoRepositorio;
import com.autobots.automanager.repositorios.VendaRepositorio;

@RestController
public class VendaControle {
	@Autowired
	private VendaRepositorio vendaRepositorio;
	@Autowired
	private AdicionadorLinkVenda adicionadorLinkVenda;
	@Autowired
	private UsuarioRepositorio usuarioRepositorio;
	@Autowired
	private RepositorioEmpresa empresaRepositorio;
	@Autowired
	private VeiculoRepositorio veiculoRepositorio;
	@Autowired
	private MercadoriaRepositorio mercadoriaRepositorio;
	@Autowired
	private ServicoRepositorio servicoRepositorio;
	
	@GetMapping("/vendas")
    public List<Venda> obterVendas() {
		List<Venda> venda = vendaRepositorio.findAll();
        adicionadorLinkVenda.adicionarLink(venda);
        return vendaRepositorio.findAll();
    }
	
	@GetMapping("/venda/{id}")
    public Venda obterVendaPorId(@PathVariable Long id) {
		Venda venda = vendaRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        adicionadorLinkVenda.adicionarLink(venda);
        return venda;
    }
	
	@PutMapping("/venda/atualizar/{id}")
	public ResponseEntity<Venda> atualizarVenda(@PathVariable Long id, @RequestBody VendaDTO vendaDTO) {
	    Venda vendaExistente = vendaRepositorio.findById(id)
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venda não encontrada."));

	    // Atualiza os atributos da venda
	    vendaExistente.setIdentificacao(vendaDTO.getIdentificacao());

	    // Atualiza o Cliente (se o ID do Cliente for fornecido)
	    if (vendaDTO.getIdCliente() != null) {
	        Usuario cliente = usuarioRepositorio.findById(vendaDTO.getIdCliente())
	                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado."));
	        vendaExistente.setCliente(cliente);
	    }

	    // Atualiza o Funcionário (se o ID do Funcionário for fornecido)
	    if (vendaDTO.getIdFuncionario() != null) {
	        Usuario funcionario = usuarioRepositorio.findById(vendaDTO.getIdFuncionario())
	                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado."));
	        vendaExistente.setFuncionario(funcionario);
	    }

	    // Atualiza o Veículo (se o ID do Veículo for fornecido)
	    if (vendaDTO.getIdVeiculo() != null) {
	        Veiculo veiculo = veiculoRepositorio.findById(vendaDTO.getIdVeiculo())
	                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veículo não encontrado."));
	        vendaExistente.setVeiculo(veiculo);
	    }

	    // Atualiza as Mercadorias (se houver IDs de Mercadoria no DTO)
	    if (!vendaDTO.getMercadorias().isEmpty()) { 
	        Set<Mercadoria> mercadorias = vendaDTO.getMercadorias().stream()
	                .map(mercadoriaId -> mercadoriaRepositorio.findById(mercadoriaId)
	                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mercadoria não encontrada.")))
	                .collect(Collectors.toSet());
	        vendaExistente.setMercadorias(mercadorias);
	    }

	    // Atualiza os Serviços (se houver IDs de Servico no DTO)
	    if (!vendaDTO.getServicos().isEmpty()) { 
	        Set<Servico> servicos = vendaDTO.getServicos().stream()
	                .map(servicoId -> servicoRepositorio.findById(servicoId)
	                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado.")))
	                .collect(Collectors.toSet());
	        vendaExistente.setServicos(servicos);
	    }

	    // Salva a venda atualizada
	    Venda vendaAtualizada = vendaRepositorio.save(vendaExistente);
	    adicionadorLinkVenda.adicionarLink(vendaAtualizada);
	    return new ResponseEntity<>(vendaAtualizada, HttpStatus.OK);
	}

	@Transactional
	@DeleteMapping("/venda/deletar/{id}") 
	public ResponseEntity<Void> deletarVenda(@PathVariable Long id) {
	    Optional<Venda> vendaOptional = vendaRepositorio.findById(id);
	    if (vendaOptional.isPresent()) {
	        Venda venda = vendaOptional.get();
	        
            List<Empresa> empresas = empresaRepositorio.findByVendas(venda); 

            for (Empresa empresa : empresas) {
                empresa.getVendas().remove(venda);
                empresaRepositorio.save(empresa); 
            }
            
            List<Usuario> usuarios = usuarioRepositorio.findByVendas(venda);
            
            for (Usuario usuario : usuarios) {
            	usuario.getVendas().remove(venda);
            	usuarioRepositorio.save(usuario);
            }
            
            List<Veiculo> veiculos = veiculoRepositorio.findByVendas(venda);
	        for (Veiculo veiculo: veiculos) {
	        	veiculo.getVendas().remove(venda);
	        	veiculoRepositorio.save(veiculo);
	        }
            
	        venda.setCliente(null);
	        venda.setFuncionario(null);
	        venda.setVeiculo(null);
	        venda.setMercadorias(new HashSet<>()); 
	        venda.setServicos(new HashSet<>()); 

	        vendaRepositorio.delete(venda); 
	        return ResponseEntity.noContent().build();
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}
	
	@PostMapping("/venda/cadastrar")
    public ResponseEntity<Venda> criarEmpresa(@RequestBody VendaDTO vendaDTO) {
        Venda novaVenda = new Venda();
        novaVenda.setIdentificacao(vendaDTO.getIdentificacao());

        // Adiciona o cliente (se informado)
        if (vendaDTO.getIdCliente() != null) {
            Usuario cliente = usuarioRepositorio.findById(vendaDTO.getIdCliente())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
            novaVenda.setCliente(cliente);
        }

        // Adiciona o funcionário (se informado)
        if (vendaDTO.getIdFuncionario() != null) {
            Usuario funcionario = usuarioRepositorio.findById(vendaDTO.getIdFuncionario())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado"));
            novaVenda.setFuncionario(funcionario);
        }

        // Adiciona o veículo (se informado)
        if (vendaDTO.getIdVeiculo() != null) {
            Veiculo veiculo = veiculoRepositorio.findById(vendaDTO.getIdVeiculo())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veículo não encontrado"));
            novaVenda.setVeiculo(veiculo);
        }

        // Adiciona as mercadorias existentes (usando os IDs do DTO)
        if (!vendaDTO.getMercadorias().isEmpty()) {
            Set<Mercadoria> mercadorias = vendaDTO.getMercadorias().stream()
                    .map(mercadoriaId -> mercadoriaRepositorio.findById(mercadoriaId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mercadoria não encontrada")))
                    .collect(Collectors.toSet());
            novaVenda.setMercadorias(mercadorias);
        }

        // Adiciona os serviços existentes (usando os IDs do DTO)
        if (!vendaDTO.getServicos().isEmpty()) {
            Set<Servico> servicos = vendaDTO.getServicos().stream()
                    .map(servicoId -> servicoRepositorio.findById(servicoId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado")))
                    .collect(Collectors.toSet());
            novaVenda.setServicos(servicos);
        }

        novaVenda.setCadastro(new Date());
        novaVenda = vendaRepositorio.save(novaVenda);
        adicionadorLinkVenda.adicionarLink(novaVenda);
        return new ResponseEntity<>(novaVenda, HttpStatus.CREATED);
    }
	
	
}