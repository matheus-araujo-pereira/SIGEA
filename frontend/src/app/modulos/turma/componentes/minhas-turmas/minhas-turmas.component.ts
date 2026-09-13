import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { TooltipModule } from 'primeng/tooltip';
import { TagModule } from 'primeng/tag';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { MessageService } from 'primeng/api';

import { TurmaService } from '../../servicos/turma.service';
import { AutenticacaoService } from '../../../autenticacao/servicos/autenticacao.service';
import { Turma } from '../../modelos/turma.modelos';

/** Projeção de linha da tabela de turmas do docente com campos essenciais para exibição. */
export interface TurmaDocenteLinha {
  id: number;
  codigoDisciplina: string;
  periodoLetivo: string;
  ativa: boolean;
  totalAlunos: number;
  original: Turma;
}

/**
 * Componente de visualização das turmas do docente autenticado (visão PROFESSOR).
 *
 * Exibe tabela paginada das turmas pelas quais o professor é responsável,
 * com filtros por código de disciplina, período letivo e status.
 * Permite acessar o painel de atividades e a listagem de alunos de cada turma.
 */
@Component({
  selector: 'app-minhas-turmas',
  imports: [
    FormsModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    SelectModule,
    TooltipModule,
    TagModule,
    IconFieldModule,
    InputIconModule,
  ],
  templateUrl: './minhas-turmas.component.html',
  styleUrl: './minhas-turmas.component.scss',
})
export class MinhasTurmasComponent implements OnInit {
  private readonly turmaService = inject(TurmaService);
  private readonly autenticacaoService = inject(AutenticacaoService);
  private readonly router = inject(Router);
  private readonly messageService = inject(MessageService);

  readonly turmas = signal<Turma[]>([]);
  readonly carregando = signal(false);
  readonly termoBusca = signal('');
  readonly filtroStatus = signal<string>('TODAS');

  readonly statusOptions = [
    { label: 'Todas as Situações', value: 'TODAS' },
    { label: 'Apenas Ativas', value: 'ATIVAS' },
    { label: 'Apenas Inativas', value: 'INATIVAS' },
  ];

  readonly totalTurmas = computed(() => this.turmas().length);
  readonly totalTurmasAtivas = computed(() => this.turmas().filter((t) => t.ativa).length);
  readonly totalDiscentes = computed(() =>
    this.turmas().reduce((acc, t) => acc + (t.totalAlunos || 0), 0),
  );

  readonly turmasFiltradas = computed<TurmaDocenteLinha[]>(() => {
    const termo = this.termoBusca().trim().toLowerCase();
    const status = this.filtroStatus();

    return this.turmas()
      .filter((t) => {
        const matchStatus =
          status === 'TODAS' ||
          (status === 'ATIVAS' && t.ativa) ||
          (status === 'INATIVAS' && !t.ativa);

        const matchTermo =
          !termo ||
          t.codigoDisciplina.toLowerCase().includes(termo) ||
          t.periodoLetivo.toLowerCase().includes(termo);

        return matchStatus && matchTermo;
      })
      .sort((a, b) =>
        a.codigoDisciplina.localeCompare(b.codigoDisciplina, 'pt-BR', {
          sensitivity: 'base',
        }),
      )
      .map((t) => ({
        id: t.id,
        codigoDisciplina: t.codigoDisciplina,
        periodoLetivo: t.periodoLetivo,
        ativa: t.ativa,
        totalAlunos: t.totalAlunos,
        original: t,
      }));
  });

  ngOnInit(): void {
    this.carregarMinhasTurmas();
  }

  carregarMinhasTurmas(): void {
    this.carregando.set(true);
    const professorId = this.autenticacaoService.usuarioLogado()?.id;
    this.turmaService.listar(professorId).subscribe({
      next: (lista: Turma[]) => {
        this.turmas.set(lista);
        this.carregando.set(false);
      },
      error: () => {
        this.carregando.set(false);
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: 'Falha ao carregar turmas vinculadas ao docente.',
        });
      },
    });
  }

  navegarParaAlunos(turma: Turma): void {
    this.router.navigate(['/turmas', turma.id, 'alunos']);
  }

  navegarParaAtividades(turma: Turma): void {
    this.router.navigate(['/atividades'], { queryParams: { turmaId: turma.id } });
  }
}
