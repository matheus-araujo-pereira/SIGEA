#!/usr/bin/env python3
"""
Gerador de Carga Hiper-Realista para Homologação do SIGEA-GTT (UFS).
Gera o script database/dml/03_seed_homologation.sql com dados clínicos completos.
"""

import json
import uuid

def esc(text):
    if text is None:
        return "NULL"
    return "'" + str(text).replace("'", "''") + "'"

def json_esc(obj):
    return "'" + json.dumps(obj, ensure_ascii=False).replace("'", "''") + "'::jsonb"

def main():
    lines = []
    lines.append("-- ====================================================================")
    lines.append("-- SIGEA-GTT: Sistema Inteligente de Gestão de Eventos Adversos")
    lines.append("-- Script DML: Carga de Dados Hiper-Realista para Fase de Homologação")
    lines.append("-- Universidade Federal de Sergipe (UFS) - DCOMP / Enfermagem")
    lines.append("-- Autor Líder: Matheus Araujo Pereira")
    lines.append("-- ====================================================================\n")

    lines.append("-- 1. Limpeza de Dados Pré-Existentes (Preserva Módulos, Gatilhos e Gravidades)")
    lines.append("TRUNCATE TABLE activity_submissions CASCADE;")
    lines.append("TRUNCATE TABLE activities CASCADE;")
    lines.append("TRUNCATE TABLE class_students CASCADE;")
    lines.append("TRUNCATE TABLE academic_classes CASCADE;")
    lines.append("TRUNCATE TABLE users CASCADE;\n")

    # Hashes de senha (ambos conferem SigeaUFS@2026)
    pwd_hash = "$2a$12$e2gg/066sAz11ugh4tdsBu9z4hakyHQqxafgz.N8wfcmYrW98xY.2"

    admin_id = "a1000000-0000-0000-0000-000000000001"
    prof_ana_id = "a2000000-0000-0000-0000-000000000001"
    prof_gilton_id = "a2000000-0000-0000-0000-000000000002"

    lines.append("-- 2. Inserção dos Usuários Principais (1 Administrador e 2 Professores)")
    lines.append(f"""INSERT INTO users (id, full_name, email, password_hash, role, registration_number, is_active, must_change_password) VALUES
({esc(admin_id)}, 'Matheus Araujo Pereira', 'matheusaraujopereira@academico.ufs.br', '{pwd_hash}', 'ADMIN', NULL, TRUE, FALSE),
({esc(prof_ana_id)}, 'Profª. Drª. Ana Waleska Silva Patrício', 'anawaleska@academico.ufs.br', '{pwd_hash}', 'PROFESSOR', NULL, TRUE, FALSE),
({esc(prof_gilton_id)}, 'Prof. Dr. Gilton José Ferreira da Silva', 'gilton@academico.ufs.br', '{pwd_hash}', 'PROFESSOR', NULL, TRUE, FALSE);\n""")

    # 4 Turmas
    # Turma 1: Ana Waleska, 2025.2, fechada
    # Turma 2: Gilton, 2025.2, fechada
    # Turma 3: Ana Waleska, 2026.1, ativa
    # Turma 4: Gilton, 2026.1, ativa
    class_1_id = "c1000000-0000-0000-0000-000000000001"
    class_2_id = "c1000000-0000-0000-0000-000000000002"
    class_3_id = "c1000000-0000-0000-0000-000000000003"
    class_4_id = "c1000000-0000-0000-0000-000000000004"

    lines.append("-- 3. Inserção das 4 Turmas Acadêmicas (2 por professor, 2 por período)")
    lines.append(f"""INSERT INTO academic_classes (id, subject_name, class_code, academic_period, professor_id, is_closed) VALUES
({esc(class_1_id)}, 'Enfermagem na Atenção à Saúde do Adulto e do Idoso I', 'T01', '2025.2', {esc(prof_ana_id)}, TRUE),
({esc(class_2_id)}, 'Gestão da Qualidade e Segurança do Paciente', 'T01', '2025.2', {esc(prof_gilton_id)}, TRUE),
({esc(class_3_id)}, 'Enfermagem em Terapia Intensiva e Cuidados Críticos', 'T01', '2026.1', {esc(prof_ana_id)}, FALSE),
({esc(class_4_id)}, 'Auditoria Clínica e Metodologia Global Trigger Tool', 'T02', '2026.1', {esc(prof_gilton_id)}, FALSE);\n""")

    # Alunos (120 alunos brasileiros reais)
    first_names_m = [
        "Lucas", "Gabriel", "Matheus", "Guilherme", "Felipe", "Rafael", "Rodrigo", "Thiago",
        "Leonardo", "Bruno", "Daniel", "Eduardo", "João Pedro", "Vinicius", "Caio", "Marcos"
    ]
    first_names_f = [
        "Beatriz", "Larissa", "Mariana", "Camila", "Gabriela", "Isabela", "Juliana", "Letícia",
        "Carolina", "Fernanda", "Amanda", "Bruna", "Débora", "Helena", "Natália", "Renata"
    ]
    last_names = [
        "Silva", "Santos", "Oliveira", "Souza", "Lima", "Pereira", "Ferreira", "Costa",
        "Rodrigues", "Almeida", "Nascimento", "Alves", "Carvalho", "Menezes", "Araújo",
        "Ribeiro", "Barbosa", "Cardoso", "Freitas", "Cavalcanti", "Montenegro", "Dantas"
    ]

    students = []
    # Test student explicitly
    students.append({
        "id": "e1000000-0000-0000-0000-000000000001",
        "name": "Lucas Gabriel Fontes",
        "email": "lucas.fontes@academico.ufs.br",
        "reg": "20260001001",
        "must_change": False
    })
    # Student with must_change_password=True for testing first-login
    students.append({
        "id": "e1000000-0000-0000-0000-000000000030",
        "name": "Mariana Barreto Santana",
        "email": "primeiro.acesso@academico.ufs.br",
        "reg": "20260001030",
        "must_change": True
    })

    # Generate 118 other unique students
    idx = 1
    for cohort_year in [2025, 2026]:
        for cohort_class in [1, 2]:
            start_num = 1 if (cohort_year == 2025 or cohort_class == 2) else 3
            end_num = 31
            for num in range(start_num, end_num):
                if len(students) >= 120:
                    break
                idx += 1
                is_female = (idx % 2 == 0)
                fn = first_names_f[(idx % len(first_names_f))] if is_female else first_names_m[(idx % len(first_names_m))]
                ln1 = last_names[(idx * 3) % len(last_names)]
                ln2 = last_names[(idx * 7) % len(last_names)]
                if ln1 == ln2:
                    ln2 = "Nunes"
                full_name = f"{fn} {ln1} {ln2}"
                # Clean email username
                email_user = f"{fn.lower().replace(' ', '')}.{ln1.lower()}{cohort_year % 100}{num}"
                email = f"{email_user}@academico.ufs.br"
                reg = f"{cohort_year}{cohort_class:02d}{num:04d}"
                st_id = f"e{cohort_year % 100:02d}{cohort_class:02d}000-0000-0000-0000-{num:012d}"
                students.append({
                    "id": st_id,
                    "name": full_name,
                    "email": email,
                    "reg": reg,
                    "must_change": False
                })

    lines.append("-- 4. Inserção dos Alunos (120 Alunos Reais da UFS)")
    student_values = []
    for s in students:
        student_values.append(
            f"({esc(s['id'])}, {esc(s['name'])}, {esc(s['email'])}, '{pwd_hash}', 'STUDENT', {esc(s['reg'])}, TRUE, {'TRUE' if s['must_change'] else 'FALSE'})"
        )
    lines.append("INSERT INTO users (id, full_name, email, password_hash, role, registration_number, is_active, must_change_password) VALUES\n" + ",\n".join(student_values) + ";\n")

    # Matrículas nas 4 turmas (30 alunos em cada turma)
    # Turma 1: alunos 0 a 29 (2025.2)
    # Turma 2: alunos 30 a 59 (2025.2)
    # Turma 3: alunos 0 (Lucas), 1 (Mariana/primeiro.acesso) e 60 a 87 (2026.1)
    # Turma 4: alunos 0 (Lucas) e 88 a 116 e 59 (2026.1)
    lines.append("-- 5. Matrícula dos Alunos nas Turmas (Exatamente 30 alunos por turma)")
    class_students_values = []

    # Turma 1
    t1_students = students[0:30]
    for s in t1_students:
        class_students_values.append(f"({esc(class_1_id)}, {esc(s['id'])})")

    # Turma 2
    t2_students = students[30:60]
    for s in t2_students:
        class_students_values.append(f"({esc(class_2_id)}, {esc(s['id'])})")

    # Turma 3 (inclui Lucas Fontes e Primeiro Acesso)
    t3_students = [students[0], students[1]] + students[60:88]
    for s in t3_students:
        class_students_values.append(f"({esc(class_3_id)}, {esc(s['id'])})")

    # Turma 4 (inclui Lucas Fontes)
    t4_students = [students[0]] + students[88:117]
    for s in t4_students:
        class_students_values.append(f"({esc(class_4_id)}, {esc(s['id'])})")

    lines.append("INSERT INTO class_students (class_id, student_id) VALUES\n" + ",\n".join(class_students_values) + ";\n")

    # 12 Atividades Clínicas
    # Casos clínicos detalhados
    act_ids = [f"d1000000-0000-0000-0000-{i:012d}" for i in range(1, 13)]

    activities = [
        # Turma 1 (Profª Ana Waleska - 2025.2)
        {
            "id": act_ids[0],
            "class_id": class_1_id,
            "title": "Caso Clínico 01: Rastreamento de Sangramento Oculto em Pós-Operatório Ortopédico",
            "description": "Paciente idoso submetido à artroplastia total de quadril, em profilaxia antitrombótica farmacológica com Enoxaparina. Analisar os gatilhos de medicação e cuidados associados à queda súbita nos parâmetros hematológicos.",
            "deadline": "2025-10-20T23:59:59Z",
            "case": {
                "patientName": "José de Souza Ramos",
                "age": 68,
                "gender": "M",
                "bed": "Leito 204-B - Enfermaria Ortopédica HU-UFS",
                "admissionDate": "2025-10-02",
                "patientDays": 8,
                "admissionNotes": "Paciente admitido para realização eletiva de artroplastia total de quadril direito (ATQ). Hipertenso controlado, sem histórico prévio de coagulopatias. Procedimento cirúrgico realizado sob raquianestesia sem intercorrências imediatas.",
                "evolutionNotes": [
                    {
                        "dateTime": "2025-10-03 08:30",
                        "professionalRole": "Médico Ortopedista",
                        "note": "1º DPO: Paciente estável, queixa de dor moderada em ferida operatória controlada com dipirona. Dreno de sucção com débito hemático de 120 mL em 12h. Mantida Enoxaparina 40mg SC/dia para tromboprofilaxia."
                    },
                    {
                        "dateTime": "2025-10-04 14:00",
                        "professionalRole": "Enfermeira Assistencial",
                        "note": "2º DPO: Paciente refere tontura intensa ao tentar sedestação no leito, palidez cutaneomucosa 2+/4+, sudorese fria. PA: 90x55 mmHg, FC: 112 bpm. Curativo operatório com sangramento moderado ativo peridreno. Comunicado ao médico plantonista."
                    },
                    {
                        "dateTime": "2025-10-04 15:15",
                        "professionalRole": "Médico Plantonista",
                        "note": "Avaliado paciente hipotenso e taquicárdico. Solicitado hemograma de urgência e prova cruzada para 2 concentrados de hemácias. Suspenso uso de Enoxaparina. Iniciada hidratação venosa rápida com Ringer Lactato 1000 mL."
                    },
                    {
                        "dateTime": "2025-10-04 17:00",
                        "professionalRole": "Enfermeira Assistencial",
                        "note": "Instalada 1ª bolsa de Concentrado de Hemácias (CH) nº 849204. Paciente monitorizado, sem sinais de reação transfusional imediata. PA estabilizada em 110x70 mmHg."
                    }
                ],
                "prescriptions": [
                    {
                        "medication": "Enoxaparina sódica",
                        "dosage": "40 mg",
                        "route": "Subcutânea",
                        "frequency": "1x ao dia (20h)",
                        "administrationCheck": "Administrado no 1º DPO às 20h. Suspenso no 2º DPO às 15h."
                    },
                    {
                        "medication": "Dipirona sódica",
                        "dosage": "1 g",
                        "route": "Endovenosa",
                        "frequency": "6/6h se dor",
                        "administrationCheck": "Administrado às 06h e 12h por Enfª Larissa."
                    },
                    {
                        "medication": "Concentrado de Hemácias",
                        "dosage": "2 unidades",
                        "route": "Endovenosa",
                        "frequency": "Infusão contínua em 2h cada",
                        "administrationCheck": "1ª unidade transfundida às 17h; 2ª unidade às 20h."
                    }
                ],
                "labExams": [
                    {
                        "examName": "Hemoglobina (Hb) - Admissional",
                        "result": "13.8 g/dL",
                        "referenceValue": "13.0 - 17.5 g/dL",
                        "date": "2025-10-02"
                    },
                    {
                        "examName": "Hematócrito (Hct) - Admissional",
                        "result": "41.5%",
                        "referenceValue": "40.0 - 50.0%",
                        "date": "2025-10-02"
                    },
                    {
                        "examName": "Hemoglobina (Hb) - 2º DPO Urgência",
                        "result": "8.1 g/dL (Queda de 41,3%)",
                        "referenceValue": "13.0 - 17.5 g/dL",
                        "date": "2025-10-04"
                    },
                    {
                        "examName": "Hematócrito (Hct) - 2º DPO Urgência",
                        "result": "24.6%",
                        "referenceValue": "40.0 - 50.0%",
                        "date": "2025-10-04"
                    },
                    {
                        "examName": "Plaquetas",
                        "result": "185.000 /mm³",
                        "referenceValue": "150.000 - 450.000 /mm³",
                        "date": "2025-10-04"
                    }
                ],
                "procedures": [
                    {
                        "procedureName": "Artroplastia Total de Quadril Direito",
                        "description": "Colocação de prótese cimentada sem intercorrências técnicas aparentes.",
                        "date": "2025-10-02"
                    },
                    {
                        "procedureName": "Transfusão de Hemocomponentes",
                        "description": "Transfusão de 2 bolsas de concentrado de hemácias isogrupo isofator sob monitorização.",
                        "date": "2025-10-04"
                    }
                ]
            }
        },
        {
            "id": act_ids[1],
            "class_id": class_1_id,
            "title": "Caso Clínico 02: Reação de Hipersensibilidade Aguda a Antimicrobiano",
            "description": "Investigação de evento adverso a medicamentos em paciente internada para tratamento de pneumonia comunitária que evoluiu com rash maculopapular difuso e prurido após administração de Ceftriaxona.",
            "deadline": "2025-11-15T23:59:59Z",
            "case": {
                "patientName": "Maria das Graças Conceição",
                "age": 54,
                "gender": "F",
                "bed": "Leito 112-A - Clínica Médica HU-UFS",
                "admissionDate": "2025-11-01",
                "patientDays": 6,
                "admissionNotes": "Paciente admitida com tosse produtiva, febre alta e dispneia leve. Radiografia de tórax evidencia consolidação em lobo inferior direito. Prescrita Ceftriaxona 2g EV 1x/dia.",
                "evolutionNotes": [
                    {
                        "dateTime": "2025-11-03 10:30",
                        "professionalRole": "Enfermeiro Assistencial",
                        "note": "Durante infusão da 3ª dose de Ceftriaxona, paciente passa a queixar-se de calor súbito no rosto, prurido intenso em tronco e membros superiores. Ao exame: placas eritematosas disseminadas (rash urticariforme). Sem estridor laríngeo ou sibilância. Infusão pausada imediatamente."
                    },
                    {
                        "dateTime": "2025-11-03 10:45",
                        "professionalRole": "Médico Plantonista",
                        "note": "Avaliada reação anafilactoide moderada secundária a betalactâmico. Prescrita Clemastina (anti-histamínico) 2 mg EV imediata e Hidrocortisona 200 mg EV. Suspensa definitivamente Ceftriaxona da prescrição médica."
                    },
                    {
                        "dateTime": "2025-11-03 12:00",
                        "professionalRole": "Enfermeiro Assistencial",
                        "note": "Paciente refere melhora substancial do prurido, regressão progressiva das placas eritematosas cutâneas. Sinais vitais: PA 120x80 mmHg, SpO2 97% em ar ambiente. Registro de alergia a cefalosporinas afixado no prontuário e na pulseira de identificação."
                    }
                ],
                "prescriptions": [
                    {
                        "medication": "Ceftriaxona sódica",
                        "dosage": "2 g",
                        "route": "Endovenosa",
                        "frequency": "1x ao dia (10h)",
                        "administrationCheck": "Interrompida aos 15 min de infusão por reação cutânea aguda."
                    },
                    {
                        "medication": "Clemastina (anti-histamínico)",
                        "dosage": "2 mg",
                        "route": "Endovenosa",
                        "frequency": "Dose única de emergência",
                        "administrationCheck": "Administrado às 10h50 por Enf. Carlos."
                    },
                    {
                        "medication": "Hidrocortisona",
                        "dosage": "200 mg",
                        "route": "Endovenosa",
                        "frequency": "Dose única",
                        "administrationCheck": "Administrado às 10h55 por Enf. Carlos."
                    }
                ],
                "labExams": [
                    {
                        "examName": "Hemograma com Leucograma",
                        "result": "Leucócitos: 11.200/mm³ (Eosinófilos: 7%)",
                        "referenceValue": "4.000 - 10.000 /mm³ (Eos: 1-4%)",
                        "date": "2025-11-03"
                    }
                ],
                "procedures": []
            }
        },
        {
            "id": act_ids[2],
            "class_id": class_1_id,
            "title": "Caso Clínico 03: Lesão por Pressão Estágio II em Paciente sob Contenção Mecânica no Leito",
            "description": "Auditoria de evento adverso de cuidados: surgimento de lesão por pressão sacral em paciente neurológico acamado submetido a contenção física prolongada por agitação psicomotora.",
            "deadline": "2025-12-10T23:59:59Z",
            "case": {
                "patientName": "Raimundo Nonato de Jesus",
                "age": 77,
                "gender": "M",
                "bed": "Leito 105-C - Enfermaria de Neurologia HU-UFS",
                "admissionDate": "2025-11-20",
                "patientDays": 12,
                "admissionNotes": "Admitido por sequela motora de Acidente Vascular Cerebral Isquêmico (AVCI), apresentando hemiplegia à esquerda, afasia motora e desorientação temporal.",
                "evolutionNotes": [
                    {
                        "dateTime": "2025-11-22 22:00",
                        "professionalRole": "Médico Plantonista",
                        "note": "Paciente agitado no leito, com tentativas de retirada da sonda nasoenteral e queda do leito. Prescrito haloperidol e indicada contenção física nos quatro membros para segurança."
                    },
                    {
                        "dateTime": "2025-11-25 09:00",
                        "professionalRole": "Enfermeira de Pele",
                        "note": "Durante banho no leito e inspeção tegumentar, constatada presença de lesão por pressão em região sacral medindo 3x2 cm, com perda parcial da espessura dérmica, leito eritematoso sem necrose (Estágio II). Histórico de contenção mecânica contínua por 72h com ausência de registro formal de mudança de decúbito no período noturno."
                    }
                ],
                "prescriptions": [
                    {
                        "medication": "Haloperidol",
                        "dosage": "5 mg",
                        "route": "Intramuscular",
                        "frequency": "Se agitação intensa",
                        "administrationCheck": "Administrado às 22h30 do dia 22/11."
                    }
                ],
                "labExams": [
                    {
                        "examName": "Albumina sérica",
                        "result": "2.8 g/dL (Hipoproteinemia moderada)",
                        "referenceValue": "3.5 - 5.2 g/dL",
                        "date": "2025-11-21"
                    }
                ],
                "procedures": [
                    {
                        "procedureName": "Contenção Mecânica no Leito",
                        "description": "Contenção física com ataduras em membros superiores e inferiores por 72 horas.",
                        "date": "2025-11-22"
                    },
                    {
                        "procedureName": "Curativo com Placa de Hidrocoloide",
                        "description": "Limpeza da lesão sacral com SF 0,9% e aplicação de placa de hidrocoloide estéril.",
                        "date": "2025-11-25"
                    }
                ]
            }
        },

        # Turma 2 (Prof. Gilton - 2025.2)
        {
            "id": act_ids[3],
            "class_id": class_2_id,
            "title": "Caso Clínico 04: Erro de Medicação e Choque Hipoglicêmico Grave por Insulina NPH",
            "description": "Análise da cadeia medicamentosa com aplicação de ferramentas da qualidade (Ishikawa e 5W2H) após administração inadvertida de dose triplicada de Insulina NPH.",
            "deadline": "2025-10-25T23:59:59Z",
            "case": {
                "patientName": "Antônio Fagundes de Oliveira",
                "age": 61,
                "gender": "M",
                "bed": "Leito 302-A - Clínica Médica HU-UFS",
                "admissionDate": "2025-10-12",
                "patientDays": 7,
                "admissionNotes": "Paciente com Diabetes Mellitus tipo 2 descompensado e pé diabético infectado. Prescrita Insulina NPH 14 UI antes do café da manhã.",
                "evolutionNotes": [
                    {
                        "dateTime": "2025-10-15 07:30",
                        "professionalRole": "Técnico de Enfermagem",
                        "note": "Administrada insulina matinal conforme prescrição médica (leitura ambígua na folha: administradas 40 UI em seringa de 100 UI por confusão de caligrafia com 14 UI)."
                    },
                    {
                        "dateTime": "2025-10-15 10:45",
                        "professionalRole": "Enfermeira Chefe",
                        "note": "Paciente encontrado desacordado no leito, sudorese profusa e torporoso, sem resposta a chamados verbais. Glicemia capilar de urgência: 32 mg/dL. Acionado time de resposta rápida."
                    },
                    {
                        "dateTime": "2025-10-15 10:55",
                        "professionalRole": "Médico do Time de Resposta Rápida",
                        "note": "Quadro de hipoglicemia severa neuroglicopênica secundária a sobredose iatrogênica de insulina. Administradas 4 ampolas de Glicose 50% EV em bolus. Paciente recuperou nível de consciência em 15 minutos. Solicitada abertura de notificação de quase-evento e EA grave no NUSP."
                    }
                ],
                "prescriptions": [
                    {
                        "medication": "Insulina NPH Humana",
                        "dosage": "14 UI (Administradas 40 UI por erro)",
                        "route": "Subcutânea",
                        "frequency": "1x ao dia (07h)",
                        "administrationCheck": "Administrado às 07:30 pelo plantão."
                    },
                    {
                        "medication": "Glicose Hipertônica 50%",
                        "dosage": "4 ampolas (80 mL)",
                        "route": "Endovenosa",
                        "frequency": "Bolus imediato",
                        "administrationCheck": "Administrado às 10:52 pela equipe do TRR."
                    }
                ],
                "labExams": [
                    {
                        "examName": "Glicemia Capilar (HGT) - Evento",
                        "result": "32 mg/dL",
                        "referenceValue": "70 - 99 mg/dL",
                        "date": "2025-10-15"
                    },
                    {
                        "examName": "Glicemia Capilar pós-resgate (15 min)",
                        "result": "118 mg/dL",
                        "referenceValue": "70 - 99 mg/dL",
                        "date": "2025-10-15"
                    }
                ],
                "procedures": []
            }
        },
        {
            "id": act_ids[4],
            "class_id": class_2_id,
            "title": "Caso Clínico 05: Superdosagem de Varfarina com Sangramento Ativo e Reversão com Vitamina K",
            "description": "Investigação retrospectiva GTT sobre monitoramento de anticoagulação oral em paciente com Fibrilação Atrial crônica que atingiu RNI > 7.0 com hematoma muscular e sangramento gengival.",
            "deadline": "2025-11-20T23:59:59Z",
            "case": {
                "patientName": "Neuza Barbosa Santos",
                "age": 70,
                "gender": "F",
                "bed": "Leito 210-A - Cardiologia HU-UFS",
                "admissionDate": "2025-11-05",
                "patientDays": 10,
                "admissionNotes": "Paciente em uso contínuo de Varfarina 5mg para profilaxia de tromboembolismo em FA. Internada para tratamento de infecção urinária tratada com ciprofloxacino (potencializador da varfarina).",
                "evolutionNotes": [
                    {
                        "dateTime": "2025-11-09 11:00",
                        "professionalRole": "Enfermeira Assistencial",
                        "note": "Paciente relata sangramento gengival espontâneo ao escovar os dentes e hematoma volumoso em coxa direita sem trauma relatado. Coletado coagulograma com urgência."
                    },
                    {
                        "dateTime": "2025-11-09 13:30",
                        "professionalRole": "Médico Hematologista",
                        "note": "RNI laboratorial alarmante de 7.8 (valor alvo 2.0 - 3.0). Risco iminente de hemorragia cerebral ou intracavitária. Indicada suspensão imediata da Varfarina e infusão venosa lenta de Fitomenadiona (Vitamina K1) 10 mg diluída em SF 0,9%."
                    }
                ],
                "prescriptions": [
                    {
                        "medication": "Varfarina sódica",
                        "dosage": "5 mg",
                        "route": "Via Oral",
                        "frequency": "1x ao dia (18h)",
                        "administrationCheck": "Suspensa imediatamente em 09/11 às 13:30."
                    },
                    {
                        "medication": "Fitomenadiona (Vitamina K1)",
                        "dosage": "10 mg",
                        "route": "Endovenosa",
                        "frequency": "Dose única em infusão lenta de 30 min",
                        "administrationCheck": "Administrado às 14:00 por Enfª Camila."
                    }
                ],
                "labExams": [
                    {
                        "examName": "RNI / INR - Admissão",
                        "result": "2.4",
                        "referenceValue": "2.0 - 3.0 (em anticoagulação)",
                        "date": "2025-11-05"
                    },
                    {
                        "examName": "RNI / INR - Dia do Evento",
                        "result": "7.8 (Gatilho M3)",
                        "referenceValue": "2.0 - 3.0",
                        "date": "2025-11-09"
                    }
                ],
                "procedures": []
            }
        },
        {
            "id": act_ids[5],
            "class_id": class_2_id,
            "title": "Caso Clínico 06: Readmissão Precoce em 12 Dias de Paciente Cardiopata Pós-Alta",
            "description": "Auditoria de transição do cuidado hospitalar e reconciliação medicamentosa: reinternação precoce de paciente com Insuficiência Cardíaca por orientações incompletas de desmame e dieta no sumário de alta.",
            "deadline": "2025-12-18T23:59:59Z",
            "case": {
                "patientName": "Manoel Vieira dos Passos",
                "age": 65,
                "gender": "M",
                "bed": "Leito 108-B - Urgência Cardíaca HU-UFS",
                "admissionDate": "2025-12-02",
                "patientDays": 6,
                "admissionNotes": "Paciente com diagnóstico de IC perfil B descompensada. Recebeu alta em 20/11/2025 do mesmo hospital e retorna no 12º dia pós-alta com anasarca e dispneia aos mínimos esforços.",
                "evolutionNotes": [
                    {
                        "dateTime": "2025-12-02 09:30",
                        "professionalRole": "Médico Emergencista",
                        "note": "Constatada readmissão hospitalar em 12 dias após alta anterior (Gatilho C9). Na anamnese com a família, constatou-se que a receita de alta não especificava a continuidade da furosemida nem a restrição hidrossalina, resultando em sobrecarga volêmica aguda."
                    }
                ],
                "prescriptions": [
                    {
                        "medication": "Furosemida",
                        "dosage": "40 mg",
                        "route": "Endovenosa",
                        "frequency": "8/8h",
                        "administrationCheck": "Iniciado protocolo de descompensação volêmica."
                    }
                ],
                "labExams": [
                    {
                        "examName": "BNP (Peptídeo Natriurético Cerebral)",
                        "result": "1.850 pg/mL",
                        "referenceValue": "< 100 pg/mL",
                        "date": "2025-12-02"
                    }
                ],
                "procedures": []
            }
        },

        # Turma 3 (Profª Ana Waleska - 2026.1)
        {
            "id": act_ids[6],
            "class_id": class_3_id,
            "title": "Caso Clínico 07: Pneumonia Associada à Ventilação Mecânica (PAV) e Sepse na UTI Adulto",
            "description": "Vigilância epidemiológica e rastreamento GTT no ambiente de terapia intensiva: identificação de infecção do trato respiratório inferior relacionada à assistência à saúde com isolamento de patógeno multirresistente.",
            "deadline": "2026-04-10T23:59:59Z",
            "case": {
                "patientName": "Severina Santos da Silva",
                "age": 59,
                "gender": "F",
                "bed": "Leito 06 - UTI Adulto HU-UFS",
                "admissionDate": "2026-03-01",
                "patientDays": 15,
                "admissionNotes": "Admitida na UTI após cirurgia de revascularização miocárdica de urgência, em ventilação mecânica invasiva (VMI) via tubo orotraqueal e sedoanalgesia contínua.",
                "evolutionNotes": [
                    {
                        "dateTime": "2026-03-07 10:00",
                        "professionalRole": "Fisioterapeuta Intensivista",
                        "note": "6º dia de VMI: Piora significativa da mecânica ventilatória, necessidade de elevação de FiO2 de 35% para 60% e PEEP de 8 para 12 cmH2O para manter SpO2 > 92%. Secreção traqueal abundante, purulenta e espessa."
                    },
                    {
                        "dateTime": "2026-03-07 14:00",
                        "professionalRole": "Médico Intensivista",
                        "note": "Curva febril atingindo 38.9ºC, hipotensão com necessidade de início de Noradrenalina 0.15 mcg/kg/min. Leucocitose importante. Rx de tórax no leito revela infiltrado alveolar novo bilateral. Coletadas hemoculturas e aspirado traqueal quantitativo. Fechado critério clínico para PAV (Gatilhos I1 e C11)."
                    }
                ],
                "prescriptions": [
                    {
                        "medication": "Meropenem",
                        "dosage": "1 g",
                        "route": "Endovenosa",
                        "frequency": "8/8h em infusão estendida de 3h",
                        "administrationCheck": "Iniciado empiricamente após coleta de culturas."
                    },
                    {
                        "medication": "Noradrenalina",
                        "dosage": "0.15 mcg/kg/min",
                        "route": "Endovenosa em Bomba de Infusão",
                        "frequency": "Contínua com desmame guiado por PAM > 65",
                        "administrationCheck": "Infusão ativa em cateter venoso central."
                    }
                ],
                "labExams": [
                    {
                        "examName": "Hemocultura (2 amostras)",
                        "result": "Positiva para Acinetobacter baumannii resistente a carbapenêmicos",
                        "referenceValue": "Negativa",
                        "date": "2026-03-09"
                    },
                    {
                        "examName": "Procalcitonina sérica",
                        "result": "8.4 ng/mL (Sepse grave)",
                        "referenceValue": "< 0.5 ng/mL",
                        "date": "2026-03-07"
                    }
                ],
                "procedures": [
                    {
                        "procedureName": "Intubação Orotraqueal e Ventilação Mecânica Invasiva",
                        "description": "TOT nº 7.5 com cuff inflado a 25 cmH2O.",
                        "date": "2026-03-01"
                    }
                ]
            }
        },
        {
            "id": act_ids[7],
            "class_id": class_3_id,
            "title": "Caso Clínico 08: Extubação Acidental Não Programada e Parada Respiratória Revertida",
            "description": "Rastreamento de evento adverso de gravidade extrema (Categoria H - necessidade de intervenção para manter a vida): falha de fixação do tubo orotraqueal e sedação inadequada com extubação inadvertida.",
            "deadline": "2026-05-15T23:59:59Z",
            "case": {
                "patientName": "Clóvis Santana dos Reis",
                "age": 52,
                "gender": "M",
                "bed": "Leito 03 - UTI Adulto HU-UFS",
                "admissionDate": "2026-03-12",
                "patientDays": 5,
                "admissionNotes": "Paciente politraumatizado vítima de colisão auto x anteparo fixo, com contusão pulmonar e trauma cranioencefálico moderado.",
                "evolutionNotes": [
                    {
                        "dateTime": "2026-03-14 03:15",
                        "professionalRole": "Técnico de Enfermagem",
                        "note": "Durante plantão noturno, paciente com despertar súbito e agitação psicomotora vigorosa conseguiu soltar a fixação do tubo com as mãos e removeu completamente o tubo orotraqueal (extubação não programada). Imediatamente acionada equipe médica."
                    },
                    {
                        "dateTime": "2026-03-14 03:18",
                        "professionalRole": "Médico Intensivista",
                        "note": "Paciente em apneia, cianose labial severa e bradicardia crítica (FC 32 bpm) evoluindo para Parada Cardiorrespiratória em AESP. Iniciadas manobras de RCP, ventilação com ambú com O2 a 100%, administrada 1 ampola de Adrenalina 1 mg EV e procedida reintubação orotraqueal imediata com TOT nº 8.5 com guia metálico. Retorno da circulação espontânea após 3 minutos de RCP (Gatilhos I4, C2 e Severidade H)."
                    }
                ],
                "prescriptions": [
                    {
                        "medication": "Adrenalina (Epinefrina)",
                        "dosage": "1 mg",
                        "route": "Endovenosa",
                        "frequency": "Bolus imediato na PCR",
                        "administrationCheck": "Administrado às 03h20 por Enf. Beatriz."
                    },
                    {
                        "medication": "Midazolam + Fentanil",
                        "dosage": "Solução padrão contínua",
                        "route": "Endovenosa em BIC",
                        "frequency": "Ajuste para RASS -4 a -5",
                        "administrationCheck": "Reiniciada infusão às 03h30."
                    }
                ],
                "labExams": [
                    {
                        "examName": "Gasometria Arterial pós-RCP",
                        "result": "pH: 7.18, pO2: 62 mmHg, pCO2: 58 mmHg, HCO3: 16 mmol/L, Lactato: 4.8 mmol/L",
                        "referenceValue": "pH 7.35-7.45, pO2 80-100",
                        "date": "2026-03-14"
                    }
                ],
                "procedures": [
                    {
                        "procedureName": "Reintubação Orotraqueal de Emergência",
                        "description": "Laringoscopia direta urgente sob via aérea difícil pós-extubação acidental.",
                        "date": "2026-03-14"
                    },
                    {
                        "procedureName": "Ressuscitação Cardiopulmonar (RCP)",
                        "description": "1 ciclo de compressões torácicas de alta qualidade e ventilação assistida com RCE.",
                        "date": "2026-03-14"
                    }
                ]
            }
        },
        {
            "id": act_ids[8],
            "class_id": class_3_id,
            "title": "Caso Clínico 09: Insuficiência Renal Aguda Nefrotóxica por Vancomicina e Diálise de Urgência",
            "description": "Auditoria de dano farmacológico por toxicidade renal: elevação abrupta de creatinina sérica superior a três vezes o valor basal associada a níveis séricos supraterapêuticos de vancomicina.",
            "deadline": "2026-06-20T23:59:59Z",
            "case": {
                "patientName": "Givaldo Bispo Menezes",
                "age": 63,
                "gender": "M",
                "bed": "Leito 10 - UTI Adulto HU-UFS",
                "admissionDate": "2026-03-18",
                "patientDays": 11,
                "admissionNotes": "Internado por osteomielite de fêmur e sepse secundária. Prescrita Vancomicina 1g EV 12/12h associada a Gentamicina sem realização tempestiva de vancocinemia sérica de controle.",
                "evolutionNotes": [
                    {
                        "dateTime": "2026-03-24 11:30",
                        "professionalRole": "Médico Nefrologista",
                        "note": "Avaliação renal solicitada por oligúria (diurese 200 mL em 24h). Creatinina sérica saltou de 1.0 mg/dL (basal) para 4.6 mg/dL e ureia para 180 mg/dL. Vancocinemia de vale tóxica em 48 mcg/mL (alvo 15-20 mcg/mL). Diagnosticada Injúria Renal Aguda KDIGO 3 por nefrotoxicidade medicamentosa. Indicada suspensão imediata e implantação de Cateter de Shilley para início de hemodiálise aguda (Gatilhos M5 e C3)."
                    }
                ],
                "prescriptions": [
                    {
                        "medication": "Vancomicina",
                        "dosage": "1 g",
                        "route": "Endovenosa",
                        "frequency": "12/12h",
                        "administrationCheck": "Suspensa em 24/03 devido a falência renal aguda."
                    }
                ],
                "labExams": [
                    {
                        "examName": "Creatinina Sérica Basal",
                        "result": "1.0 mg/dL",
                        "referenceValue": "0.7 - 1.3 mg/dL",
                        "date": "2026-03-18"
                    },
                    {
                        "examName": "Creatinina Sérica - Evento",
                        "result": "4.6 mg/dL (Elevação > 4x basal - Gatilho M5)",
                        "referenceValue": "0.7 - 1.3 mg/dL",
                        "date": "2026-03-24"
                    },
                    {
                        "examName": "Nível Sérico de Vancomicina (Vale)",
                        "result": "48 mcg/mL (Nível tóxico grave)",
                        "referenceValue": "15 - 20 mcg/mL",
                        "date": "2026-03-24"
                    }
                ],
                "procedures": [
                    {
                        "procedureName": "Implante de Cateter de Hemodiálise Temporário",
                        "description": "Punção de veia jugular interna direita sob guia ultrassonográfica para inserção de duplo lúmen.",
                        "date": "2026-03-24"
                    },
                    {
                        "procedureName": "Hemodiálise Aguda Contínua / Intermitente",
                        "description": "Sessão inicial de hemodiálise com ultrafiltração de 1500 mL.",
                        "date": "2026-03-24"
                    }
                ]
            }
        },

        # Turma 4 (Prof. Gilton - 2026.1)
        {
            "id": act_ids[9],
            "class_id": class_4_id,
            "title": "Caso Clínico 10: Lesão Iatrogênica de Ducto Biliar em Colecistectomia e Conversão Aberta",
            "description": "Aplicação do módulo cirúrgico GTT: lesão de órgão adjacente intraoperatória durante videolaparoscopia, mudança de plano cirúrgico e admissão imprevista em UTI pós-operatória.",
            "deadline": "2026-04-18T23:59:59Z",
            "case": {
                "patientName": "Cláudia Valença de Carvalho",
                "age": 46,
                "gender": "F",
                "bed": "Leito 201-A - Centro Cirúrgico / UTI HU-UFS",
                "admissionDate": "2026-03-05",
                "patientDays": 9,
                "admissionNotes": "Paciente com colelitíase sintomática admitida para colecistectomia videolaparoscópica eletiva.",
                "evolutionNotes": [
                    {
                        "dateTime": "2026-03-05 16:30",
                        "professionalRole": "Médico Cirurgião Geral",
                        "note": "Durante a dissecção do triângulo de Calot por vídeo, constatada anatomia distorcida por processo inflamatório crônico. Ocorreu secção inadvertida de ducto hepático comum direito com saída biliar ativa abundante. Decidida conversão imediata para laparotomia subcostal direita aberta para reparo biliar com anastomose biliodigestiva em Y de Roux e drenagem de cavidade (Gatilhos S2 e S10)."
                    },
                    {
                        "dateTime": "2026-03-05 20:00",
                        "professionalRole": "Médico Anestesiologista",
                        "note": "Cirurgia estendida por 5 horas, sangramento estimado em 800 mL. Paciente extubada sob estabilidade, porém necessitando de monitorização intensiva hemodinâmica. Transferida para leito vago na UTI pós-operatória não programado (Gatilho S3)."
                    }
                ],
                "prescriptions": [
                    {
                        "medication": "Ceftriaxona + Metronidazol",
                        "dosage": "2 g + 500 mg",
                        "route": "Endovenosa",
                        "frequency": "Terapia cirúrgica profilática/terapêutica",
                        "administrationCheck": "Administrado no bloco cirúrgico."
                    }
                ],
                "labExams": [
                    {
                        "examName": "Bilirrubinas Totais e Frações",
                        "result": "BT: 4.8 mg/dL (Direta: 3.9 mg/dL)",
                        "referenceValue": "BT < 1.2 mg/dL",
                        "date": "2026-03-06"
                    }
                ],
                "procedures": [
                    {
                        "procedureName": "Conversão Laparoscópica para Laparotomia Aberta",
                        "description": "Mudança não planejada da abordagem cirúrgica decorrente de acidente cirúrgico biliar.",
                        "date": "2026-03-05"
                    },
                    {
                        "procedureName": "Hepaticojejunostomia em Y de Roux",
                        "description": "Reconstrução do trânsito biliar com dreno de Kehr e dreno tubular.",
                        "date": "2026-03-05"
                    }
                ]
            }
        },
        {
            "id": act_ids[10],
            "class_id": class_4_id,
            "title": "Caso Clínico 11: Depressão Respiratória na RPA Revertida com Naloxona",
            "description": "Rastreamento GTT de evento adverso pós-anestésico decorrente de sobredose de analgésicos opioides na sala de recuperação pós-anestésica, necessitando de antídoto de emergência.",
            "deadline": "2026-05-25T23:59:59Z",
            "case": {
                "patientName": "Roberto Menezes Dantas",
                "age": 55,
                "gender": "M",
                "bed": "Leito RPA-04 - Bloco Cirúrgico HU-UFS",
                "admissionDate": "2026-03-22",
                "patientDays": 3,
                "admissionNotes": "Paciente pós-osteossíntese de tíbia sob anestesia geral associada a bloqueio regional.",
                "evolutionNotes": [
                    {
                        "dateTime": "2026-03-22 15:45",
                        "professionalRole": "Enfermeira da RPA",
                        "note": "Paciente admitido na RPA sonolento. Administrados 10 mg de Morfina fracionada em 30 minutos por queixa de dor intensa. Trinta minutos depois, paciente evoluiu com bradipneia severa (FR 6 irpm), pupilas mióticas puntiformes e SpO2 caindo para 84% em cateter de O2. Acionado médico anestesista de plantão."
                    },
                    {
                        "dateTime": "2026-03-22 16:00",
                        "professionalRole": "Médico Anestesiologista",
                        "note": "Intoxicação aguda por opioide (depressão respiratória grave). Realizada ventilação assistida por bolsa-válvula-máscara e administrada Naloxona 0.4 mg EV diluída lenta. Em 2 minutos, paciente despertou, FR subiu para 16 irpm e SpO2 atingiu 98% (Gatilhos M9, M11 e Severidade E)."
                    }
                ],
                "prescriptions": [
                    {
                        "medication": "Morfina Sulfato",
                        "dosage": "10 mg",
                        "route": "Endovenosa",
                        "frequency": "Fracionado na RPA",
                        "administrationCheck": "Administrado às 15:15."
                    },
                    {
                        "medication": "Cloridrato de Naloxona",
                        "dosage": "0.4 mg",
                        "route": "Endovenosa",
                        "frequency": "Dose de resgate imediata",
                        "administrationCheck": "Administrado às 15:58 por Enfª Gabriela."
                    }
                ],
                "labExams": [],
                "procedures": [
                    {
                        "procedureName": "Ventilação sob Pressão Positiva com Máscara Facial",
                        "description": "Suporte ventilatório manual durante reversão farmacológica com antagonista opioide.",
                        "date": "2026-03-22"
                    }
                ]
            }
        },
        {
            "id": act_ids[11],
            "class_id": class_4_id,
            "title": "Caso Clínico 12: Análise Longitudinal de Múltiplos EAs e Queda com Dano em Idoso",
            "description": "Atividade integradora da metodologia Global Trigger Tool: auditoria integral de prontuário com múltiplos gatilhos encadeados (queda, lesão por pressão e readmissão) e desenho de ciclo PDCA.",
            "deadline": "2026-06-30T23:59:59Z",
            "case": {
                "patientName": "Eunice Alcantara dos Santos",
                "age": 82,
                "gender": "F",
                "bed": "Leito 101-A - Geriatria HU-UFS",
                "admissionDate": "2026-03-15",
                "patientDays": 14,
                "admissionNotes": "Paciente octogenária frágil admitida para compensação de insuficiência cardíaca e desnutrição calórico-proteica.",
                "evolutionNotes": [
                    {
                        "dateTime": "2026-03-19 04:30",
                        "professionalRole": "Técnica de Enfermagem",
                        "note": "Paciente levantou do leito desacompanhada para ir ao banheiro e sofreu queda da própria altura com trauma craniano frontal. Grades do leito estavam rebaixadas. Ao exame: hematoma subgaleal volumoso em região frontal e escoriações em cotovelo direito (Gatilho C7)."
                    },
                    {
                        "dateTime": "2026-03-19 06:00",
                        "professionalRole": "Médico Plantonista",
                        "note": "Avaliação pós-queda: Glasgow 14 (desorientada temporalmente). Solicitada Tomografia Computadorizada de Crânio urgente para descartar hemorragia intracraniana e sutura de ferimento corto-contuso superficial. Prescrita profilaxia antitetânica e repouso absoluto no leito com grades elevadas."
                    }
                ],
                "prescriptions": [
                    {
                        "medication": "Dipirona sódica",
                        "dosage": "1 g",
                        "route": "Endovenosa",
                        "frequency": "6/6h",
                        "administrationCheck": "Administrado às 06:30."
                    }
                ],
                "labExams": [
                    {
                        "examName": "Tomografia Computadorizada de Crânio",
                        "result": "Ausência de sangramentos agudos intracranianos ou fraturas ósseas. Hematoma de partes moles extracraniano.",
                        "referenceValue": "Sem alterações agudas",
                        "date": "2026-03-19"
                    }
                ],
                "procedures": [
                    {
                        "procedureName": "Sutura de Ferimento em Supercílio Direito",
                        "description": "Sutura simples com fio mononylon 5-0 (3 pontos) sob anestesia local.",
                        "date": "2026-03-19"
                    }
                ]
            }
        }
    ]

    lines.append("-- 6. Inserção das 12 Atividades Avaliativas (3 por turma)")
    act_values = []
    for a in activities:
        act_values.append(
            f"({esc(a['id'])}, {esc(a['class_id'])}, {esc(a['title'])}, {esc(a['description'])}, {json_esc(a['case'])}, '{a['deadline']}')"
        )
    lines.append("INSERT INTO activities (id, class_id, title, description, clinical_case_data, deadline) VALUES\n" + ",\n".join(act_values) + ";\n")

    # Submissões realistas
    # Turma 1: 3 atividades x 30 alunos = 90 submissões (todas avaliadas com nota e feedback)
    # Turma 2: 3 atividades x 30 alunos = 90 submissões (todas avaliadas com nota e feedback)
    # Turma 3:
    #   - Atividade 7: 28 submissões (22 avaliadas, 6 pendentes)
    #   - Atividade 8: 18 submissões (5 avaliadas, 13 pendentes)
    #   - Atividade 9: 5 submissões (0 avaliadas, 25 alunos pendentes incluindo Lucas Fontes)
    # Turma 4:
    #   - Atividade 10: 25 submissões (20 avaliadas, 5 pendentes)
    #   - Atividade 11: 15 submissões (4 avaliadas, 11 pendentes)
    #   - Atividade 12: 4 submissões (0 avaliadas, 26 alunos pendentes incluindo Lucas Fontes)
    
    # Trigger templates
    trig_c6 = {
        "triggerId": "a53e90e1-4270-4a20-9ee2-b24a80e86561",
        "triggerCode": "C6",
        "triggerName": "Queda superior a 25% nos valores de hemoglobina ou hematócrito",
        "moduleCode": "CUIDADOS",
        "isHarm": True,
        "harmSeverityLetter": "E",
        "clinicalJustification": "A hemoglobina do paciente decaiu de 13.8 g/dL para 8.1 g/dL em 48h (queda de 41,3%), associada a sangramento ativo peridreno e instabilidade pressórica decorrente de anticoagulação pós-operatória."
    }
    trig_c1 = {
        "triggerId": "7d6fd944-780b-4529-ba08-95ac86369e59",
        "triggerCode": "C1",
        "triggerName": "Transfusão de sangue, hemocomponentes ou hemoderivados",
        "moduleCode": "CUIDADOS",
        "isHarm": True,
        "harmSeverityLetter": "E",
        "clinicalJustification": "Houve necessidade de hemotransfusão de urgência de 2 concentrados de hemácias para reversão de anemia aguda pós-operatória sintomática."
    }
    trig_m7 = {
        "triggerId": "8cbc0e33-06ba-48ba-98bd-73090b1e7cd5",
        "triggerCode": "M7",
        "triggerName": "Administração de anti-histamínico",
        "moduleCode": "MEDICACAO",
        "isHarm": True,
        "harmSeverityLetter": "E",
        "clinicalJustification": "Prescrição e administração imediata de Clemastina para conter reação anafilactoide urticariforme induzida por Ceftriaxona."
    }
    trig_m4 = {
        "triggerId": "d45d49ee-1040-440b-8802-f24f441e8d22",
        "triggerCode": "M4",
        "triggerName": "Glicemia menor que 50 mg/dL",
        "moduleCode": "MEDICACAO",
        "isHarm": True,
        "harmSeverityLetter": "E",
        "clinicalJustification": "Paciente desenvolveu hipoglicemia severa (32 mg/dL) com torpor e perda transitória de consciência por administração de 40 UI de insulina no lugar de 14 UI prescritas."
    }
    trig_m3 = {
        "triggerId": "efaa6740-8a84-4bd5-8801-dd8aa7769c61",
        "triggerCode": "M3",
        "triggerName": "Razão Normalizada Internacional (INR/RNI) maior que 6",
        "moduleCode": "MEDICACAO",
        "isHarm": True,
        "harmSeverityLetter": "F",
        "clinicalJustification": "RNI laboratorial atingiu 7.8 com hematoma muscular e sangramento gengival, prolongando a hospitalização em 5 dias adicionais."
    }
    trig_i1 = {
        "triggerId": "4971f8c1-0689-4432-a02d-e9bab17766c8",
        "triggerCode": "I1",
        "triggerName": "Pneumonia com início no hospital",
        "moduleCode": "UTI",
        "isHarm": True,
        "harmSeverityLetter": "F",
        "clinicalJustification": "Desenvolvimento de PAV após 6 dias de VMI na UTI com febre, piora ventilatória e cultura positiva para Acinetobacter multirresistente."
    }
    trig_s2 = {
        "triggerId": "666391f9-d4d6-4450-856b-59cdc4072fe7",
        "triggerCode": "S2",
        "triggerName": "Mudança de procedimento",
        "moduleCode": "CIRURGICO",
        "isHarm": True,
        "harmSeverityLetter": "F",
        "clinicalJustification": "Conversão não programada de videolaparoscopia para laparotomia aberta após laceração acidental de via biliar principal intraoperatória."
    }

    def sample_quality_tools(problem):
        return {
            "ishikawa": {
                "centralProblem": problem,
                "methodCauses": ["Falta de protocolo de checagem em dupla conferência", "Legibilidade prejudicada da prescrição manual"],
                "manpowerCauses": ["Sobrecarga da equipe de enfermagem no plantão", "Fadiga e rotatividade de técnicos"],
                "materialCauses": ["Seringas de insulina sem graduação ampliada destacada", "Rótulos de ampolas semelhantes"],
                "machineCauses": ["Falta de bombas de infusão inteligentes com limite de dose", "Ausência de prontuário eletrônico com alerta de bloqueio"],
                "environmentCauses": ["Ruído e interrupções frequentes durante a preparação de medicamentos", "Iluminação inadequada no posto"],
                "measurementCauses": ["Monitoramento glicêmico com intervalos longos demais", "Demora na liberação de exames laboratoriais"]
            },
            "gutItems": [
                {"problem": "Erro na administração de medicamentos de alta vigilância", "gravity": 5, "urgency": 5, "trend": 4},
                {"problem": "Falha na comunicação de passagem de plantão", "gravity": 4, "urgency": 4, "trend": 3},
                {"problem": "Atraso no atendimento do Time de Resposta Rápida", "gravity": 4, "urgency": 3, "trend": 2}
            ],
            "fiveWTwoHItems": [
                {
                    "what": "Implantar dupla checagem obrigatória para insulinas e anticoagulantes",
                    "why": "Eliminar erros de dosagem e troca de fármacos de alto risco",
                    "where": "Todas as enfermarias e UTI do HU-UFS",
                    "when": "Próximos 30 dias",
                    "who": "Comissão de Segurança do Paciente e Enfermagem",
                    "how": "Capacitação presencial e checklist impresso no posto de medicação",
                    "howMuch": "R$ 1.500,00 (materiais informativos e cartilhas)"
                }
            ],
            "pdca": {
                "plan": "Reduzir em 80% as ocorrências de eventos adversos relacionados a medicamentos de alta vigilância em 90 dias.",
                "doPhase": "Instituir código de barras na beira do leito e treinamento de dupla checagem cega para 100% dos técnicos.",
                "checkPhase": "Auditar semanalmente as fichas de medicação e monitorar a taxa de gatilhos GTT de medicação por 1.000 pacientes-dia.",
                "actPhase": "Padronizar o Procedimento Operacional Padrão (POP-MED-04) em todo o complexo hospitalar universitário."
            },
            "swot": {
                "strengths": ["Corpo docente e preceptoria de enfermagem qualificados", "Comitê de Segurança do Paciente ativo"],
                "weaknesses": ["Sobrecarga assistencial nos horários de pico", "Prontuário parcialmente físico"],
                "opportunities": ["Apoio da Reitoria UFS para modernização e digitalização hospitalar", "Pesquisa clínica aplicada"],
                "threats": ["Desabastecimento pontual de insumos médicos hospitalares"]
            },
            "brainstormingNotes": [
                "Revisar cores das etiquetas de alerta de medicamentos de alta vigilância",
                "Fixar cartaz de 'Não Interrompa: Preparo de Medicamento em Andamento' no posto"
            ]
        }

    lines.append("-- 7. Inserção das Submissões e Avaliações de Alunos")
    submission_values = []
    sub_count = 0

    # Função auxiliar para gerar submissões
    def add_sub(act_id, st_id, trig_list, problem, is_graded, grade, feedback):
        nonlocal sub_count
        sub_count += 1
        sub_id = f"f1000000-0000-0000-0000-{sub_count:012d}"
        q_data = sample_quality_tools(problem)
        grade_str = f"{grade:.2f}" if is_graded else "NULL"
        graded_at = "'2025-11-01 14:00:00Z'" if is_graded else "NULL"
        fb_str = esc(feedback) if is_graded else "NULL"
        submission_values.append(
            f"({esc(sub_id)}, {esc(act_id)}, {esc(st_id)}, {json_esc(trig_list)}, {json_esc(q_data)}, '2025-10-18 19:30:00Z', {grade_str}, {fb_str}, {graded_at})"
        )

    # Turma 1: 30 alunos nas 3 atividades (100% entregue e corrigido)
    for i, s in enumerate(t1_students):
        # Atividade 1
        grade1 = 8.5 + (i % 4) * 0.5
        add_sub(act_ids[0], s["id"], [trig_c6, trig_c1], "Hemorragia aguda por enoxaparina em pós-operatório", True, grade1,
                f"Excelente identificação dos gatilhos C6 e C1, {s['name'].split()[0]}. A análise dos 6M no diagrama de Ishikawa contemplou com clareza o método de desmame antitrombótico e o manejo transfusional.")
        # Atividade 2
        grade2 = 8.0 + (i % 5) * 0.5
        add_sub(act_ids[1], s["id"], [trig_m7], "Reação adversa anafilactoide à Ceftriaxona", True, grade2,
                "Identificação precisa da gravidade E. Parabéns pelo detalhamento das ações corretivas no plano 5W2H.")
        # Atividade 3
        grade3 = 9.0 + (i % 3) * 0.5
        if grade3 > 10.0: grade3 = 10.0
        add_sub(act_ids[2], s["id"], [{"triggerId": "7477c128-6621-4cc8-b945-c2feba3f1faa", "triggerCode": "C8", "triggerName": "Lesões por pressão", "moduleCode": "CUIDADOS", "isHarm": True, "harmSeverityLetter": "E", "clinicalJustification": "LPP sacral estágio II decorrente de imobilidade e contenção prolongada."}], "Lesão por pressão sacral por contenção no leito", True, grade3,
                "Raciocínio clínico brilhante correlacionando a contenção mecânica prolongada ao dano tegumentar.")

    # Turma 2: 30 alunos nas 3 atividades (100% entregue e corrigido)
    for i, s in enumerate(t2_students):
        grade1 = 8.0 + (i % 5) * 0.5
        add_sub(act_ids[3], s["id"], [trig_m4], "Choque hipoglicêmico grave por erro de dosagem de insulina", True, grade1,
                f"Parabéns {s['name'].split()[0]}, a matriz GUT foi estruturada de forma impecável, priorizando a dupla checagem na alta vigilância.")
        grade2 = 8.5 + (i % 4) * 0.5
        add_sub(act_ids[4], s["id"], [trig_m3], "Superdosagem de varfarina com alargamento crítico de RNI", True, grade2,
                "Classificação de gravidade F perfeita, considerando a necessidade de extensão da internação para reversão com Fitomenadiona.")
        grade3 = 7.5 + (i % 6) * 0.5
        add_sub(act_ids[5], s["id"], [{"triggerId": "7d6fd944-780b-4529-ba08-95ac86369e59", "triggerCode": "C9", "triggerName": "Readmissão em até 30 dias após a alta", "moduleCode": "CUIDADOS", "isHarm": True, "harmSeverityLetter": "F", "clinicalJustification": "Reinternação em 12 dias por descompensação de IC devido a sumário de alta deficitário."}], "Readmissão precoce por falha na transição do cuidado", True, grade3,
                "Ótima reflexão sobre a reconciliação medicamentosa na alta hospitalar.")

    # Turma 3: 2026.1 (Ativa)
    # Atividade 7: 28 entregas (22 corrigidas, 6 aguardando correção)
    for i, s in enumerate(t3_students[0:28]):
        is_graded = (i < 22)
        grade = (8.5 + (i % 4) * 0.5) if is_graded else None
        fb = "Excelente identificação da PAV na UTI e classificação precisa de dano." if is_graded else None
        add_sub(act_ids[6], s["id"], [trig_i1], "Pneumonia associada à ventilação mecânica na UTI Adulto", is_graded, grade or 0.0, fb)

    # Atividade 8: 18 entregas (5 corrigidas, 13 aguardando correção)
    for i, s in enumerate(t3_students[0:18]):
        is_graded = (i < 5)
        grade = (9.0 + (i % 3) * 0.5) if is_graded else None
        if grade and grade > 10.0: grade = 10.0
        fb = "Análise correta da gravidade H e parada respiratória por extubação acidental." if is_graded else None
        add_sub(act_ids[7], s["id"], [{"triggerId": "706cbff3-3108-4b98-a245-511898edaeaf", "triggerCode": "I4", "triggerName": "Intubação ou reintubação", "moduleCode": "UTI", "isHarm": True, "harmSeverityLetter": "H", "clinicalJustification": "Extubação inadvertida seguida de parada respiratória e reintubação com suporte de vida."}], "Extubação acidental com parada respiratória", is_graded, grade or 0.0, fb)

    # Atividade 9: apenas 5 alunos entregaram (nenhum corrigido ainda; Lucas Fontes NÃO entregou, para testar no portal do aluno!)
    for i, s in enumerate(t3_students[5:10]):
        add_sub(act_ids[8], s["id"], [{"triggerId": "112dbf87-88ec-4231-a896-72c798bb6f92", "triggerCode": "M5", "triggerName": "Elevação de ureia ou creatinina sérica para valor duas vezes superior ao basal", "moduleCode": "MEDICACAO", "isHarm": True, "harmSeverityLetter": "F", "clinicalJustification": "Elevação de Cr para 4x o basal e diálise aguda por vancomicina."}], "Nefrotoxicidade severa por vancomicina", False, 0.0, None)

    # Turma 4: 2026.1 (Ativa)
    # Atividade 10: 25 entregas (20 corrigidas, 5 pendentes)
    for i, s in enumerate(t4_students[0:25]):
        is_graded = (i < 20)
        grade = (8.0 + (i % 5) * 0.5) if is_graded else None
        fb = "Identificação cirúrgica de alta precisão quanto aos gatilhos S2 e S10." if is_graded else None
        add_sub(act_ids[9], s["id"], [trig_s2], "Laceração de ducto biliar e conversão para cirurgia aberta", is_graded, grade or 0.0, fb)

    # Atividade 11: 15 entregas (4 corrigidas, 11 pendentes)
    for i, s in enumerate(t4_students[0:15]):
        is_graded = (i < 4)
        grade = (8.5 + (i % 4) * 0.5) if is_graded else None
        fb = "Excelente abordagem da reversão de opioide na RPA com Naloxona." if is_graded else None
        add_sub(act_ids[10], s["id"], [{"triggerId": "7ef553e4-4bdf-4420-a427-735db2b45a60", "triggerCode": "M9", "triggerName": "Administração de naloxona", "moduleCode": "MEDICACAO", "isHarm": True, "harmSeverityLetter": "E", "clinicalJustification": "Bradipneia crítica por morfina revertida tempestivamente com Naloxona."}], "Depressão respiratória pós-anestésica", is_graded, grade or 0.0, fb)

    # Atividade 12: 4 alunos entregaram (nenhum corrigido; Lucas Fontes NÃO entregou para poder resolver!)
    for i, s in enumerate(t4_students[4:8]):
        add_sub(act_ids[11], s["id"], [{"triggerId": "7d6fd944-780b-4529-ba08-95ac86369e59", "triggerCode": "C7", "triggerName": "Queda do paciente", "moduleCode": "CUIDADOS", "isHarm": True, "harmSeverityLetter": "E", "clinicalJustification": "Queda do leito com hematoma frontal e necessidade de sutura."}], "Queda de leito com trauma craniano em idoso", False, 0.0, None)

    lines.append("INSERT INTO activity_submissions (id, activity_id, student_id, identified_triggers, quality_tools_data, submission_date, grade, professor_feedback, graded_at) VALUES\n" + ",\n".join(submission_values) + ";\n")

    lines.append("-- Fim da carga de dados de homologação")

    output_path = "/home/matheus/Projetos/SIGEA-GTT/database/dml/03_seed_homologation.sql"
    with open(output_path, "w", encoding="utf-8") as f:
        f.write("\n".join(lines))
    print(f"✅ Script gerado com sucesso: {output_path} ({len(lines)} linhas)")

if __name__ == "__main__":
    main()
