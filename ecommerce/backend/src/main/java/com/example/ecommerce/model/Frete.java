package com.example.ecommerce.model;

import java.math.BigDecimal;

public class Frete {
    private String nome;
    private BigDecimal valor;
    private String prazo;

    public Frete(String nome, BigDecimal valor, String prazo) {
        this.nome = nome;
        this.valor = valor;
        this.prazo = prazo;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getPrazo() {
        return prazo;
    }

    
}
