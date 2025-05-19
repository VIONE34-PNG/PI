package com.example.ecommerce.services;

import com.example.ecommerce.model.Endereco;
import com.example.ecommerce.model.Status;
import com.example.ecommerce.model.TipoUser;
import com.example.ecommerce.model.Usuario;
import com.example.ecommerce.repository.EnderecoRepository;
import com.example.ecommerce.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EnderecoRepository enderecoRepository;
    private final CarrinhoService carrinhoService;

    public UsuarioService(UsuarioRepository usuarioRepository, EnderecoRepository enderecoRepository, CarrinhoService carrinhoService) {
        this.usuarioRepository = usuarioRepository;
        this.enderecoRepository = enderecoRepository;
        this.carrinhoService = carrinhoService;
    }

    public Usuario cadastrar(Usuario usuario) throws Exception {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new Exception("Email já cadastrado.");
        }

        if (usuarioRepository.existsByCpf(usuario.getCpf())) {
            throw new Exception("CPF já cadastrado.");
        }

        usuario.setSenha(criptografarSenha(usuario.getSenha()));

        // Ajuste: só define o tipo como cliente se não veio nenhum tipo no JSON
        if (usuario.getTipo() == null) {
            usuario.setTipo(TipoUser.cliente);
        }

        usuario.setStatus(Status.ativo);

        if (usuario.getEnderecoFaturamento() != null) {
            Endereco enderecoFaturamento = usuario.getEnderecoFaturamento();
            enderecoFaturamento.setUsuario(usuario);
        }

        return usuarioRepository.save(usuario);
    }

    public Usuario atualizarDados(Long id, String nome, String genero, String dataNascimento) throws Exception {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new Exception("Usuário não encontrado."));

        usuario.setNome(nome);
        usuario.setGenero(genero);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            java.util.Date utilDate = sdf.parse(dataNascimento);
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            usuario.setDataNascimento(sqlDate);
        } catch (ParseException e) {
            throw new Exception("Erro ao converter data: " + e.getMessage());
        }

        return usuarioRepository.save(usuario);
    }

    public void alterarSenha(Long id, String senhaAtual, String novaSenha) throws Exception {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new Exception("Usuário não encontrado."));

        if (!criptografarSenha(senhaAtual).equals(usuario.getSenha())) {
            throw new Exception("Senha atual incorreta.");
        }

        usuario.setSenha(criptografarSenha(novaSenha));
        usuarioRepository.save(usuario);
    }

    public void adicionarEndereco(Long usuarioId, Endereco endereco) throws Exception {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new Exception("Usuário não encontrado."));
        endereco.setUsuario(usuario);
        enderecoRepository.save(endereco);
    }

    private String criptografarSenha(String senha) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(senha.getBytes());
        StringBuilder hexString = new StringBuilder();

        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }

        return hexString.toString();
    }

    public Usuario login(String email, String senha, HttpSession session) throws Exception {
        if (!usuarioRepository.existsByEmail(email)) {
            throw new Exception("Usuário não encontrado.");
        }

        Usuario usuario = usuarioRepository.findUsuarioByEmail(email)
                .orElseThrow(() -> new Exception("Erro ao buscar usuário no banco de dados."));

        if (!criptografarSenha(senha).equals(usuario.getSenha())) {
            throw new Exception("Senha inválida.");
        }

        carrinhoService.associarCarrinhoAoUsuario(session, usuario.getId());
        return usuario;
    }
}
