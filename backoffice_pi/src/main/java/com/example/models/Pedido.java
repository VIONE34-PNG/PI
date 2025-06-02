package com.example.models;

public class Pedido {
    private int id;
    private String clienteNome;
    private String status;
    private String numeroPedido;
    private double totalGeral;

    public Pedido(int id, String clienteNome, String status, String numeroPedido, double total) {
        this.id = id;
        this.clienteNome = clienteNome;
        this.status = status;
        this.numeroPedido = numeroPedido;
        this.totalGeral = totalGeral;
    }

    public int getId() { return id; }
    public String getClienteNome() { return clienteNome; }
    public String getStatus() { return status; }
    public String getNumeroPedido() { return numeroPedido; }
    public double getTotal() { return totalGeral; }

    public void setStatus(String status) { this.status = status; }
    public void setNumeroPedido(String numeroPedido) { this.numeroPedido = numeroPedido; }
    public void setTotal(double total) { this.totalGeral = totalGeral; }
}