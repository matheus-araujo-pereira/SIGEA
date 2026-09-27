# SIGEA — Guia Prático de Desenvolvimento Local

**Instituição**: Universidade Federal de Sergipe (UFS) — DCOMP / Departamento de Enfermagem  
**Autor Líder**: Matheus Araujo Pereira  
**Ambiente Homologado**: Fedora 44 Workstation (x86_64) | Antigravity IDE  

Este guia detalha o processo de configuração, execução, testes e ciclo de desenvolvimento local do ecossistema **SIGEA**.

---

## 🛠️ 1. Requisitos de Ambiente

Para executar e desenvolver o SIGEA localmente, certifique-se de possuir instalado:

| Ferramenta | Versão Mínima | Finalidade | Verificação |
| :--- | :--- | :--- | :--- |
| **Java JDK** | 21 LTS (OpenJDK / Temurin) | Runtime da API Spring Boot | `java -version` |
| **Node.js** | 20 LTS ou 22 LTS | Runtime do frontend Angular | `node -v` |
| **npm** | 10.x | Gerenciador de pacotes frontend | `npm -v` |
| **Podman ou Docker** | 4.x / 24.x | Containerização do PostgreSQL | `podman -v` ou `docker -v` |
| **Git** | 2.40+ | Controle de versionamento | `git --version` |

> [!TIP]
> Em distribuições Linux, recomendamos o uso de [SDKMAN!](https://sdkman.io) para gerenciar o Java 21 (`sdk install java 21.0.6-tem`) e [NVM](https://github.com/nvm-sh/nvm) para gerenciar o Node.js (`nvm install 22`).

---

## 🗄️ 2. Provisionamento do Banco de Dados (PostgreSQL 16)

O banco de dados relacional oficial é o **PostgreSQL 16 LTS**. Você pode executá-lo via container ou conectando a uma instância local nativa.

### Opção A: Execução via Container (Recomendado)
A raiz da configuração de containers reside em `config/docker/docker-compose.yml`, que utiliza volume nomeado persistente (`sigea_postgres_data`) e montagem automática dos scripts em `/docker-entrypoint-initdb.d`:

```bash
# Iniciar o container em segundo plano
podman compose -f config/docker/docker-compose.yml up -d
# ou com docker:
docker compose -f config/docker/docker-compose.yml up -d
```

### Opção B: Carga Manual dos Scripts Canônicos
Caso já possua um PostgreSQL 16 em execução na sua máquina:
```bash
psql -U sigea_admin -d sigea -f database/init_database.sql
```
O script [database/init_database.sql](file:///home/matheus/Projetos/SIGEA-GTT/database/init_database.sql) executa em ordem sequencial:
1. [database/01_schema.sql](file:///home/matheus/Projetos/SIGEA-GTT/database/01_schema.sql): Criação das 10 tabelas, enum `user_role`, extensão `uuid-ossp` e índices.
2. [database/02_seeds.sql](file:///home/matheus/Projetos/SIGEA-GTT/database/02_seeds.sql): Carga dos 6 módulos IHI-GTT, 53 gatilhos clínicos padronizados (100% fieis ao manual do IHI), 9 gravidades NCC MERP e dos 2 administradores padrão.

---

## ☕ 3. Execução da API Backend (Spring Boot 3.3 / Java 21)

1. Acesse o diretório do backend:
   ```bash
   cd backend
   ```
2. Configure as variáveis de ambiente necessárias (caso difiram do padrão `application.yml`):
   ```bash
   export DB_HOST=localhost
   export DB_PORT=5432
   export DB_NAME=sigea
   export DB_USER=sigea_admin
   export DB_PASSWORD=sigea_dev_password
   export JWT_SECRET=sigea-secret-key-ufs-computacao-enfermagem-2026-very-secure-key-32chars
   ```
3. Execute a aplicação via Maven Wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```
4. A API estará disponível em:
   * **URL Base**: `http://localhost:8080/api`
   * **Swagger UI / Documentação OpenAPI**: `http://localhost:8080/swagger-ui.html`
   * **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

---

## 🅰️ 4. Execução do Frontend SPA (Angular 18+)

1. Acesse o diretório do frontend:
   ```bash
   cd frontend
   ```
2. Instale as dependências:
   ```bash
   npm install
   ```
3. Inicie o servidor de desenvolvimento:
   ```bash
   npm start
   # ou: npm run dev
   ```
4. Acesse a aplicação no navegador em:
   * **Aplicação Web**: `http://localhost:4200`

> [!NOTE]
> O proxy de desenvolvimento já está configurado no arquivo `proxy.conf.json` para encaminhar automaticamente chamadas `/api/*` para `http://localhost:8080`, eliminando restrições de CORS no ambiente local.

---

## 🔑 5. Credenciais de Acesso Inicial

O banco é inicializado exclusivamente com as contas dos dois administradores líderes:

| Nome de Exibição | E-mail Institucional | Perfil | Matrícula | Senha Inicial | Status do Acesso |
| :--- | :--- | :---: | :---: | :---: | :---: |
| **Matheus Araujo Pereira** | `matheusaraujopereira@academico.ufs.br` | `ADMIN` | — | `SigeaUFS@2026` | 1º Acesso Pendente |
| **Profª. Drª. Ana Waleska** | `anawaleska@academico.ufs.br` | `ADMIN` | — | `SigeaUFS@2026` | 1º Acesso Pendente |

### Fluxo de Primeiro Acesso
Ao realizar o primeiro login com a senha provisória `SigeaUFS@2026`, o sistema detecta `must_change_password = true`, bloqueia rotas comuns e redireciona para a tela `/first-login`. Após salvar a nova senha, a navegação completa é liberada.

---

## 🛡️ 6. Quality Gate Institucional (Critério de Aceite Estrito)

Nenhum código pode ser commitado ou integrado à branch principal sem atingir **100% de cobertura e zero regressões**:

### Validação Completa do Backend (JUnit 5 + JaCoCo 100%)
```bash
cd backend
./mvnw clean verify
```
* Executa todos os testes unitários e de integração (`342+ testes`).
* Gera o relatório JaCoCo em `backend/target/site/jacoco/index.html`.
* Valida a regra de 100% de cobertura de linhas e branches. O build falha automaticamente se a cobertura cair abaixo de 100%.

### Validação Completa do Frontend (Jest 100%)
```bash
cd frontend
npm test
# Para conferir relatório com branches e linhas:
npm test -- --coverage
```
* Executa todas as suítes de teste de componentes, services e interceptors (`51+ suítes, 472+ testes`).
* Valida 100% de cobertura de branches, funções, linhas e statements.

---

## 📐 7. Regras de Código e Boas Práticas

1. **Domínio de E-mail**: Toda funcionalidade que manipula e-mails deve exigir a terminação `@academico.ufs.br`.
2. **Desktop Only**: Não construa elementos responsivos móveis (menus hambúrguer, navegação inferior de celular). Mantenha alta densidade para estações de trabalho clínicas (1280x720 até Ultrawide).
3. **Padrão de Tabelas**: Exatamente 10 registros por página, com busca com debounce de 300ms e ordenação por cabeçalho.
4. **Modais de Confirmação**: Obrigatórios para exclusões, inativações e finalizações irreversíveis de auditoria.
5. **Documentação de Código**:
   - Backend: Javadoc em 100% das classes, interfaces, métodos de serviço e endpoints SpringDoc.
   - Frontend: JSDoc padronizado para geração de relatórios Compodoc.
