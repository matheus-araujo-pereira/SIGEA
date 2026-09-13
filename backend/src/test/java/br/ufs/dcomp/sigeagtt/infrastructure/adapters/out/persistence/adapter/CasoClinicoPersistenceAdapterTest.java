package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.ufs.dcomp.sigeagtt.domain.model.CasoClinico;
import br.ufs.dcomp.sigeagtt.domain.model.UnidadeHospitalar;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.CasoClinicoJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.UnidadeHospitalarJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.UsuarioJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.CasoClinicoSpringDataRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CasoClinicoPersistenceAdapterTest {

    @Mock private CasoClinicoSpringDataRepository repository;

    @InjectMocks private CasoClinicoPersistenceAdapter adapter;

    private CasoClinicoJpaEntity criarEntidade(Long id) {
        UsuarioJpaEntity prof = UsuarioJpaEntity.builder().id(1L).nomeCompleto("Prof").build();
        UnidadeHospitalarJpaEntity unid =
                UnidadeHospitalarJpaEntity.builder().id(2L).sigla("UTI-A").build();
        return CasoClinicoJpaEntity.builder()
                .id(id)
                .professorCriador(prof)
                .unidadeHospitalar(unid)
                .titulo("Caso Sepse")
                .descricaoCaso("Desc")
                .objetivosAprendizagem("Obj")
                .numeroAtendimento("ATD100")
                .idadePaciente(50)
                .dataAdmissao(LocalDate.of(2026, 1, 1))
                .dataAlta(LocalDate.of(2026, 1, 5))
                .tempoPermanenciaDias(4)
                .sumarioAlta("Sum")
                .prescricoesMedicas("Presc")
                .examesLaboratoriais("Exames")
                .relatorioCirurgico("Cir")
                .evolucoesMultiprofissionais("Evol")
                .criadoEm(LocalDateTime.now())
                .build();
    }

    private CasoClinico criarDominio(Long id) {
        Usuario prof = new Usuario();
        prof.setId(1L);
        UnidadeHospitalar unid = new UnidadeHospitalar();
        unid.setId(2L);
        return new CasoClinico(
                id,
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
    }

    @Test
    @DisplayName("Deve listar todos os casos")
    void deveListarTodos() {
        when(repository.findAllByOrderByCriadoEmDesc()).thenReturn(List.of(criarEntidade(1L)));

        List<CasoClinico> lista = adapter.listarTodos();
        assertEquals(1, lista.size());
        assertEquals("Caso Sepse", lista.get(0).getTitulo());
    }

    @Test
    @DisplayName("Deve listar por professor criador ID")
    void deveListarPorProfessorCriadorId() {
        when(repository.findByProfessorCriadorIdOrderByCriadoEmDesc(1L))
                .thenReturn(List.of(criarEntidade(1L)));

        List<CasoClinico> lista = adapter.listarPorProfessorCriadorId(1L);
        assertEquals(1, lista.size());
        assertTrue(adapter.listarPorProfessorCriadorId(null).isEmpty());
    }

    @Test
    @DisplayName("Deve buscar por ID existente e nulo")
    void deveBuscarPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(criarEntidade(1L)));

        assertTrue(adapter.buscarPorId(1L).isPresent());
        assertTrue(adapter.buscarPorId(null).isEmpty());
    }

    @Test
    @DisplayName("Deve salvar caso clinico")
    void deveSalvar() {
        when(repository.save(any(CasoClinicoJpaEntity.class))).thenReturn(criarEntidade(1L));

        CasoClinico salvo = adapter.salvar(criarDominio(1L));
        assertNotNull(salvo);
        assertEquals(1L, salvo.getId());
    }

    @Test
    @DisplayName("Deve excluir caso clinico")
    void deveExcluir() {
        doNothing().when(repository).deleteById(1L);

        adapter.excluir(1L);
        verify(repository).deleteById(1L);

        adapter.excluir(null);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Deve converter paraDominio e paraEntidade tratando nulos")
    void deveTestarConversoesNulas() {
        assertNull(CasoClinicoPersistenceAdapter.paraDominio(null));
        assertNull(CasoClinicoPersistenceAdapter.paraEntidade(null));

        CasoClinico dom = CasoClinicoPersistenceAdapter.paraDominio(criarEntidade(1L));
        assertNotNull(dom);
        assertEquals("Caso Sepse", dom.getTitulo());

        CasoClinicoJpaEntity ent = CasoClinicoPersistenceAdapter.paraEntidade(criarDominio(1L));
        assertNotNull(ent);
        assertEquals("Caso Sepse", ent.getTitulo());
    }
}
