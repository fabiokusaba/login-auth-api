package br.com.fabiokusaba.loginauthapi.infra.security;

//Cadeia de filtro de segurança que vai pegar o retorno do token e autenticar ou não o usuário
//É uma configuração meio que padrão toda vez que vamos criar um novo security filter para o Spring Security
//A nossa classe 'SecurityFilter' vai receber a anotação '@Component' para que o Spring consiga enxergar ela
//Essa nossa classe vai extender 'OncePerRequestFilter' que significa que ele é um filtro que vai executar uma vez para
//cada request que chegar na minha aplicação
//Então o Spring Security é como se fosse um filtro para os nossos controllers, antes da informação chegar no controller
//e processar nos nossos services a gente tem esse filtro do Spring Security que faz a autenticação do usuário, vê se
//esse usuário está autenticado, se ele pode fazer esse request ou não
//Quando declaramos uma classe que extende de 'OncePerRequestFilter' é uma classe que vai ser um filtro que vai rodar
//uma vez a cada requisição
//Essa nossa classe em específico vai fazer a verificação do usuário, se o token que o usuário mandou está válido, se é
//um token que a nossa aplicação emitiu e se for ele já vai salvar nas informações do contexto da autenticação quem é
//esse usuário que está fazendo a requisição que aí podemos usar nos outros componentes aqui dentro da nossa aplicação
//pra operar essas informações

import br.com.fabiokusaba.loginauthapi.domain.user.User;
import br.com.fabiokusaba.loginauthapi.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;

    @Autowired
    UserRepository userRepository;



    //Aqui vamos declara dois métodos que é o método 'doFilterInternal' que é literalmente o método que vai ser o filtro
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //Recuperando o token que veio da requisição
        var token = this.recoverToken(request);

        //Depois que recuperamos o token nós chamamos o nosso 'tokenService' com a função 'validateToken' para estar
        //fazendo a validação do token
        var login = tokenService.validateToken(token);

        //Após a validação do token pegamos o seu retorno, caso tenha, e verificamos se não é nulo porque lá no nosso
        //método 'validateToken' caso o token não seja validado com sucesso ele retornará nulo, então se o token não for
        //nulo eu vou entrar dentro desse 'if'
        if (login != null) {

            //Buscando o usuário no banco de dados
            //Aqui estamos esperando um User, mas estamos recebendo um Optional que é algo opcional em que eu posso ou
            //não encontrar um usuário com esse email
            //Então para extrairmos um User nós vamos fazer um 'orElseThrow' para caso eu não encontre um usuário eu
            //jogue uma exceção, nesse caso já estamos nos prevenindo para caso nossa consulta dê errado
            User user = userRepository.findByEmail(login).orElseThrow(() -> new RuntimeException("User not found"));

            //Assim que encontrarmos o usuário criamos uma coleção de roles que esse usuário tem
            var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

            //Então criamos o objeto de autenticação contendo o usuário e as suas roles, podemos deixar as 'credentials'
            //como null porque elas não se aplicam ao nosso caso
            var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);

            //Criando esse objeto específico setamos no 'SecurityContextHolder' que é o contexto de segurança do Spring
            //Security, então cada elemento, cada componente do Spring Security faz uma etapa e eles vão alimentando
            //esse 'SecurityContextHolder' para ele saber o que ele já validou, o que ele não validou e salvar as
            //informações do usuário que já estiver autenticado
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    //E o método 'recoverToken' que é um método auxiliar que vai receber a request que veio do usuário e vai pegar o
    //header "Authorization" que é onde vai estar o token, então se não tiver nada no header "Authorization" já retorno
    //null se não retorno 'authHeader' que é o nosso header de "Authorization" substituindo o "Bearer " por vazio porque
    //normalmente os headers de "Authorization" vem "Authorization: Bearer asalkfowqldqjq", dessa forma pegamos apenas o
    //token
    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}
