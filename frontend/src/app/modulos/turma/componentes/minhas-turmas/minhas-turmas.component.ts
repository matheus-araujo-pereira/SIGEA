import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { TooltipModule } from 'primeng/tooltip';
import { TagModule } from 'primeng/tag';
import { BadgeModule } from 'primeng/badge';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { MessageService } from 'primeng/api';

import { TurmaService } from '../../servicos/turma.service';
import { AutenticacaoService } from '../../../autenticacao/servicos/autenticacao.service';
import { Turma } from '../../modelos/turma.modelos';

export interface TurmaDocenteLinha {
  id: number;
  codigoDisciplina: string;
  periodoLetivo: string;
  ativa: boolean;
  totalAlunos: number;
  original: Turma;
}

@Component({
  selector: 'app-minhas-turmas',
  imports: [
    FormsModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    TooltipModule,
    TagModule,
    BadgeModule,
    IconFieldModule,
    InputIconModule,
  ],
  templateUrl: './minhas-turmas.component.html',
})
export class MinhasTurmasComponent implements OnInit {
  private readonly turmaService = inject(TurmaService);
  private readonly autenticacaoService = inject(AutenticacaoService);
  private readonly router = inject(Router);
  private readonly messageService = inject(MessageService);

  readonly turmas = signal<Turma[]>([]);
  readonly carregando = signal(false);
  readonly termoBusca = signal('');

  readonly totalTurmas = computed(() => this.turmas().length);

  readonly turmasFiltradas = computed<TurmaDocenteLinha[]>(() => {
    const termo = this.termoBusca().trim().toLowerCase();
    return this.turmas()
      .filter((t) => {
        return (
          !termo ||
          t.codigoDisciplina.toLowerCase().includes(termo) ||
          t.periodoLetivo.toLowerCase().includes(termo)
        );
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
}
