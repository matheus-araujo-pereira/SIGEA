package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.security;

import br.ufs.dcomp.sigeagtt.domain.ports.output.PasswordEncoderPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Adaptador de segurança para hash de senhas, implementando {@link PasswordEncoderPort}. */
@Component
public class PasswordEncoderAdapter implements PasswordEncoderPort {

    private final PasswordEncoder passwordEncoder;

    public PasswordEncoderAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String codificar(String senhaPlana) {
        if (senhaPlana == null) return null;
        return passwordEncoder.encode(senhaPlana);
    }

    @Override
    public boolean corresponde(String senhaPlana, String hashCodificado) {
        if (senhaPlana == null || hashCodificado == null) return false;
        return passwordEncoder.matches(senhaPlana, hashCodificado);
    }
}
