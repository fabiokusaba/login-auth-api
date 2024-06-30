package br.com.fabiokusaba.loginauthapi.dto;

//DTO (data transfer object) que vai conter as informações de login da nossa aplicação
//Vamos receber por parâmetro uma String email e uma String password
//Aqui vamos usar um 'record' que vem com os getters automáticos e é um tipo de classe que podemos utilizar para a
//criação dos nossos DTOs

public record LoginRequestDTO(String email, String password) {
}
