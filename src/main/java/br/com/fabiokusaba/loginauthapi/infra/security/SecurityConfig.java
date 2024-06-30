package br.com.fabiokusaba.loginauthapi.infra.security;

//Partimos agora para o último componente da nossa camada do Spring Security que é o 'SecurityConfig' que é a
//configuração final que vai juntar todas essas peças que a gente criou e adicionar os filtros, o proxy em cada uma das
//requisições da nossa aplicação

//A primeira coisa que a gente precisa se atentar é colocar a anotação '@Configuration' e o '@EnableWebSecurity' para
//indicar que essa é uma classe de configuração que o Spring vai ter que carregar antes de carregar todos os outros
//componentes porque é uma classe de configuração, ou seja, pode até criar componentes
//E o '@EnableWebSecurity' para dizer que é a classe que cuida da configuração da segurança web

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //Aqui dentro do 'SecurityConfig' vamos ter duas dependências que vai ser o nosso 'securityFilter' e o nosso
    //'CustomUserDetailsService'
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    //O 'SecurityFilter' a gente vai utilizar para colocar como um filtro nas requisições que a gente quer que sejam
    //autenticadas
    @Autowired
    SecurityFilter securityFilter;

    //Basicamente nesse método 'securityFilterChain' a gente adicionou essa parte de 'authorizeHttpRequest' para dizer
    //que os endpoints "/auth/login" e "/auth/register" não precisam de autenticação que por padrão quando adicionamos
    //o Spring Security na nossa aplicação ele já bloqueia todos os endpoints que passam a precisar de autenticação, mas
    //aqui o que estamos fazendo é liberando especificamente esses dois endpoints porque qualquer pessoa pode fazer o
    //login ou se registrar na nossa aplicação
    //Já as outras requisições 'anyRequest', ou seja, qualquer outra requisição que vier eu quero que o usuário esteja
    //autenticado se não vai retornar um 403 forbidden para ele
    //E por final eu coloco um 'addFilterBefore' before significa antes em inglês, então o que estamos fazendo é antes
    //de fazer o 'authorizeHttpRequests' adiciona esse filtro, roda esse filtro antes que é o nosso 'securityFilter' e
    //esse filtro vai pegar o token do usuário validar se esse token está correto, se estiver correto ele vai adicionar
    //o usuário no contexto do Spring Security, se não estiver logado a gente não vai adicionar nada no contexto, então
    //é através desse filtro que a gente consegue fazer as verificações do header de "Authorization" se o usuário passou
    //e se passou se ele está válido ou não e desta forma conseguimos saber se está apto para passar para o nosso
    //controller a requisição caso o usuário esteja autenticado e caso ela não esteja o próprio Spring Security vai
    //barrar e não vai permitir que se faça a requisição retornando um 403
    //Outra coisa importante é o 'sessionManagement' que a gente colocou que na nossa aplicação não irá ter, que a nossa
    //aplicação é 'STATELESS', todas as aplicações REST, todas as APIs RESTFul elas tem que ser 'STATELESS', ou seja,
    //elas não guardam estado de login dentro delas, então toda vez que o nosso usuário bater na nossa aplicação ele vai
    //ter que passar o token de autenticação dele para verificarmos se ele está autenticado, não temos um banco de dados
    //temporário ou algum cache temporário para saber os usuários que já se autenticaram, esse é o padrão das APIS
    //RESTFul, então aqui fazemos o 'sessionCreationPolicy' como 'STATELESS' não guardo estado na aplicação isso faz com
    //que cada requisição que o usuário precisar fazer para a nossa aplicação se for uma requisição que exija
    //autenticação ele vai ter que passar o token em todas essas requisições que é o padrão da web
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    //Por fim, adicionamos outros dois métodos aqui nessa classe que eles vão criar os Beans de 'PasswordEncoder' e o
    //Bean de 'AuthenticationManager'
    //O 'passwordEncoder' vamos utilizar no controller para fazer o encoding da password para não salvarmos a String
    //direto no banco de dados
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //E esse aqui é um Bean necessário para o Spring Security conseguir funcionar
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
