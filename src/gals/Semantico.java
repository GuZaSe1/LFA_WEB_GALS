package gals;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Semantico implements Constants {
    private final Map<String, BigInteger> variaveis = new HashMap<>();
    private final Stack<BigInteger> pilha = new Stack<>();
    private String variavelAtribuicao;

    public void executeAction(int action, Token token) throws SemanticError {
        switch (action) {
            case 1:
                // Guarda a variável que receberá o resultado
                variavelAtribuicao = token.getLexeme();
                break;

            case 2:
                // Converte um número binário e coloca seu valor na pilha
                try {
                    pilha.push(new BigInteger(token.getLexeme(), 2));
                } catch (NumberFormatException e) {
                    throw erro("Número binário inválido: " + token.getLexeme(), token);
                }
                break;

            case 3:
                // Busca o valor de uma variável já existente
                String nome = token.getLexeme();
                BigInteger valor = variaveis.get(nome);

                if (valor == null) {
                    throw erro("Variável não definida: " + nome, token);
                }

                pilha.push(valor);
                break;

            case 4:
                // Soma
                operarSoma(token);
                break;

            case 5:
                // Subtração
                operarSubtracao(token);
                break;

            case 6:
                // Multiplicação
                operarMultiplicacao(token);
                break;

            case 7:
                // Divisão
                operarDivisao(token);
                break;

            case 8:
                // Exponenciação
                operarPotencia(token);
                break;

            case 9:
                // Log
                operarLog2(token);
                break;

            case 10:
                // Finaliza uma atribuição
                if (variavelAtribuicao == null) {
                    throw erro("Atribuição sem variável de destino.", token);
                }

                BigInteger resultado = desempilhar(token);

                variaveis.put(variavelAtribuicao, resultado);

                variavelAtribuicao = null;
                break;

            case 11:
                // Exibe o resultado em binário
                BigInteger valorImpressao = desempilhar(token);

                System.out.println(valorImpressao.toString(2));
                break;

            default:
                throw erro("Ação semântica desconhecida: #" + action, token);
        }
    }

    private void operarSoma(Token token) throws SemanticError {
        BigInteger direita = desempilhar(token);
        BigInteger esquerda = desempilhar(token);

        pilha.push(esquerda.add(direita));
    }

    private void operarSubtracao(Token token) throws SemanticError {
        BigInteger direita = desempilhar(token);
        BigInteger esquerda = desempilhar(token);

        BigInteger resultado = esquerda.subtract(direita);

        if (resultado.signum() < 0) {
            throw erro("A subtração resultou em número negativo.", token);
        }

        pilha.push(resultado);
    }

    private void operarMultiplicacao(Token token) throws SemanticError {
        BigInteger direita = desempilhar(token);
        BigInteger esquerda = desempilhar(token);

        pilha.push(esquerda.multiply(direita));
    }

    private void operarDivisao(Token token) throws SemanticError {
        BigInteger direita = desempilhar(token);
        BigInteger esquerda = desempilhar(token);

        if (direita.equals(BigInteger.ZERO)) {
            throw erro("Divisão por zero não é permitida.", token);
        }

        pilha.push(esquerda.divide(direita));
    }

    private void operarPotencia(Token token) throws SemanticError {
        BigInteger expoente = desempilhar(token);
        BigInteger base = desempilhar(token);

        if (expoente.signum() < 0 || expoente.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
            throw erro("Expoente fora do intervalo suportado.", token);
        }

        pilha.push(base.pow(expoente.intValue()));
    }

    private void operarLog2(Token token) throws SemanticError {
        BigInteger valor = desempilhar(token);

        if (valor.signum() <= 0) {
            throw erro("Logaritmo exige um valor maior que zero.", token);
        }

        boolean potenciaDeDois = valor.and(valor.subtract(BigInteger.ONE)).equals(BigInteger.ZERO);

        if (!potenciaDeDois) {
            throw erro("log base 2 não resulta em inteiro para o valor informado.", token);
        }

        pilha.push(BigInteger.valueOf(valor.bitLength() - 1L));
    }

    private BigInteger desempilhar(Token token) throws SemanticError {

        if (pilha.isEmpty()) {
            throw erro("Expressão inválida: não há valor suficiente para executar a operação.", token);
        }

        return pilha.pop();
    }

    private SemanticError erro(String mensagem, Token token) {

        if (token != null) {
            return new SemanticError(mensagem, token.getPosition());
        }

        return new SemanticError(mensagem);
    }
}