package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class PasswordEncoderAdapterTest {

    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private PasswordEncoderAdapter adapter;

    @Test
    @DisplayName("Deve codificar senha plana e retornar nulo se senha for nula")
    void deveCodificar() {
        when(passwordEncoder.encode("senha123")).thenReturn("hash123");

        assertEquals("hash123", adapter.codificar("senha123"));
        assertNull(adapter.codificar(null));
    }

    @Test
    @DisplayName("Deve verificar correspondencia de senha tratando nulos")
    void deveVerificarCorrespondencia() {
        when(passwordEncoder.matches("senha123", "hash123")).thenReturn(true);
        when(passwordEncoder.matches("senha_errada", "hash123")).thenReturn(false);

        assertTrue(adapter.corresponde("senha123", "hash123"));
        assertFalse(adapter.corresponde("senha_errada", "hash123"));
        assertFalse(adapter.corresponde(null, "hash123"));
        assertFalse(adapter.corresponde("senha123", null));
        assertFalse(adapter.corresponde(null, null));
    }
}
