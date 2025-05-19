package com.example.ecommerce.repository;


import com.example.ecommerce.model.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    Optional<Usuario> findUsuarioByEmail(String email);

}
