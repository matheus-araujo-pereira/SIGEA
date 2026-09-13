import { Component, inject, computed } from '@angular/core';
import { Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AutenticacaoService } from '../modulos/autenticacao/servicos/autenticacao.service';
import { ButtonModule } from 'primeng/button';
import { AvatarModule } from 'primeng/avatar';
import { TooltipModule } from 'primeng/tooltip';
import { TagModule } from 'primeng/tag';
import { BadgeModule } from 'primeng/badge';

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
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    ButtonModule,
    AvatarModule,
    TooltipModule,
    TagModule,
    BadgeModule,
  ],
  templateUrl: './main-layout.component.html',
})
export class MainLayoutComponent {
  private readonly authService = inject(AutenticacaoService);
  private readonly router = inject(Router);

  readonly usuario = this.authService.usuarioLogado;

  readonly nomeUsuario = computed(() => this.usuario()?.nomeCompleto || 'Usuário');
  readonly perfilUsuario = computed(() => this.usuario()?.perfil || 'PERFIL');

  readonly iniciaisUsuario = computed(() => {
    const nome = this.nomeUsuario();
    const partes = nome.trim().split(/\s+/);
    if (partes.length === 1) return partes[0].substring(0, 2).toUpperCase();
    return (partes[0][0] + partes[partes.length - 1][0]).toUpperCase();
  });

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

  sair(): void {
    this.authService.sair();
    this.router.navigate(['/login']);
  }
}
