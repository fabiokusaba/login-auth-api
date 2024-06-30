package br.com.fabiokusaba.loginauthapi.dto;

//DTO que vai conter as informações para registro de um novo usuário

public record RegisterRequestDTO(String name, String email, String password) {
}
