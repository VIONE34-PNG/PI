package com.example.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.ecommerce.model.Carrinho;
import com.example.ecommerce.model.Produto;
import com.example.ecommerce.model.Usuario;

@Repository
public interface CarrinhoRepository extends JpaRepository<Carrinho, Integer> {
    List<Carrinho> findByProduto(Produto produto);
    List<Carrinho> findByUsuario(Usuario usuario);
    Carrinho findByUsuarioAndProduto(Usuario usuario, Produto produto);
}


