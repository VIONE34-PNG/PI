package com.example.ecommerce.services;

import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.PedidoRepository;
import com.example.ecommerce.repository.ProdutoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    // Busca pedido pelo ID e retorna o pedido completo
    public Pedido buscarPedidoPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado para o ID: " + id));
    }

    // Retorna resumo do pedido (DTO) para o front, pode ajustar conforme necessário
    public OrderSummaryDTO getOrderSummary(Long orderId) {
        Optional<Pedido> pedidoOptional = pedidoRepository.findById(orderId);

        if (pedidoOptional.isPresent()) {
            Pedido pedido = pedidoOptional.get();

            OrderSummaryDTO summaryDTO = new OrderSummaryDTO();
            summaryDTO.setProdutos(pedido.getItens().stream().map(itemPedido -> {
                OrderSummaryDTO.ProdutoDTO produtoDTO = new OrderSummaryDTO.ProdutoDTO();
                produtoDTO.setNome(itemPedido.getProduto().getNome());
                produtoDTO.setPrecoUnitario(itemPedido.getPrecoUnitario().doubleValue());
                produtoDTO.setTotal(itemPedido.getPrecoUnitario().doubleValue() * itemPedido.getQuantidade());
                return produtoDTO;
            }).toList());

            summaryDTO.setEnderecoEntrega(pedido.getEnderecoEntrega());
            summaryDTO.setFormaPagamento(pedido.getFormaPagamento());
            summaryDTO.setFrete(pedido.getFrete());
            summaryDTO.setTotalGeral(pedido.getTotalGeral());

            return summaryDTO;
        } else {
            throw new RuntimeException("Pedido não encontrado para o ID: " + orderId);
        }
    }

    // Finaliza o pedido, calcula total e salva no banco
    public Pedido finalizeOrder(Pedido pedido) {
        pedido.setStatus("aguardando pagamento");
        pedido.setDataHora(LocalDateTime.now());
        pedido.setNumeroPedido("PED-" + System.currentTimeMillis());

        if (pedido.getPagamento() == null) {
            throw new IllegalArgumentException("Pagamento não pode ser nulo.");
        }

        double total = 0.0;

        if (pedido.getItens() != null) {
            for (ItemPedido item : pedido.getItens()) {
                Integer produtoId = item.getProduto().getCodigo();
                Produto produto = produtoRepository.findById(produtoId)
                        .orElseThrow(() -> new IllegalArgumentException("Produto com código " + produtoId + " não encontrado."));

                item.setProduto(produto);
                item.setPedido(pedido);

                if (item.getPrecoUnitario() == null) {
                    item.setPrecoUnitario(produto.getValorProduto());
                }

                total += item.getPrecoUnitario().doubleValue() * item.getQuantidade();
            }
        }

        total += pedido.getFrete();

        pedido.setTotalGeral(total);

        return pedidoRepository.save(pedido);
    }

    // Atualiza status do pedido
    public void atualizarStatus(Long id, String novoStatus) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado com ID: " + id));

        pedido.setStatus(novoStatus);
        pedidoRepository.save(pedido);
    }

    public List<PedidoResumoDTO> listarPedidosResumo() {
        List<Pedido> pedidos = pedidoRepository.findAll();

        return pedidos.stream().map(pedido -> {
            PedidoResumoDTO resumo = new PedidoResumoDTO();
            resumo.setId(pedido.getId());
            resumo.setClienteNome(pedido.getUsuario().getNome());
            resumo.setStatus(pedido.getStatus());
            resumo.setDataHora(pedido.getDataHora());
            resumo.setNumeroPedido(pedido.getNumeroPedido());

            // Convertendo double para BigDecimal
            resumo.setTotalGeral(BigDecimal.valueOf(pedido.getTotalGeral()));

            return resumo;
        }).collect(Collectors.toList());
    }

    // Lista pedidos de um usuário específico
    public List<Pedido> listarPedidosDoUsuario(Usuario usuario) {
        return pedidoRepository.findByUsuario(usuario);
    }
}
