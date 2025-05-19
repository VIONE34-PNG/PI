package com.example.ecommerce.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.model.Pagamento;
import com.example.ecommerce.model.Pedido;
import com.example.ecommerce.model.StatusPagamento;
import com.example.ecommerce.repository.PedidoRepository;
import com.example.ecommerce.services.PagamentoService;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {
     @Autowired
    private PagamentoService pagamentoService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @PostMapping("/criar")
    public ResponseEntity<?> criarPagamento(@RequestBody Map<String, Object> dadosPagamento) {
        Long pedidoId = Long.valueOf(dadosPagamento.get("pedidoId").toString());
        String tipoPagamento = dadosPagamento.get("tipoPagamento").toString();

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        Pagamento pagamento = pagamentoService.criarPagamento(pedido, tipoPagamento);

        return ResponseEntity.ok(pagamento);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<?> atualizarPagamento(@PathVariable Long id, @RequestBody Map<String, String> status) {
        StatusPagamento novoStatus = StatusPagamento.valueOf(status.get("status").toUpperCase());
        Pagamento pagamento = pagamentoService.atualizarPagamento(id, novoStatus);
        return ResponseEntity.ok(pagamento);
    }
}
