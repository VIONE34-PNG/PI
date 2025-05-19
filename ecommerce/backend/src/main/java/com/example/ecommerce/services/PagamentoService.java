package com.example.ecommerce.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ecommerce.model.Pagamento;
import com.example.ecommerce.model.PagamentoBoleto;
import com.example.ecommerce.model.PagamentoCartao;
import com.example.ecommerce.model.Pedido;
import com.example.ecommerce.model.StatusPagamento;
import com.example.ecommerce.repository.PagamentoRepository;
import com.example.ecommerce.repository.PedidoRepository;

@Service
public class PagamentoService {
     @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    public Pagamento criarPagamento(Pedido pedido, String tipoPagamento) {
        if (tipoPagamento.equals("cartao")) {
            PagamentoCartao pagamentoCartao = new PagamentoCartao();
            pagamentoCartao.setStatus(StatusPagamento.PENDENTE);
            return pagamentoRepository.save(pagamentoCartao);
        } else if (tipoPagamento.equals("boleto")) {
            PagamentoBoleto pagamentoBoleto = new PagamentoBoleto();
            pagamentoBoleto.setStatus(StatusPagamento.PENDENTE);
            return pagamentoRepository.save(pagamentoBoleto);
        }
        throw new IllegalArgumentException("Tipo de pagamento inválido.");
    }

    public Pagamento atualizarPagamento(Long pagamentoId, StatusPagamento statusPagamento) {
        Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado"));

        pagamento.setStatus(statusPagamento);
        return pagamentoRepository.save(pagamento);
    }
}

