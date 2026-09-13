import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule, DatePipe, DecimalPipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { MessageModule } from 'primeng/message';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { TagModule } from 'primeng/tag';
import { TooltipModule } from 'primeng/tooltip';
import { EducacionalService } from '../../../servicos/educacional.service';
import { Submissao } from '../../../modelos/educacional.modelos';

/**
 * Componente de visualização do resultado de uma atividade GTT avaliada pelo docente.
 *
 * Exibe a nota atribuída, feedback textual do docente e um resumo detalhado
 * de todos os pilares da auditoria enviada: Gatilhos identificados, Análise Ishikawa 6M,
 * Plano de Ação 5W3H e Ciclo PDCA.
 * Disponível para o perfil ALUNO após a submissão ser marcada como AVALIADA.
 */
@Component({
  selector: 'app-resultado-atividade',
  imports: [
    CommonModule,
    DatePipe,
    DecimalPipe,
    ButtonModule,
    TableModule,
    MessageModule,
    ProgressSpinnerModule,
    TagModule,
    TooltipModule,
  ],
  templateUrl: './resultado-atividade.component.html',
  styleUrl: './resultado-atividade.component.scss',
})
export class ResultadoAtividadeComponent implements OnInit {
  private readonly educacionalService = inject(EducacionalService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly carregando = signal(true);
  readonly submissao = signal<Submissao | null>(null);
  readonly mensagemErro = signal<string | null>(null);

  readonly abaResolucao = signal<'gatilhos' | 'ishikawa' | 'plano5w3h' | 'pdca' | 'prontuario'>(
    'gatilhos',
  );

  readonly abaProntuario = signal<'sumario' | 'prescricoes' | 'exames' | 'evolucoes' | 'cirurgico'>(
    'sumario',
  );

  readonly totalDanos = computed(
    () => this.submissao()?.achadosGatilhos?.filter((g) => g.confirmouDano).length || 0,
  );

  readonly totalPOA = computed(
    () => this.submissao()?.achadosGatilhos?.filter((g) => g.danoPresenteAdmissao).length || 0,
  );

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.carregarResultado(Number(idParam));
    }
  }

  carregarResultado(id: number): void {
    this.carregando.set(true);
    this.educacionalService.buscarSubmissao(id).subscribe({
      next: (dados) => {
        this.submissao.set(dados);
        this.carregando.set(false);
      },
      error: (err) => {
        this.mensagemErro.set(
          'Erro ao carregar resultado da atividade: ' + (err.error?.mensagem || err.message),
        );
        this.carregando.set(false);
      },
    });
  }

  formatarTempo(segundos?: number): string {
    if (!segundos && segundos !== 0) return '-';
    const min = Math.floor(segundos / 60);
    const seg = segundos % 60;
    return `${min}m ${seg < 10 ? '0' : ''}${seg}s`;
  }

  voltar(): void {
    this.router.navigate(['/minhas-atividades']);
  }
}
