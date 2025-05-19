package com.example.ecommerce.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CARTAO")
public class PagamentoCartao extends Pagamento {
    private String numeroCartao;
    private String nomeImpresso;
    private String codigoVerificador;
    private String validade;
    private int parcelas;

    public String getNumeroCartao() {
        return numeroCartao;
    }
    public void setNumeroCartao(String numeroCartao) {
        this.numeroCartao = numeroCartao;
    }
    public String getNomeImpresso() {
        return nomeImpresso;
    }
    public void setNomeImpresso(String nomeImpresso) {
        this.nomeImpresso = nomeImpresso;
    }
    public String getCodigoVerificador() {
        return codigoVerificador;
    }
    public void setCodigoVerificador(String codigoVerificador) {
        this.codigoVerificador = codigoVerificador;
    }
    public String getValidade() {
        return validade;
    }
    public void setValidade(String validade) {
        this.validade = validade;
    }
    public int getParcelas() {
        return parcelas;
    }
    public void setParcelas(int parcelas) {
        this.parcelas = parcelas;
    }

}
