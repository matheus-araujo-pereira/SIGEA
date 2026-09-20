---
name: deploy-homologacao
description: >-
  Runbook de homologação, conteinerização Docker/Podman e deploy em nuvem (Render, Vercel, Neon.tech).
  Use esta skill quando o usuário solicitar deploy, verificação de containers locais,
  configuração de banco PostgreSQL ou solução de problemas de infraestrutura e CORS.
---

# Skill: Deploy e Homologação (Docker, Render, Vercel & Neon)

Este guia padroniza o ciclo de vida de deploy e teste integrado dos ambientes do SIGEA-GTT.

---

## 1. Execução Local Completa via Docker

Para testar a aplicação de ponta a ponta com banco PostgreSQL 16 local:

1. Inicie todos os containers em segundo plano:
   ```bash
   docker compose -f config/docker/docker-compose.yml up --build -d
   ```
2. Acompanhe os logs dos serviços:
   - Backend: `docker compose -f config/docker/docker-compose.yml logs -f sigea-backend`
   - Frontend: `docker compose -f config/docker/docker-compose.yml logs -f sigea-frontend`
   - Banco: `docker compose -f config/docker/docker-compose.yml logs -f sigea-db`
3. Validação dos endpoints locais:
   - Frontend: `http://localhost:80`
   - Backend API: `http://localhost:8080/api`
   - Healthcheck: `http://localhost:8080/actuator/health`
4. Encerramento do ambiente:
   ```bash
   docker compose -f config/docker/docker-compose.yml down
   ```

---

## 2. Deploy em Homologação em Nuvem

- **Banco de Dados (Neon.tech)**:
  - PostgreSQL Serverless 16.
  - Parâmetro obrigatório na connection string: `?sslmode=require`.
- **Backend (Render.com)**:
  - Build Command: `./mvnw clean package -DskipTests`
  - Start Command: `java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -jar target/*.jar`
  - Variáveis de ambiente:
    - `SPRING_PROFILES_ACTIVE`: `prod`
    - `SPRING_DATASOURCE_URL`: `jdbc:postgresql://<neon-host>/<db-name>?sslmode=require`
    - `SPRING_DATASOURCE_USERNAME`: `<user>`
    - `SPRING_DATASOURCE_PASSWORD`: `<password>`
    - `JWT_SECRET`: `<chave-secreta-forte-256-bits>`
- **Frontend (Vercel)**:
  - Framework Preset: Angular
  - Root Directory: `frontend`
  - Build Command: `npm run build`
  - Output Directory: `dist/sigea-gtt-frontend/browser`
  - O arquivo `config/deploy/vercel.json` gerencia o roteamento e proxy para o backend no Render.
