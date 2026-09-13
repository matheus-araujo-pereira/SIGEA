package br.ufs.dcomp.sigeagtt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Classe principal de inicialização do backend do SIGEA-GTT (Spring Boot 4.1 / Java 25 LTS). */
@SpringBootApplication
public class SigeaGttBackendApplication {

    /**
     * Ponto de entrada da aplicação.
     *
     * @param args Argumentos de linha de comando.
     */
    public static void main(String[] args) {
        SpringApplication.run(SigeaGttBackendApplication.class, args);
    }
}
