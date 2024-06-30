package br.com.fabiokusaba.loginauthapi.infra.cors;

//Classe de configuração de cors para que a gente consiga receber as requisições vindas do nosso Frontend
//Para isso precisamos usar a anotação '@Configuration' para indicar ao Spring que essa é uma classe de configuração

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    //Basicamente a configuração do cors vai habilitar todas as requisições que vierem do domínio do nosso Frontend
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200") //Passando a origem que pode fazer as requisições
                .allowedMethods("GET", "POST", "PUT", "DELETE"); //Passando os métodos http que são permitidos
    }
}
