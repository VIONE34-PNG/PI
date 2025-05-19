package com.example.ecommerce.model;

import java.util.List;

public class OrderSummaryDTO {
    private List<ProdutoDTO> produtos;
    private String enderecoEntrega;
    private String formaPagamento;
    private double frete;
    private double totalGeral;

    // Getters e Setters
    public List<ProdutoDTO> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<ProdutoDTO> produtos) {
        this.produtos = produtos;
    }

    public String getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public void setEnderecoEntrega(String enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public double getFrete() {
        return frete;
    }

    public void setFrete(double frete) {
        this.frete = frete;
    }

    public double getTotalGeral() {
        return totalGeral;
    }

    public void setTotalGeral(double totalGeral) {
        this.totalGeral = totalGeral;
    }

    // Classe estática interna para detalhamento dos produtos no resumo do pedido
    public static class ProdutoDTO {
        private String nome;
        private double precoUnitario;
        private int quantidade;
        private double total;

        // Getters e Setters
        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public double getPrecoUnitario() {
            return precoUnitario;
        }

        public void setPrecoUnitario(double precoUnitario) {
            this.precoUnitario = precoUnitario;
        }

        public int getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(int quantidade) {
            this.quantidade = quantidade;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }
    }
}
