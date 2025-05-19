package com.example.ecommerce.repository;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ecommerce.model.Pagamento;
import com.example.ecommerce.model.Pedido;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    Optional<Pagamento> findByPedido(Pedido pedido);
}