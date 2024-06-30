package br.com.fabiokusaba.loginauthapi.repositories;

import br.com.fabiokusaba.loginauthapi.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    //Declarando o método 'findByEmail' para que o Spring possa fazer a geração automática filtrando pela coluna email
    Optional<User> findByEmail(String email);
}
