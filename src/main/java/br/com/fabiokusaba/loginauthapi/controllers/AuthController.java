package br.com.fabiokusaba.loginauthapi.controllers;

//Controller que vai ter os endpoints de login e de cadastro de usuário
//Para isso precisamos colocar a anotação '@RestController' e '@RequestMapping' para mapear o endpoint que esse
//controller fica ouvindo, no nosso caso ele vai ouvir especificamente o endpoint de "/auth"

import br.com.fabiokusaba.loginauthapi.domain.user.User;
import br.com.fabiokusaba.loginauthapi.dto.LoginRequestDTO;
import br.com.fabiokusaba.loginauthapi.dto.RegisterRequestDTO;
import br.com.fabiokusaba.loginauthapi.dto.ResponseDTO;
import br.com.fabiokusaba.loginauthapi.infra.security.TokenService;
import br.com.fabiokusaba.loginauthapi.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    //Aqui dentro a gente vai declarar algumas dependências desse nosso controller que vai ser o nosso 'UserRepository',
    //'PasswordEncoder' e por fim o nosso 'TokenService'
    //Vamos colocar a anotação '@RequiredArgsConstructor' para que o Lombok gere automaticamente o construtor dessa
    //classe contendo como parâmetro o repository, encoder e o token service e aí o Spring consegue fazer a injeção de
    //dependência corretamente
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    //O primeiro método que vamos declarar aqui vai ser um método público que vai nos retornar um 'ResponseEntity' cujo
    //nome vai ser 'login'
    //Para fazer o login do usuário a gente vai receber no body via '@RequestBody' as informações de login que é o email
    //e a senha, precisamos colocar o mapeamento '@PostMapping' com o nosso endpoint "/login"
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequestDTO body) {

        //Primeiro vamos tentar encontrar o usuário, vamos verificar se existe um usuário com esse email e caso eu não
        //encontre vou lançar uma exceção
        User user = this.userRepository.findByEmail(body.email()).orElseThrow(() -> new RuntimeException("User not found"));

        //Se encontrarmos o usuário vamos salvar na nossa variável 'user' do tipo 'User' e vamos verificar se a senha
        //desse usuário é igual a senha que recebi por parâmetro
        if (passwordEncoder.matches(body.password(), user.getPassword())) {

            //Se as senhas forem iguais, ou seja, deu match, vou criar um token passando o nosso usuário
            String token = this.tokenService.generateToken(user);

            //Então vou retornar um 'ResponseEntity' "ok" passando no body as informações que o nosso Frontend precisa
            //que são o token e o name
            return ResponseEntity.ok(new ResponseDTO(user.getName(), token));
        }

        //Caso as senhas não sejam iguais, ou seja, não derem match, retornamos um 'ResponseEntity' "badRequest"
        return ResponseEntity.badRequest().build();
    }

    //Agora podemos partir para a criação do endpoint de register e como parâmetro vamos receber o nome, email e senha
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequestDTO body) {

        //E aqui vamos declarar um novo usuário, mas antes podemos fazer uma verificação para ver se já existe um
        //usuário cadastrado com o email que estamos recebendo da requisição
        Optional<User> user = this.userRepository.findByEmail(body.email());

        //Para isso podemos verificar se o 'user.isEmpty', ou seja, se o nosso usuário está vazio e dessa forma podemos
        //fazer o registro desse usuário
        if (user.isEmpty()) {

            //Declarando o nosso novo usuário
            User newUser = new User();

            //Setando as propriedades desse usuário que estamos criando
            newUser.setPassword(passwordEncoder.encode(body.password())); //Salvando a senha de forma criptografada
            newUser.setEmail(body.email()); //Salvando o email que recebemos no body
            newUser.setName(body.name()); //Salvando o nome que recebemos no body

            //Criado o nosso usuário vamos chamar o repository para salvar ele no banco de dados
            this.userRepository.save(newUser);

            //Depois que salvamos esse usuário no banco de dados podemos fazer a geração do token
            String token = this.tokenService.generateToken(newUser);

            //E assim que gerarmos o token vamos retornar um 'ResponseEntity' "ok" com o nosso 'ResponseDTO'
            return ResponseEntity.ok(new ResponseDTO(newUser.getName(), token));
        }

        //Caso ocorra algum erro no registro retornamos um 'ResponseEntity' "badRequest"
        return ResponseEntity.badRequest().build();
    }
}
