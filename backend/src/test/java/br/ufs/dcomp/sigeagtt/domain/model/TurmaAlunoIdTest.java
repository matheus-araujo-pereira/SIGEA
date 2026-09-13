package br.ufs.dcomp.sigeagtt.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TurmaAlunoIdTest {

    @Test
    @DisplayName("Deve testar construtor padrao e construtor completo")
    void deveTestarConstrutoresEGettersSetters() {
        TurmaAlunoId id1 = new TurmaAlunoId();
        id1.setTurmaId(1L);
        id1.setAlunoId(2L);

        assertEquals(1L, id1.getTurmaId());
        assertEquals(2L, id1.getAlunoId());

        TurmaAlunoId id2 = new TurmaAlunoId(1L, 2L);
        assertEquals(1L, id2.getTurmaId());
        assertEquals(2L, id2.getAlunoId());

        assertEquals(id1, id1);
        assertEquals(id1, id2);
        assertNotEquals(id1, null);
        assertNotEquals(id1, "outro");
        assertEquals(id1.hashCode(), id2.hashCode());

        TurmaAlunoId id3 = new TurmaAlunoId(1L, 3L);
        assertNotEquals(id1, id3);

        TurmaAlunoId id4 = new TurmaAlunoId(2L, 2L);
        assertNotEquals(id1, id4);
    }
}
