package com.example.ecommerce.controller;

import com.example.ecommerce.model.OrderSummaryDTO;
import com.example.ecommerce.model.Pedido;
import com.example.ecommerce.model.PedidoResumoDTO;
import com.example.ecommerce.model.TipoUser;
import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.services.OrderService;

import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // === 1. Finalizar pedido ===
    @PostMapping("/finalize")
    public ResponseEntity<?> finalizeOrder(@RequestBody Pedido pedido, HttpSession session) {
        try {
            Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
            if (usuarioLogado == null) {
                return ResponseEntity.status(401).body("Usuário não autenticado.");
            }

            if (pedido.getFormaPagamento() == null || pedido.getFormaPagamento().isBlank()) {
                return ResponseEntity.badRequest().body("A forma de pagamento é obrigatória.");
            }

            pedido.setUsuario(usuarioLogado);

            System.out.println("Dados recebidos para finalização: " + pedido);

            Pedido savedOrder = orderService.finalizeOrder(pedido);
            return ResponseEntity.ok(savedOrder);
        } catch (IllegalArgumentException e) {
            System.out.println("Erro de validação: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro ao processar pedido: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro ao criar o pedido: " + e.getMessage());
        }
    }

    // === 2. Listar pedidos do usuário logado ===
    @GetMapping("/user")
    public ResponseEntity<?> listarPedidosUsuario(HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return ResponseEntity.status(401).body("Usuário não autenticado.");
        }

        List<Pedido> pedidos = orderService.listarPedidosDoUsuario(usuarioLogado);
        return ResponseEntity.ok(pedidos);
    }

    // === 3. Obter resumo do pedido ===
    @GetMapping("/summary")
    public ResponseEntity<?> getOrderSummary(@RequestParam Long orderId) {
        OrderSummaryDTO resumo = orderService.getOrderSummary(orderId);
        if (resumo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resumo);
    }

    // === 4. Obter pedido detalhado por ID ===
    @GetMapping("/{id}")
    public ResponseEntity<?> getPedidoDetalhado(@PathVariable Long id, HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return ResponseEntity.status(401).body("Usuário não autenticado.");
        }

        Pedido pedido = orderService.buscarPedidoPorId(id);
        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }

        // Permitir acesso apenas se for o dono do pedido ou estoquista
        if (!pedido.getUsuario().getId().equals(usuarioLogado.getId())
                && usuarioLogado.getTipo() != TipoUser.estoquista) {
            return ResponseEntity.status(403).body("Acesso negado ao pedido.");
        }

        return ResponseEntity.ok(pedido);
    }

    // === 5. Listar todos os pedidos (acesso estoquista) ===
    @GetMapping
    public ResponseEntity<?> listarTodosPedidos(HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null || usuarioLogado.getTipo() != TipoUser.estoquista) {
            return ResponseEntity.status(403).body("Acesso negado. Apenas estoquistas podem visualizar os pedidos.");
        }

        List<PedidoResumoDTO> pedidos = orderService.listarPedidosResumo();
        return ResponseEntity.ok(pedidos);
    }

    // === 6. Atualizar status do pedido ===
    @PutMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatusPedido(@PathVariable Long id, @RequestBody Map<String, String> body, HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null || usuarioLogado.getTipo() != TipoUser.estoquista) {
            return ResponseEntity.status(403).body("Acesso negado. Apenas estoquistas podem alterar o status.");
        }

        String novoStatus = body.get("novoStatus");
        if (novoStatus == null || novoStatus.isBlank()) {
            return ResponseEntity.badRequest().body("O novo status não pode estar vazio.");
        }

        try {
            orderService.atualizarStatus(id, novoStatus);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Removi método duplicado listarPedidosResumo que era redundante com listarTodosPedidos
    // Se quiser manter, apenas garanta que não haja duplicidade de endpoint ("/resumo" e "/")

}
