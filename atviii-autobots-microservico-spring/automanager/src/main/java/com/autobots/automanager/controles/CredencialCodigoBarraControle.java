package com.autobots.automanager.controles;

import java.util.Date;
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

import com.autobots.automanager.entitades.CredencialCodigoBarra;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.modelos.AdicionadorLinkCredencialCodigoBarra;
import com.autobots.automanager.repositorios.CredencialCodigoBarraRepositorio;
import com.autobots.automanager.repositorios.UsuarioRepositorio;

@RestController
public class CredencialCodigoBarraControle {
	@Autowired
	private CredencialCodigoBarraRepositorio repositorioCodigoBarra;
	@Autowired
	private AdicionadorLinkCredencialCodigoBarra adicionadorLinkCredencialCodigoBarra;
	@Autowired
    private CredencialCodigoBarraRepositorio credencialCodigoBarraRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
	
	
	@GetMapping("/credenciais/codigo-barra")
    public ResponseEntity<List<CredencialCodigoBarra>> obterTodasCredenciaisCodigoBarra() {
        List<CredencialCodigoBarra> credenciais = repositorioCodigoBarra.findAll();
        adicionadorLinkCredencialCodigoBarra.adicionarLink(credenciais);
        return ResponseEntity.ok(credenciais);
    }
	
	@GetMapping("/credenciais/codigo-barra/{id}")
    public ResponseEntity<CredencialCodigoBarra> obterCredencialCodigoBarraPorId(@PathVariable Long id) {
        CredencialCodigoBarra credencial = repositorioCodigoBarra.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Credencial de Código de Barras não encontrada."));
        adicionadorLinkCredencialCodigoBarra.adicionarLink(credencial);
        return ResponseEntity.ok(credencial);
    }
	
	@PostMapping("/usuarios/{idUsuario}/credenciais/codigo-barra")
    public ResponseEntity<CredencialCodigoBarra> criarCredencialCodigoBarra(@PathVariable Long idUsuario,
                                                                       @RequestBody CredencialCodigoBarra credencial) {

        Usuario usuario = usuarioRepositorio.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));

        credencial.setCriacao(new Date()); 
        credencial.setInativo(false); 
        CredencialCodigoBarra novaCredencial = credencialCodigoBarraRepositorio.save(credencial);

        usuario.getCredenciais().add(novaCredencial); 
        usuario = usuarioRepositorio.save(usuario); 
        adicionadorLinkCredencialCodigoBarra.adicionarLink(credencial);
        return new ResponseEntity<>(novaCredencial, HttpStatus.CREATED);
    }
	
	@PutMapping("/credenciais/codigo-barra/atualizar/{id}")
    public ResponseEntity<CredencialCodigoBarra> atualizarCredencialCodigoBarra(@PathVariable Long id,
                                                                       @RequestBody CredencialCodigoBarra credencialAtualizada) {

        CredencialCodigoBarra credencialExistente = credencialCodigoBarraRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Credencial de Código de Barras não encontrada."));

        credencialExistente.setCodigo(credencialAtualizada.getCodigo());
        credencialExistente.setUltimoAcesso(credencialAtualizada.getUltimoAcesso());
        credencialExistente.setInativo(credencialAtualizada.isInativo());

        CredencialCodigoBarra credencialAtualizadaSalva = credencialCodigoBarraRepositorio.save(credencialExistente);
        adicionadorLinkCredencialCodigoBarra.adicionarLink(credencialExistente);
        return new ResponseEntity<>(credencialAtualizadaSalva, HttpStatus.OK);
    }
	
	@DeleteMapping("/credenciais/codigo-barra/deletar/{id}")
	public ResponseEntity<Void> deletarCredencialCodigoBarra(@PathVariable Long id) {
	    CredencialCodigoBarra credencial = credencialCodigoBarraRepositorio.findById(id)
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Credencial de Código de Barras não encontrada."));

	    Usuario usuario = usuarioRepositorio.findByCredenciaisId(id).orElse(null); 

	    if (usuario != null) {
	        usuario.removerCredencial(credencial); 
	        usuarioRepositorio.save(usuario); 
	    }
        adicionadorLinkCredencialCodigoBarra.adicionarLink(credencial);
	    credencialCodigoBarraRepositorio.delete(credencial); 
	    return ResponseEntity.noContent().build();
	}
}