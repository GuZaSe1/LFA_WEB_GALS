package gals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        try {

            String caminhoArquivo = "programa.txt";
            byte[] bytes = Files.readAllBytes(Paths.get(caminhoArquivo));

            String codigo = new String(bytes, StandardCharsets.UTF_8);
            executar(codigo);

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo de entrada: " + e.getMessage());
        }
    }

    private static void executar(String codigo) {
        Reader entrada = new StringReader(codigo);

        Lexico lexico = new Lexico(entrada);
        Sintatico sintatico = new Sintatico();
        Semantico semantico = new Semantico();

        try {

            sintatico.parse(lexico, semantico);
            System.out.println("Programa executado com sucesso.");

        } catch (AnalysisError e) {

            System.err.println("Erro: " + e.getMessage());

            if (e.getPosition() >= 0) {
                System.err.println("Posição: " + e.getPosition());
            }
        }
    }
}