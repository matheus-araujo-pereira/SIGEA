---
name: quality-gate
description: >-
  Procedimento de verificação estrita do Quality Gate institucional (100% de cobertura de testes e documentação).
  Use esta skill sempre que o usuário solicitar validação de código, antes de concluir qualquer fase,
  após refatorações ou ao preparar commits para garantir zero regressões no backend e frontend.
---

# Skill: Quality Gate (Verificação de 100% de Testes e Documentação)

Esta skill define o protocolo mandatório de verificação de qualidade do SIGEA-GTT.

---

## 1. Passo a Passo de Execução

### Etapa 1: Backend (Java 21 / Spring Boot 3.3.x)
1. Navegue até o diretório `backend/`.
2. Execute o ciclo de compilação, testes e geração de relatórios JaCoCo:
   ```bash
   cd backend && ./mvnw clean verify
   ```
3. Inspecione o resultado do console:
   - Não pode haver falhas em testes (`Failures: 0, Errors: 0, Skipped: 0`).
   - O relatório JaCoCo é gerado em `target/site/jacoco/index.html`.
4. Verifique a compilação do Javadoc:
   ```bash
   ./mvnw javadoc:javadoc
   ```
   Certifique-se de que não haja warnings de parâmetros ou métodos sem documentação.

### Etapa 2: Frontend (Angular 18+)
1. Navegue até o diretório `frontend/`.
2. Execute o build estático de produção para validar tipagem TypeScript e empacotamento:
   ```bash
   cd frontend && npm run build
   ```
3. Execute a suíte de testes unitários com relatório de cobertura Jest:
   ```bash
   npm test -- --coverage
   ```
4. Verifique a tabela de cobertura no terminal:
   - Branches: **100%**
   - Statements: **100%**
   - Functions: **100%**
   - Lines: **100%**

---

## 2. Critérios de Rejeição Imediata
O Quality Gate **REPROVA** automaticamente se:
- Qualquer teste estiver com anotação de skip (`@Disabled`, `xit`, `xdescribe`).
- O e-mail de teste não seguir o padrão `@academico.ufs.br`.
- Houver erro de compilação ou advertência não tratada no Spring Boot ou Angular.
- Métodos públicos de serviço ou endpoints REST estiverem sem Javadoc ou Swagger.
