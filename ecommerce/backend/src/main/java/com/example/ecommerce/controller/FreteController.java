package com.example.ecommerce.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.model.Frete;

@RestController
@RequestMapping("/api/frete")
public class FreteController {

    @GetMapping("/calcular/{cep}")
    public ResponseEntity<List<Frete>> calcularFrete(@PathVariable String cep) {
        List<Frete> opcoes = new ArrayList<>();

        int valorBase = Integer.parseInt(cep.substring(cep.length() - 1)) % 5 + 1;

        opcoes.add(new Frete("Entrega Normal", BigDecimal.valueOf(valorBase + 5), "5 a 7 dias úteis"));
        opcoes.add(new Frete("Entrega Rápida", BigDecimal.valueOf(valorBase + 15), "2 a 3 dias úteis"));
        opcoes.add(new Frete("Entrega Expressa", BigDecimal.valueOf(valorBase + 25), "1 dia útil"));

        return ResponseEntity.ok(opcoes);
    }
}