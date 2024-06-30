package br.com.fabiokusaba.loginauthapi.infra.security;

import br.com.fabiokusaba.loginauthapi.domain.user.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

//Responsável pela lógica de validação e geração dos tokens

@Service
public class TokenService {

    //Nós vamos salvar a nossa chave privada no 'application.properties' para isso vamos criar uma variável privada e
    //usar a anotação '@Value' para indicar que estamos recuperando esse valor lá do nosso 'application.properties'
    @Value("${api.security.token.secret}")
    private String secret;

    //Geração do token para quando o usuário estiver fazendo o login na nossa aplicação
    public String generateToken(User user) {

        //Como pode ter um erro na hora de criar o nosso token vamos abrir um bloco try-catch
        try {

            //Aqui dentro do bloco try a primeira coisa que precisamos fazer é definir o algorítmo de geração do token
            //Quando definimos o algorítmo de geração do token nós precisamos passar uma secret key que vai ser a chave
            //privada que iremos utilizar na nossa criptografia
            //Os algorítmos de criptografia pegam uma informação e fazem um hash dessa informação, criptografam essa
            //informação usando uma chave privada e só quem tem a chave privada consegue descriptografar depois e obter
            //a informação que está ali hasheada
            //O nosso servidor vai ter uma chave privada que vamos utilizar para criptografar e descriptografar os dados
            //dessa forma a gente consegue ter certeza que quem emitiu esse token foi o nosso servidor
            //Essa informação, chave privada, deve ser guardada de uma forma muito segura usando variáveis de ambiente
            Algorithm algorithm = Algorithm.HMAC256(secret);

            //O próximo passo é fazer a geração do nosso token através do método 'JWT.create' onde vamos passar algumas
            //informações
            String token = JWT.create()
                    //Quem está emitindo o token
                    .withIssuer("login-auth-api")
                    //Quem está sendo o sujeito que está ganhando esse token, estamos salvando o email no token
                    .withSubject(user.getEmail())
                    //Tempo de expiração do nosso token
                    .withExpiresAt(this.generateExpirationDate())
                    //Por fim, vamos usar o 'sign' passando o nosso 'algorithm' para gerar de fato o nosso token
                    .sign(algorithm);

            //Retornamos o nosso token
            return token;

        } catch (JWTCreationException exception) {

            //Caso capture uma exceção vou lançá-la
            throw new RuntimeException("Error while authenticating user");
        }
    }

    //Validação do token da nossa aplicação
    //Vamos receber como parâmetro o token
    public String validateToken(String token) {

        //Novamente vamos utilizar um bloco try-catch porque caso der algum erro na validação do token o JWT vai lançar
        //uma exceção
        try {

            //Se conseguirmos validar o token vou retornar o email do usuário que está nesse token e a gente salvou na
            //hora de gerar o token

            //Aqui basicamente vamos fazer a mesma construção do 'algorithm'
            Algorithm algorithm = Algorithm.HMAC256(secret);

            //Vamos retornar usando o método 'JWT.require' passando o nosso 'algorithm'
            return JWT.require(algorithm)
                    //Aqui vamos passar o mesmo Issuer, estamos montando o objeto para fazer a verificação
                    .withIssuer("login-auth-api")
                    .build()
                    //Aqui nós passamos o token para ele ser verificado
                    .verify(token)
                    //Quando a gente fizer o 'getSubject' a gente vai pegar o valor que foi salvo no token no momento da
                    //geração
                    .getSubject();

        } catch (JWTVerificationException exception) {

            //Caso der um erro de verificação de token vou retornar nulo porque aqui estamos apenas validando o token
            //então caso a gente não consiga validar esse token, por exemplo se for um token inválido, token faltando
            //informação, etc, vamos retornar null e depois fazemos uma verificação no nosso filter chain, na nossa
            //cadeia de segurança que a gente vai implementar que se vier como null vamos colocar o usuário como não
            //autenticado
            return null;
        }

    }

    //Função responsável pelo tempo de expiração do nosso token
    private Instant generateExpirationDate() {

        //A nossa regra consiste em pegar o tempo de agora e adicionar 2 horas
        //Por fim, transformamos em um Instant passando a nossa time zone
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
