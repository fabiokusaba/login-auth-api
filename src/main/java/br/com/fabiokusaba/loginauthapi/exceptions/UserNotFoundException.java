package br.com.fabiokusaba.loginauthapi.exceptions;

//Primeiro ponto para tratar exceções no Java Spring é criar exceções que sejam específicas da nossa aplicação é o que
//chamamos de custom exception que são exceções customizadas e a gente faz isso extendendo da classe 'RuntimeException'
//do Java e aí a gente cria a nossa própria classe, por exemplo 'UserNotFoundException' que diz respeito a um
//comportamento específico da nossa aplicação, da nossa regra de negócio
//E aqui precisamos extender de 'RuntimeException' para que essa classe se comporte como uma exceção nativa da linguagem
//só que com a nossa mensagem customizada e com o nome customizado
//Podemos construir diversas exceções customizadas da nossa aplicação que vão corresponder a erros específicos e dessa
//maneira quando estivermos fazendo a nossa regra de negócio e encontrar algum desses erros e tiver que lançar a
//exceção nós lançamos essa exceção específica que vai nos auxiliar em momentos de debug da aplicação e na construção de
//uma mensagem mais amigável para o usuário da nossa aplicação

public class UserNotFoundException extends RuntimeException {

    //Construtor onde chamamos a super classe que é 'RuntimeException' passando a nossa mensagem customizada
    public UserNotFoundException() {
        super("User not found!");
    }

    //Construtor onde recebo a mensagem por parâmetro e só passo essa mensagem para a super classe
    public UserNotFoundException(String message) {
        super(message);
    }
}
