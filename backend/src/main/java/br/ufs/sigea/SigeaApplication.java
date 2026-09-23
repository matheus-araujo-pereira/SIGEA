package br.ufs.sigea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal de inicialização do sistema SIGEA.
 * Sistema Inteligente de Gestão de Eventos Adversos
 * Universidade Federal de Sergipe (UFS)
 *
 * @author Matheus Araujo Pereira
 */
@SpringBootApplication
public class SigeaApplication {

    /**
     * Ponto de entrada da aplicação Spring Boot.
     *
     * @param args Argumentos de linha de comando
     */
    public static void main(String[] args) {
        SpringApplication.run(SigeaApplication.class, args);
    }
}
