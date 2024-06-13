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
import com.autobots.automanager.entitades.CredencialUsuarioSenha;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.modelos.AdicionadorLinkCredencialUsuarioSenha;
import com.autobots.automanager.repositorios.CredencialUsuarioSenhaRepositorio;
import com.autobots.automanager.repositorios.UsuarioRepositorio;

@RestController
public class CredencialUsuarioSenhaControle {
	@Autowired
	private CredencialUsuarioSenhaRepositorio repositorioUsuarioSenha;
	@Autowired
    private UsuarioRepositorio usuarioRepositorio;
	@Autowired
	private AdicionadorLinkCredencialUsuarioSenha adicionadorLinkCredencialUsuarioSenha;
	
	@GetMapping("/credenciais/usuario-senha")
    public ResponseEntity<List<CredencialUsuarioSenha>> obterTodasCredenciaisUsuarioSenha() {
        List<CredencialUsuarioSenha> credenciais = repositorioUsuarioSenha.findAll();
        adicionadorLinkCredencialUsuarioSenha.adicionarLink(credenciais);
        return ResponseEntity.ok(credenciais);
    }
	
	@GetMapping("/credenciais/usuario-senha/{id}")
    public ResponseEntity<CredencialUsuarioSenha> obterCredencialUsuarioSenhaPorId(@PathVariable Long id) {
        CredencialUsuarioSenha credencial = repositorioUsuarioSenha.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Credencial de Código de Barras não encontrada."));
        adicionadorLinkCredencialUsuarioSenha.adicionarLink(credencial);
        return ResponseEntity.ok(credencial);
    }
	
	@PostMapping("/usuarios/{idUsuario}/credenciais/usuario-senha")
    public ResponseEntity<CredencialUsuarioSenha> criarCredencialUsuarioSenha(@PathVariable Long idUsuario,
                                                                       @RequestBody CredencialUsuarioSenha credencial) {

        Usuario usuario = usuarioRepositorio.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));

        // Atribuir data de criação e estado para a nova credencial
        credencial.setCriacao(new Date()); 
        credencial.setInativo(false); 

        // Salvar a nova credencial no repositório
        CredencialUsuarioSenha novaCredencial = repositorioUsuarioSenha.save(credencial);

        // Adicionar a nova credencial ao usuário
        usuario.getCredenciais().add(novaCredencial);

        // Salvar o usuário atualizado no repositório
        usuario = usuarioRepositorio.save(usuario);

        adicionadorLinkCredencialUsuarioSenha.adicionarLink(credencial);
        // Retornar a nova credencial com status CREATED
        return new ResponseEntity<>(novaCredencial, HttpStatus.CREATED);
    }
	
	@PutMapping("/credenciais/usuario-senha/atualizar/{id}")
    public ResponseEntity<CredencialUsuarioSenha> atualizarCredencialUsuarioSenha(@PathVariable Long id,
                                                                       @RequestBody CredencialUsuarioSenha credencialAtualizada) {

        CredencialUsuarioSenha credencialExistente = repositorioUsuarioSenha.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Credencial de Usuario e Senha não encontrada."));

        credencialExistente.setNomeUsuario(credencialAtualizada.getNomeUsuario());
        credencialExistente.setSenha(credencialAtualizada.getSenha());
        credencialExistente.setUltimoAcesso(credencialAtualizada.getUltimoAcesso());
        credencialExistente.setInativo(credencialAtualizada.isInativo());

        CredencialUsuarioSenha credencialAtualizadaSalva = repositorioUsuarioSenha.save(credencialExistente);
        adicionadorLinkCredencialUsuarioSenha.adicionarLink(credencialExistente);

        return new ResponseEntity<>(credencialAtualizadaSalva, HttpStatus.OK);
    }
	
	@DeleteMapping("/credenciais/usuario-senha/deletar/{id}")
	public ResponseEntity<Void> deletarCredencialUsuarioSenha(@PathVariable Long id) {
	    CredencialUsuarioSenha credencial = repositorioUsuarioSenha.findById(id)
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Credencial de Usuario e senha não encontrada."));

	    Usuario usuario = usuarioRepositorio.findByCredenciaisId(id).orElse(null); 

	    if (usuario != null) {
	        usuario.removerCredencial(credencial); 
	        usuarioRepositorio.save(usuario); 
	    }

	    repositorioUsuarioSenha.delete(credencial); 
	    return ResponseEntity.noContent().build();
	}
}
