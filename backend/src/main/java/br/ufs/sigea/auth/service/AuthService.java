package br.ufs.sigea.auth.service;

import br.ufs.sigea.auth.dto.ChangePasswordDTO;
import br.ufs.sigea.auth.dto.FirstLoginChangePasswordDTO;
import br.ufs.sigea.auth.dto.LoginRequestDTO;
import br.ufs.sigea.auth.dto.LoginResponseDTO;
import br.ufs.sigea.common.exception.BusinessException;
import br.ufs.sigea.common.exception.ResourceNotFoundException;
import br.ufs.sigea.user.domain.User;
import br.ufs.sigea.user.mapper.UserMapper;
import br.ufs.sigea.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Serviço de autenticação e fluxos de segurança de senha.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    /**
     * Autentica o usuário com suas credenciais e retorna o token JWT.
     *
     * @param dto Credenciais de acesso
     * @return Resposta com token JWT e dados do usuário
     */
    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO dto) {
        String email = dto.getEmail().trim().toLowerCase();

        if (!email.endsWith("@academico.ufs.br")) {
            throw new BadCredentialsException("Apenas e-mails institucionais @academico.ufs.br são permitidos.");
        }

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadCredentialsException("E-mail institucional ou senha incorretos."));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("E-mail institucional ou senha incorretos.");
        }

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new DisabledException("Usuário inativo no sistema. Entre em contato com a coordenação do SIGEA-GTT.");
        }

        String token = jwtTokenService.generateToken(user);
        log.info("Usuário autenticado com sucesso: email={}, role={}, mustChangePassword={}",
                user.getEmail(), user.getRole(), user.getMustChangePassword());

        return LoginResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .user(userMapper.toResponseDTO(user))
                .build();
    }

    /**
     * Redefine a senha provisória de forma obrigatória no primeiro acesso.
     *
     * @param userId Identificador do usuário autenticado
     * @param dto    Senhas atual e nova
     * @return Resposta com novo token JWT atualizado
     */
    @Transactional
    public LoginResponseDTO firstLoginChangePassword(UUID userId, FirstLoginChangePasswordDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + userId));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("A senha provisória atual informada está incorreta.");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException("A nova senha e a confirmação de senha não conferem.");
        }

        if (passwordEncoder.matches(dto.getNewPassword(), user.getPasswordHash())) {
            throw new BusinessException("A nova senha não pode ser idêntica à senha provisória.");
        }

        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        user.setMustChangePassword(false);
        User savedUser = userRepository.save(user);

        log.info("Senha de primeiro acesso alterada com sucesso para o usuário id={}", userId);

        String newToken = jwtTokenService.generateToken(savedUser);
        return LoginResponseDTO.builder()
                .token(newToken)
                .tokenType("Bearer")
                .user(userMapper.toResponseDTO(savedUser))
                .build();
    }

    /**
     * Altera a senha voluntariamente pelo próprio usuário autenticado.
     *
     * @param userId Identificador do usuário
     * @param dto    Senhas atual e nova
     */
    @Transactional
    public void changePassword(UUID userId, ChangePasswordDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + userId));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("A senha atual informada está incorreta.");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException("A nova senha e a confirmação de senha não conferem.");
        }

        if (passwordEncoder.matches(dto.getNewPassword(), user.getPasswordHash())) {
            throw new BusinessException("A nova senha não pode ser idêntica à senha atual.");
        }

        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        log.info("Senha alterada com sucesso pelo próprio usuário id={}", userId);
    }
}
