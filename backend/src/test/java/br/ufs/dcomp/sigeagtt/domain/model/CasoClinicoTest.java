package br.ufs.dcomp.sigeagtt.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CasoClinicoTest {

    @Test
    @DisplayName("Deve inicializar com valores padrao no construtor sem argumentos")
    void deveInicializarComValoresPadrao() {
        CasoClinico cc = new CasoClinico();
        assertNotNull(cc.getCriadoEm());
        assertNull(cc.getId());
    }

    @Test
    @DisplayName("Deve construir com construtor completo e respeitar defaults")
    void deveConstruirComConstrutorCompleto() {
        Usuario prof = new Usuario();
        UnidadeHospitalar unid = new UnidadeHospitalar();
        LocalDate adm = LocalDate.of(2026, 3, 1);
        LocalDate alta = LocalDate.of(2026, 3, 10);
        LocalDateTime agora = LocalDateTime.now();

        CasoClinico c1 =
                new CasoClinico(
                        1L,
                        prof,
                        unid,
                        "Caso Sepse",
                        "Paciente em choque",
                        "Identificar choque",
                        "ATD123",
                        65,
                        adm,
                        alta,
                        9,
                        "Alta curado",
                        "Ceftriaxona",
                        "Leucocitose",
                        "Sem cirurgia",
                        "Evolução estável",
                        agora);

        assertEquals(1L, c1.getId());
        assertEquals(prof, c1.getProfessorCriador());
        assertEquals(unid, c1.getUnidadeHospitalar());
        assertEquals("Caso Sepse", c1.getTitulo());
        assertEquals("Paciente em choque", c1.getDescricaoCaso());
        assertEquals("Identificar choque", c1.getObjetivosAprendizagem());
        assertEquals("ATD123", c1.getNumeroAtendimento());
        assertEquals(65, c1.getIdadePaciente());
        assertEquals(adm, c1.getDataAdmissao());
        assertEquals(alta, c1.getDataAlta());
        assertEquals(9, c1.getTempoPermanenciaDias());
        assertEquals("Alta curado", c1.getSumarioAlta());
        assertEquals("Ceftriaxona", c1.getPrescricoesMedicas());
        assertEquals("Leucocitose", c1.getExamesLaboratoriais());
        assertEquals("Sem cirurgia", c1.getRelatorioCirurgico());
        assertEquals("Evolução estável", c1.getEvolucoesMultiprofissionais());
        assertEquals(agora, c1.getCriadoEm());

        CasoClinico c2 =
                new CasoClinico(
                        2L, prof, unid, "Caso 2", "Desc", "Obj", "ATD456", 40, adm, alta, 9, "Alta",
                        "Presc", "Exame", "Cirurg", "Evol", null);
        assertNotNull(c2.getCriadoEm());
    }

    @Test
    @DisplayName("Deve validar com sucesso quando titulo e atendimento presentes e datas corretas")
    void deveValidarComSucesso() {
        CasoClinico c = new CasoClinico();
        c.setTitulo("Choque Séptico");
        c.setNumeroAtendimento("ATD-999");
        c.setDataAdmissao(LocalDate.of(2026, 3, 1));
        c.setDataAlta(LocalDate.of(2026, 3, 5));

        assertDoesNotThrow(c::validarInvariantes);

        // Também deve validar se datas forem nulas
        c.setDataAdmissao(null);
        c.setDataAlta(null);
        assertDoesNotThrow(c::validarInvariantes);
    }

    @Test
    @DisplayName("Deve lancar excecao quando titulo for nulo ou em branco")
    void deveLancarExcecaoQuandoTituloInvalido() {
        CasoClinico c1 = new CasoClinico();
        c1.setTitulo(null);
        c1.setNumeroAtendimento("ATD1");
        RegraNegocioException ex1 =
                assertThrows(RegraNegocioException.class, c1::validarInvariantes);
        assertEquals("O título do caso clínico é obrigatório.", ex1.getMessage());

        CasoClinico c2 = new CasoClinico();
        c2.setTitulo("   ");
        c2.setNumeroAtendimento("ATD1");
        RegraNegocioException ex2 =
                assertThrows(RegraNegocioException.class, c2::validarInvariantes);
        assertEquals("O título do caso clínico é obrigatório.", ex2.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao quando numero de atendimento for nulo ou em branco")
    void deveLancarExcecaoQuandoNumeroAtendimentoInvalido() {
        CasoClinico c1 = new CasoClinico();
        c1.setTitulo("Título");
        c1.setNumeroAtendimento(null);
        RegraNegocioException ex1 =
                assertThrows(RegraNegocioException.class, c1::validarInvariantes);
        assertEquals("O número de atendimento do prontuário é obrigatório.", ex1.getMessage());

        CasoClinico c2 = new CasoClinico();
        c2.setTitulo("Título");
        c2.setNumeroAtendimento("   ");
        RegraNegocioException ex2 =
                assertThrows(RegraNegocioException.class, c2::validarInvariantes);
        assertEquals("O número de atendimento do prontuário é obrigatório.", ex2.getMessage());
    }

    @Test
    @DisplayName("Deve lancar excecao quando data de alta for anterior a data de admissao")
    void deveLancarExcecaoQuandoDataAltaAnteriorAdmissao() {
        CasoClinico c = new CasoClinico();
        c.setTitulo("Título");
        c.setNumeroAtendimento("ATD1");
        c.setDataAdmissao(LocalDate.of(2026, 3, 10));
        c.setDataAlta(LocalDate.of(2026, 3, 5));

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, c::validarInvariantes);
        assertEquals("A data de alta não pode ser anterior à data de admissão.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve testar getters, setters, equals, hashCode e toString")
    void deveTestarMetodosUtilitarios() {
        CasoClinico c1 = new CasoClinico();
        c1.setId(10L);
        Usuario prof = new Usuario();
        UnidadeHospitalar unid = new UnidadeHospitalar();
        c1.setProfessorCriador(prof);
        c1.setUnidadeHospitalar(unid);
        c1.setTitulo("Caso Teste");
        c1.setDescricaoCaso("Desc");
        c1.setObjetivosAprendizagem("Obj");
        c1.setNumeroAtendimento("ATD888");
        c1.setIdadePaciente(50);
        c1.setDataAdmissao(LocalDate.of(2026, 1, 1));
        c1.setDataAlta(LocalDate.of(2026, 1, 10));
        c1.setTempoPermanenciaDias(9);
        c1.setSumarioAlta("Sum");
        c1.setPrescricoesMedicas("Presc");
        c1.setExamesLaboratoriais("Exames");
        c1.setRelatorioCirurgico("Cir");
        c1.setEvolucoesMultiprofissionais("Evol");
        LocalDateTime agora = LocalDateTime.now();
        c1.setCriadoEm(agora);

        assertEquals(10L, c1.getId());
        assertEquals(prof, c1.getProfessorCriador());
        assertEquals(unid, c1.getUnidadeHospitalar());
        assertEquals("Caso Teste", c1.getTitulo());
        assertEquals("Desc", c1.getDescricaoCaso());
        assertEquals("Obj", c1.getObjetivosAprendizagem());
        assertEquals("ATD888", c1.getNumeroAtendimento());
        assertEquals(50, c1.getIdadePaciente());
        assertEquals(LocalDate.of(2026, 1, 1), c1.getDataAdmissao());
        assertEquals(LocalDate.of(2026, 1, 10), c1.getDataAlta());
        assertEquals(9, c1.getTempoPermanenciaDias());
        assertEquals("Sum", c1.getSumarioAlta());
        assertEquals("Presc", c1.getPrescricoesMedicas());
        assertEquals("Exames", c1.getExamesLaboratoriais());
        assertEquals("Cir", c1.getRelatorioCirurgico());
        assertEquals("Evol", c1.getEvolucoesMultiprofissionais());
        assertEquals(agora, c1.getCriadoEm());

        CasoClinico c2 = new CasoClinico();
        c2.setId(10L);
        CasoClinico c3 = new CasoClinico();
        c3.setId(20L);

        assertEquals(c1, c1);
        assertEquals(c1, c2);
        assertNotEquals(c1, c3);
        assertNotEquals(c1, null);
        assertNotEquals(c1, "outro");
        assertEquals(c1.hashCode(), c2.hashCode());

        String str = c1.toString();
        assertTrue(str.contains("Caso Teste"));
        assertTrue(str.contains("ATD888"));
    }

    @Test
    @DisplayName("Deve permitir datas nulas na validação")
    void devePermitirDatasNulas() {
        CasoClinico c = new CasoClinico();
        c.setTitulo("Título");
        c.setNumeroAtendimento("ATD123");
        c.setDataAdmissao(null);
        c.setDataAlta(LocalDate.now());
        assertDoesNotThrow(c::validarInvariantes);

        c.setDataAdmissao(LocalDate.now());
        c.setDataAlta(null);
        assertDoesNotThrow(c::validarInvariantes);
    }
}
