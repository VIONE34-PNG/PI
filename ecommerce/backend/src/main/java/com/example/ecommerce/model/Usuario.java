package com.example.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String senha;
    private String cpf;

    @Temporal(TemporalType.DATE)
    private Date dataNascimento;

    @Enumerated(EnumType.STRING)
    private TipoUser tipo;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String genero;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_faturamento_id")
    private Endereco enderecoFaturamento;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Endereco> enderecosEntrega;

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public TipoUser getTipo() {
        return tipo;
    }

    public void setTipo(TipoUser tipo) {
        this.tipo = tipo;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Endereco getEnderecoFaturamento() {
        return enderecoFaturamento;
    }

    public void setEnderecoFaturamento(Endereco enderecoFaturamento) {
        this.enderecoFaturamento = enderecoFaturamento;
    }

    public List<Endereco> getEnderecosEntrega() {
        return enderecosEntrega;
    }

    public void setEnderecosEntrega(List<Endereco> enderecosEntrega) {
        this.enderecosEntrega = enderecosEntrega;
    }
}
