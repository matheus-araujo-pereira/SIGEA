package br.ufs.dcomp.sigeagtt.infrastructure.security;

import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.output.TokenServicePort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.UsuarioRepositoryPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Filtro de autenticação JWT executado uma única vez por requisição HTTP. */
@Component
public class FiltroAutenticacaoToken extends OncePerRequestFilter {

    private final TokenServicePort tokenServico;
    private final UsuarioRepositoryPort usuarioRepositorio;

    public FiltroAutenticacaoToken(
            TokenServicePort tokenServico, UsuarioRepositoryPort usuarioRepositorio) {
        this.tokenServico = tokenServico;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String cabecalhoAuth = request.getHeader("Authorization");

        if (cabecalhoAuth != null && cabecalhoAuth.startsWith("Bearer ")) {
            String token = cabecalhoAuth.substring(7).trim();
            TokenServicePort.DadosToken dados = tokenServico.validarToken(token);

            if (dados != null) {
                Usuario usuario =
                        dados.id() != null
                                ? usuarioRepositorio.buscarPorId(dados.id()).orElse(null)
                                : usuarioRepositorio.buscarPorEmail(dados.email()).orElse(null);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                usuario != null ? usuario : dados.email(),
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + dados.perfil())));
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(auth);
                SecurityContextHolder.setContext(context);
            }
        }

        filterChain.doFilter(request, response);
    }
}
