-- ====================================================================
-- SIGEA: Sistema Inteligente de Gestão de Eventos Adversos
-- Script DML: Modelos Canônicos de Prontuários Simulados em JSONB
-- Cobertura dos 6 Módulos Especializados da Metodologia IHI-GTT
-- Conformidade Ética: CEP/UFS - CAAE nº 91836925.8.0000.5546 (100% Fictício)
-- ====================================================================

-- 1. MÓDULO MEDICAÇÃO (M)
INSERT INTO clinical_case_templates (
    id, title, description, module_code, primary_trigger_code, expected_severity, clinical_case_data, is_system_template
) VALUES (
    'c1000000-0000-0000-0000-000000000001',
    'Caso Clínico 01: Insuficiência Renal Aguda Nefrotóxica por Vancomicina',
    'Investigação de evento adverso associado ao uso de antimicrobiano nefrotóxico sem monitorização da vancocinemia sérica, culminando em necessidade de hemodiálise de urgência.',
    'M',
    'M5',
    'F',
    '{
        "patientName": "Givaldo Bispo Menezes",
        "age": 63,
        "gender": "Masculino",
        "bed": "Leito 10 - UTI Adulto",
        "admissionDate": "2026-03-10",
        "patientDays": 6,
        "admissionNotes": "Paciente admitido na UTI por choque séptico secundário a pneumonia nosocomial. Iniciado esquema antimicrobiano empírico ampliado.",
        "evolutionNotes": [
            {
                "date": "2026-03-10 10:00",
                "role": "Médico Intensivista",
                "content": "Paciente em ventilação mecânica sob sedoanalgesia. Prescrita Vancomicina 1g EV a cada 12h. Função renal basal preservada (Creatinina 1.0 mg/dL)."
            },
            {
                "date": "2026-03-13 14:30",
                "role": "Enfermeiro Assistencial",
                "content": "Débito urinário em queda progressiva nas últimas 12 horas (250 mL/12h). Diurese concentrada e colúrica em bolsa coletora."
            },
            {
                "date": "2026-03-14 09:00",
                "role": "Nefrologista",
                "content": "Creatinina sérica quadruplicou em relação ao basal (1.0 -> 4.6 mg/dL). Vancocinemia de vale tóxica em 48 mcg/mL. Injúria Renal Aguda estágio 3 (KDIGO). Indicada hemodiálise de urgência."
            }
        ],
        "prescriptions": [
            {
                "medication": "Vancomicina 1g EV em infusão de 60 min",
                "dosage": "1g EV 12/12h",
                "checked": true
            },
            {
                "medication": "Furosemida 20mg EV",
                "dosage": "1 ampola EV 8/8h",
                "checked": true
            },
            {
                "medication": "Suspensão imediata da Vancomicina",
                "dosage": "Dose suspensa",
                "checked": true
            }
        ],
        "labExams": [
            {
                "examName": "Creatinina Sérica Basal",
                "result": "1.0 mg/dL",
                "referenceRange": "0.7 a 1.2 mg/dL"
            },
            {
                "examName": "Creatinina Sérica (D5)",
                "result": "4.6 mg/dL (Alerta: elevação > 4x)",
                "referenceRange": "0.7 a 1.2 mg/dL"
            },
            {
                "examName": "Ureia Sérica",
                "result": "142 mg/dL",
                "referenceRange": "15 a 45 mg/dL"
            },
            {
                "examName": "Vancocinemia Sérica de Vale",
                "result": "48 mcg/mL (Nível tóxico)",
                "referenceRange": "15 a 20 mcg/mL"
            }
        ],
        "procedures": [
            {
                "procedureName": "Punção de Veia Jugular Interna Direita para Cateter Duplo Lúmen",
                "details": "Cateter Shaldon implantado sem intercorrências imediatas para acesso dialítico."
            },
            {
                "procedureName": "Hemodiálise Contínua em UTI",
                "details": "Sessão de diálise de urgência por oligúria persistente e uremia aguda."
            }
        ]
    }',
    TRUE
) ON CONFLICT (id) DO NOTHING;

-- 2. MÓDULO CUIDADOS (C)
INSERT INTO clinical_case_templates (
    id, title, description, module_code, primary_trigger_code, expected_severity, clinical_case_data, is_system_template
) VALUES (
    'c2000000-0000-0000-0000-000000000001',
    'Caso Clínico 02: Queda do Leito com Fratura Transtrocanteriana em Idoso',
    'Evento adverso assistencial relacionado à ausência de contenção preventiva e manutenção de grades do leito arriadas em enfermaria clínica.',
    'C',
    'C7',
    'F',
    '{
        "patientName": "Severino dos Ramos Lima",
        "age": 79,
        "gender": "Masculino",
        "bed": "Leito 204-A - Enfermaria Geriátrica",
        "admissionDate": "2026-03-12",
        "patientDays": 10,
        "admissionNotes": "Paciente idoso internado para compensação de DPOC descompensada e insuficiência cardíaca crônica.",
        "evolutionNotes": [
            {
                "date": "2026-03-14 22:30",
                "role": "Técnico de Enfermagem",
                "content": "Administrado Zolpidem conforme prescrição médica. Grades do leito mantidas arriadas para conforto do acompanhante."
            },
            {
                "date": "2026-03-15 03:20",
                "role": "Enfermeiro de Plantão",
                "content": "Paciente encontrado caído ao solo ao lado do leito. Queixa de dor intensa em quadril direito, membro inferior encurtado e em rotação externa."
            },
            {
                "date": "2026-03-15 05:00",
                "role": "Ortopedista",
                "content": "Radiografia confirma fratura transtrocanteriana de fêmur direito decorrente do impacto da queda. Indicada osteossíntese cirúrgica de urgência."
            }
        ],
        "prescriptions": [
            {
                "medication": "Zolpidem 10mg VO",
                "dosage": "1 comprimido à noite",
                "checked": true
            },
            {
                "medication": "Dipirona 1g EV",
                "dosage": "1 ampola EV se dor",
                "checked": true
            },
            {
                "medication": "Morfina 2mg EV",
                "dosage": "1 ampola EV SOS",
                "checked": true
            }
        ],
        "labExams": [
            {
                "examName": "Hemoglobina pré-queda",
                "result": "11.5 g/dL",
                "referenceRange": "12.0 a 16.0 g/dL"
            },
            {
                "examName": "Hemoglobina pós-operatório",
                "result": "8.8 g/dL (Queda por sangramento operatório da fratura)",
                "referenceRange": "12.0 a 16.0 g/dL"
            }
        ],
        "procedures": [
            {
                "procedureName": "Radiografia de Bacia e Fêmur Direito",
                "details": "Fratura transtrocanteriana com desvio ósseo significativo."
            },
            {
                "procedureName": "Osteossíntese com Haste Intramedular",
                "details": "Cirurgia ortopédica para fixação da fratura em Centro Cirúrgico."
            }
        ]
    }',
    TRUE
) ON CONFLICT (id) DO NOTHING;

-- 3. MÓDULO CIRÚRGICO (S)
INSERT INTO clinical_case_templates (
    id, title, description, module_code, primary_trigger_code, expected_severity, clinical_case_data, is_system_template
) VALUES (
    'c3000000-0000-0000-0000-000000000001',
    'Caso Clínico 03: Hemorragia Intra-abdominal e Reabordagem Cirúrgica na RPA',
    'Evento adverso cirúrgico com sangramento agudo decorrente de soltura de clipe da artéria cística após colecistectomia videolaparoscópica.',
    'S',
    'S1',
    'F',
    '{
        "patientName": "Clarice Mendonça Prado",
        "age": 48,
        "gender": "Feminino",
        "bed": "Leito 03 - Recuperação Pós-Anestésica (RPA)",
        "admissionDate": "2026-03-15",
        "patientDays": 5,
        "admissionNotes": "Submetida a colecistectomia videolaparoscópica eletiva por colelitíase sintomática.",
        "evolutionNotes": [
            {
                "date": "2026-03-15 11:30",
                "role": "Enfermeiro da RPA",
                "content": "Paciente admitida lúcida e orientada na RPA. Sinais vitais basais estáveis (PA 120/80 mmHg, FC 78 bpm)."
            },
            {
                "date": "2026-03-15 13:00",
                "role": "Enfermeiro da RPA",
                "content": "Evolui com sudorese fria, palidez cutâneo-mucosa importante e hipotensão grave (PA 70/40 mmHg, FC 138 bpm). Dreno abdominal exteriorizou 800 mL de sangue vivo em 40 minutos."
            },
            {
                "date": "2026-03-15 13:20",
                "role": "Cirurgião Geral",
                "content": "Choque hemorrágico agudo. Indicada reabertura cirúrgica e conversão para laparotomia exploradora imediata."
            }
        ],
        "prescriptions": [
            {
                "medication": "Ringer Lactato 1000 mL",
                "dosage": "EV aberto em bólus",
                "checked": true
            },
            {
                "medication": "Concentrado de Hemácias 2 Bolsas",
                "dosage": "EV sob urgência",
                "checked": true
            }
        ],
        "labExams": [
            {
                "examName": "Hematócrito Pré-operatório",
                "result": "39 %",
                "referenceRange": "36 a 46 %"
            },
            {
                "examName": "Hematócrito na RPA",
                "result": "22 % (Queda de 43% em relação ao basal)",
                "referenceRange": "36 a 46 %"
            },
            {
                "examName": "Hemoglobina na RPA",
                "result": "7.2 g/dL",
                "referenceRange": "12 a 16 g/dL"
            }
        ],
        "procedures": [
            {
                "procedureName": "Laparotomia Exploradora de Emergência",
                "details": "Identificada soltura de clipe da artéria cística com hemoperitônio de 1.200 mL. Realizada ligadura cirúrgica e hemostasia."
            },
            {
                "procedureName": "Transfusão de Hemoderivados",
                "details": "Transfusão de 2 concentrados de hemácias intraoperatórios."
            }
        ]
    }',
    TRUE
) ON CONFLICT (id) DO NOTHING;

-- 4. MÓDULO UTI (I)
INSERT INTO clinical_case_templates (
    id, title, description, module_code, primary_trigger_code, expected_severity, clinical_case_data, is_system_template
) VALUES (
    'c4000000-0000-0000-0000-000000000001',
    'Caso Clínico 04: Pneumonia Associada à Ventilação Mecânica (PAV) com Extubação Acidental',
    'Evento adverso em cuidados intensivos com agitação psicomotora, extubação acidental e desenvolvimento de infecção respiratória hospitalar.',
    'I',
    'I1',
    'G',
    '{
        "patientName": "Valdemar Santana Filho",
        "age": 59,
        "gender": "Masculino",
        "bed": "Leito 06 - UTI Geral",
        "admissionDate": "2026-03-18",
        "patientDays": 14,
        "admissionNotes": "Internado na UTI por traumatismo cranioencefálico moderado necessitando de via aérea avançada.",
        "evolutionNotes": [
            {
                "date": "2026-03-21 04:10",
                "role": "Fisioterapeuta",
                "content": "Paciente em agitação no leito, fixação do tubo traqueal cedeu, acarretando extubação acidental. Reintubação de emergência com múltiplas tentativas e broncoaspiração."
            },
            {
                "date": "2026-03-24 10:00",
                "role": "Infectologista",
                "content": "Piora de parâmetros ventilatórios, secreção purulenta abundante pelo TOT e infiltrado alveolar novo em base pulmonar direita. Diagnosticada PAV."
            }
        ],
        "prescriptions": [
            {
                "medication": "Meropenem 1g EV",
                "dosage": "1g EV 8/8h em infusão estendida",
                "checked": true
            },
            {
                "medication": "Polimixina B 1.000.000 UI",
                "dosage": "1.000.000 UI EV 12/12h",
                "checked": true
            }
        ],
        "labExams": [
            {
                "examName": "Leucócitos Totais",
                "result": "22.800 /mm³ (16% de bastonetes)",
                "referenceRange": "4.000 a 10.000 /mm³"
            },
            {
                "examName": "Cultura de Aspirado Traqueal",
                "result": "> 1.000.000 UFC/mL Pseudomonas aeruginosa MDR",
                "referenceRange": "Negativo"
            },
            {
                "examName": "Relação PaO2/FiO2",
                "result": "135 (SARA Moderada)",
                "referenceRange": "> 300"
            }
        ],
        "procedures": [
            {
                "procedureName": "Reintubação Traqueal de Emergência",
                "details": "Procedimento realizado sob urgência pós-extubação acidental."
            },
            {
                "procedureName": "Broncoscopia com Lavagem Broncoalveolar",
                "details": "Coleta microbiológica profunda e toalete brônquica."
            }
        ]
    }',
    TRUE
) ON CONFLICT (id) DO NOTHING;

-- 5. MÓDULO PERINATAL (P)
INSERT INTO clinical_case_templates (
    id, title, description, module_code, primary_trigger_code, expected_severity, clinical_case_data, is_system_template
) VALUES (
    'c5000000-0000-0000-0000-000000000001',
    'Caso Clínico 05: Hemorragia Pós-Parto Grave por Atonia Uterina',
    'Complicação puerperal com hemorragia de grande porte (> 1.000 mL) decorrente de atonia uterina, exigindo ocitócicos e balão hemostático.',
    'P',
    'P4',
    'E',
    '{
        "patientName": "Camila Santos Vasconcelos",
        "age": 28,
        "gender": "Feminino",
        "bed": "Leito 12 - Centro Obstétrico / Maternidade",
        "admissionDate": "2026-03-20",
        "patientDays": 4,
        "admissionNotes": "Secundigesta admitida em fase ativa de trabalho de parto eutócico.",
        "evolutionNotes": [
            {
                "date": "2026-03-20 16:30",
                "role": "Enfermeiro Obstetra",
                "content": "Parto normal sem intercorrências às 16:15h. Recém-nascido hígido, Apgar 9/10."
            },
            {
                "date": "2026-03-20 17:10",
                "role": "Médico Obstetra",
                "content": "Sangramento transvaginal volumoso e contínuo. Útero atônico, amolecido à palpação, acima da cicatriz umbilical. Estimada perda de 1.400 mL de sangue. Iniciado protocolo de choque hemorrágico puerperal."
            }
        ],
        "prescriptions": [
            {
                "medication": "Ocitocina 20 UI",
                "dosage": "20 UI em 500 mL de SF 0.9% EV a 250 mL/h",
                "checked": true
            },
            {
                "medication": "Metilergometrina 0.2mg",
                "dosage": "1 ampola IM dose única",
                "checked": true
            },
            {
                "medication": "Ácido Tranexâmico 1g",
                "dosage": "1g EV diluído em 100 mL de SF",
                "checked": true
            }
        ],
        "labExams": [
            {
                "examName": "Hemoglobina Pré-parto",
                "result": "12.8 g/dL",
                "referenceRange": "12.0 a 15.5 g/dL"
            },
            {
                "examName": "Hemoglobina Pós-hemorragia",
                "result": "8.0 g/dL",
                "referenceRange": "12.0 a 15.5 g/dL"
            }
        ],
        "procedures": [
            {
                "procedureName": "Tamponamento com Balão Intrauterino de Bakri",
                "details": "Infundidos 350 mL de soro morno para contenção do sítio placentário."
            },
            {
                "procedureName": "Transfusão de Concentrado de Hemácias",
                "details": "Infusão de 2 bolsas de concentrado de hemácias."
            }
        ]
    }',
    TRUE
) ON CONFLICT (id) DO NOTHING;

-- 6. MÓDULO URGÊNCIA (E)
INSERT INTO clinical_case_templates (
    id, title, description, module_code, primary_trigger_code, expected_severity, clinical_case_data, is_system_template
) VALUES (
    'c6000000-0000-0000-0000-000000000001',
    'Caso Clínico 06: Hipoglicemia Severa e Readmissão na Emergência em 12 Horas',
    'Readmissão no serviço de emergência em menos de 24 horas por hipoglicemia medicamentosa grave decorrente de dose excessiva de insulina e orientação insuficiente.',
    'E',
    'E1',
    'E',
    '{
        "patientName": "José Raimundo Teles",
        "age": 66,
        "gender": "Masculino",
        "bed": "Leito 04 - Sala Vermelha / Emergência",
        "admissionDate": "2026-03-22",
        "patientDays": 2,
        "admissionNotes": "Atendido às 07:00h no pronto-socorro com hiperglicemia e liberado às 09:00h. Readmitido às 21:00h pelo SAMU em rebaixamento de consciência.",
        "evolutionNotes": [
            {
                "date": "2026-03-22 21:15",
                "role": "Médico Emergencista",
                "content": "Paciente trazido pelo SAMU em estado comatoso (Glasgow 7), sudorese profusa e pele pegajosa 12h após liberação do serviço. Glicemia capilar de 28 mg/dL (M4). Aplicada Glicose 50% EV com despertar progressivo."
            }
        ],
        "prescriptions": [
            {
                "medication": "Glicose 50% 40 mL",
                "dosage": "EV em bólus imediato",
                "checked": true
            },
            {
                "medication": "Soro Glicosado 10% 500 mL",
                "dosage": "EV contínuo a 80 mL/h",
                "checked": true
            }
        ],
        "labExams": [
            {
                "examName": "Glicemia Capilar na Chegada",
                "result": "28 mg/dL (Hipoglicemia grave)",
                "referenceRange": "70 a 99 mg/dL"
            },
            {
                "examName": "Glicemia Plasmática Central",
                "result": "33 mg/dL",
                "referenceRange": "70 a 99 mg/dL"
            }
        ],
        "procedures": [
            {
                "procedureName": "Acesso Venoso Periférico e Monitorização Contínua",
                "details": "Estabilização hemodinâmica e infusão glicosada na Sala Vermelha."
            }
        ]
    }',
    TRUE
) ON CONFLICT (id) DO NOTHING;
