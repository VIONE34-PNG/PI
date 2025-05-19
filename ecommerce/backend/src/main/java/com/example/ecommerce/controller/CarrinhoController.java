package com.example.ecommerce.controller;


import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.CarrinhoRepository;
import com.example.ecommerce.repository.ProdutoRepository;
import com.example.ecommerce.services.CarrinhoService;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/carrinho")
public class CarrinhoController {

    @Autowired
    private CarrinhoService carrinhoService;

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @PostMapping("/adicionar")
    public ResponseEntity<?> adicionarProduto(@RequestBody Map<String, Object> request, HttpSession session) {
    try {
        Integer codigoProduto = (Integer) request.get("codigo");
        if (codigoProduto == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Código do produto é obrigatório"));
        }

        Produto produto = produtoRepository.findById(codigoProduto)
            .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        System.out.println("Sessão ID (ADICIONAR): " + session.getId());

        if (usuario != null) {
            Carrinho item = carrinhoService.adicionarProdutoAoCarrinho(usuario, produto);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "item", item
            ));
        } else {
            @SuppressWarnings("unchecked")
            List<Carrinho> carrinhoTemp = (List<Carrinho>) session.getAttribute("carrinhoTemporario");
            if (carrinhoTemp == null) {
                carrinhoTemp = new ArrayList<>();
                System.out.println("Criando nova lista para carrinho temporário");
            }

            Optional<Carrinho> existente = carrinhoTemp.stream()
                .filter(c -> c.getProduto().getCodigo().equals(produto.getCodigo()))
                .findFirst();

            if (existente.isPresent()) {
                existente.get().setQuantidade(existente.get().getQuantidade() + 1);
            } else {
                Carrinho item = new Carrinho(produto, 1);
                item.setId((int) (System.currentTimeMillis() % Integer.MAX_VALUE)); 
                carrinhoTemp.add(item);
            }

            session.setAttribute("carrinhoTemporario", carrinhoTemp);

            System.out.println("Produto adicionado ao carrinho temporário. Total de itens: " + carrinhoTemp.size());

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Item adicionado ao carrinho temporário"
            ));
        }
    } catch (Exception e) {
        System.err.println("ERRO: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.internalServerError().body(Map.of(
            "error", e.getMessage()
        ));
    }
}

@GetMapping("/listar")
public ResponseEntity<List<Carrinho>> listarCarrinho(HttpSession session) {
    Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

    if (usuarioLogado != null) {
        List<Carrinho> itens = carrinhoService.listarCarrinho(usuarioLogado);

        itens.forEach(item -> {
            Hibernate.initialize(item.getProduto()); 
            System.out.println("Carrinho - ID: " + item.getId() + " | Produto ID: " + item.getProduto().getCodigo());
        });

        return ResponseEntity.ok(itens);
    } else {
        @SuppressWarnings("unchecked")
        List<Carrinho> carrinhoTemp = (List<Carrinho>) session.getAttribute("carrinhoTemporario");

        if (carrinhoTemp == null || carrinhoTemp.isEmpty()) {
            System.out.println("Carrinho temporário está vazio ou nulo.");
            return ResponseEntity.ok(Collections.emptyList());
        }

        System.out.println("Carrinho temporário contém " + carrinhoTemp.size() + " itens.");

        carrinhoTemp.forEach(item ->
            System.out.println("Carrinho Temporário - ID: " + item.getId() + " | Produto ID: " + item.getProduto().getCodigo())
        );

        return ResponseEntity.ok(carrinhoTemp);
    }
}


@PutMapping("/atualizar/{id}")
   public ResponseEntity<?> atualizarQuantidade(@PathVariable int id, @RequestBody Map<String, Integer> dados, HttpSession session) {
    Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

    System.out.println("Tentando atualizar quantidade do item no carrinho. ID recebido: " + id);

    if (id <= 0) {
        System.out.println("Erro: ID inválido recebido.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID inválido.");
    }

    if (usuarioLogado != null) {
        Optional<Carrinho> carrinhoOptional = carrinhoRepository.findById(id);

        if (carrinhoOptional.isPresent()) {
            Carrinho carrinho = carrinhoOptional.get();
            carrinho.setQuantidade(dados.get("quantidade"));
            carrinhoRepository.save(carrinho);
            System.out.println("Quantidade atualizada no banco para ID: " + carrinho.getId());
            return ResponseEntity.ok("Quantidade atualizada no banco!");
        } else {
            System.out.println("Erro: Item do carrinho não encontrado no banco.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item do carrinho não encontrado.");
        }
    } else {
        @SuppressWarnings("unchecked")
        List<Carrinho> carrinhoTemporario = (List<Carrinho>) session.getAttribute("carrinhoTemporario");

        if (carrinhoTemporario == null || carrinhoTemporario.isEmpty()) {
            System.out.println("Nenhum produto no carrinho temporário.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nenhum produto no carrinho temporário.");
        }

        boolean atualizado = false;

        for (Carrinho carrinho : carrinhoTemporario) {
            if (carrinho.getId() == id) {
                Integer novaQtd = dados.get("quantidade");
                if (novaQtd != null && novaQtd > 0) {
                    carrinho.setQuantidade(novaQtd);  
                    atualizado = true;
                }
            }
        }

        if (atualizado) {
            session.setAttribute("carrinhoTemporario", carrinhoTemporario);
            System.out.println("Quantidade atualizada no carrinho temporário para ID: " + id);
            return ResponseEntity.ok("Quantidade atualizada no carrinho temporário!");
        } else {
            System.out.println("Erro: Produto não encontrado no carrinho temporário.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado no carrinho temporário.");
        }
    }
}


@DeleteMapping("/{id}")
public ResponseEntity<String> removerItem(@PathVariable int id, HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

    if (usuario != null) {
        try {
            carrinhoService.removerItem(id);
            System.out.println("Item removido do carrinho persistente. ID: " + id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.err.println("Erro ao remover item do carrinho persistente. ID: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao remover item do carrinho.");
        }
    } else {
        @SuppressWarnings("unchecked")
        List<Carrinho> carrinhoTemporario = (List<Carrinho>) session.getAttribute("carrinhoTemporario");

        if (carrinhoTemporario != null) {
            boolean removido = carrinhoTemporario.removeIf(item -> item.getId() == id);

            if (removido) {
                session.setAttribute("carrinhoTemporario", carrinhoTemporario);
                System.out.println("Item removido do carrinho temporário. ID: " + id);
                return ResponseEntity.noContent().build();
            } else {
                System.out.println("Item não encontrado no carrinho temporário. ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item não encontrado no carrinho temporário.");
            }
        } else {
            System.out.println("Carrinho temporário não encontrado na sessão.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Carrinho temporário não encontrado.");
        }
    }
}

@DeleteMapping("/limpar")
public ResponseEntity<Void> limparCarrinho(HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

    if (usuario != null) {
        carrinhoService.limparCarrinho(usuario);
        System.out.println("Carrinho do banco de dados foi limpo para o usuário: " + usuario.getId());
    } else {
        Object carrinhoTemporario = session.getAttribute("carrinhoTemporario");
        if (carrinhoTemporario != null && carrinhoTemporario instanceof List<?>) {
            session.removeAttribute("carrinhoTemporario");
            System.out.println("Carrinho temporário foi limpo.");
        } else {
            System.out.println("Nenhum carrinho temporário encontrado na sessão.");
        }
    }

    return ResponseEntity.noContent().build();
}

@PostMapping("/associar")
public ResponseEntity<?> associarCarrinhoAoUsuario(HttpSession session) {
    Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
    if (usuarioLogado == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não logado.");
    }

    try {
        List<Carrinho> carrinhoTemporario = (List<Carrinho>) session.getAttribute("carrinhoTemporario");
        if (carrinhoTemporario != null && !carrinhoTemporario.isEmpty()) {
            carrinhoService.associarCarrinhoAoUsuario(session, usuarioLogado.getId());
            return ResponseEntity.ok("Carrinho associado ao usuário com sucesso.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Carrinho temporário vazio.");
        }
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao associar carrinho: " + e.getMessage());
    }
}
}
