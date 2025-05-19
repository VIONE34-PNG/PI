package com.example.ecommerce.controller;

import com.example.ecommerce.model.Produto;
import com.example.ecommerce.services.ProdutoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estoque")
public class EstoqueController {

    private final ProdutoService produtoService;

    public EstoqueController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public ResponseEntity<List<Produto>> listarProdutos() {
        List<Produto> produtos = produtoService.listarProdutos();
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Produto> buscarPorCodigo(@PathVariable int codigo) {
        Produto produto = produtoService.buscarPorCodigo(codigo);
        if (produto != null) {
            return ResponseEntity.ok(produto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Produto> adicionarProduto(@RequestBody Produto produto) {
        try {
            Produto novoProduto = produtoService.adicionarProduto(produto);
            return ResponseEntity.ok(novoProduto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Produto> atualizarProduto(@PathVariable int codigo, @RequestBody Produto produtoAtualizado) {
        Produto produto = produtoService.atualizarProduto(codigo, produtoAtualizado);
        if (produto != null) {
            return ResponseEntity.ok(produto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> excluirProduto(@PathVariable int codigo) {
        boolean excluido = produtoService.excluirProduto(codigo);
        if (excluido) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
