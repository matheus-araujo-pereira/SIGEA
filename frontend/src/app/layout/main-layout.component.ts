import { Component, inject, computed, signal } from '@angular/core';
import { Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AutenticacaoService } from '../modulos/autenticacao/servicos/autenticacao.service';
import { ButtonModule } from 'primeng/button';
import { TooltipModule } from 'primeng/tooltip';

/** Representa um item de navegação do menu lateral. */
export interface ItemMenu {
  /** Rota Angular de destino ao clicar no item. */
  rota: string;
  /** Rótulo textual exibido no menu. */
  rotulo: string;
  /** Classe do ícone PrimeIcons (ex: `pi pi-bolt`). */
  icone: string;
}

/** Agrupa itens de menu por seção temática. */
export interface GrupoMenu {
  /** Título do grupo exibido como cabeçalho de seção no menu lateral. */
  titulo: string;
  /** Lista de itens de navegação que compõem o grupo. */
  itens: ItemMenu[];
}

/**
 * Componente de layout principal do SIGEA-GTT.
 *
 * Implementa o shell da aplicação com sidebar responsivo, cabeçalho institucional,
 * área de conteúdo via `<router-outlet>` e rodapé de usuário.
 * O menu lateral é gerado dinamicamente com base no perfil institucional do usuário autenticado
 * (ADMINISTRADOR, PROFESSOR ou ALUNO), com grupos e itens ordenados alfabeticamente.
 */
@Component({
  selector: 'app-main-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, ButtonModule, TooltipModule],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.scss',
})
export class MainLayoutComponent {
  private readonly authService = inject(AutenticacaoService);
  private readonly router = inject(Router);

  readonly sidebarMobileAberta = signal(false);
  readonly usuario = this.authService.usuarioLogado;

  /** Nome completo do usuário autenticado. Fallback para `'Usuário'` se não disponível. */
  readonly nomeUsuario = computed(() => this.usuario()?.nomeCompleto || 'Usuário');
  /** Perfil institucional do usuário (ADMINISTRADOR, PROFESSOR, ALUNO). */
  readonly perfilUsuario = computed(() => this.usuario()?.perfil || 'PERFIL');

  /** Iniciais do usuário (primeira + última palavra do nome completo) para o avatar do menu. */
  readonly iniciaisUsuario = computed(() => {
    const nome = this.nomeUsuario();
    const partes = nome.trim().split(/\s+/);
    if (partes.length === 1) return partes[0].substring(0, 2).toUpperCase();
    return (partes[0][0] + partes[partes.length - 1][0]).toUpperCase();
  });

  /**
   * Lista de grupos de menu disponíveis para o perfil do usuário logado.
   * Grupos e itens internos são ordenados alfabeticamente em português.
   */
  readonly gruposMenu = computed<GrupoMenu[]>(() => {
    const perfil = this.usuario()?.perfil;
    if (!perfil) return [];

    const grupos: GrupoMenu[] = [];

    if (perfil === 'ADMINISTRADOR') {
      grupos.push({
        titulo: 'Administração',
        itens: [
          { rota: '/gatilhos', rotulo: 'Gatilhos GTT', icone: 'pi pi-bolt' },
          { rota: '/modulos', rotulo: 'Módulos GTT', icone: 'pi pi-th-large' },
          { rota: '/turmas', rotulo: 'Turmas & Alunos', icone: 'pi pi-users' },
          { rota: '/unidades', rotulo: 'Unidades HU', icone: 'pi pi-building' },
          { rota: '/usuarios', rotulo: 'Usuários & Perfis', icone: 'pi pi-id-card' },
        ],
      });

      grupos.push({
        titulo: 'Gestão Acadêmica',
        itens: [
          { rota: '/atividades', rotulo: 'Atividades da Turma', icone: 'pi pi-file-edit' },
          { rota: '/casos-clinicos', rotulo: 'Prontuários & Casos', icone: 'pi pi-book' },
        ],
      });

      grupos.push({
        titulo: 'Epidemiologia',
        itens: [{ rota: '/indicadores', rotulo: 'Indicadores IHI', icone: 'pi pi-chart-line' }],
      });
    }

    if (perfil === 'PROFESSOR') {
      grupos.push({
        titulo: 'Gestão Acadêmica',
        itens: [
          { rota: '/atividades', rotulo: 'Atividades da Turma', icone: 'pi pi-file-edit' },
          { rota: '/casos-clinicos', rotulo: 'Prontuários & Casos', icone: 'pi pi-book' },
          { rota: '/minhas-turmas', rotulo: 'Minhas Turmas', icone: 'pi pi-graduation-cap' },
        ],
      });
    }

    if (perfil === 'ALUNO') {
      grupos.push({
        titulo: 'Ambiente do Aluno',
        itens: [
          { rota: '/minhas-atividades', rotulo: 'Minhas Atividades', icone: 'pi pi-clipboard' },
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

  /** Alterna a visibilidade do menu lateral em dispositivos móveis. */
  alternarSidebarMobile(): void {
    this.sidebarMobileAberta.update((v) => !v);
  }

  /** Fecha o menu lateral em dispositivos móveis. */
  fecharSidebarMobile(): void {
    this.sidebarMobileAberta.set(false);
  }

  /** Encerra a sessão do usuário e redireciona para a tela de login. */
  sair(): void {
    this.authService.sair();
    this.router.navigate(['/login']);
  }
}
