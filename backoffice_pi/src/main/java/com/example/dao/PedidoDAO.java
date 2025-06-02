package com.example.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.example.models.Pedido;

public class PedidoDAO {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ecommerce_pi";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "Sd.iago12";

    // Método para listar pedidos
    public List<Pedido> listarPedidos() {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = """
            SELECT pedido.id, usuarios.nome AS cliente_nome, pedido.status, pedido.totalGeral, pedido.numeroPedido
            FROM pedido
            JOIN usuarios ON pedido.usuario_id = usuarios.id
            ORDER BY pedido.id DESC
        """;

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                pedidos.add(new Pedido(
                    rs.getInt("id"), 
                    rs.getString("cliente_nome"), 
                    rs.getString("status"), 
                    rs.getString("numeroPedido"), // Corrigido o nome correto da coluna
                    rs.getDouble("totalGeral") // Corrigido o nome correto da coluna
                ));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar pedidos: " + e.getMessage());
        }

        return pedidos;
    }

    // Método para atualizar o status do pedido
    public boolean atualizarStatus(long id, String novoStatus) {
        // Verifica se o status fornecido é válido
        List<String> statusPermitidos = List.of(
            "aguardando pagamento", "pagamento rejeitado", "pagamento com sucesso",
            "aguardando retirada", "em transito", "entregue"
        );

        if (!statusPermitidos.contains(novoStatus.toLowerCase())) {
            System.out.println("Erro: Status inválido.");
            return false;
        }

        String sql = "UPDATE pedido SET status = ? WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, novoStatus);
            statement.setLong(2, id);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar status do pedido: " + e.getMessage());
            return false;
        }
    }
}