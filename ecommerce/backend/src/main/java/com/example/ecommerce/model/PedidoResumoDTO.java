package com.example.ecommerce.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PedidoResumoDTO {
    private Long id;
    private String clienteNome;
    private String status;
    private LocalDateTime dataHora;
    private String numeroPedido;
    private BigDecimal totalGeral; // ✅ Adicionado

    // Getters e Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getClienteNome() {
        return clienteNome;
    }
    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }
    public void setNumeroPedido(String numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public BigDecimal getTotalGeral() {
        return totalGeral;
    }
    public void setTotalGeral(BigDecimal totalGeral) {
        this.totalGeral = totalGeral;
    }
}
