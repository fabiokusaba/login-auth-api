package br.com.fabiokusaba.loginauthapi.dto;

//DTO que vai conter as informações de resposta para o nosso Frontend com os valores que ele precisa que no nosso caso é
//o nome e o token

public record ResponseDTO(String name, String token) {
}
