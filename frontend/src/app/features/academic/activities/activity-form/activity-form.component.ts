import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ActivityService } from '../../../../core/services/activity.service';
import { AcademicClassService } from '../../../../core/services/academic-class.service';
import { ToastService } from '../../../../core/services/toast.service';
import { AcademicClassResponseDTO } from '../../../../core/models/academic-class.model';
import { ActivityDetailDTO, ClinicalCaseData } from '../../../../core/models/activity.model';
import { ClinicalCaseTemplateService } from '../../../../core/services/clinical-case-template.service';
import { ClinicalCaseTemplateResponseDTO } from '../../../../core/models/clinical-case-template.model';

/**
 * Criação e edição de Atividades Avaliativas com Prontuário Simulado estruturado.
 */
@Component({
  selector: 'app-activity-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './activity-form.component.html',
})
export class ActivityFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly activityService = inject(ActivityService);
  private readonly classService = inject(AcademicClassService);
  private readonly templateService = inject(ClinicalCaseTemplateService);
  private readonly toast = inject(ToastService);

  readonly classes = signal<AcademicClassResponseDTO[]>([]);
  readonly templates = signal<ClinicalCaseTemplateResponseDTO[]>([]);
  readonly selectedTemplateId = signal<string>('');
  readonly activeTemplate = signal<ClinicalCaseTemplateResponseDTO | null>(null);
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
    this.loadTemplates();

    if (this.activityId) {
      this.loadActivity(this.activityId);
    }
  }

  loadClasses(): void {
    this.classService.getMyClasses(0, 50).subscribe({
      next: (res) => this.classes.set(res.data.content),
    });
  }

  loadTemplates(): void {
    this.templateService.listTemplates().subscribe({
      next: (res) => this.templates.set(res.data),
      error: () => this.toast.error('Erro ao carregar catálogo de casos clínicos.'),
    });
  }

  onTemplateSelect(id: string): void {
    this.selectedTemplateId.set(id);
    const found = this.templates().find((t) => t.id === id) || null;
    this.activeTemplate.set(found);
  }

  applySelectedTemplate(): void {
    const tpl = this.activeTemplate();
    if (!tpl || !tpl.clinicalCaseData) {
      this.toast.error('Nenhum modelo selecionado.');
      return;
    }

    const currentTitle = this.form.get('title')?.value;
    const currentDesc = this.form.get('description')?.value;

    this.form.patchValue({
      title: currentTitle || tpl.title,
      description: currentDesc || tpl.description,
    });

    const cc = tpl.clinicalCaseData;
    this.clinicalCaseGroup.patchValue({
      patientName: cc.patientName || '',
      age: cc.age ?? null,
      gender: cc.gender || 'Feminino',
      bed: cc.bed || '',
      admissionDate: cc.admissionDate || '',
      patientDays: cc.patientDays || 1,
      admissionNotes: cc.admissionNotes || '',
    });

    this.evolutionNotesArray.clear();
    (cc.evolutionNotes || []).forEach((n: any) => {
      const { dateTime, professionalRole, note } = this.extractEvolutionNote(n);
      this.addEvolutionNote(dateTime, professionalRole, note);
    });

    this.prescriptionsArray.clear();
    (cc.prescriptions || []).forEach((p: any) => {
      const { medication, dosage, route, frequency, check } = this.extractPrescription(p);
      this.addPrescription(medication, dosage, route, frequency, check);
    });

    this.labExamsArray.clear();
    (cc.labExams || []).forEach((e: any) => {
      const { examName, result, ref, date } = this.extractLabExam(e);
      this.addLabExam(examName, result, ref, date);
    });

    this.proceduresArray.clear();
    (cc.procedures || []).forEach((pr: any) => {
      const { procedureName, description, date } = this.extractProcedure(pr);
      this.addProcedure(procedureName, description, date);
    });

    this.toast.success(`Modelo "${tpl.title}" aplicado ao prontuário com sucesso!`);
  }

  private extractEvolutionNote(n: any): { dateTime: string; professionalRole: string; note: string } {
    const dateTime = n.dateTime ? n.dateTime : (n.date ? n.date : '');
    const professionalRole = n.professionalRole ? n.professionalRole : (n.role ? n.role : '');
    const note = n.note ? n.note : (n.content ? n.content : '');
    return { dateTime, professionalRole, note };
  }

  private extractPrescription(p: any): { medication: string; dosage: string; route: string; frequency: string; check: string } {
    let check = p.administrationCheck ? p.administrationCheck : '';
    if (!check && p.checked) {
      check = 'Checado/Administrado';
    }
    return {
      medication: p.medication ? p.medication : '',
      dosage: p.dosage ? p.dosage : '',
      route: p.route ? p.route : '',
      frequency: p.frequency ? p.frequency : '',
      check,
    };
  }

  private extractLabExam(e: any): { examName: string; result: string; ref: string; date: string } {
    const ref = e.referenceValue ? e.referenceValue : (e.referenceRange ? e.referenceRange : '');
    return {
      examName: e.examName ? e.examName : '',
      result: e.result ? e.result : '',
      ref,
      date: e.date ? e.date : '',
    };
  }

  private extractProcedure(pr: any): { procedureName: string; description: string; date: string } {
    const description = pr.description ? pr.description : (pr.details ? pr.details : '');
    return {
      procedureName: pr.procedureName ? pr.procedureName : '',
      description,
      date: pr.date ? pr.date : '',
    };
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
