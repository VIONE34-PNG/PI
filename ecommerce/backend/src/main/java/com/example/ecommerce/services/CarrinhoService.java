package com.example.ecommerce.services;
import com.example.ecommerce.model.Carrinho;
import com.example.ecommerce.model.Produto;
import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.repository.CarrinhoRepository;
import com.example.ecommerce.repository.ProdutoRepository;
import com.example.ecommerce.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CarrinhoService {

    @Autowired
    private CarrinhoRepository carrinhoRepository;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository; 

    public Carrinho adicionarProdutoAoCarrinho(Usuario usuario, Produto produtoEnviado) {
        if (produtoEnviado == null) {
            throw new IllegalArgumentException("Erro: O produto não pode ser nulo ao adicionar ao carrinho.");
        }
    
        Produto produto = produtoRepository.findById(produtoEnviado.getCodigo())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
    
        Carrinho existente = carrinhoRepository.findByUsuarioAndProduto(usuario, produto);
    
        if (existente != null) {
            existente.setQuantidade(existente.getQuantidade() + 1); 
            return carrinhoRepository.save(existente);
        } else {
            Carrinho novoCarrinho = new Carrinho();
            novoCarrinho.setUsuario(usuario);
            novoCarrinho.setProduto(produto);
            novoCarrinho.setPrecoUnitario(produto.getValorProduto());
            novoCarrinho.setQuantidade(1);
    
            Carrinho carrinhoSalvo = carrinhoRepository.save(novoCarrinho);
            System.out.println("Produto adicionado ao carrinho com ID: " + carrinhoSalvo.getId());
    
            return carrinhoSalvo;
        }
    }
    
    
    public List<Carrinho> listarCarrinho(Usuario usuario) {
        return carrinhoRepository.findByUsuario(usuario);
    }

    public Carrinho atualizarQuantidade(int id, int novaQuantidade) {
        Optional<Carrinho> carrinhoOptional = carrinhoRepository.findById(id);
        if (carrinhoOptional.isPresent()) {
            Carrinho carrinho = carrinhoOptional.get();
            carrinho.setQuantidade(novaQuantidade);
            return carrinhoRepository.save(carrinho);
        }
        return null;
    }

    public void removerItem(int id) {
        carrinhoRepository.deleteById(id);
    }

    public void limparCarrinho(Usuario usuario) {
        List<Carrinho> itensCarrinho = carrinhoRepository.findByUsuario(usuario);
        
        if (itensCarrinho != null && !itensCarrinho.isEmpty()) {
            carrinhoRepository.deleteAll(itensCarrinho);
            System.out.println("Carrinho de usuário " + usuario.getId() + " limpo com sucesso.");
        } else {
            System.out.println("Nenhum item encontrado no carrinho para o usuário " + usuario.getId());
        }
    }

    public List<Carrinho> buscarPorProduto(Produto produto) {
        return carrinhoRepository.findByProduto(produto);
    }

    public void associarCarrinhoAoUsuario(HttpSession session, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
    
        @SuppressWarnings("unchecked")
        List<Carrinho> carrinhoTemporario = (List<Carrinho>) session.getAttribute("carrinhoTemporario");
    
        System.out.println("Sessão ID na associação de carrinho: " + session.getId());
    
        if (carrinhoTemporario == null || carrinhoTemporario.isEmpty()) {
            System.out.println("Carrinho temporário está vazio ou não existe.");
        } else {
            System.out.println("Carrinho temporário encontrado! Produtos na sessão: " + carrinhoTemporario.size());
    
            for (Carrinho carrinho : carrinhoTemporario) {
                Produto produto = carrinho.getProduto();  
                Produto produtoEncontrado = produtoRepository.findById(produto.getCodigo())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
    
                Carrinho existente = carrinhoRepository.findByUsuarioAndProduto(usuario, produtoEncontrado);
                if (existente != null) {
                    existente.setQuantidade(existente.getQuantidade() + carrinho.getQuantidade());
                    carrinhoRepository.save(existente);
                    System.out.println("Quantidade do produto " + produtoEncontrado.getNome() + " foi atualizada.");
                } else {
                    Carrinho novo = new Carrinho();
                    novo.setUsuario(usuario);
                    novo.setProduto(produtoEncontrado);
                    novo.setPrecoUnitario(produtoEncontrado.getValorProduto());
                    novo.setQuantidade(carrinho.getQuantidade());
                    carrinhoRepository.save(novo);
                    System.out.println("Produto " + produtoEncontrado.getNome() + " adicionado ao carrinho do usuário.");
                }
            }
    
            session.removeAttribute("carrinhoTemporario");
            System.out.println("Carrinho temporário removido da sessão.");
        }
    }
    
    
}
