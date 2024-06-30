package br.com.fabiokusaba.loginauthapi.infra.security;

//Classe que o Spring Security vai utilizar pra consultar os usuários
//Essa classe vai implementar a interface 'UserDetailsService' que vem do Spring Security e ao implementar essa
//interface a gente precisa implementar alguns métodos que são obrigatórios que é o método de 'loadUserByUsername'
//Aqui precisamos colocar a anotação '@Component' para que essa classe seja visível para o Spring

import br.com.fabiokusaba.loginauthapi.domain.user.User;
import br.com.fabiokusaba.loginauthapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    //Vamos ter uma dependência do nosso 'UserRepository' e vamos colocar a anotação '@Autowired' para que o Spring
    //possa fazer a injeção de dependência para nós
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //Aqui dentro do nosso método nós vamos procurar esse usuário para isso vamos salvar uma nova variável do tipo
        //User que vai ser o nosso usuário e como o nosso repository retorna um Optional nós precisamos fazer a mesma
        //coisa 'orElseThrow'
        //Aqui precisamos lançar uma exceção específica do Spring Security
        User user = this.userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        //Assim que eu encontrar o usuário vou ter que retornar um objeto do tipo 'UserDetails' que é o 'User' na visão
        //do Spring Security e não 'User' entidade aqui da nossa classe, então precisamos passar o email, a senha e um
        //ArrayList que é o array de roles, como no nosso caso ele não tem nenhuma role passamos um ArrayList vazio
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }
}
