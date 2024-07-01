package br.com.fabiokusaba.loginauthapi.infra;

//Classe que vai concentrar o tratamento de todos os erros do nosso controller, cada um dos métodos dessa classe vão ser
//responsáveis por tratar uma exceção diferente e a gente vai fazer isso usando o mecanismo de '@ControllerAdvice' do
//Spring, então usando essa anotação em cima da nossa classe o Spring já vai entender que se ele detectar alguma exceção
//se alguma exceção for lançada na nossa aplicação e não tiver um try-catch para tratar dela, ou seja, se ela apenas foi
//lançada para o Spring ele vai saber que tem que chamar essa classe '@ControllerAdvice' e procurar nos métodos dessa
//classe se tem algum método que faz o tratamento dessa exceção, se ele encontrar algum método que faz o tratamento da
//exceção ele vai retornar a exceção tratada para o usuário, caso contrário o Spring vai retornar a exceção no formato
//padrão dele exibindo o código de erro e o seu stack trace
//Essa nossa classe iremos anotá-la com '@ControllerAdvice' e também ela vai precisar extender de
//'ResponseEntityExceptionHandler'

import br.com.fabiokusaba.loginauthapi.exceptions.ErrorResponse;
import br.com.fabiokusaba.loginauthapi.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    //Aqui dentro vamos escrever os métodos que vão tratar cada uma das nossas exceções passando como parâmetro a nossa
    //exceção que queremos tratar
    //Por fim precisamos colocar nos nossos métodos a anotação '@ExceptionHandler' para o Spring identificar que esse é
    //o método responsável por tratar a exceção

    @ExceptionHandler(UserNotFoundException.class)
    private ResponseEntity<ErrorResponse> userNotFoundHandler(UserNotFoundException exception) {
        var threatResponse = new ErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(threatResponse);
    }

    //Podemos tratar não só as nossas exceções específicas como também exceções genéricas do Java como por exemplo a
    //'RuntimeException' retornando para o usuário uma resposta de erro customizada

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> runtimeExceptionHandler(RuntimeException exception) {
        var threatResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(threatResponse);
    }
}
