package com.example.ecommerce.controller;

import com.example.ecommerce.model.Carrinho;
import com.example.ecommerce.model.Endereco;
import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.services.CarrinhoService;
import com.example.ecommerce.services.UsuarioService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;
    private final CarrinhoService carrinhoService;

    public UsuarioController(UsuarioService service, CarrinhoService carrinhoService) {
        this.service = service;
        this.carrinhoService = carrinhoService;
    }

   @PostMapping("/cadastro")
   public ResponseEntity<?> cadastrar(@RequestBody Usuario usuario, HttpSession session) {
    try {
        // 1. Cadastrar o usuário
        Usuario novoUsuario = service.cadastrar(usuario);

        @SuppressWarnings("unchecked")
        List<Carrinho> carrinhoTemporario = (List<Carrinho>) session.getAttribute("carrinhoTemporario");

        if (carrinhoTemporario != null && !carrinhoTemporario.isEmpty()) {
            carrinhoService.associarCarrinhoAoUsuario(session, novoUsuario.getId());
            session.removeAttribute("carrinhoTemporario");
            System.out.println("Carrinho temporário transferido para o usuário com ID: " + novoUsuario.getId());

            return ResponseEntity.ok(Map.of("redirect", "carrinho"));
        }

        return ResponseEntity.ok(Map.of("redirect", "home"));

    } catch (Exception e) {
        
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
    @PostMapping("/login")
    public ResponseEntity<?> login(HttpSession session, @RequestBody Map<String, String> dadosLogin) {
    try {
        String email = dadosLogin.get("email");
        String senha = dadosLogin.get("senha");

        Usuario usuario = service.login(email, senha, session);
        session.setAttribute("usuarioLogado", usuario);
        carrinhoService.associarCarrinhoAoUsuario(session, usuario.getId());
        return ResponseEntity.ok("Bem vindo(a) " + usuario.getNome()); 
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
}

  @GetMapping("/sessao")
   public ResponseEntity<?> verificarSessao(HttpSession session) {
    Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
    if (usuarioLogado != null) {
        return ResponseEntity.ok(usuarioLogado); 
    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não logado.");
    }
}

@PutMapping("/atualizar")
public ResponseEntity<?> atualizarDados(HttpSession session, @RequestBody Map<String, String> novosDados) {
    Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
    if (usuario == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não logado.");

    try {
        String nome = novosDados.get("nome");
        String dataNascimento = novosDados.get("dataNascimento"); 
        String genero = novosDados.get("genero");

        Usuario usuarioAtualizado = service.atualizarDados(usuario.getId(), nome, genero, dataNascimento);

        return ResponseEntity.ok(usuarioAtualizado);
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
    }
}


@PutMapping("/alterarSenha")
public ResponseEntity<?> alterarSenha(HttpSession session, @RequestBody Map<String, String> senhas) {
    Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
    if (usuario == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não logado.");

    try {
        String senhaAtual = senhas.get("senhaAtual");
        String novaSenha = senhas.get("novaSenha");

        service.alterarSenha(usuario.getId(), senhaAtual, novaSenha);
        return ResponseEntity.ok("Senha alterada com sucesso.");
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

@PostMapping("/enderecos")
public ResponseEntity<?> adicionarEndereco(HttpSession session, @RequestBody Endereco endereco) {
    Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
    if (usuario == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não logado.");

    try {
        service.adicionarEndereco(usuario.getId(), endereco);
        return ResponseEntity.ok("Endereço adicionado com sucesso.");
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
    @PostMapping("/logout")
public ResponseEntity<?> logout(HttpSession session) {
    try {
        session.invalidate();  // Invalida a sessão do usuário
        return ResponseEntity.ok("Logout realizado com sucesso.");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao realizar o logout.");
    }
}


}
