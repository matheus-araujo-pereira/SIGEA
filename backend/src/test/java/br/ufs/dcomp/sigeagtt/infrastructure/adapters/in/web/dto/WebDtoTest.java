package br.ufs.dcomp.sigeagtt.infrastructure.adapters.in.web.dto;

import static org.junit.jupiter.api.Assertions.*;

import br.ufs.dcomp.sigeagtt.domain.model.AtividadeEducacional;
import br.ufs.dcomp.sigeagtt.domain.model.CasoClinico;
import br.ufs.dcomp.sigeagtt.domain.model.CategoriaEventoAdverso;
import br.ufs.dcomp.sigeagtt.domain.model.GatilhoGtt;
import br.ufs.dcomp.sigeagtt.domain.model.GravidadeNccMerp;
import br.ufs.dcomp.sigeagtt.domain.model.ModuloGtt;
import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.StatusSubmissao;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoAtividade;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoGatilho;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoIshikawa;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoPdca;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoPlano5w3h;
import br.ufs.dcomp.sigeagtt.domain.model.Turma;
import br.ufs.dcomp.sigeagtt.domain.model.UnidadeHospitalar;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.input.AtividadeEducacionalUseCase.DadosPainelAtividade;
import br.ufs.dcomp.sigeagtt.domain.ports.input.AtividadeEducacionalUseCase.ItemAtividadeResumo;
import br.ufs.dcomp.sigeagtt.domain.ports.input.AtividadeEducacionalUseCase.ProgressoAluno;
import br.ufs.dcomp.sigeagtt.domain.ports.input.SubmissaoAtividadeUseCase.DadosSalvarSubmissao;
import br.ufs.dcomp.sigeagtt.domain.ports.input.SubmissaoAtividadeUseCase.ItemMinhaAtividade;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WebDtoTest {

        @Test
        @DisplayName("Deve cobrir UsuarioRespostaDTO, UsuarioRequisicaoDTO e UsuarioEdicaoDTO")
        void deveCobrirDtosUsuario() {
                Usuario u = new Usuario();
                u.setId(10L);
                u.setNomeCompleto("João Silva");
                u.setEmail("joao@academico.ufs.br");
                u.setMatriculaSigaa("202612345678");
                u.setPerfil(PerfilUsuario.ALUNO);
                u.setPrimeiroAcesso(true);
                u.setAtivo(true);
                u.setCriadoEm(LocalDateTime.now());

                UsuarioRespostaDTO resp = UsuarioRespostaDTO.deEntidade(u);
                assertEquals(10L, resp.id());
                assertEquals("João Silva", resp.nomeCompleto());
                assertEquals("joao@academico.ufs.br", resp.email());
                assertEquals("202612345678", resp.matriculaSigaa());
                assertEquals(PerfilUsuario.ALUNO, resp.perfil());
                assertTrue(resp.primeiroAcesso());
                assertTrue(resp.ativo());
                assertNotNull(resp.criadoEm());

                UsuarioRequisicaoDTO req = new UsuarioRequisicaoDTO(
                                "Nome", "email@academico.ufs.br", "202612345678", PerfilUsuario.ALUNO);
                assertEquals("Nome", req.nomeCompleto());
                assertEquals("email@academico.ufs.br", req.email());
                assertEquals("202612345678", req.matriculaSigaa());
                assertEquals(PerfilUsuario.ALUNO, req.perfil());

                UsuarioEdicaoDTO ed = new UsuarioEdicaoDTO(
                                "Novo", "novo@academico.ufs.br", "202612345678", PerfilUsuario.ALUNO);
                assertEquals("Novo", ed.nomeCompleto());
                assertEquals("novo@academico.ufs.br", ed.email());
                assertEquals("202612345678", ed.matriculaSigaa());
                assertEquals(PerfilUsuario.ALUNO, ed.perfil());
        }

        @Test
        @DisplayName("Deve cobrir AlterarSenhaDTO, PrimeiroAcessoRequisicaoDTO, LoginRequisicaoDTO e LoginRespostaDTO")
        void deveCobrirDtosAutenticacao() {
                AlterarSenhaDTO alt = new AlterarSenhaDTO("antiga", "nova", "nova");
                assertEquals("antiga", alt.senhaAtual());
                assertEquals("nova", alt.novaSenha());
                assertEquals("nova", alt.confirmacaoNovaSenha());

                PrimeiroAcessoRequisicaoDTO prim = new PrimeiroAcessoRequisicaoDTO(1L, "atual", "nova", "nova");
                assertEquals(1L, prim.usuarioId());
                assertEquals("atual", prim.senhaAtual());
                assertEquals("nova", prim.novaSenha());
                assertEquals("nova", prim.confirmacaoNovaSenha());

                LoginRequisicaoDTO logReq = new LoginRequisicaoDTO("email@academico.ufs.br", "senha");
                assertEquals("email@academico.ufs.br", logReq.email());
                assertEquals("senha", logReq.senha());

                Usuario u = new Usuario();
                u.setId(1L);
                u.setNomeCompleto("Usuário Teste");
                u.setEmail("teste@ufs.br");
                u.setMatriculaSigaa("12345");
                u.setPerfil(PerfilUsuario.ADMINISTRADOR);
                u.setPrimeiroAcesso(false);
                u.setAtivo(true);

                LoginRespostaDTO logResp = LoginRespostaDTO.deEntidade(u, "token123");
                assertEquals("token123", logResp.token());
                assertEquals(1L, logResp.id());
                assertEquals("Usuário Teste", logResp.nomeCompleto());
                assertEquals("teste@ufs.br", logResp.email());
                assertEquals("12345", logResp.matriculaSigaa());
                assertEquals(PerfilUsuario.ADMINISTRADOR, logResp.perfil());
                assertFalse(logResp.primeiroAcesso());
                assertTrue(logResp.ativo());

                LoginRespostaDTO logResp2 = LoginRespostaDTO.deEntidade(u);
                assertNull(logResp2.token());
                assertEquals(1L, logResp2.id());
        }

        @Test
        @DisplayName("Deve cobrir UnidadeHospitalarRespostaDTO e UnidadeHospitalarRequisicaoDTO")
        void deveCobrirDtosUnidadeHospitalar() {
                UnidadeHospitalar u = new UnidadeHospitalar(1L, "UTI Adulto", "UTI-A", true);
                UnidadeHospitalarRespostaDTO resp = UnidadeHospitalarRespostaDTO.deEntidade(u);
                assertEquals(1L, resp.id());
                assertEquals("UTI Adulto", resp.nome());
                assertEquals("UTI-A", resp.sigla());
                assertTrue(resp.ativa());

                UnidadeHospitalarRequisicaoDTO req = new UnidadeHospitalarRequisicaoDTO("UTI-A", "UTI Adulto");
                assertEquals("UTI-A", req.sigla());
                assertEquals("UTI Adulto", req.nome());
        }

        @Test
        @DisplayName("Deve cobrir TurmaRespostaDTO e TurmaRequisicaoDTO")
        void deveCobrirDtosTurma() {
                Usuario prof = new Usuario();
                prof.setId(2L);
                prof.setNomeCompleto("Dr. Marcos");

                Turma t = new Turma();
                t.setId(10L);
                t.setProfessorResponsavel(prof);
                t.setCodigoDisciplina("MED001");
                t.setPeriodoLetivo("2026.1");
                t.setAnoSemestre("2026/1");
                t.setAtiva(true);
                t.setCriadaEm(LocalDateTime.now());

                TurmaRespostaDTO resp = TurmaRespostaDTO.deEntidade(t, 25L);
                assertEquals(10L, resp.id());
                assertEquals(2L, resp.professorResponsavelId());
                assertEquals("Dr. Marcos", resp.professorResponsavelNome());
                assertEquals("MED001", resp.codigoDisciplina());
                assertEquals(25L, resp.totalAlunos());

                // Caso de professor nulo
                t.setProfessorResponsavel(null);
                TurmaRespostaDTO resp2 = TurmaRespostaDTO.deEntidade(t, 0L);
                assertNull(resp2.professorResponsavelId());
                assertNull(resp2.professorResponsavelNome());

                TurmaRequisicaoDTO req = new TurmaRequisicaoDTO(2L, "MED001", "2026.1", "2026/1");
                assertEquals(2L, req.professorResponsavelId());
                assertEquals("MED001", req.codigoDisciplina());
                assertEquals("2026.1", req.periodoLetivo());
                assertEquals("2026/1", req.anoSemestre());
        }

        @Test
        @DisplayName("Deve cobrir ModuloGttRespostaDTO e ModuloGttRequisicaoDTO")
        void deveCobrirDtosModuloGtt() {
                ModuloGtt m = new ModuloGtt(1L, "CUIDADOS", "Cuidados Gerais", "Desc", true, LocalDateTime.now());
                ModuloGttRespostaDTO resp = ModuloGttRespostaDTO.deEntidade(m);
                assertEquals(1L, resp.id());
                assertEquals("CUIDADOS", resp.codigo());
                assertEquals("Cuidados Gerais", resp.nome());
                assertEquals("Desc", resp.descricao());
                assertTrue(resp.ativo());

                ModuloGttRequisicaoDTO req = new ModuloGttRequisicaoDTO("CUIDADOS", "Cuidados Gerais", "Desc");
                assertEquals("CUIDADOS", req.codigo());
                assertEquals("Cuidados Gerais", req.nome());
                assertEquals("Desc", req.descricao());
        }

        @Test
        @DisplayName("Deve cobrir GatilhoGttRespostaDTO e GatilhoGttRequisicaoDTO")
        void deveCobrirDtosGatilhoGtt() {
                ModuloGtt mod = new ModuloGtt(5L, "MED", "Medicamentos", "", true, null);
                GatilhoGtt g = new GatilhoGtt(1L, "M1", mod, "Vitamina K", "INR > 5", true);

                GatilhoGttRespostaDTO resp = GatilhoGttRespostaDTO.deEntidade(g);
                assertEquals(1L, resp.id());
                assertEquals("M1", resp.codigo());
                assertEquals(5L, resp.moduloId());
                assertEquals("Medicamentos", resp.moduloNome());
                assertEquals("Vitamina K", resp.descricao());
                assertEquals("INR > 5", resp.limiarReferencia());
                assertTrue(resp.ativo());

                // Gatilho sem modulo
                g.setModulo(null);
                GatilhoGttRespostaDTO resp2 = GatilhoGttRespostaDTO.deEntidade(g);
                assertNull(resp2.moduloId());
                assertNull(resp2.moduloNome());

                GatilhoGttRequisicaoDTO req = new GatilhoGttRequisicaoDTO(5L, "M1", "Vitamina K", "INR > 5");
                assertEquals(5L, req.moduloId());
                assertEquals("M1", req.codigo());
                assertEquals("Vitamina K", req.descricao());
                assertEquals("INR > 5", req.limiarReferencia());
        }

        @Test
        @DisplayName("Deve cobrir CasoClinicoDTO e SalvarCasoClinicoDTO")
        void deveCobrirDtosCasoClinico() {
                assertNull(CasoClinicoDTO.deEntidade(null));

                Usuario prof = new Usuario();
                prof.setId(1L);
                prof.setNomeCompleto("Dr. Silva");

                UnidadeHospitalar unid = new UnidadeHospitalar(2L, "UTI Adulto", "UTI-A", true);

                CasoClinico c = new CasoClinico(
                                100L,
                                prof,
                                unid,
                                "Caso Sepse",
                                "Desc",
                                "Obj",
                                "ATD100",
                                50,
                                LocalDate.of(2026, 1, 1),
                                LocalDate.of(2026, 1, 5),
                                4,
                                "Sum",
                                "Presc",
                                "Exames",
                                "Cir",
                                "Evol",
                                LocalDateTime.now());

                CasoClinicoDTO dto = CasoClinicoDTO.deEntidade(c);
                assertNotNull(dto);
                assertEquals(100L, dto.id());
                assertEquals(1L, dto.professorCriadorId());
                assertEquals("Dr. Silva", dto.professorCriadorNome());
                assertEquals(2L, dto.unidadeHospitalarId());
                assertEquals("UTI Adulto", dto.unidadeHospitalarNome());
                assertEquals("UTI-A", dto.unidadeHospitalarSigla());
                assertEquals("Caso Sepse", dto.titulo());

                // Caso com prof e unidade nulos
                c.setProfessorCriador(null);
                c.setUnidadeHospitalar(null);
                CasoClinicoDTO dto2 = CasoClinicoDTO.deEntidade(c);
                assertNull(dto2.professorCriadorId());
                assertNull(dto2.unidadeHospitalarId());

                SalvarCasoClinicoDTO salvarDto = new SalvarCasoClinicoDTO(
                                2L,
                                "Caso Sepse",
                                "Desc",
                                "Obj",
                                "ATD100",
                                50,
                                LocalDate.of(2026, 1, 1),
                                LocalDate.of(2026, 1, 5),
                                4,
                                "Sum",
                                "Presc",
                                "Exames",
                                "Cir",
                                "Evol");
                assertEquals(2L, salvarDto.unidadeHospitalarId());
                assertEquals("Caso Sepse", salvarDto.titulo());
        }

        @Test
        @DisplayName("Deve cobrir AtividadeEducacionalDTO e SalvarAtividadeDTO")
        void deveCobrirDtosAtividadeEducacional() {
                Usuario prof = new Usuario();
                prof.setId(1L);
                prof.setNomeCompleto("Dr. Silva");

                Turma t = new Turma();
                t.setId(10L);
                t.setCodigoDisciplina("MED001");
                t.setPeriodoLetivo("2026.1");
                t.setProfessorResponsavel(prof);

                UnidadeHospitalar unid = new UnidadeHospitalar(2L, "UTI Adulto", "UTI-A", true);
                CasoClinico c = new CasoClinico();
                c.setId(100L);
                c.setTitulo("Caso Choque");
                c.setUnidadeHospitalar(unid);

                AtividadeEducacional a = new AtividadeEducacional();
                a.setId(50L);
                a.setTurma(t);
                a.setCasoClinico(c);
                a.setTitulo("Auditoria 1");
                a.setOrientacoesPedagogicas("Orientações");
                a.setDataInicio(LocalDateTime.now());
                a.setDataFim(LocalDateTime.now().plusDays(5));
                a.setTempoLimiteMinutos(20);
                a.setAtiva(true);
                a.setCriadaEm(LocalDateTime.now());

                AtividadeEducacionalDTO dto = AtividadeEducacionalDTO.deEntidade(a, 30, 25, 20);
                assertEquals(50L, dto.id());
                assertEquals(10L, dto.turmaId());
                assertEquals("MED001", dto.turmaCodigoDisciplina());
                assertEquals("MED001", dto.turmaCodigo());
                assertEquals("Segurança do Paciente e Auditoria Clínica", dto.turmaDisciplina());
                assertEquals("2026.1", dto.turmaPeriodoLetivo());
                assertEquals("2026.1", dto.periodoLetivo());
                assertEquals("Dr. Silva", dto.professorResponsavelNome());
                assertEquals(100L, dto.casoClinicoId());
                assertEquals("Caso Choque", dto.casoClinicoTitulo());
                assertEquals("UTI-A", dto.casoClinicoUnidadeSigla());
                assertEquals(30, dto.totalAlunos());
                assertEquals(30, dto.totalAlunosTurma());
                assertEquals(25, dto.totalSubmissoes());
                assertEquals(20, dto.totalAvaliadas());

                // Testar com campos aninhados nulos
                a.setTurma(null);
                a.setCasoClinico(null);
                AtividadeEducacionalDTO dto2 = AtividadeEducacionalDTO.deEntidade(a, 0, 0, 0);
                assertNull(dto2.turmaId());
                assertNull(dto2.casoClinicoId());

                // Testar turma com professorResponsavel nulo e casoClinico com
                // unidadeHospitalar nulo
                Turma turmaSemProf = new Turma();
                turmaSemProf.setId(11L);
                turmaSemProf.setProfessorResponsavel(null);
                CasoClinico casoSemUnid = new CasoClinico();
                casoSemUnid.setId(101L);
                casoSemUnid.setUnidadeHospitalar(null);
                a.setTurma(turmaSemProf);
                a.setCasoClinico(casoSemUnid);
                AtividadeEducacionalDTO dto3 = AtividadeEducacionalDTO.deEntidade(a, 1, 1, 1);
                assertEquals(11L, dto3.turmaId());
                assertNull(dto3.professorResponsavelNome());
                assertEquals(101L, dto3.casoClinicoId());
                assertNull(dto3.casoClinicoUnidadeSigla());

                SalvarAtividadeDTO salvarDto = new SalvarAtividadeDTO(
                                10L,
                                100L,
                                "Auditoria 1",
                                "Ori",
                                LocalDateTime.now(),
                                LocalDateTime.now().plusDays(5),
                                20,
                                true);
                assertEquals(10L, salvarDto.turmaId());
                assertEquals(100L, salvarDto.casoClinicoId());
                assertTrue(salvarDto.ativa());
        }

        @Test
        @DisplayName("Deve cobrir PainelAtividadeDTO, AlunoProgressoDTO e MinhaAtividadeItemDTO")
        void deveCobrirPainelEProgressoDtos() {
                assertNull(PainelAtividadeDTO.deDadosPainel(null));

                AtividadeEducacional a = new AtividadeEducacional();
                a.setId(1L);
                ItemAtividadeResumo atvResumo = new ItemAtividadeResumo(a, 20, 15, 10);
                CasoClinico caso = new CasoClinico();
                caso.setId(2L);

                ProgressoAluno prog = new ProgressoAluno(
                                10L,
                                "Aluno Teste",
                                "aluno@academico.ufs.br",
                                "202612345678",
                                50L,
                                StatusSubmissao.SUBMETIDA,
                                600,
                                LocalDateTime.now(),
                                new BigDecimal("9.0"),
                                "Parecer",
                                LocalDateTime.now());

                DadosPainelAtividade dados = new DadosPainelAtividade(
                                atvResumo, caso, 20, 15, 5, 10, new BigDecimal("9.00"), List.of(prog));

                PainelAtividadeDTO painelDto = PainelAtividadeDTO.deDadosPainel(dados);
                assertNotNull(painelDto);
                assertEquals(20, painelDto.totalAlunosTurma());
                assertEquals(15, painelDto.totalSubmissoes());
                assertEquals(5, painelDto.totalPendentesCorrecao());
                assertEquals(10, painelDto.totalAvaliadas());
                assertEquals(new BigDecimal("9.00"), painelDto.mediaNotas());
                assertEquals(1, painelDto.alunos().size());

                // Painel com alunos nulo
                DadosPainelAtividade dadosSemAlunos = new DadosPainelAtividade(
                                atvResumo, caso, 20, 15, 5, 10, new BigDecimal("9.00"), null);
                PainelAtividadeDTO painelSemAlunos = PainelAtividadeDTO.deDadosPainel(dadosSemAlunos);
                assertNotNull(painelSemAlunos);
                assertTrue(painelSemAlunos.alunos().isEmpty());

                // MinhaAtividadeItemDTO
                assertNull(MinhaAtividadeItemDTO.deItemMinhaAtividade(null));

                ItemMinhaAtividade itemMinha = new ItemMinhaAtividade(
                                1L,
                                "Titulo",
                                10L,
                                "MED001",
                                "Segurança",
                                "Dr. Silva",
                                100L,
                                "Caso",
                                "UTI",
                                LocalDateTime.now(),
                                LocalDateTime.now().plusDays(5),
                                20,
                                50L,
                                StatusSubmissao.AVALIADA,
                                new BigDecimal("8.50"),
                                500,
                                LocalDateTime.now(),
                                LocalDateTime.now());

                MinhaAtividadeItemDTO minhaDto = MinhaAtividadeItemDTO.deItemMinhaAtividade(itemMinha);
                assertNotNull(minhaDto);
                assertEquals(1L, minhaDto.atividadeId());
                assertEquals(StatusSubmissao.AVALIADA, minhaDto.status());
                assertEquals(new BigDecimal("8.50"), minhaDto.nota());
        }

        @Test
        @DisplayName("Deve cobrir SubmissaoDTO e componentes Ishikawa, 5W3H, PDCA, Gatilho")
        void deveCobrirDtosSubmissaoEComponentes() {
                assertNull(SubmissaoDTO.deEntidade(null));
                assertNull(SubmissaoGatilhoDTO.deEntidade(null));
                assertNull(SubmissaoIshikawaDTO.deEntidade(null));
                assertNull(SubmissaoPlano5w3hDTO.deEntidade(null));
                assertNull(SubmissaoPdcaDTO.deEntidade(null));

                AtividadeEducacional a = new AtividadeEducacional();
                a.setId(10L);
                a.setTitulo("Auditoria 10");
                Turma t = new Turma();
                t.setNomeDisciplina("Segurança do Paciente");
                Usuario prof = new Usuario();
                prof.setId(2L);
                prof.setNomeCompleto("Dr. Carlos");
                t.setProfessorResponsavel(prof);
                a.setTurma(t);

                CasoClinico c = new CasoClinico();
                c.setId(20L);
                a.setCasoClinico(c);

                Usuario aluno = new Usuario();
                aluno.setId(1L);
                aluno.setNomeCompleto("Discente Teste");
                aluno.setMatriculaSigaa("202612345678");
                aluno.setEmail("aluno@academico.ufs.br");

                SubmissaoAtividade sub = new SubmissaoAtividade(a, aluno);
                sub.setId(100L);
                sub.setStatus(StatusSubmissao.AVALIADA);
                sub.setProfessorCorretor(prof);
                sub.setNota(new BigDecimal("9.50"));
                sub.setParecerDocente("Muito bom");
                sub.setDataAvaliacao(LocalDateTime.now());

                ModuloGtt mod = new ModuloGtt(1L, "CUIDADOS", "Cuidados Gerais", "", true, null);
                GatilhoGtt gat = new GatilhoGtt(1L, "C1", mod, "PCR", null, true);
                CategoriaEventoAdverso cat = new CategoriaEventoAdverso(1L, "IRAS", "Infecção", true);
                SubmissaoGatilho achado = new SubmissaoGatilho(
                                1L,
                                sub,
                                gat,
                                cat,
                                true,
                                "Dano confirmado",
                                false,
                                GravidadeNccMerp.CATEGORIA_E);
                sub.setAchadosGatilhos(List.of(achado));

                SubmissaoIshikawa ish = new SubmissaoIshikawa(1L, sub, "Efeito", "Met", "Mao", "Mat", "Med", "Amb",
                                "Maq");
                sub.setIshikawa(ish);

                SubmissaoPlano5w3h p = new SubmissaoPlano5w3h(
                                1L,
                                sub,
                                "O que",
                                "Por que",
                                "Quem",
                                "Onde",
                                "Quando",
                                "Como",
                                new BigDecimal("100"),
                                "Medir");
                sub.setPlanos5w3h(List.of(p));

                SubmissaoPdca pdca = new SubmissaoPdca(1L, sub, "P", "D", "C", "A");
                sub.setPdca(pdca);

                SubmissaoDTO dto = SubmissaoDTO.deEntidade(sub);
                assertNotNull(dto);
                assertEquals(100L, dto.id());
                assertEquals(10L, dto.atividadeId());
                assertEquals("Auditoria 10", dto.atividadeTitulo());
                assertEquals("Segurança do Paciente", dto.disciplinaNome());
                assertEquals("Dr. Carlos", dto.professorNome());
                assertEquals(1L, dto.alunoId());
                assertEquals("Discente Teste", dto.alunoNome());
                assertEquals(2L, dto.professorCorretorId());
                assertEquals("Dr. Carlos", dto.professorCorretorNome());
                assertEquals(new BigDecimal("9.50"), dto.nota());
                assertEquals("Muito bom", dto.parecerDocente());
                assertEquals(1, dto.achadosGatilhos().size());
                assertNotNull(dto.ishikawa());
                assertEquals(1, dto.planos5w3h().size());
                assertNotNull(dto.pdca());

                // SubmissaoDTO com campos internos nulos
                SubmissaoAtividade subVazia = new SubmissaoAtividade();
                SubmissaoDTO dtoVazio = SubmissaoDTO.deEntidade(subVazia);
                assertNotNull(dtoVazio);
                assertNull(dtoVazio.atividadeId());
                assertNull(dtoVazio.alunoId());
                assertNull(dtoVazio.professorCorretorId());
                assertTrue(dtoVazio.achadosGatilhos().isEmpty());
                assertTrue(dtoVazio.planos5w3h().isEmpty());

                // SubmissaoDTO com campos intermediários nulos (atividade com turma sem
                // professor e sem
                // caso clínico)
                AtividadeEducacional atvParcial = new AtividadeEducacional();
                atvParcial.setId(15L);
                atvParcial.setTitulo("Atividade Parcial");
                Turma turmaSemProfSub = new Turma();
                turmaSemProfSub.setNomeDisciplina("Disciplina Teste");
                turmaSemProfSub.setProfessorResponsavel(null);
                atvParcial.setTurma(turmaSemProfSub);
                atvParcial.setCasoClinico(null);

                SubmissaoAtividade subIntermediaria = new SubmissaoAtividade();
                subIntermediaria.setId(102L);
                subIntermediaria.setAtividade(atvParcial);
                subIntermediaria.setProfessorCorretor(null);
                subIntermediaria.setAchadosGatilhos(null);
                subIntermediaria.setPlanos5w3h(null);

                SubmissaoDTO dtoInter = SubmissaoDTO.deEntidade(subIntermediaria);
                assertNotNull(dtoInter);
                assertEquals(15L, dtoInter.atividadeId());
                assertEquals("Disciplina Teste", dtoInter.disciplinaNome());
                assertNull(dtoInter.professorNome());
                assertNull(dtoInter.casoClinico());
                assertNull(dtoInter.professorCorretorId());
                assertNull(dtoInter.professorCorretorNome());
                assertTrue(dtoInter.achadosGatilhos().isEmpty());
                assertTrue(dtoInter.planos5w3h().isEmpty());

                // Gatilho sem GatilhoGtt e sem CategoriaEventoAdverso
                SubmissaoGatilho achadoVazio = new SubmissaoGatilho();
                SubmissaoGatilhoDTO achadoDto = SubmissaoGatilhoDTO.deEntidade(achadoVazio);
                assertNotNull(achadoDto);
                assertNull(achadoDto.gatilhoId());
                assertNull(achadoDto.categoriaEaId());

                // Gatilho presente porém com modulo nulo, e categoria nula
                GatilhoGtt gatSemModulo = new GatilhoGtt();
                gatSemModulo.setId(88L);
                gatSemModulo.setCodigo("G88");
                gatSemModulo.setDescricao("Gatilho sem modulo");
                gatSemModulo.setModulo(null);
                SubmissaoGatilho achadoSemModulo = new SubmissaoGatilho();
                achadoSemModulo.setId(201L);
                achadoSemModulo.setGatilho(gatSemModulo);
                achadoSemModulo.setCategoriaEventoAdverso(null);
                SubmissaoGatilhoDTO achadoDto2 = SubmissaoGatilhoDTO.deEntidade(achadoSemModulo);
                assertNotNull(achadoDto2);
                assertEquals(88L, achadoDto2.gatilhoId());
                assertNull(achadoDto2.moduloCodigo());
                assertNull(achadoDto2.moduloNome());
                assertNull(achadoDto2.categoriaEaId());
                assertNull(achadoDto2.categoriaEaNome());

                // DTOs de submissão auxiliares
                AvaliarSubmissaoDTO avalDto = new AvaliarSubmissaoDTO(new BigDecimal("9.00"), "Parecer");
                assertEquals(new BigDecimal("9.00"), avalDto.nota());
                assertEquals("Parecer", avalDto.parecerDocente());

                // SalvarSubmissaoDTO completo
                SubmissaoGatilhoDTO gatDto = new SubmissaoGatilhoDTO(
                                1L,
                                10L,
                                "M1",
                                "Naloxona",
                                "MED",
                                "Medicamentos",
                                2L,
                                "Erro",
                                true,
                                "Dano",
                                false,
                                GravidadeNccMerp.CATEGORIA_E);
                SubmissaoIshikawaDTO ishDto = new SubmissaoIshikawaDTO("E", "M", "M", "M", "M", "A", "M");
                SubmissaoPlano5w3hDTO planoDto = new SubmissaoPlano5w3hDTO(
                                1L, "O", "P", "Q", "O", "Q", "C", new BigDecimal("10"), "M");
                SubmissaoPdcaDTO pdcaDto = new SubmissaoPdcaDTO("P", "D", "C", "A");

                SalvarSubmissaoDTO salvSubDto = new SalvarSubmissaoDTO(
                                1200, true, List.of(gatDto), ishDto, List.of(planoDto), pdcaDto);
                assertTrue(salvSubDto.finalizar());
                assertEquals(1200, salvSubDto.tempoGastoSegundos());

                DadosSalvarSubmissao cmd = salvSubDto.paraComando();
                assertTrue(cmd.finalizar());
                assertEquals(1200, cmd.tempoGastoSegundos());
                assertEquals(1, cmd.gatilhos().size());
                assertNotNull(cmd.ishikawa());
                assertEquals(1, cmd.planos5w3h().size());
                assertNotNull(cmd.pdca());

                // SalvarSubmissaoDTO com finalizar = false
                SalvarSubmissaoDTO salvSubFalso = new SalvarSubmissaoDTO(600, false, null, null, null, null);
                DadosSalvarSubmissao cmdFalso = salvSubFalso.paraComando();
                assertFalse(cmdFalso.finalizar());
                assertEquals(600, cmdFalso.tempoGastoSegundos());

                // SalvarSubmissaoDTO com campos nulos
                SalvarSubmissaoDTO salvSubNulo = new SalvarSubmissaoDTO(null, null, null, null, null, null);
                DadosSalvarSubmissao cmdNulo = salvSubNulo.paraComando();
                assertFalse(cmdNulo.finalizar());
                assertEquals(0, cmdNulo.tempoGastoSegundos());
                assertTrue(cmdNulo.gatilhos().isEmpty());
                assertNull(cmdNulo.ishikawa());
                assertTrue(cmdNulo.planos5w3h().isEmpty());
                assertNull(cmdNulo.pdca());

                // ErroRespostaDTO
                ErroRespostaDTO erro1 = new ErroRespostaDTO(LocalDateTime.now(), 400, "Erro", "Mensagem");
                assertEquals(400, erro1.status());
                assertEquals("Erro", erro1.erro());
                assertEquals("Mensagem", erro1.mensagem());
                assertNull(erro1.campos());

                ErroRespostaDTO erro2 = new ErroRespostaDTO(
                                LocalDateTime.now(), 400, "Erro", "Mensagem", Map.of("campo", "erro"));
                assertNotNull(erro2.campos());
        }
}
