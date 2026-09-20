# Regra 05: Quality Gate, Cobertura de Testes e Documentação

## 1. Diretriz Inegociável do Quality Gate
Nenhuma fase, funcionalidade ou branch de desenvolvimento poderá ser mesclada ou homologada sem atingir **100% de conformidade** nos pilares:
1. Funcionalidade integral.
2. Cobertura de testes automatizados (unitários e integração).
3. Documentação técnica de código e APIs.

---

## 2. Testes no Backend (Java 21 / Spring Boot 3.3.x)
- **Frameworks**: JUnit 5 (Jupiter), Mockito, MockMvc, AssertJ.
- **Estrutura de Testes**:
  - **Serviços (`*ServiceTest`)**: Testes unitários com mocks de repositórios e serviços auxiliares. Todos os caminhos felizes e de exceção devem ser cobertos.
  - **Controladores (`*ControllerTest`)**: Testes de camada web com `@WebMvcTest` ou `MockMvc`, validando códigos de status HTTP (200, 201, 204, 400, 401, 403, 404, 422), headers de resposta e payload JSON.
  - **Mappers & DTOs**: Garantir que todas as propriedades sejam mapeadas sem valores nulos indesejados.
  - **Segurança**: Testes verificando o bloqueio de acesso a endpoints protegidos quando sem token JWT ou com perfil insuficiente.
- **Verificação via JaCoCo**:
  ```bash
  cd backend && ./mvnw clean verify
  ```
  O relatório gerado em `target/site/jacoco/index.html` deve comprovar 100% de cobertura de branches e linhas nos pacotes de negócio.

---

## 3. Testes no Frontend (Angular 18+)
- **Framework**: Jest (com `jest-preset-angular`).
- **Escopo**:
  - **Componentes**: Ciclo de vida, renderização de templates com Signals, disparos de eventos de clique e submissão de formulários reativos.
  - **Serviços**: Requisições HTTP mockadas via `HttpTestingController`, mapeamento de respostas e tratamento de falhas.
  - **Guards e Interceptors**: Validação de redirecionamento de rotas não autorizadas e injeção do token JWT.
- **Verificação de Cobertura**:
  ```bash
  cd frontend && npm test -- --coverage
  ```
  O relatório deve indicar 100% em Statements, Branches, Functions e Lines.

---

## 4. Documentação de Código e OpenAPI
1. **Javadoc**:
   - 100% das classes públicas, interfaces, métodos de serviço, DTOs e controladores devem conter Javadoc descritivo em português técnico.
   - Tags `@param`, `@return` e `@throws` detalhadas.
2. **SpringDoc OpenAPI 3 / Swagger UI**:
   - Cada endpoint anotado com `@Operation(summary = "...", description = "...")`.
   - Mapeamento explícito de `@ApiResponses` incluindo respostas de erro (400, 403, 404, 500).
   - Documentação de schemas e exemplos nos DTOs com `@Schema`.
3. **Compodoc**:
   - Frontend preparado para geração de documentação de componentes e serviços via Compodoc sem advertências críticas.
