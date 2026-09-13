package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.adapter;

import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.output.UsuarioRepositoryPort;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity.UsuarioJpaEntity;
import br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.repository.UsuarioSpringDataRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Adaptador de persistência para a entidade {@link Usuario}, implementando {@link
 * UsuarioRepositoryPort}.
 */
@Component
public class UsuarioPersistenceAdapter implements UsuarioRepositoryPort {

    private final UsuarioSpringDataRepository repository;

    public UsuarioPersistenceAdapter(UsuarioSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Usuario> listarTodos() {
        return repository.findAll().stream().map(UsuarioPersistenceAdapter::paraDominio).toList();
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return repository.findById(id).map(UsuarioPersistenceAdapter::paraDominio);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        if (email == null) return Optional.empty();
        return repository.findByEmail(email).map(UsuarioPersistenceAdapter::paraDominio);
    }

    @Override
    public Optional<Usuario> buscarPorMatriculaSigaa(String matriculaSigaa) {
        if (matriculaSigaa == null) return Optional.empty();
        return repository
                .findByMatriculaSigaa(matriculaSigaa)
                .map(UsuarioPersistenceAdapter::paraDominio);
    }

    @Override
    public Optional<Usuario> buscarPorEmailEIdDiferente(String email, Long id) {
        if (email == null || id == null) return Optional.empty();
        return repository
                .findByEmailAndIdNot(email, id)
                .map(UsuarioPersistenceAdapter::paraDominio);
    }

    @Override
    public Optional<Usuario> buscarPorMatriculaEIdDiferente(String matriculaSigaa, Long id) {
        if (matriculaSigaa == null || id == null) return Optional.empty();
        return repository
                .findByMatriculaSigaaAndIdNot(matriculaSigaa, id)
                .map(UsuarioPersistenceAdapter::paraDominio);
    }

    @Override
    public long contarPorPerfilEAtivo(PerfilUsuario perfil) {
        return repository.countByPerfilAndAtivoTrue(perfil);
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        UsuarioJpaEntity entity = paraEntidade(usuario);
        UsuarioJpaEntity saved = repository.save(entity);
        return paraDominio(saved);
    }

    public static Usuario paraDominio(UsuarioJpaEntity entity) {
        if (entity == null) return null;
        return new Usuario(
                entity.getId(),
                entity.getNomeCompleto(),
                entity.getEmail(),
                entity.getSenha(),
                entity.getPrimeiroAcesso(),
                entity.getMatriculaSigaa(),
                entity.getPerfil(),
                entity.getAtivo(),
                entity.getCriadoEm());
    }

    public static UsuarioJpaEntity paraEntidade(Usuario domain) {
        if (domain == null) return null;
        return UsuarioJpaEntity.builder()
                .id(domain.getId())
                .nomeCompleto(domain.getNomeCompleto())
                .email(domain.getEmail())
                .senha(domain.getSenha())
                .primeiroAcesso(domain.getPrimeiroAcesso())
                .matriculaSigaa(domain.getMatriculaSigaa())
                .perfil(domain.getPerfil())
                .ativo(domain.getAtivo())
                .criadoEm(domain.getCriadoEm())
                .build();
    }
}
