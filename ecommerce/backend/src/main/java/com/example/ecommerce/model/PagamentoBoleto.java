package com.example.ecommerce.model;

import java.time.LocalDate;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BOLETO")
public class PagamentoBoleto extends Pagamento {
    private String codigoBoleto;
    private LocalDate dataVencimento;
    public String getCodigoBoleto() {
        return codigoBoleto;
    }
    public void setCodigoBoleto(String codigoBoleto) {
        this.codigoBoleto = codigoBoleto;
    }
    public LocalDate getDataVencimento() {
        return dataVencimento;
    }
    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    
}