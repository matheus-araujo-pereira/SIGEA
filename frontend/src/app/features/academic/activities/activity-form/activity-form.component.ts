import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ActivityService } from '../../../../core/services/activity.service';
import { AcademicClassService } from '../../../../core/services/academic-class.service';
import { ToastService } from '../../../../core/services/toast.service';
import { AcademicClassResponseDTO } from '../../../../core/models/academic-class.model';
import { ActivityDetailDTO, ClinicalCaseData } from '../../../../core/models/activity.model';

/**
 * Criação e edição de Atividades Avaliativas com Prontuário Simulado estruturado.
 */
@Component({
  selector: 'app-activity-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  template: `
    <div class="space-y-6 max-w-5xl mx-auto">
      <!-- Cabeçalho -->
      <div class="flex items-center justify-between">
        <button
          type="button"
          (click)="goBack()"
          class="btn-secondary gap-2 text-xs"
        >
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
          </svg>
          <span>Voltar</span>
        </button>

        <h2 class="text-xl font-black text-slate-900">
          {{ isEditMode ? 'Editar Atividade Avaliativa' : 'Nova Atividade com Prontuário Simulado' }}
        </h2>
      </div>

      <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-6 text-xs">
        <!-- Card 1: Dados Gerais da Atividade -->
        <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs space-y-4">
          <div class="flex items-center gap-2 border-b border-slate-100 pb-3">
            <span class="w-6 h-6 rounded-lg bg-clinical-100 text-clinical-700 flex items-center justify-center font-bold text-xs">1</span>
            <h3 class="text-sm font-black text-slate-900">Informações da Atividade</h3>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <!-- Turma Acadêmica -->
            @if (!isEditMode) {
              <div class="md:col-span-1">
                <label class="block font-bold text-slate-700 mb-1.5">
                  Turma <span class="text-rose-500">*</span>
                </label>
                <select
                  formControlName="classId"
                  class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium text-slate-800 focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
                >
                  <option value="">Selecione a turma...</option>
                  @for (c of classes(); track c.id) {
                    <option [value]="c.id">{{ c.formattedName }}</option>
                  }
                </select>
                @if (form.get('classId')?.touched && form.get('classId')?.invalid) {
                  <span class="text-rose-600 text-[10px] mt-1 block">Selecione uma turma</span>
                }
              </div>
            }

            <!-- Título da Atividade -->
            <div [class.md:col-span-2]="!isEditMode" [class.md:col-span-2]="isEditMode">
              <label class="block font-bold text-slate-700 mb-1.5">
                Título da Atividade <span class="text-rose-500">*</span>
              </label>
              <input
                type="text"
                formControlName="title"
                placeholder="Ex: Caso Clínico 1 - Evento Adverso em Terapia Medicamentosa"
                class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
              />
              @if (form.get('title')?.touched && form.get('title')?.invalid) {
                <span class="text-rose-600 text-[10px] mt-1 block">Título obrigatório</span>
              }
            </div>

            <!-- Prazo Limite -->
            <div>
              <label class="block font-bold text-slate-700 mb-1.5">
                Prazo de Entrega (Data e Hora) <span class="text-rose-500">*</span>
              </label>
              <input
                type="datetime-local"
                formControlName="deadline"
                class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500 font-mono"
              />
              @if (form.get('deadline')?.touched && form.get('deadline')?.invalid) {
                <span class="text-rose-600 text-[10px] mt-1 block">Informe uma data futura</span>
              }
            </div>
          </div>

          <!-- Orientações Pedagógicas -->
          <div>
            <label class="block font-bold text-slate-700 mb-1.5">
              Orientações para os Alunos <span class="text-rose-500">*</span>
            </label>
            <textarea
              rows="3"
              formControlName="description"
              placeholder="Instruções aos discentes para revisão do prontuário, identificação de gatilhos IHI e aplicação das ferramentas da qualidade..."
              class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500 resize-y"
            ></textarea>
            @if (form.get('description')?.touched && form.get('description')?.invalid) {
              <span class="text-rose-600 text-[10px] mt-1 block">Orientações obrigatórias</span>
            }
          </div>
        </div>

        <!-- Card 2: Prontuário Simulado - Dados do Paciente e Admissão -->
        <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs space-y-4" formGroupName="clinicalCase">
          <div class="flex items-center gap-2 border-b border-slate-100 pb-3">
            <span class="w-6 h-6 rounded-lg bg-clinical-100 text-clinical-700 flex items-center justify-center font-bold text-xs">2</span>
            <h3 class="text-sm font-black text-slate-900">Prontuário Simulado: Admissão e Identificação do Paciente</h3>
          </div>

          <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4">
            <!-- Nome do Paciente -->
            <div class="md:col-span-2">
              <label class="block font-bold text-slate-700 mb-1.5">
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
              <label class="block font-bold text-slate-700 mb-1.5">Idade (anos)</label>
              <input
                type="number"
                formControlName="age"
                placeholder="Ex: 68"
                class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500 font-mono"
              />
            </div>

            <!-- Gênero -->
            <div>
              <label class="block font-bold text-slate-700 mb-1.5">Gênero</label>
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
              <label class="block font-bold text-slate-700 mb-1.5">Leito / Setor</label>
              <input
                type="text"
                formControlName="bed"
                placeholder="Ex: Leito 204 - Ala Clínica"
                class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
              />
            </div>

            <!-- Data de Admissão -->
            <div>
              <label class="block font-bold text-slate-700 mb-1.5">Data de Admissão</label>
              <input
                type="text"
                formControlName="admissionDate"
                placeholder="Ex: 10/09/2026"
                class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
              />
            </div>

            <!-- Pacientes-Dia / Tempo de Permanência -->
            <div>
              <label class="block font-bold text-slate-700 mb-1.5">
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
            <label class="block font-bold text-slate-700 mb-1.5">Histórico Clínico e Nota de Admissão</label>
            <textarea
              rows="3"
              formControlName="admissionNotes"
              placeholder="Descreva a queixa principal, história da doença atual, comorbidades, alergias e achados de exame físico admissional..."
              class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500 resize-y"
            ></textarea>
          </div>
        </div>

        <!-- Card 3: Notas de Evolução Médica e de Enfermagem -->
        <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs space-y-4" formGroupName="clinicalCase">
          <div class="flex items-center justify-between border-b border-slate-100 pb-3">
            <div class="flex items-center gap-2">
              <span class="w-6 h-6 rounded-lg bg-clinical-100 text-clinical-700 flex items-center justify-center font-bold text-xs">3</span>
              <h3 class="text-sm font-black text-slate-900">Evoluções Multiprofissionais</h3>
            </div>
            <button
              type="button"
              (click)="addEvolutionNote()"
              class="btn-secondary text-xs py-1.5 px-3 gap-1"
            >
              <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
              </svg>
              <span>Adicionar Evolução</span>
            </button>
          </div>

          <div formArrayName="evolutionNotes" class="space-y-3">
            @for (note of evolutionNotesArray.controls; track $index) {
              <div [formGroupName]="$index" class="p-3.5 bg-slate-50 border border-slate-200 rounded-2xl relative space-y-2">
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 pr-8">
                  <input
                    type="text"
                    formControlName="dateTime"
                    placeholder="Data/Hora (Ex: D+2 10:00)"
                    class="px-3 py-2 bg-white border border-slate-200 rounded-xl text-xs"
                  />
                  <input
                    type="text"
                    formControlName="professionalRole"
                    placeholder="Profissional (Ex: Médico Plantonista / Enfermeiro)"
                    class="px-3 py-2 bg-white border border-slate-200 rounded-xl text-xs"
                  />
                </div>
                <textarea
                  rows="2"
                  formControlName="note"
                  placeholder="Registro de evolução clínica, intercorrências, sinais vitais e condutas..."
                  class="w-full px-3 py-2 bg-white border border-slate-200 rounded-xl text-xs resize-y"
                ></textarea>
                <button
                  type="button"
                  (click)="removeEvolutionNote($index)"
                  class="absolute top-3 right-3 text-slate-400 hover:text-rose-500 p-1 rounded-lg"
                  title="Remover anotação"
                >
                  <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                  </svg>
                </button>
              </div>
            } @empty {
              <p class="text-center text-slate-400 py-3 text-xs">Nenhuma nota de evolução adicionada.</p>
            }
          </div>
        </div>

        <!-- Card 4: Prescrições Medicamentosas e Checagem -->
        <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs space-y-4" formGroupName="clinicalCase">
          <div class="flex items-center justify-between border-b border-slate-100 pb-3">
            <div class="flex items-center gap-2">
              <span class="w-6 h-6 rounded-lg bg-clinical-100 text-clinical-700 flex items-center justify-center font-bold text-xs">4</span>
              <h3 class="text-sm font-black text-slate-900">Prescrições e Checagem de Administração</h3>
            </div>
            <button
              type="button"
              (click)="addPrescription()"
              class="btn-secondary text-xs py-1.5 px-3 gap-1"
            >
              <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
              </svg>
              <span>Adicionar Medicamento</span>
            </button>
          </div>

          <div formArrayName="prescriptions" class="space-y-3">
            @for (rx of prescriptionsArray.controls; track $index) {
              <div [formGroupName]="$index" class="p-3.5 bg-slate-50 border border-slate-200 rounded-2xl relative space-y-2">
                <div class="grid grid-cols-1 sm:grid-cols-4 gap-3 pr-8">
                  <input
                    type="text"
                    formControlName="medication"
                    placeholder="Medicamento (Ex: Heparina)"
                    class="px-3 py-2 bg-white border border-slate-200 rounded-xl text-xs sm:col-span-2"
                  />
                  <input
                    type="text"
                    formControlName="dosage"
                    placeholder="Dose (Ex: 5.000 UI)"
                    class="px-3 py-2 bg-white border border-slate-200 rounded-xl text-xs"
                  />
                  <input
                    type="text"
                    formControlName="route"
                    placeholder="Via (Ex: SC / EV)"
                    class="px-3 py-2 bg-white border border-slate-200 rounded-xl text-xs"
                  />
                </div>
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 pr-8">
                  <input
                    type="text"
                    formControlName="frequency"
                    placeholder="Frequência (Ex: 8/8h)"
                    class="px-3 py-2 bg-white border border-slate-200 rounded-xl text-xs"
                  />
                  <input
                    type="text"
                    formControlName="administrationCheck"
                    placeholder="Checagem (Ex: Administrado 14h por Enf. Beatriz)"
                    class="px-3 py-2 bg-white border border-slate-200 rounded-xl text-xs text-clinical-900"
                  />
                </div>
                <button
                  type="button"
                  (click)="removePrescription($index)"
                  class="absolute top-3 right-3 text-slate-400 hover:text-rose-500 p-1 rounded-lg"
                  title="Remover medicamento"
                >
                  <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                  </svg>
                </button>
              </div>
            } @empty {
              <p class="text-center text-slate-400 py-3 text-xs">Nenhum medicamento prescrito cadastrado.</p>
            }
          </div>
        </div>

        <!-- Card 5: Exames Laboratoriais e Procedimentos -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6" formGroupName="clinicalCase">
          <!-- Exames Laboratoriais -->
          <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs space-y-4">
            <div class="flex items-center justify-between border-b border-slate-100 pb-3">
              <div class="flex items-center gap-2">
                <span class="w-6 h-6 rounded-lg bg-clinical-100 text-clinical-700 flex items-center justify-center font-bold text-xs">5</span>
                <h3 class="text-sm font-black text-slate-900">Exames Laboratoriais</h3>
              </div>
              <button
                type="button"
                (click)="addLabExam()"
                class="btn-secondary text-xs py-1.5 px-3 gap-1"
              >
                <span>Adicionar</span>
              </button>
            </div>

            <div formArrayName="labExams" class="space-y-3">
              @for (exam of labExamsArray.controls; track $index) {
                <div [formGroupName]="$index" class="p-3 bg-slate-50 border border-slate-200 rounded-2xl relative space-y-2">
                  <div class="grid grid-cols-2 gap-2 pr-7">
                    <input
                      type="text"
                      formControlName="examName"
                      placeholder="Exame (Ex: Hemoglobina)"
                      class="px-2.5 py-1.5 bg-white border border-slate-200 rounded-xl text-xs"
                    />
                    <input
                      type="text"
                      formControlName="result"
                      placeholder="Resultado (Ex: 6.8 g/dL)"
                      class="px-2.5 py-1.5 bg-white border border-slate-200 rounded-xl text-xs font-bold"
                    />
                  </div>
                  <div class="grid grid-cols-2 gap-2 pr-7">
                    <input
                      type="text"
                      formControlName="referenceValue"
                      placeholder="Ref: 12 - 16 g/dL"
                      class="px-2.5 py-1.5 bg-white border border-slate-200 rounded-xl text-xs"
                    />
                    <input
                      type="text"
                      formControlName="date"
                      placeholder="Data (Ex: D+1 07:00)"
                      class="px-2.5 py-1.5 bg-white border border-slate-200 rounded-xl text-xs"
                    />
                  </div>
                  <button
                    type="button"
                    (click)="removeLabExam($index)"
                    class="absolute top-2 right-2 text-slate-400 hover:text-rose-500 p-1"
                  >
                    <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                  </button>
                </div>
              } @empty {
                <p class="text-center text-slate-400 py-3 text-xs">Nenhum exame laboratorial registrado.</p>
              }
            </div>
          </div>

          <!-- Procedimentos Cirúrgicos / Invasivos -->
          <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs space-y-4">
            <div class="flex items-center justify-between border-b border-slate-100 pb-3">
              <div class="flex items-center gap-2">
                <span class="w-6 h-6 rounded-lg bg-clinical-100 text-clinical-700 flex items-center justify-center font-bold text-xs">6</span>
                <h3 class="text-sm font-black text-slate-900">Procedimentos Cirúrgicos</h3>
              </div>
              <button
                type="button"
                (click)="addProcedure()"
                class="btn-secondary text-xs py-1.5 px-3 gap-1"
              >
                <span>Adicionar</span>
              </button>
            </div>

            <div formArrayName="procedures" class="space-y-3">
              @for (proc of proceduresArray.controls; track $index) {
                <div [formGroupName]="$index" class="p-3 bg-slate-50 border border-slate-200 rounded-2xl relative space-y-2">
                  <div class="grid grid-cols-2 gap-2 pr-7">
                    <input
                      type="text"
                      formControlName="procedureName"
                      placeholder="Procedimento (Ex: Laparotomia)"
                      class="px-2.5 py-1.5 bg-white border border-slate-200 rounded-xl text-xs"
                    />
                    <input
                      type="text"
                      formControlName="date"
                      placeholder="Data (Ex: 11/09/2026)"
                      class="px-2.5 py-1.5 bg-white border border-slate-200 rounded-xl text-xs"
                    />
                  </div>
                  <input
                    type="text"
                    formControlName="description"
                    placeholder="Descrição / Achados cirúrgicos"
                    class="w-full px-2.5 py-1.5 bg-white border border-slate-200 rounded-xl text-xs pr-7"
                  />
                  <button
                    type="button"
                    (click)="removeProcedure($index)"
                    class="absolute top-2 right-2 text-slate-400 hover:text-rose-500 p-1"
                  >
                    <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                  </button>
                </div>
              } @empty {
                <p class="text-center text-slate-400 py-3 text-xs">Nenhum procedimento invasivo registrado.</p>
              }
            </div>
          </div>
        </div>

        <!-- Botões de Finalização -->
        <div class="flex items-center justify-end gap-3 pt-4">
          <button
            type="button"
            (click)="goBack()"
            class="btn-secondary text-xs py-2.5 px-5"
          >
            Cancelar
          </button>
          <button
            type="submit"
            [disabled]="form.invalid || isSaving()"
            class="btn-primary text-xs py-2.5 px-6"
          >
            @if (isSaving()) {
              <span>Salvando Atividade...</span>
            } @else {
              <span>{{ isEditMode ? 'Salvar Alterações' : 'Publicar Atividade na Turma' }}</span>
            }
          </button>
        </div>
      </form>
    </div>
  `,
})
export class ActivityFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly activityService = inject(ActivityService);
  private readonly classService = inject(AcademicClassService);
  private readonly toast = inject(ToastService);

  readonly classes = signal<AcademicClassResponseDTO[]>([]);
  readonly isSaving = signal(false);

  activityId: string | null = null;
  targetClassId: string | null = null;

  form: FormGroup = this.fb.group({
    classId: ['', [Validators.required]],
    title: ['', [Validators.required, Validators.maxLength(150)]],
    description: ['', [Validators.required]],
    deadline: ['', [Validators.required]],
    clinicalCase: this.fb.group({
      patientName: ['', [Validators.required]],
      age: [null],
      gender: ['Feminino'],
      bed: [''],
      admissionDate: [''],
      patientDays: [1, [Validators.required, Validators.min(1)]],
      admissionNotes: [''],
      evolutionNotes: this.fb.array([]),
      prescriptions: this.fb.array([]),
      labExams: this.fb.array([]),
      procedures: this.fb.array([]),
    }),
  });

  get isEditMode(): boolean {
    return !!this.activityId;
  }

  get clinicalCaseGroup(): FormGroup {
    return this.form.get('clinicalCase') as FormGroup;
  }

  get evolutionNotesArray(): FormArray {
    return this.clinicalCaseGroup.get('evolutionNotes') as FormArray;
  }

  get prescriptionsArray(): FormArray {
    return this.clinicalCaseGroup.get('prescriptions') as FormArray;
  }

  get labExamsArray(): FormArray {
    return this.clinicalCaseGroup.get('labExams') as FormArray;
  }

  get proceduresArray(): FormArray {
    return this.clinicalCaseGroup.get('procedures') as FormArray;
  }

  ngOnInit(): void {
    this.activityId = this.route.snapshot.paramMap.get('id');
    this.targetClassId = this.route.snapshot.queryParamMap.get('classId');

    if (this.targetClassId) {
      this.form.patchValue({ classId: this.targetClassId });
    }

    this.loadClasses();

    if (this.activityId) {
      this.loadActivity(this.activityId);
    }
  }

  loadClasses(): void {
    this.classService.getMyClasses(0, 50).subscribe({
      next: (res) => this.classes.set(res.data.content),
    });
  }

  loadActivity(id: string): void {
    this.activityService.getActivityById(id).subscribe({
      next: (res) => {
        const a = res.data;
        this.targetClassId = a.classId;

        // Converter deadline para formato datetime-local (YYYY-MM-DDTHH:mm)
        const d = new Date(a.deadline);
        const isoLocal = new Date(d.getTime() - d.getTimezoneOffset() * 60000)
          .toISOString()
          .slice(0, 16);

        this.form.patchValue({
          classId: a.classId,
          title: a.title,
          description: a.description,
          deadline: isoLocal,
        });

        const cc = a.clinicalCaseData;
        if (cc) {
          this.clinicalCaseGroup.patchValue({
            patientName: cc.patientName,
            age: cc.age,
            gender: cc.gender || 'Feminino',
            bed: cc.bed,
            admissionDate: cc.admissionDate,
            patientDays: cc.patientDays || 1,
            admissionNotes: cc.admissionNotes,
          });

          this.evolutionNotesArray.clear();
          cc.evolutionNotes?.forEach((n) => this.addEvolutionNote(n.dateTime, n.professionalRole, n.note));

          this.prescriptionsArray.clear();
          cc.prescriptions?.forEach((p) =>
            this.addPrescription(p.medication, p.dosage, p.route, p.frequency, p.administrationCheck)
          );

          this.labExamsArray.clear();
          cc.labExams?.forEach((e) => this.addLabExam(e.examName, e.result, e.referenceValue, e.date));

          this.proceduresArray.clear();
          cc.procedures?.forEach((pr) => this.addProcedure(pr.procedureName, pr.description, pr.date));
        }
      },
      error: () => this.toast.error('Erro ao carregar atividade.'),
    });
  }

  addEvolutionNote(dateTime = '', professionalRole = '', note = ''): void {
    this.evolutionNotesArray.push(
      this.fb.group({
        dateTime: [dateTime],
        professionalRole: [professionalRole],
        note: [note],
      })
    );
  }

  removeEvolutionNote(index: number): void {
    this.evolutionNotesArray.removeAt(index);
  }

  addPrescription(medication = '', dosage = '', route = '', frequency = '', administrationCheck = ''): void {
    this.prescriptionsArray.push(
      this.fb.group({
        medication: [medication],
        dosage: [dosage],
        route: [route],
        frequency: [frequency],
        administrationCheck: [administrationCheck],
      })
    );
  }

  removePrescription(index: number): void {
    this.prescriptionsArray.removeAt(index);
  }

  addLabExam(examName = '', result = '', referenceValue = '', date = ''): void {
    this.labExamsArray.push(
      this.fb.group({
        examName: [examName],
        result: [result],
        referenceValue: [referenceValue],
        date: [date],
      })
    );
  }

  removeLabExam(index: number): void {
    this.labExamsArray.removeAt(index);
  }

  addProcedure(procedureName = '', description = '', date = ''): void {
    this.proceduresArray.push(
      this.fb.group({
        procedureName: [procedureName],
        description: [description],
        date: [date],
      })
    );
  }

  removeProcedure(index: number): void {
    this.proceduresArray.removeAt(index);
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.toast.error('Por favor, preencha todos os campos obrigatórios.');
      return;
    }

    this.isSaving.set(true);
    const v = this.form.value;

    const clinicalCaseData: ClinicalCaseData = {
      patientName: v.clinicalCase.patientName,
      age: v.clinicalCase.age,
      gender: v.clinicalCase.gender,
      bed: v.clinicalCase.bed,
      admissionDate: v.clinicalCase.admissionDate,
      patientDays: v.clinicalCase.patientDays,
      admissionNotes: v.clinicalCase.admissionNotes,
      evolutionNotes: v.clinicalCase.evolutionNotes,
      prescriptions: v.clinicalCase.prescriptions,
      labExams: v.clinicalCase.labExams,
      procedures: v.clinicalCase.procedures,
    };

    const deadlineInstant = new Date(v.deadline).toISOString();

    if (this.isEditMode && this.activityId) {
      this.activityService
        .updateActivity(this.activityId, {
          title: v.title,
          description: v.description,
          clinicalCaseData,
          deadline: deadlineInstant,
        })
        .subscribe({
          next: () => {
            this.isSaving.set(false);
            this.toast.success('Atividade avaliativa atualizada com sucesso!');
            this.goBack();
          },
          error: (err) => {
            this.isSaving.set(false);
            this.toast.error(err?.error?.message || 'Erro ao atualizar atividade.');
          },
        });
    } else {
      this.activityService
        .createActivity({
          classId: v.classId,
          title: v.title,
          description: v.description,
          clinicalCaseData,
          deadline: deadlineInstant,
        })
        .subscribe({
          next: () => {
            this.isSaving.set(false);
            this.toast.success('Atividade avaliativa criada com sucesso!');
            this.goBack();
          },
          error: (err) => {
            this.isSaving.set(false);
            this.toast.error(err?.error?.message || 'Erro ao criar atividade.');
          },
        });
    }
  }

  goBack(): void {
    if (this.targetClassId) {
      this.router.navigate(['/academic/classes', this.targetClassId]);
    } else {
      this.router.navigate(['/academic/classes']);
    }
  }
}
