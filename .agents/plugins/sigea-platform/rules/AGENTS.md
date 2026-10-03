# SIGEA Platform Plugin — Normas Executivas e Diretrizes de Engenharia

**Instituição**: Universidade Federal de Sergipe (UFS) — DCOMP / Departamento de Enfermagem  
**Autor Líder**: Matheus Araujo Pereira | Matrícula: 202100114080  
**Orientadores**: Profª. Drª. Ana Waleska de Menezes Seixas Souza · Prof. Dr. Gilton José Ferreira da Silva  
**Aprovação Ética**: CEP/UFS — CAAE nº 91836925.8.0000.5546  

---

## 1. Diretrizes de Escopo e Arquitetura

1. **Intocabilidade do Documento Aprovado**: O diretório `projeto_departamental/` encontra-se aprovado institucionalmente e é estritamente de somente-leitura.
2. **Stack Tecnológica Homologada**:
   - Backend: Java 21 LTS + Spring Boot 3.3.6.
   - Frontend: Angular 18+ (Standalone Components, Signals, componentes nativos instalados, zero CSS customizado ad-hoc).
   - Banco de Dados: PostgreSQL 16 LTS (suporte completo a JSONB, UUID e índices GIN) / Neon.tech Serverless.
   - Deploy: NeonDB (banco com SSL), Render (backend containerizado), Vercel (SPA Angular com proxy reverso).
3. **Protocolo IHI Global Trigger Tool**:
   - 6 módulos especializados: Cuidados (15, C1-C15), Medicação (13, M1-M13), Cirúrgico (11, S1-S11), UTI (4, I1-I4), Perinatal (8, P1-P8), Urgência (2, E1-E2) — Total: 53 gatilhos padronizados.
   - Taxonomia NCC MERP: Categorias A a D (sem dano físico) e E a I (com dano físico / eventos adversos).
   - Taxas epidemiológicas: EAs por 1.000 pacientes-dia, % de admissões com dano, EAs por 100 admissões.
4. **Regras de Negócio e Segurança**:
   - Domínio institucional exclusivo: `@academico.ufs.br`.
   - Perfis de acesso (RBAC): `ADMIN`, `PROFESSOR`, `STUDENT`.
   - Campo matrícula: obrigatório exclusivamente para `STUDENT`; nulo para os demais.
   - Primeiro acesso: redirecionamento mandatório para redefinição de senha (`must_change_password = true`).
5. **Ergonomia Visual Hospitalar**:
   - Resoluções de desktop: WUXGA (1920×1200), HD+ (1600×900), HD (1280×720) e Ultrawide (21:9).
   - Tabelas padronizadas com exatamente 10 registros por página.
6. **Quality Gate Estrito**:
   - 100% de testes automatizados no backend (JUnit 5 + Mockito + JaCoCo) e no frontend (Jest).
   - 0 testes ignorados, pulados ou desabilitados.
