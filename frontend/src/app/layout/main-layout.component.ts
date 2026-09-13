import { Component, inject, computed } from '@angular/core';
import { Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AutenticacaoService } from '../modulos/autenticacao/servicos/autenticacao.service';
import { ButtonModule } from 'primeng/button';

export interface ItemMenu {
  rota: string;
  rotulo: string;
}

export interface GrupoMenu {
  titulo: string;
  itens: ItemMenu[];
}

@Component({
  selector: 'app-main-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, ButtonModule],
  template: `
    <div class="layout-raiz">
      <!-- SIDEBAR FIXA 260px -->
      <aside class="menu-lateral-fixo">
        <!-- TOPO: IDENTIFICAÇÃO INSTITUCIONAL -->
        <div class="sidebar-brand">
          <div class="brand-title">SIGEA-GTT</div>
          <div class="brand-subtitle">Hospital Universitário HU-UFS</div>
        </div>

        <!-- CENTRO: NAVEGAÇÃO TEXTUAL CENTRALIZADA -->
        <nav class="sidebar-nav">
          @for (grupo of gruposMenu(); track grupo.titulo) {
            <div class="nav-group-title">{{ grupo.titulo }}</div>
            @for (item of grupo.itens; track item.rota) {
              <a
                [routerLink]="item.rota"
                routerLinkActive="active"
                [routerLinkActiveOptions]="{ exact: item.rota === '/atividades' }"
                class="nav-link"
              >
                <span>{{ item.rotulo }}</span>
              </a>
            }
          }
        </nav>

        <!-- BASE INFERIOR: USUÁRIO, PERFIL E SAIR -->
        <div class="sidebar-user-block">
          <div class="user-info">
            <span class="user-name" [title]="nomeUsuario()">{{ nomeUsuario() }}</span>
            <span class="user-perfil">[{{ perfilUsuario() }}]</span>
          </div>
          <div class="user-actions">
            <button
              pButton
              type="button"
              label="PERFIL"
              class="btn-perfil"
              routerLink="/perfil"
            ></button>
            <button pButton type="button" label="SAIR" class="btn-sair" (click)="sair()"></button>
          </div>
        </div>
      </aside>

      <!-- ÁREA DE TRABALHO CENTRALIZADA -->
      <main class="area-trabalho-central">
        <div class="cartao-conteudo">
          <div class="area-rolagem-interna">
            <router-outlet></router-outlet>
          </div>
        </div>
      </main>
    </div>
  `,
})
export class MainLayoutComponent {
  private readonly authService = inject(AutenticacaoService);
  private readonly router = inject(Router);

  readonly usuario = this.authService.usuarioLogado;

  readonly nomeUsuario = computed(() => this.usuario()?.nomeCompleto || 'Usuário');
  readonly perfilUsuario = computed(() => this.usuario()?.perfil || 'PERFIL');

  readonly gruposMenu = computed<GrupoMenu[]>(() => {
    const perfil = this.usuario()?.perfil;
    if (!perfil) return [];

    const grupos: GrupoMenu[] = [];

    if (perfil === 'ADMINISTRADOR') {
      grupos.push({
        titulo: 'Administração',
        itens: [
          { rota: '/gatilhos', rotulo: 'Gatilhos GTT' },
          { rota: '/modulos', rotulo: 'Módulos GTT' },
          { rota: '/turmas', rotulo: 'Turmas & Alunos' },
          { rota: '/unidades', rotulo: 'Unidades HU' },
          { rota: '/usuarios', rotulo: 'Usuários & Perfis' },
        ],
      });

      grupos.push({
        titulo: 'Gestão Acadêmica',
        itens: [
          { rota: '/atividades', rotulo: 'Atividades da Turma' },
          { rota: '/casos-clinicos', rotulo: 'Prontuários & Casos' },
        ],
      });

      grupos.push({
        titulo: 'Epidemiologia',
        itens: [{ rota: '/indicadores', rotulo: 'Indicadores IHI' }],
      });
    }

    if (perfil === 'PROFESSOR') {
      grupos.push({
        titulo: 'Gestão Acadêmica',
        itens: [
          { rota: '/atividades', rotulo: 'Atividades da Turma' },
          { rota: '/casos-clinicos', rotulo: 'Prontuários & Casos' },
          { rota: '/minhas-turmas', rotulo: 'Minhas Turmas' },
        ],
      });
    }

    if (perfil === 'ALUNO') {
      grupos.push({
        titulo: 'Ambiente do Aluno',
        itens: [{ rota: '/minhas-atividades', rotulo: 'Minhas Atividades' }],
      });
    }

    return grupos
      .map((g) => ({
        ...g,
        itens: [...g.itens].sort((a, b) =>
          a.rotulo.localeCompare(b.rotulo, 'pt-BR', { sensitivity: 'base' }),
        ),
      }))
      .sort((a, b) => a.titulo.localeCompare(b.titulo, 'pt-BR', { sensitivity: 'base' }));
  });

  sair(): void {
    this.authService.sair();
    this.router.navigate(['/login']);
  }
}
