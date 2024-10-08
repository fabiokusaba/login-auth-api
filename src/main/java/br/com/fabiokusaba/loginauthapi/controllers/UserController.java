package br.com.fabiokusaba.loginauthapi.controllers;

//Endpoint para testar a nossa aplicação

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping
    public ResponseEntity<String> getUser() {

        return ResponseEntity.ok("sucesso em buscar user!");
    }
}
