package com.example.ecommerce.services;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImagemProdutoService {

    @Value("${imagens.produto}")
    private String basePath; 

    public byte[] carregarImagem(String produtoId, String nomeArquivo) throws Exception {
        Path caminhoImagem = Paths.get(basePath, produtoId, nomeArquivo);

        if (!Files.exists(caminhoImagem)) {
            throw new Exception("Imagem não encontrada: " + caminhoImagem);
        }

        return Files.readAllBytes(caminhoImagem);
    }
}
