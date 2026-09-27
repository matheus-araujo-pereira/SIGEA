import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';

/**
 * Componente para inserção de dados sociodemográficos e de internação do prontuário simulado.
 */
@Component({
  selector: 'app-patient-demographics-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs space-y-4" [formGroup]="clinicalCaseGroup">
      <div class="flex items-center gap-2 border-b border-slate-100 pb-3">
        <span class="w-6 h-6 rounded-lg bg-clinical-100 text-clinical-700 flex items-center justify-center font-bold text-xs">2</span>
        <h3 class="text-sm font-black text-slate-900">Prontuário Simulado: Admissão e Identificação do Paciente</h3>
      </div>

      <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4">
        <!-- Nome do Paciente -->
        <div class="md:col-span-2">
          <label class="block font-bold text-slate-700 mb-1.5 text-xs">
            Nome Fictício do Paciente <span class="text-rose-500">*</span>
          </label>
          <input
            type="text"
            formControlName="patientName"
            placeholder="Ex: J. S. O. (ou Maria das Graças Silva)"
            class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
          />
        </div>

        <!-- Idade -->
        <div>
          <label class="block font-bold text-slate-700 mb-1.5 text-xs">Idade (anos)</label>
          <input
            type="number"
            formControlName="age"
            placeholder="Ex: 68"
            class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500 font-mono"
          />
        </div>

        <!-- Gênero -->
        <div>
          <label class="block font-bold text-slate-700 mb-1.5 text-xs">Gênero</label>
          <select
            formControlName="gender"
            class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium text-slate-700 focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
          >
            <option value="Feminino">Feminino</option>
            <option value="Masculino">Masculino</option>
            <option value="Outro">Outro</option>
          </select>
        </div>

        <!-- Leito / Enfermaria -->
        <div>
          <label class="block font-bold text-slate-700 mb-1.5 text-xs">Leito / Setor</label>
          <input
            type="text"
            formControlName="bed"
            placeholder="Ex: Leito 204 - Ala Clínica"
            class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
          />
        </div>

        <!-- Data de Admissão -->
        <div>
          <label class="block font-bold text-slate-700 mb-1.5 text-xs">Data de Admissão</label>
          <input
            type="text"
            formControlName="admissionDate"
            placeholder="Ex: 10/09/2026"
            class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
          />
        </div>

        <!-- Pacientes-Dia / Tempo de Permanência -->
        <div>
          <label class="block font-bold text-slate-700 mb-1.5 text-xs">
            Tempo de Permanência (Pacientes-Dia) <span class="text-rose-500">*</span>
          </label>
          <input
            type="number"
            min="1"
            formControlName="patientDays"
            placeholder="Ex: 5"
            class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500 font-mono"
          />
        </div>
      </div>

      <!-- Anotação de Admissão / Histórico Clínico -->
      <div>
        <label class="block font-bold text-slate-700 mb-1.5 text-xs">Histórico Clínico e Nota de Admissão</label>
        <textarea
          rows="3"
          formControlName="admissionNotes"
          placeholder="Descreva a queixa principal, história da doença atual, comorbidades, alergias e achados de exame físico admissional..."
          class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500 resize-y"
        ></textarea>
      </div>
    </div>
  `
})
export class PatientDemographicsFormComponent {
  /**
   * Sub-grupo reativo do prontuário simulado contendo dados do paciente.
   */
  @Input({ required: true }) clinicalCaseGroup!: FormGroup;
}
