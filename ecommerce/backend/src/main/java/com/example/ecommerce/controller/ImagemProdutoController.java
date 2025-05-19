package com.example.ecommerce.controller;
import com.example.ecommerce.services.ImagemProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/imagens")

public class ImagemProdutoController {
    @Autowired
    private ImagemProdutoService imagemProdutoService;

    @GetMapping("/{produtoId}/{nomeArquivo}")
    public ResponseEntity<byte[]> obterImagem(
            @PathVariable String produtoId,
            @PathVariable String nomeArquivo) {

        try {
            byte[] imagem = imagemProdutoService.carregarImagem(produtoId, nomeArquivo);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); 
            
            return new ResponseEntity<>(imagem, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
