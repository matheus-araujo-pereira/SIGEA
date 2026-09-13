package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity;

import static org.junit.jupiter.api.Assertions.*;

import br.ufs.dcomp.sigeagtt.domain.model.GravidadeNccMerp;
import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.StatusSubmissao;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JpaEntitiesTest {

    @Test
    @DisplayName("Deve testar UsuarioJpaEntity, builders, getters/setters, equals/hash e aoCriar")
    void deveTestarUsuarioJpaEntity() {
        UsuarioJpaEntity u1 = new UsuarioJpaEntity();
        u1.setId(1L);
        u1.setNomeCompleto("Nome");
        u1.setEmail("email@ufs.br");
        u1.setSenha("senha");
        u1.setPrimeiroAcesso(true);
        u1.setMatriculaSigaa("123");
        u1.setPerfil(PerfilUsuario.ALUNO);
        u1.setAtivo(true);
        u1.setCriadoEm(LocalDateTime.now());

        UsuarioJpaEntity u2 =
                UsuarioJpaEntity.builder()
                        .id(1L)
                        .nomeCompleto("Nome 2")
                        .email("email2@ufs.br")
                        .senha("senha2")
                        .primeiroAcesso(false)
                        .matriculaSigaa("456")
                        .perfil(PerfilUsuario.PROFESSOR)
                        .ativo(false)
                        .criadoEm(LocalDateTime.now())
                        .build();

        UsuarioJpaEntity u3 =
                new UsuarioJpaEntity(
                        2L,
                        "Nome 3",
                        "email3@ufs.br",
                        "senha3",
                        false,
                        "789",
                        PerfilUsuario.ADMINISTRADOR,
                        true,
                        LocalDateTime.now());

        assertEquals(u1, u2);
        assertNotEquals(u1, u3);
        assertNotEquals(u1, null);
        assertNotEquals(u1, new Object());
        assertEquals(u1.hashCode(), u2.hashCode());
        assertEquals("Nome", u1.getNomeCompleto());
        assertEquals("email@ufs.br", u1.getEmail());
        assertEquals("senha", u1.getSenha());
        assertTrue(u1.getPrimeiroAcesso());
        assertEquals("123", u1.getMatriculaSigaa());
        assertEquals(PerfilUsuario.ALUNO, u1.getPerfil());
        assertTrue(u1.getAtivo());
        assertNotNull(u1.getCriadoEm());

        // Testar aoCriar() com campos nulos
        UsuarioJpaEntity uNovo = new UsuarioJpaEntity();
        uNovo.setAtivo(null);
        uNovo.setPrimeiroAcesso(null);
        uNovo.aoCriar();
        assertNotNull(uNovo.getCriadoEm());
        assertTrue(uNovo.getAtivo());
        assertTrue(uNovo.getPrimeiroAcesso());

        // Testar aoCriar() com campos já preenchidos
        LocalDateTime fixo = LocalDateTime.of(2026, 1, 1, 0, 0);
        UsuarioJpaEntity uPreenchido =
                UsuarioJpaEntity.builder()
                        .criadoEm(fixo)
                        .ativo(false)
                        .primeiroAcesso(false)
                        .build();
        uPreenchido.aoCriar();
        assertEquals(fixo, uPreenchido.getCriadoEm());
        assertFalse(uPreenchido.getAtivo());
        assertFalse(uPreenchido.getPrimeiroAcesso());
    }

    @Test
    @DisplayName("Deve testar UnidadeHospitalarJpaEntity")
    void deveTestarUnidadeHospitalarJpaEntity() {
        UnidadeHospitalarJpaEntity u1 = new UnidadeHospitalarJpaEntity();
        u1.setId(1L);
        u1.setNome("UTI Adulto");
        u1.setSigla("UTI-A");
        u1.setAtiva(true);

        UnidadeHospitalarJpaEntity u2 =
                UnidadeHospitalarJpaEntity.builder()
                        .id(1L)
                        .nome("Outro")
                        .sigla("OUT")
                        .ativa(false)
                        .build();

        UnidadeHospitalarJpaEntity u3 =
                new UnidadeHospitalarJpaEntity(2L, "Centro Cirurgico", "CC", true);

        assertEquals(u1, u2);
        assertNotEquals(u1, u3);
        assertEquals(u1.hashCode(), u2.hashCode());
        assertEquals("UTI Adulto", u1.getNome());
        assertEquals("UTI-A", u1.getSigla());
        assertTrue(u1.getAtiva());
    }

    @Test
    @DisplayName("Deve testar TurmaJpaEntity e aoCriar")
    void deveTestarTurmaJpaEntity() {
        UsuarioJpaEntity prof = UsuarioJpaEntity.builder().id(2L).build();
        TurmaJpaEntity t1 = new TurmaJpaEntity();
        t1.setId(10L);
        t1.setProfessorResponsavel(prof);
        t1.setCodigoDisciplina("MED001");
        t1.setNomeDisciplina("Segurança");
        t1.setPeriodoLetivo("2026.1");
        t1.setAnoSemestre("2026/1");
        t1.setAtiva(true);
        t1.setCriadaEm(LocalDateTime.now());

        TurmaJpaEntity t2 = TurmaJpaEntity.builder().id(10L).build();
        TurmaJpaEntity t3 =
                new TurmaJpaEntity(
                        20L,
                        prof,
                        "MED002",
                        "Farmaco",
                        "2026.1",
                        "2026/1",
                        true,
                        LocalDateTime.now());

        assertEquals(t1, t2);
        assertNotEquals(t1, t3);
        assertEquals(t1.hashCode(), t2.hashCode());
        assertEquals("MED001", t1.getCodigoDisciplina());
        assertEquals("Segurança", t1.getNomeDisciplina());
        assertEquals("2026.1", t1.getPeriodoLetivo());
        assertEquals("2026/1", t1.getAnoSemestre());
        assertTrue(t1.getAtiva());
        assertEquals(prof, t1.getProfessorResponsavel());

        // aoCriar com campos nulos e disciplina em branco
        TurmaJpaEntity tNovo = new TurmaJpaEntity();
        tNovo.setAtiva(null);
        tNovo.setNomeDisciplina("");
        tNovo.aoCriar();
        assertNotNull(tNovo.getCriadaEm());
        assertTrue(tNovo.getAtiva());
        assertEquals("Segurança do Paciente e Auditoria Clínica", tNovo.getNomeDisciplina());

        // aoCriar com nomeDisciplina nulo
        TurmaJpaEntity tNovo2 = new TurmaJpaEntity();
        tNovo2.setNomeDisciplina(null);
        tNovo2.aoCriar();
        assertEquals("Segurança do Paciente e Auditoria Clínica", tNovo2.getNomeDisciplina());

        // aoCriar com campos já preenchidos
        LocalDateTime agora = LocalDateTime.now();
        TurmaJpaEntity tPreenchido =
                TurmaJpaEntity.builder()
                        .criadaEm(agora)
                        .ativa(false)
                        .nomeDisciplina("Personalizada")
                        .build();
        tPreenchido.aoCriar();
        assertEquals(agora, tPreenchido.getCriadaEm());
        assertFalse(tPreenchido.getAtiva());
        assertEquals("Personalizada", tPreenchido.getNomeDisciplina());
    }

    @Test
    @DisplayName("Deve testar TurmaAlunoJpaEntity, TurmaAlunoIdJpa e aoMatricular")
    void deveTestarTurmaAlunoJpaEntity() {
        TurmaAlunoIdJpa id1 = new TurmaAlunoIdJpa(10L, 20L);
        TurmaAlunoIdJpa id2 = new TurmaAlunoIdJpa(10L, 20L);
        TurmaAlunoIdJpa id3 = new TurmaAlunoIdJpa(10L, 30L);
        TurmaAlunoIdJpa id4 = new TurmaAlunoIdJpa(20L, 20L);
        TurmaAlunoIdJpa idVazio = new TurmaAlunoIdJpa();
        idVazio.setTurmaId(10L);
        idVazio.setAlunoId(20L);

        assertEquals(id1, id2);
        assertEquals(id1, id1);
        assertNotEquals(id1, id3);
        assertNotEquals(id1, id4);
        assertNotEquals(id1, null);
        assertNotEquals(id1, new Object());
        assertEquals(id1.hashCode(), id2.hashCode());
        assertEquals(10L, id1.getTurmaId());
        assertEquals(20L, id1.getAlunoId());

        TurmaJpaEntity t = TurmaJpaEntity.builder().id(10L).build();
        UsuarioJpaEntity u = UsuarioJpaEntity.builder().id(20L).build();

        TurmaAlunoJpaEntity ta1 = new TurmaAlunoJpaEntity();
        ta1.setId(id1);
        ta1.setTurma(t);
        ta1.setAluno(u);
        ta1.setMatriculadoEm(LocalDateTime.now());

        TurmaAlunoJpaEntity ta2 = TurmaAlunoJpaEntity.builder().id(id1).build();
        TurmaAlunoJpaEntity ta3 = new TurmaAlunoJpaEntity(id3, t, u, LocalDateTime.now());

        assertEquals(ta1, ta2);
        assertNotEquals(ta1, ta3);
        assertEquals(ta1.hashCode(), ta2.hashCode());
        assertEquals(t, ta1.getTurma());
        assertEquals(u, ta1.getAluno());
        assertNotNull(ta1.getMatriculadoEm());

        // aoMatricular()
        TurmaAlunoJpaEntity taNovo = new TurmaAlunoJpaEntity();
        taNovo.aoMatricular();
        assertNotNull(taNovo.getMatriculadoEm());

        LocalDateTime fixo = LocalDateTime.of(2026, 1, 1, 0, 0);
        TurmaAlunoJpaEntity taPreenchido =
                TurmaAlunoJpaEntity.builder().matriculadoEm(fixo).build();
        taPreenchido.aoMatricular();
        assertEquals(fixo, taPreenchido.getMatriculadoEm());
    }

    @Test
    @DisplayName("Deve testar ModuloGttJpaEntity e aoCriar")
    void deveTestarModuloGttJpaEntity() {
        ModuloGttJpaEntity m1 = new ModuloGttJpaEntity();
        m1.setId(1L);
        m1.setCodigo("MED");
        m1.setNome("Medicamentos");
        m1.setDescricao("Desc");
        m1.setAtivo(true);
        m1.setCriadoEm(LocalDateTime.now());
        m1.setGatilhos(new ArrayList<>());

        ModuloGttJpaEntity m2 = ModuloGttJpaEntity.builder().id(1L).build();
        ModuloGttJpaEntity m3 =
                new ModuloGttJpaEntity(
                        2L,
                        "CIR",
                        "Cirurgico",
                        "Desc",
                        true,
                        LocalDateTime.now(),
                        new ArrayList<>());

        assertEquals(m1, m2);
        assertNotEquals(m1, m3);
        assertEquals(m1.hashCode(), m2.hashCode());
        assertEquals("MED", m1.getCodigo());
        assertEquals("Medicamentos", m1.getNome());
        assertEquals("Desc", m1.getDescricao());
        assertTrue(m1.getAtivo());
        assertNotNull(m1.getGatilhos());

        // aoCriar()
        ModuloGttJpaEntity mNovo = new ModuloGttJpaEntity();
        mNovo.setAtivo(null);
        mNovo.aoCriar();
        assertNotNull(mNovo.getCriadoEm());
        assertTrue(mNovo.getAtivo());

        LocalDateTime fixo = LocalDateTime.of(2026, 1, 1, 0, 0);
        ModuloGttJpaEntity mPreenchido =
                ModuloGttJpaEntity.builder().criadoEm(fixo).ativo(false).build();
        mPreenchido.aoCriar();
        assertEquals(fixo, mPreenchido.getCriadoEm());
        assertFalse(mPreenchido.getAtivo());
    }

    @Test
    @DisplayName("Deve testar GatilhoGttJpaEntity")
    void deveTestarGatilhoGttJpaEntity() {
        ModuloGttJpaEntity mod = ModuloGttJpaEntity.builder().id(1L).build();
        GatilhoGttJpaEntity g1 = new GatilhoGttJpaEntity();
        g1.setId(10L);
        g1.setCodigo("M1");
        g1.setModulo(mod);
        g1.setDescricao("Vitamina K");
        g1.setLimiarReferencia("INR > 5");
        g1.setAtivo(true);

        GatilhoGttJpaEntity g2 = GatilhoGttJpaEntity.builder().id(10L).build();
        GatilhoGttJpaEntity g3 = new GatilhoGttJpaEntity(20L, "M2", mod, "Desc", "Lim", true);

        assertEquals(g1, g2);
        assertNotEquals(g1, g3);
        assertEquals(g1.hashCode(), g2.hashCode());
        assertEquals("M1", g1.getCodigo());
        assertEquals(mod, g1.getModulo());
        assertEquals("Vitamina K", g1.getDescricao());
        assertEquals("INR > 5", g1.getLimiarReferencia());
        assertTrue(g1.getAtivo());
    }

    @Test
    @DisplayName("Deve testar CategoriaEventoAdversoJpaEntity")
    void deveTestarCategoriaEventoAdversoJpaEntity() {
        CategoriaEventoAdversoJpaEntity c1 = new CategoriaEventoAdversoJpaEntity();
        c1.setId(1L);
        c1.setNome("IRAS");
        c1.setDefinicaoOperacional("Infecção");
        c1.setAtiva(true);

        CategoriaEventoAdversoJpaEntity c2 =
                CategoriaEventoAdversoJpaEntity.builder().id(1L).build();
        CategoriaEventoAdversoJpaEntity c3 =
                new CategoriaEventoAdversoJpaEntity(2L, "Queda", "Dano", true);

        assertEquals(c1, c2);
        assertNotEquals(c1, c3);
        assertEquals(c1.hashCode(), c2.hashCode());
        assertEquals("IRAS", c1.getNome());
        assertEquals("Infecção", c1.getDefinicaoOperacional());
        assertTrue(c1.getAtiva());
    }

    @Test
    @DisplayName("Deve testar CasoClinicoJpaEntity e aoCriar")
    void deveTestarCasoClinicoJpaEntity() {
        UsuarioJpaEntity prof = UsuarioJpaEntity.builder().id(1L).build();
        UnidadeHospitalarJpaEntity unid = UnidadeHospitalarJpaEntity.builder().id(2L).build();

        CasoClinicoJpaEntity c1 = new CasoClinicoJpaEntity();
        c1.setId(100L);
        c1.setProfessorCriador(prof);
        c1.setUnidadeHospitalar(unid);
        c1.setTitulo("Sepse");
        c1.setDescricaoCaso("Desc");
        c1.setObjetivosAprendizagem("Obj");
        c1.setNumeroAtendimento("ATD");
        c1.setIdadePaciente(60);
        c1.setDataAdmissao(LocalDate.now());
        c1.setDataAlta(LocalDate.now().plusDays(3));
        c1.setTempoPermanenciaDias(3);
        c1.setSumarioAlta("Sum");
        c1.setPrescricoesMedicas("Presc");
        c1.setExamesLaboratoriais("Exames");
        c1.setRelatorioCirurgico("Cir");
        c1.setEvolucoesMultiprofissionais("Evol");
        c1.setCriadoEm(LocalDateTime.now());

        CasoClinicoJpaEntity c2 = CasoClinicoJpaEntity.builder().id(100L).build();
        CasoClinicoJpaEntity c3 =
                new CasoClinicoJpaEntity(
                        200L,
                        prof,
                        unid,
                        "Choque",
                        "Desc",
                        "Obj",
                        "ATD2",
                        50,
                        LocalDate.now(),
                        LocalDate.now(),
                        1,
                        "S",
                        "P",
                        "E",
                        "C",
                        "Ev",
                        LocalDateTime.now());

        assertEquals(c1, c2);
        assertNotEquals(c1, c3);
        assertEquals(c1.hashCode(), c2.hashCode());
        assertEquals("Sepse", c1.getTitulo());
        assertEquals(prof, c1.getProfessorCriador());
        assertEquals(unid, c1.getUnidadeHospitalar());
        assertEquals("Desc", c1.getDescricaoCaso());
        assertEquals("Obj", c1.getObjetivosAprendizagem());
        assertEquals("ATD", c1.getNumeroAtendimento());
        assertEquals(60, c1.getIdadePaciente());
        assertNotNull(c1.getDataAdmissao());
        assertNotNull(c1.getDataAlta());
        assertEquals(3, c1.getTempoPermanenciaDias());
        assertEquals("Sum", c1.getSumarioAlta());
        assertEquals("Presc", c1.getPrescricoesMedicas());
        assertEquals("Exames", c1.getExamesLaboratoriais());
        assertEquals("Cir", c1.getRelatorioCirurgico());
        assertEquals("Evol", c1.getEvolucoesMultiprofissionais());

        // aoCriar()
        CasoClinicoJpaEntity cNovo = new CasoClinicoJpaEntity();
        cNovo.aoCriar();
        assertNotNull(cNovo.getCriadoEm());

        LocalDateTime fixo = LocalDateTime.of(2026, 1, 1, 0, 0);
        CasoClinicoJpaEntity cPreenchido = CasoClinicoJpaEntity.builder().criadoEm(fixo).build();
        cPreenchido.aoCriar();
        assertEquals(fixo, cPreenchido.getCriadoEm());
    }

    @Test
    @DisplayName("Deve testar AtividadeEducacionalJpaEntity e aoCriar")
    void deveTestarAtividadeEducacionalJpaEntity() {
        TurmaJpaEntity t = TurmaJpaEntity.builder().id(10L).build();
        CasoClinicoJpaEntity c = CasoClinicoJpaEntity.builder().id(20L).build();

        AtividadeEducacionalJpaEntity a1 = new AtividadeEducacionalJpaEntity();
        a1.setId(1L);
        a1.setTurma(t);
        a1.setCasoClinico(c);
        a1.setTitulo("Atividade 1");
        a1.setOrientacoesPedagogicas("Ori");
        a1.setDataInicio(LocalDateTime.now());
        a1.setDataFim(LocalDateTime.now().plusDays(5));
        a1.setTempoLimiteMinutos(30);
        a1.setAtiva(true);
        a1.setCriadaEm(LocalDateTime.now());

        AtividadeEducacionalJpaEntity a2 = AtividadeEducacionalJpaEntity.builder().id(1L).build();
        AtividadeEducacionalJpaEntity a3 =
                new AtividadeEducacionalJpaEntity(
                        2L,
                        t,
                        c,
                        "Atividade 2",
                        "Ori",
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        20,
                        true,
                        LocalDateTime.now());

        assertEquals(a1, a2);
        assertNotEquals(a1, a3);
        assertEquals(a1.hashCode(), a2.hashCode());
        assertEquals(t, a1.getTurma());
        assertEquals(c, a1.getCasoClinico());
        assertEquals("Atividade 1", a1.getTitulo());
        assertEquals("Ori", a1.getOrientacoesPedagogicas());
        assertNotNull(a1.getDataInicio());
        assertNotNull(a1.getDataFim());
        assertEquals(30, a1.getTempoLimiteMinutos());
        assertTrue(a1.getAtiva());

        // aoCriar()
        AtividadeEducacionalJpaEntity aNovo = new AtividadeEducacionalJpaEntity();
        aNovo.setAtiva(null);
        aNovo.setTempoLimiteMinutos(null);
        aNovo.aoCriar();
        assertNotNull(aNovo.getCriadaEm());
        assertTrue(aNovo.getAtiva());
        assertEquals(20, aNovo.getTempoLimiteMinutos());

        LocalDateTime fixo = LocalDateTime.of(2026, 1, 1, 0, 0);
        AtividadeEducacionalJpaEntity aPreenchido =
                AtividadeEducacionalJpaEntity.builder()
                        .criadaEm(fixo)
                        .ativa(false)
                        .tempoLimiteMinutos(45)
                        .build();
        aPreenchido.aoCriar();
        assertEquals(fixo, aPreenchido.getCriadaEm());
        assertFalse(aPreenchido.getAtiva());
        assertEquals(45, aPreenchido.getTempoLimiteMinutos());
    }

    @Test
    @DisplayName("Deve testar SubmissaoAtividadeJpaEntity e aoCriar")
    void deveTestarSubmissaoAtividadeJpaEntity() {
        AtividadeEducacionalJpaEntity ativ =
                AtividadeEducacionalJpaEntity.builder().id(10L).build();
        UsuarioJpaEntity aluno = UsuarioJpaEntity.builder().id(20L).build();
        UsuarioJpaEntity prof = UsuarioJpaEntity.builder().id(30L).build();

        SubmissaoAtividadeJpaEntity s1 = new SubmissaoAtividadeJpaEntity();
        s1.setId(100L);
        s1.setAtividade(ativ);
        s1.setAluno(aluno);
        s1.setStatus(StatusSubmissao.AVALIADA);
        s1.setTempoGastoSegundos(500);
        s1.setDataInicio(LocalDateTime.now());
        s1.setDataSubmissao(LocalDateTime.now());
        s1.setProfessorCorretor(prof);
        s1.setNota(new BigDecimal("9.00"));
        s1.setParecerDocente("Bom");
        s1.setDataAvaliacao(LocalDateTime.now());
        s1.setAchadosGatilhos(new ArrayList<>());
        s1.setIshikawa(null);
        s1.setPlanos5w3h(new ArrayList<>());
        s1.setPdca(null);

        SubmissaoAtividadeJpaEntity s2 = SubmissaoAtividadeJpaEntity.builder().id(100L).build();
        SubmissaoAtividadeJpaEntity s3 =
                new SubmissaoAtividadeJpaEntity(
                        200L,
                        ativ,
                        aluno,
                        StatusSubmissao.SUBMETIDA,
                        400,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        prof,
                        new BigDecimal("8.00"),
                        "Ok",
                        LocalDateTime.now(),
                        new ArrayList<>(),
                        null,
                        new ArrayList<>(),
                        null);

        assertEquals(s1, s2);
        assertNotEquals(s1, s3);
        assertEquals(s1.hashCode(), s2.hashCode());
        assertEquals(ativ, s1.getAtividade());
        assertEquals(aluno, s1.getAluno());
        assertEquals(StatusSubmissao.AVALIADA, s1.getStatus());
        assertEquals(500, s1.getTempoGastoSegundos());
        assertNotNull(s1.getDataInicio());
        assertNotNull(s1.getDataSubmissao());
        assertEquals(prof, s1.getProfessorCorretor());
        assertEquals(new BigDecimal("9.00"), s1.getNota());
        assertEquals("Bom", s1.getParecerDocente());
        assertNotNull(s1.getDataAvaliacao());
        assertNotNull(s1.getAchadosGatilhos());
        assertNotNull(s1.getPlanos5w3h());

        // aoCriar()
        SubmissaoAtividadeJpaEntity sNovo = new SubmissaoAtividadeJpaEntity();
        sNovo.setStatus(null);
        sNovo.setTempoGastoSegundos(null);
        sNovo.aoCriar();
        assertNotNull(sNovo.getDataInicio());
        assertEquals(StatusSubmissao.EM_ANDAMENTO, sNovo.getStatus());
        assertEquals(0, sNovo.getTempoGastoSegundos());

        LocalDateTime fixo = LocalDateTime.of(2026, 1, 1, 0, 0);
        SubmissaoAtividadeJpaEntity sPreenchido =
                SubmissaoAtividadeJpaEntity.builder()
                        .dataInicio(fixo)
                        .status(StatusSubmissao.SUBMETIDA)
                        .tempoGastoSegundos(300)
                        .build();
        sPreenchido.aoCriar();
        assertEquals(fixo, sPreenchido.getDataInicio());
        assertEquals(StatusSubmissao.SUBMETIDA, sPreenchido.getStatus());
        assertEquals(300, sPreenchido.getTempoGastoSegundos());
    }

    @Test
    @DisplayName("Deve testar SubmissaoGatilhoJpaEntity")
    void deveTestarSubmissaoGatilhoJpaEntity() {
        SubmissaoAtividadeJpaEntity sub = SubmissaoAtividadeJpaEntity.builder().id(100L).build();
        GatilhoGttJpaEntity gat = GatilhoGttJpaEntity.builder().id(10L).build();
        CategoriaEventoAdversoJpaEntity cat =
                CategoriaEventoAdversoJpaEntity.builder().id(20L).build();

        SubmissaoGatilhoJpaEntity g1 = new SubmissaoGatilhoJpaEntity();
        g1.setId(1L);
        g1.setSubmissao(sub);
        g1.setGatilho(gat);
        g1.setCategoriaEventoAdverso(cat);
        g1.setConfirmouDano(true);
        g1.setJustificativaDano("Dano");
        g1.setDanoPresenteAdmissao(false);
        g1.setGravidade(GravidadeNccMerp.CATEGORIA_E);

        SubmissaoGatilhoJpaEntity g2 = SubmissaoGatilhoJpaEntity.builder().id(1L).build();
        SubmissaoGatilhoJpaEntity g3 =
                new SubmissaoGatilhoJpaEntity(
                        2L, sub, gat, cat, false, "Sem dano", true, GravidadeNccMerp.CATEGORIA_F);

        assertEquals(g1, g2);
        assertNotEquals(g1, g3);
        assertEquals(g1.hashCode(), g2.hashCode());
        assertEquals(sub, g1.getSubmissao());
        assertEquals(gat, g1.getGatilho());
        assertEquals(cat, g1.getCategoriaEventoAdverso());
        assertTrue(g1.getConfirmouDano());
        assertEquals("Dano", g1.getJustificativaDano());
        assertFalse(g1.getDanoPresenteAdmissao());
        assertEquals(GravidadeNccMerp.CATEGORIA_E, g1.getGravidade());
    }

    @Test
    @DisplayName("Deve testar SubmissaoIshikawaJpaEntity")
    void deveTestarSubmissaoIshikawaJpaEntity() {
        SubmissaoAtividadeJpaEntity sub = SubmissaoAtividadeJpaEntity.builder().id(100L).build();

        SubmissaoIshikawaJpaEntity i1 = new SubmissaoIshikawaJpaEntity();
        i1.setId(1L);
        i1.setSubmissao(sub);
        i1.setEfeitoPrincipal("E");
        i1.setMetodo("M1");
        i1.setMaoDeObra("M2");
        i1.setMaterial("M3");
        i1.setMedida("M4");
        i1.setMeioAmbiente("A");
        i1.setMaquina("M5");

        SubmissaoIshikawaJpaEntity i2 = SubmissaoIshikawaJpaEntity.builder().id(1L).build();
        SubmissaoIshikawaJpaEntity i3 =
                new SubmissaoIshikawaJpaEntity(2L, sub, "E", "M1", "M2", "M3", "M4", "A", "M5");

        assertEquals(i1, i2);
        assertNotEquals(i1, i3);
        assertEquals(i1.hashCode(), i2.hashCode());
        assertEquals(sub, i1.getSubmissao());
        assertEquals("E", i1.getEfeitoPrincipal());
        assertEquals("M1", i1.getMetodo());
        assertEquals("M2", i1.getMaoDeObra());
        assertEquals("M3", i1.getMaterial());
        assertEquals("M4", i1.getMedida());
        assertEquals("A", i1.getMeioAmbiente());
        assertEquals("M5", i1.getMaquina());
    }

    @Test
    @DisplayName("Deve testar SubmissaoPlano5w3hJpaEntity")
    void deveTestarSubmissaoPlano5w3hJpaEntity() {
        SubmissaoAtividadeJpaEntity sub = SubmissaoAtividadeJpaEntity.builder().id(100L).build();

        SubmissaoPlano5w3hJpaEntity p1 = new SubmissaoPlano5w3hJpaEntity();
        p1.setId(1L);
        p1.setSubmissao(sub);
        p1.setOQue("O");
        p1.setPorQue("P");
        p1.setQuem("Q");
        p1.setOnde("On");
        p1.setQuando("Qn");
        p1.setComo("C");
        p1.setQuantoCusta(new BigDecimal("100.00"));
        p1.setComoMedir("M");

        SubmissaoPlano5w3hJpaEntity p2 = SubmissaoPlano5w3hJpaEntity.builder().id(1L).build();
        SubmissaoPlano5w3hJpaEntity p3 =
                new SubmissaoPlano5w3hJpaEntity(
                        2L, sub, "O", "P", "Q", "On", "Qn", "C", new BigDecimal("100.00"), "M");

        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertEquals(sub, p1.getSubmissao());
        assertEquals("O", p1.getOQue());
        assertEquals("P", p1.getPorQue());
        assertEquals("Q", p1.getQuem());
        assertEquals("On", p1.getOnde());
        assertEquals("Qn", p1.getQuando());
        assertEquals("C", p1.getComo());
        assertEquals(new BigDecimal("100.00"), p1.getQuantoCusta());
        assertEquals("M", p1.getComoMedir());
    }

    @Test
    @DisplayName("Deve testar SubmissaoPdcaJpaEntity")
    void deveTestarSubmissaoPdcaJpaEntity() {
        SubmissaoAtividadeJpaEntity sub = SubmissaoAtividadeJpaEntity.builder().id(100L).build();

        SubmissaoPdcaJpaEntity p1 = new SubmissaoPdcaJpaEntity();
        p1.setId(1L);
        p1.setSubmissao(sub);
        p1.setPlanejar("P");
        p1.setFazer("D");
        p1.setChecar("C");
        p1.setAgir("A");

        SubmissaoPdcaJpaEntity p2 = SubmissaoPdcaJpaEntity.builder().id(1L).build();
        SubmissaoPdcaJpaEntity p3 = new SubmissaoPdcaJpaEntity(2L, sub, "P", "D", "C", "A");

        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertEquals(sub, p1.getSubmissao());
        assertEquals("P", p1.getPlanejar());
        assertEquals("D", p1.getFazer());
        assertEquals("C", p1.getChecar());
        assertEquals("A", p1.getAgir());
    }
}
