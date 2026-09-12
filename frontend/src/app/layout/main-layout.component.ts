import { Component, inject, computed } from '@angular/core';
import { Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AutenticacaoService } from '../modulos/autenticacao/servicos/autenticacao.service';
import { ButtonModule } from 'primeng/button';
import { Tag } from 'primeng/tag';
import { Tooltip } from 'primeng/tooltip';

export interface ItemMenu {
  rota: string;
  rotulo: string;
  icone: string;
}

export interface GrupoMenu {
  titulo: string;
  itens: ItemMenu[];
}

@Component({
  selector: 'app-main-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, ButtonModule, Tag, Tooltip],
  template: `
    <div class="layout-container">
      <!-- SIDEBAR FIXA 250px -->
      <aside class="sidebar-desktop">
        <!-- TOPO: IDENTIFICAÇÃO INSTITUCIONAL -->
        <div class="sidebar-brand">
          <div class="brand-title">SIGEA-GTT</div>
          <div class="brand-subtitle">Hospital Universitário HU-UFS</div>
        </div>

        <!-- CENTRO: NAVEGAÇÃO ANGULAR ROUTER COM PRIMEICONS -->
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
                <i [class]="item.icone"></i>
                <span>{{ item.rotulo }}</span>
              </a>
            }
          }
        </nav>

        <!-- BASE INFERIOR: USUÁRIO, PERFIL, ALTERAR SENHA E LOGOUT -->
        <div class="sidebar-user-block">
          <div class="user-info">
            <span class="user-name" [title]="nomeUsuario()">{{ nomeUsuario() }}</span>
            <p-tag [value]="perfilUsuario()" severity="secondary" styleClass="user-tag" />
          </div>
          <div class="user-actions">
            <button
              pButton
              type="button"
              icon="pi pi-user"
              [text]="true"
              severity="secondary"
              size="small"
              routerLink="/perfil"
              pTooltip="Meu Perfil / Alterar Senha"
              tooltipPosition="top"
              aria-label="Meu Perfil"
            ></button>
            <button
              pButton
              type="button"
              icon="pi pi-sign-out"
              [text]="true"
              severity="danger"
              size="small"
              (click)="sair()"
              pTooltip="Sair do Sistema"
              tooltipPosition="top"
              aria-label="Sair"
            ></button>
          </div>
        </div>
      </aside>

      <!-- ÁREA DE TRABALHO: calc(100vw - 250px), 100vh, overflow-y auto, padding 28px -->
      <main class="workspace-desktop">
        <router-outlet></router-outlet>
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
          { rota: '/gatilhos', rotulo: 'Gatilhos GTT', icone: 'pi pi-sliders-h' },
          { rota: '/modulos', rotulo: 'Módulos GTT', icone: 'pi pi-th-large' },
          { rota: '/turmas', rotulo: 'Turmas & Alunos', icone: 'pi pi-graduation-cap' },
          { rota: '/unidades', rotulo: 'Unidades HU', icone: 'pi pi-building' },
          { rota: '/usuarios', rotulo: 'Usuários & Perfis', icone: 'pi pi-users' },
        ],
      });

      grupos.push({
        titulo: 'Gestão Acadêmica',
        itens: [
          { rota: '/atividades', rotulo: 'Atividades da Turma', icone: 'pi pi-check-square' },
          { rota: '/casos-clinicos', rotulo: 'Prontuários & Casos', icone: 'pi pi-file' },
        ],
      });

      grupos.push({
        titulo: 'Epidemiologia',
        itens: [{ rota: '/indicadores', rotulo: 'Indicadores IHI', icone: 'pi pi-chart-bar' }],
      });
    }

    if (perfil === 'PROFESSOR') {
      grupos.push({
        titulo: 'Gestão Acadêmica',
        itens: [
          { rota: '/atividades', rotulo: 'Atividades da Turma', icone: 'pi pi-check-square' },
          { rota: '/casos-clinicos', rotulo: 'Prontuários & Casos', icone: 'pi pi-file' },
          { rota: '/minhas-turmas', rotulo: 'Minhas Turmas', icone: 'pi pi-graduation-cap' },
        ],
      });
    }

    if (perfil === 'ALUNO') {
      grupos.push({
        titulo: 'Ambiente do Aluno',
        itens: [
          { rota: '/minhas-atividades', rotulo: 'Minhas Atividades', icone: 'pi pi-file-edit' },
        ],
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
