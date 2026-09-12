import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { MessageModule } from 'primeng/message';
import { MessageService } from 'primeng/api';

import { UnidadeService, UnidadeRequisicao } from '../../servicos/unidade.service';

@Component({
  selector: 'app-formulario-unidade',
  imports: [FormsModule, CardModule, InputTextModule, ButtonModule, MessageModule],
  templateUrl: './formulario-unidade.component.html',
})
export class FormularioUnidadeComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly unidadeService = inject(UnidadeService);
  private readonly messageService = inject(MessageService);

  readonly idUnidade = signal<number | null>(null);
  readonly modoEdicao = computed(() => this.idUnidade() !== null);

  readonly carregando = signal(false);
  readonly salvando = signal(false);
  readonly mensagemErro = signal<string | null>(null);

  formulario: UnidadeRequisicao = {
    sigla: '',
    nome: '',
  };

  readonly tituloPagina = computed(() => {
    return this.modoEdicao()
      ? `Editar Unidade Hospitalar #${this.idUnidade()}`
      : 'Cadastrar Nova Unidade Hospitalar';
  });

  readonly subtituloPagina = computed(() => {
    return this.modoEdicao()
      ? 'Atualize a sigla e o nome da unidade hospitalar do HU-UFS.'
      : 'Preencha os dados da nova unidade assistencial do Hospital Universitário.';
  });

  ngOnInit(): void {
    const paramId = this.route.snapshot.paramMap.get('id');
    if (paramId) {
      const id = Number(paramId);
      if (!isNaN(id) && id > 0) {
        this.idUnidade.set(id);
        this.carregarDadosUnidade(id);
      } else {
        this.router.navigate(['/unidades']);
      }
    }
  }

  carregarDadosUnidade(id: number): void {
    this.carregando.set(true);
    this.unidadeService.buscarPorId(id).subscribe({
      next: (unidade) => {
        this.formulario = {
          sigla: unidade.sigla,
          nome: unidade.nome,
        };
        this.carregando.set(false);
      },
      error: (err) => {
        this.mensagemErro.set(
          'Erro ao carregar dados da unidade: ' + (err.error?.mensagem || err.message),
        );
        this.carregando.set(false);
      },
    });
  }

  validarFormulario(): boolean {
    this.mensagemErro.set(null);

    if (!this.formulario.sigla || this.formulario.sigla.trim().length === 0) {
      this.mensagemErro.set('A Sigla da Unidade é obrigatória (ex: CINF, UTI, CLIN-MED).');
      return false;
    }

    if (!this.formulario.nome || this.formulario.nome.trim().length === 0) {
      this.mensagemErro.set('O Nome da Unidade Hospitalar é obrigatório.');
      return false;
    }

    return true;
  }

  salvar(): void {
    if (!this.validarFormulario()) {
      return;
    }

    this.salvando.set(true);
    this.mensagemErro.set(null);

    const payload: UnidadeRequisicao = {
      sigla: this.formulario.sigla.trim().toUpperCase(),
      nome: this.formulario.nome.trim(),
    };

    const id = this.idUnidade();
    const requisicao$ = id
      ? this.unidadeService.editar(id, payload)
      : this.unidadeService.cadastrar(payload);

    requisicao$.subscribe({
      next: (unidade) => {
        this.salvando.set(false);
        this.messageService.add({
          severity: 'success',
          summary: 'Sucesso',
          detail: `Unidade "${unidade.sigla}" ${id ? 'atualizada' : 'cadastrada'} com sucesso!`,
        });
        setTimeout(() => this.voltarParaListagem(), 1000);
      },
      error: (err) => {
        this.salvando.set(false);
        this.mensagemErro.set(err.error?.mensagem || 'Falha ao salvar dados da unidade.');
      },
    });
  }

  voltarParaListagem(): void {
    this.router.navigate(['/unidades']);
  }
}
