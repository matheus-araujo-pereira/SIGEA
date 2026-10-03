# SIGEA — Frontend (Angular 18 + TailwindCSS)

SPA do SIGEA. Standalone Components, Signals, TailwindCSS 3.4 e Angular Material.
Projeto gerado com [Angular CLI](https://github.com/angular/angular-cli) 18.2.14. Arquitetura completa em [`../docs/arquitetura.md`](../docs/arquitetura.md).

> Requer **Node.js 20+/22 LTS** e npm 10. O backend deve estar em `http://localhost:8080` (o proxy de dev encaminha `/api/*` — ver `proxy.conf.json`).

## Comandos

| Comando | O que faz |
| :--- | :--- |
| `npm ci` | Instala dependências de forma determinística (use `npm install` apenas para alterar o `package-lock.json`) |
| `npm start` | `ng serve` com proxy → <http://localhost:4200> (hot reload) |
| `npm run build` | Build de produção em `dist/` |
| `npm test` | Jest (jest-preset-angular) |
| `npm test -- --coverage` | Jest com relatório de cobertura (**100% obrigatório**, relatório em `coverage/`) |
| `npm run test:watch` | Jest em modo *watch* |
| `npm run docs:build` | Gera documentação Compodoc em `documentation/` |
| `npm run docs:serve` | Serve a documentação Compodoc em <http://localhost:8081> |

## Convenções

- **Desktop only** (1280×720 a Ultrawide) — sem layout mobile, menu hambúrguer ou navegação inferior.
- Tabelas: 10 registros/página, busca com debounce de 300 ms, ordenação por cabeçalho.
- Cada componente em sua pasta, com `.component.ts`, `.component.html` e `.component.spec.ts`.
- E-mails apenas `@academico.ufs.br`.
- Testes com `HttpTestingController`; **nenhum** `xit`/`xdescribe`/`skip`.

## Referências

- Guia de desenvolvimento: [`../docs/desenvolvimento.md`](../docs/desenvolvimento.md)
- Ambiente local (Beekeeper, Bruno, `scripts/dev.sh`): [`../docs/ambiente-local.md`](../docs/ambiente-local.md)
