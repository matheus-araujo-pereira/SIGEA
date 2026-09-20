# Regra 01: Stack Tecnológica e Padrões Arquiteturais

## 1. Backend: Java 21 LTS & Spring Boot 3.3.x
- **Linguagem & Compilador**: Java 21 LTS com flags modernas do JDK.
- **Framework**: Spring Boot 3.3.x.
- **Segurança**: Spring Security 6 com autenticação stateless baseada em token JWT (HMAC-SHA256 ou RSA).
  - Todas as requisições autenticadas devem carregar o cabeçalho `Authorization: Bearer <token>`.
  - Controle de autorização granular baseado em papéis (`@PreAuthorize("hasRole('ADMIN')")`).
- **Persistência de Dados**:
  - Spring Data JPA com Hibernate sob PostgreSQL 16 LTS.
  - Toda tabela possui chave primária UUID ou Long sequencial padronizada, timestamps de auditoria (`created_at`, `updated_at`).
  - Migrations controladas 100% via **Flyway** (`backend/src/main/resources/db/migration/V{n}__*.sql`).
  - É expressamente vedado o uso de `ddl-auto: create` ou `update` em produção/homologação.
- **Camadas Arquiteturais**:
  1. `controller`: Controladores REST anotados com `@RestController`, sem lógica de negócio direta. Validação via `@Valid`. Retorno via `ResponseEntity<T>` ou DTO direto.
  2. `service`: Interfaces de serviço e implementações (`*ServiceImpl`) com `@Transactional`. Toda regra de negócio reside exclusivamente nesta camada.
  3. `repository`: Interfaces estendendo `JpaRepository<T, ID>` com queries derivadas ou `@Query` JPQL/HQL tipadas.
  4. `dto`: Records do Java ou classes imutáveis para transferência de dados de entrada (`*RequestDTO`) e saída (`*ResponseDTO`).
  5. `mapper`: Mapeamento entre entidades e DTOs utilizando **MapStruct** com injeção de dependência via Spring (`componentModel = "spring"`).
  6. `exception`: Handlers globais anotados com `@RestControllerAdvice`, retornando instâncias de `ProblemDetail` (RFC 7807) padronizadas.

---

## 2. Frontend: Angular 18+ Standalone & Signals
- **Arquitetura de Componentes**:
  - 100% Standalone Components (`standalone: true`). Nenhum `NgModule` legado deve ser criado.
  - Gerenciamento de estado local e reatividade utilizando **Signals** (`signal()`, `computed()`, `effect()`).
  - Novos blocos de controle de fluxo de template nativos: `@if`, `@else if`, `@else`, `@for (item of items; track item.id)`, `@empty`, `@switch`, `@case`.
- **Formulários**:
  - Uso estrito de **Reactive Forms** (`FormGroup`, `FormControl`, `FormBuilder`).
  - Validações síncronas e assíncronas com mensagens de erro claras e amigáveis ao usuário.
- **Estilização e Componentes Visuais**:
  - Angular Material para componentes estruturais e dialogs.
  - **TailwindCSS** para utilitários de espaçamento, tipografia, grid flexível e controle de cores da paleta institucional.
- **Comunicação HTTP**:
  - Uso de `HttpClient` com interceptores funcionais (`HttpInterceptorFn`) para injeção automática do Bearer token JWT e captura de erros HTTP com exibição de toasts.
  - Modelos fortemente tipados em TypeScript espelhando exatamente os DTOs do backend.

---

## 3. Banco de Dados: PostgreSQL 16 LTS
- Instância local em container Docker/Podman ou nuvem (Neon.tech Serverless Postgres).
- Codificação UTF-8, timezone `America/Maceio` / UTC.
- Nomes de tabelas em caixa baixa no plural (`gtt_triggers`, `gtt_audits`, `app_users`).
- Nomes de colunas em snake_case (`first_name`, `is_active`, `must_change_password`).
