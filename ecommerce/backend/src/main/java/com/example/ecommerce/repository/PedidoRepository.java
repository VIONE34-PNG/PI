package com.example.ecommerce.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.ecommerce.model.Pedido;
import com.example.ecommerce.model.Usuario;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuario(Usuario usuario);

    // Retorna todos os pedidos ordenados pela data/hora decrescente (mais recentes primeiro)
    List<Pedido> findAllByOrderByDataHoraDesc();
}
