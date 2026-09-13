import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { SelectModule } from 'primeng/select';
import { InputTextModule } from 'primeng/inputtext';
import { ProgressBarModule } from 'primeng/progressbar';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { TooltipModule } from 'primeng/tooltip';
import { TagModule } from 'primeng/tag';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';

import {
  IndicadoresService,
  DesempenhoGatilho,
  DesempenhoModulo,
  FiltrosIndicadores,
} from '../../servicos/indicadores.service';
import { TurmaService } from '../../../turma/servicos/turma.service';
import { UnidadeService } from '../../../unidade/servicos/unidade.service';
import { Turma } from '../../../turma/modelos/turma.modelos';
import { UnidadeHospitalar } from '../../../unidade/modelos/unidade.modelos';

/**
 * Componente de Rastreabilidade de Gatilhos IHI-GTT.
 *
 * Exibe tabela analítica com desempenho de cada gatilho canônico: número de revisões positivas,
 * danos confirmados, valor preditivo positivo (PPV / rendimento), casos graves (categorias G-I)
 * e na admissão. Permite filtros por módulo clínico, turma e busca textual por código de gatilho.
 */
@Component({
  selector: 'app-rastreabilidade-gatilhos',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    TableModule,
    ButtonModule,
    SelectModule,
    InputTextModule,
    ProgressBarModule,
    ProgressSpinnerModule,
    TooltipModule,
    TagModule,
    IconFieldModule,
    InputIconModule,
  ],
  templateUrl: './rastreabilidade-gatilhos.component.html',
  styleUrl: './rastreabilidade-gatilhos.component.scss',
})
export class RastreabilidadeGatilhosComponent implements OnInit {
  private readonly indicadoresService = inject(IndicadoresService);
  private readonly turmaService = inject(TurmaService);
  private readonly unidadeService = inject(UnidadeService);

  readonly carregando = signal<boolean>(false);

  readonly turmas = signal<Turma[]>([]);
  readonly unidades = signal<UnidadeHospitalar[]>([]);

  // Filtros
  readonly termoBusca = signal<string>('');
  readonly filtroPeriodoLetivo = signal<string>('TODOS');
  readonly filtroTurmaId = signal<string>('TODOS');
  readonly filtroUnidadeId = signal<string>('TODOS');
  readonly filtroModulo = signal<string>('TODOS');

  // Dados do backend
  readonly gatilhos = signal<DesempenhoGatilho[]>([]);
  readonly modulos = signal<DesempenhoModulo[]>([]);
  readonly totalGatilhosRastreados = signal<number>(0);
  readonly totalDanosConfirmados = signal<number>(0);
  readonly taxaConversaoGeral = signal<number>(0);

  // Opções para p-select
  readonly periodosDisponiveis = computed(() => {
    const periodos = this.turmas()
      .map((t) => t.periodoLetivo)
      .filter((p): p is string => !!p && p.trim().length > 0);
    return Array.from(new Set(periodos)).sort().reverse();
  });

  readonly periodoOptions = computed(() => [
    { label: 'Todos os Períodos', value: 'TODOS' },
    ...this.periodosDisponiveis().map((p) => ({ label: p, value: p })),
  ]);

  readonly turmasFiltradas = computed(() => {
    const periodo = this.filtroPeriodoLetivo();
    if (!periodo || periodo === 'TODOS') {
      return this.turmas();
    }
    return this.turmas().filter((t) => t.periodoLetivo === periodo);
  });

  readonly turmaOptions = computed(() => [
    { label: 'Todas as Turmas', value: 'TODOS' },
    ...this.turmasFiltradas().map((t) => ({
      label: t.codigoDisciplina,
      value: String(t.id),
    })),
  ]);

  readonly unidadeOptions = computed(() => [
    { label: 'Todas as Unidades', value: 'TODOS' },
    ...this.unidades().map((u) => ({
      label: `${u.sigla} - ${u.nome}`,
      value: String(u.id),
    })),
  ]);

  readonly moduloOptions = [
    { label: 'Todos os Módulos', value: 'TODOS' },
    { label: 'Cuidados Gerais (C)', value: 'CUIDADOS' },
    { label: 'Medicamentos (M)', value: 'MEDICACAO' },
    { label: 'Cirúrgico (S)', value: 'CIRURGICO' },
    { label: 'Terapia Intensiva (I)', value: 'TERAPIA_INTENSIVA' },
    { label: 'Perinatal (P)', value: 'PERINATAL' },
    { label: 'Pronto Atendimento (E)', value: 'URGENCIA' },
  ];

  // Gatilhos filtrados por busca textual e módulo selecionado
  readonly gatilhosFiltrados = computed(() => {
    const busca = this.termoBusca().trim().toLowerCase();
    const mod = this.filtroModulo();

    return this.gatilhos().filter((g) => {
      const matchMod = mod === 'TODOS' || g.moduloCodigo.toUpperCase() === mod.toUpperCase();
      const matchBusca =
        !busca ||
        g.codigo.toLowerCase().includes(busca) ||
        g.descricao.toLowerCase().includes(busca) ||
        g.moduloNome.toLowerCase().includes(busca);
      return matchMod && matchBusca;
    });
  });

  readonly totalDanosGraves = computed(() => {
    return this.gatilhos().reduce((acc, g) => acc + (g.danosGraves || 0), 0);
  });

  readonly dataHoraEmissao = computed(() => {
    return new Date().toLocaleString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  });

  ngOnInit(): void {
    this.carregarTurmas();
    this.unidadeService.listar().subscribe({
      next: (u) => this.unidades.set(u.filter((item) => item.ativa)),
      error: (e) => console.error('Erro ao listar unidades:', e),
    });
    this.carregarDesempenhoGatilhos();
  }

  carregarTurmas(): void {
    this.turmaService.listar().subscribe({
      next: (t) => this.turmas.set(t),
      error: (err) => console.error('Erro ao carregar turmas:', err),
    });
  }

  carregarDesempenhoGatilhos(): void {
    this.carregando.set(true);

    const filtros: FiltrosIndicadores = {};
    if (this.filtroPeriodoLetivo() !== 'TODOS') {
      filtros.periodoLetivo = this.filtroPeriodoLetivo();
    }
    if (this.filtroTurmaId() !== 'TODOS') {
      filtros.turmaId = Number(this.filtroTurmaId());
    }
    if (this.filtroUnidadeId() !== 'TODOS') {
      filtros.unidadeId = Number(this.filtroUnidadeId());
    }

    this.indicadoresService.obterDesempenhoGatilhos(filtros).subscribe({
      next: (res) => {
        this.gatilhos.set(res.gatilhos);
        this.modulos.set(res.modulos);
        this.totalGatilhosRastreados.set(res.totalGatilhosRastreados);
        this.totalDanosConfirmados.set(res.totalDanosConfirmados);
        this.taxaConversaoGeral.set(res.taxaConversaoGeral);
        this.carregando.set(false);
      },
      error: (err) => {
        console.error('Erro ao carregar desempenho de gatilhos:', err);
        this.carregando.set(false);
      },
    });
  }

  limparFiltros(): void {
    this.termoBusca.set('');
    this.filtroPeriodoLetivo.set('TODOS');
    this.filtroTurmaId.set('TODOS');
    this.filtroUnidadeId.set('TODOS');
    this.filtroModulo.set('TODOS');
    this.carregarDesempenhoGatilhos();
  }

  obterSeveridadeModulo(
    moduloCodigo: string,
  ): 'info' | 'success' | 'warn' | 'danger' | 'secondary' {
    switch (moduloCodigo?.toUpperCase()) {
      case 'C':
      case 'CUIDADOS':
        return 'info';
      case 'M':
      case 'MEDICACAO':
        return 'success';
      case 'S':
      case 'CIRURGICO':
        return 'warn';
      case 'I':
      case 'TERAPIA_INTENSIVA':
        return 'danger';
      case 'P':
      case 'PERINATAL':
        return 'secondary';
      case 'E':
      case 'URGENCIA':
        return 'secondary';
      default:
        return 'info';
    }
  }
}
