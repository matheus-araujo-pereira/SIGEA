-- ====================================================================
-- SIGEA-GTT: Sistema Inteligente de Gestão de Eventos Adversos
-- Script DML: Carga de Dados Oficiais da Metodologia IHI-GTT
-- Módulos, Gatilhos e Gravidades de Dano NCC MERP
-- ====================================================================

-- 1. Classificações de Gravidade NCC MERP (Categorias A a I)
INSERT INTO harm_severities (category_letter, name, description, is_harm) VALUES
('A', 'Categoria A', 'Circunstâncias ou eventos com capacidade para causar erros.', FALSE),
('B', 'Categoria B', 'Um erro que não atingiu o paciente.', FALSE),
('C', 'Categoria C', 'Um erro que atingiu o paciente, mas não causou danos.', FALSE),
('D', 'Categoria D', 'Um erro que atingiu o paciente e exigiu monitoramento ou intervenção para confirmar que não resultou em nenhum dano ao paciente.', FALSE),
('E', 'Categoria E', 'Dano temporário ao paciente e necessidade de intervenção.', TRUE),
('F', 'Categoria F', 'Dano temporário ao paciente e necessidade de iniciar ou prolongar hospitalização.', TRUE),
('G', 'Categoria G', 'Dano permanente ao paciente.', TRUE),
('H', 'Categoria H', 'Necessidade de intervenção para manter a vida.', TRUE),
('I', 'Categoria I', 'Morte do paciente.', TRUE)
ON CONFLICT (category_letter) DO NOTHING;

-- 2. Módulos Oficiais IHI-GTT (6 Módulos Especializados)
INSERT INTO gtt_modules (id, code, name, description) VALUES
('b1000000-0000-0000-0000-000000000001', 'CUIDADOS', 'Cuidados', 'Triggers que refletem eventos adversos gerais de cuidados hospitalares.'),
('b1000000-0000-0000-0000-000000000002', 'MEDICACAO', 'Medicação', 'Triggers associados ao uso, intoxicação ou reações de medicamentos.'),
('b1000000-0000-0000-0000-000000000003', 'CIRURGICO', 'Cirúrgico', 'Triggers para detecção de complicações intra e pós-operatórias.'),
('b1000000-0000-0000-0000-000000000004', 'UTI', 'Cuidados Intensivos/Terapia Intensiva', 'Triggers específicos da unidade de terapia intensiva.'),
('b1000000-0000-0000-0000-000000000005', 'PERINATAL', 'Perinatal', 'Triggers maternos associados ao trabalho de parto e puerpério.'),
('b1000000-0000-0000-0000-000000000006', 'URGENCIA', 'Serviço de Urgência/Pronto Atendimento', 'Triggers de atendimento emergencial e portas de entrada.')
ON CONFLICT (code) DO NOTHING;

-- 3. Gatilhos Oficiais do IHI-GTT (53 Gatilhos Clínicos)

-- Módulo Cuidados (C1 a C15)
INSERT INTO gtt_triggers (module_id, code, name, description) VALUES
('b1000000-0000-0000-0000-000000000001', 'C1', 'Transfusão de sangue, hemocomponentes ou hemoderivados', 'Qualquer transfusão de concentrado de hemácias ou sangue total deve ter sua causa investigada, incluindo sangramento excessivo cirúrgico ou por anticoagulantes.'),
('b1000000-0000-0000-0000-000000000001', 'C2', 'Paragem/parada cardíaca ou respiratória ou ativação de equipa/time de resposta rápida', 'Todos os códigos de parada ou chamados do time de resposta rápida devem ser analisados quanto a eventos adversos associados.'),
('b1000000-0000-0000-0000-000000000001', 'C3', 'Diálise aguda', 'Nova necessidade de diálise iniciada no internamento por toxicidade renal de fármacos ou contrastes.'),
('b1000000-0000-0000-0000-000000000001', 'C4', 'Hemocultura positiva', 'Hemocultura positiva coletada após 48h da admissão indicando infecção associada ao cuidado ou dispositivos.'),
('b1000000-0000-0000-0000-000000000001', 'C5', 'Exame de imagem para detecção de embolia pulmonar ou trombose venosa profunda', 'Exame solicitado que confirma TVP ou TEP ocorrido durante o internamento.'),
('b1000000-0000-0000-0000-000000000001', 'C6', 'Queda superior a 25% nos valores de hemoglobina ou hematócrito', 'Redução aguda de 25% ou mais de Hb/Hct em até 72h associada a sangramento iatrogênico.'),
('b1000000-0000-0000-0000-000000000001', 'C7', 'Queda do paciente', 'Queda durante a internação que resultou em dano físico mensurável ao paciente.'),
('b1000000-0000-0000-0000-000000000001', 'C8', 'Lesões por pressão', 'Surgimento de úlcera por pressão em qualquer estágio decorrente do internamento.'),
('b1000000-0000-0000-0000-000000000001', 'C9', 'Readmissão em até 30 dias após a alta', 'Retorno hospitalar até 30 dias pós-alta motivado por complicações do cuidado precedente.'),
('b1000000-0000-0000-0000-000000000001', 'C10', 'Uso de contenção física no leito', 'Contenção mecânica decorrente de agitação secundária a medicamentos ou delírio induzido.'),
('b1000000-0000-0000-0000-000000000001', 'C11', 'Infecções relacionadas com os cuidados de saúde', 'Infecções hospitalares diagnosticadas após 48 horas de permanência ou após procedimentos.'),
('b1000000-0000-0000-0000-000000000001', 'C12', 'Acidente Vascular Cerebral (AVC) no hospital', 'AVC intra-hospitalar relacionado a procedimentos ou distúrbios da anticoagulação administrada.'),
('b1000000-0000-0000-0000-000000000001', 'C13', 'Transferência para unidade de maior complexidade', 'Transferência imprevista para UTI/semi-intensiva por descompensação induzida por evento adverso.'),
('b1000000-0000-0000-0000-000000000001', 'C14', 'Qualquer complicação de procedimentos', 'Dano agudo derivado de biópsias, punções vasculares, cateterismos ou drenagens.'),
('b1000000-0000-0000-0000-000000000001', 'C15', 'Outros', 'Outros eventos adversos gerais de cuidados que não se encaixam nos gatilhos C1 a C14.')
ON CONFLICT (code) DO NOTHING;

-- Módulo Medicação (M1 a M13)
INSERT INTO gtt_triggers (module_id, code, name, description) VALUES
('b1000000-0000-0000-0000-000000000002', 'M1', 'Resultado positivo para Clostridium difficile em fezes', 'Exame positivo para C. difficile associado à terapia prévia com antimicrobianos.'),
('b1000000-0000-0000-0000-000000000002', 'M2', 'Tempo de tromboplastina parcial ativado (aPTT/PTTa) maior que 100 segundos', 'Elevação de PTTa > 100 segundos por heparinização com manifestação de sangramento.'),
('b1000000-0000-0000-0000-000000000002', 'M3', 'Razão Normalizada Internacional (INR/RNI) maior que 6', 'Alargamento de RNI > 6 por cumarínicos com evidência física de sangramento ou dano.'),
('b1000000-0000-0000-0000-000000000002', 'M4', 'Glicemia menor que 50 mg/dL', 'Hipoglicemia sintomática resultante do uso de insulina ou antidiabéticos orais.'),
('b1000000-0000-0000-0000-000000000002', 'M5', 'Elevação de ureia ou creatinina sérica para valor duas vezes superior ao basal', 'Aumento de 2x ou mais nos marcadores renais atribuível a drogas nefrotóxicas.'),
('b1000000-0000-0000-0000-000000000002', 'M6', 'Administração de vitamina K (fitomenadiona)', 'Prescrição de fitomenadiona para reverter coagulopatia hemorrágica por varfarina.'),
('b1000000-0000-0000-0000-000000000002', 'M7', 'Administração de anti-histamínico', 'Uso de antialérgicos para tratar reação medicamentosa cutânea ou anafilaxia.'),
('b1000000-0000-0000-0000-000000000002', 'M8', 'Administração de flumazenil', 'Antagonismo de coma, sedação profunda ou hipotensão causada por benzodiazepínicos.'),
('b1000000-0000-0000-0000-000000000002', 'M9', 'Administração de naloxona', 'Reversão de emergência de depressão respiratória aguda provocada por opioides hospitalares.'),
('b1000000-0000-0000-0000-000000000002', 'M10', 'Administração de antieméticos', 'Náuseas e vômitos refratários ou persistentes decorrentes do tratamento farmacológico.'),
('b1000000-0000-0000-0000-000000000002', 'M11', 'Hipotensão/sedação excessiva', 'Letargia prolongada ou queda pressórica acentuada decorrente de analgésicos e sedativos.'),
('b1000000-0000-0000-0000-000000000002', 'M12', 'Suspensão abrupta de medicamentos', 'Interrupção imediata não programada de fármaco devido ao surgimento de toxicidade.'),
('b1000000-0000-0000-0000-000000000002', 'M13', 'Outros', 'Outros eventos adversos causados por fármacos não descritos nos itens M1 a M12.')
ON CONFLICT (code) DO NOTHING;

-- Módulo Cirúrgico (S1 a S11)
INSERT INTO gtt_triggers (module_id, code, name, description) VALUES
('b1000000-0000-0000-0000-000000000003', 'S1', 'Reintervenção cirúrgica', 'Retorno não programado ao centro cirúrgico para sanar complicação ou sangramento.'),
('b1000000-0000-0000-0000-000000000003', 'S2', 'Mudança de procedimento', 'Alteração intraoperatória do plano cirúrgico devido a acidente ou dano inadvertido.'),
('b1000000-0000-0000-0000-000000000003', 'S3', 'Admissão em unidade de cuidados intensivos/terapia intensiva no pós-operatório', 'Encaminhamento imprevisto do pós-operatório à UTI por descompensação orgânica.'),
('b1000000-0000-0000-0000-000000000003', 'S4', 'Intubação ou reintubação ou uso de BiPap na unidade de recuperação pós-anestésica', 'Depressão respiratória aguda residual exigindo suporte ventilatório mecânico na RPA.'),
('b1000000-0000-0000-0000-000000000003', 'S5', 'Raio X intraoperatório ou na unidade de recuperação pós-anestésica', 'Exame urgente por contagem incorreta ou suspeita de corpo estranho/compressa retida.'),
('b1000000-0000-0000-0000-000000000003', 'S6', 'Morte intra ou no pós-operatório', 'Óbito ocorrido no centro cirúrgico ou na fase imediata pós-anestésica.'),
('b1000000-0000-0000-0000-000000000003', 'S7', 'Ventilação mecânica por tempo superior a 24 horas no pós-operatório', 'Incapacidade imprevista de extubação após 24h da conclusão do procedimento.'),
('b1000000-0000-0000-0000-000000000003', 'S8', 'Administração intraoperatória de adrenalina, noradrenalina, naloxona ou flumazenil', 'Uso emergencial de vasopressores ou antídotos em sala cirúrgica por colapso vital.'),
('b1000000-0000-0000-0000-000000000003', 'S9', 'Aumento do nível de troponina superior a 1,5 nanograma/mL no pós-operatório', 'Elevação de troponina demonstrando infarto agudo do miocárdio perioperatório.'),
('b1000000-0000-0000-0000-000000000003', 'S10', 'Lesão, reparação ou remoção de órgão durante o procedimento cirúrgico', 'Lesão iatrogênica acidental em órgãos adjacentes exigindo reparo durante o ato cirúrgico.'),
('b1000000-0000-0000-0000-000000000003', 'S11', 'Ocorrência de qualquer complicação cirúrgica', 'Deiscências, infecções de sítio incisional profundo ou queimaduras por eletrocautério.')
ON CONFLICT (code) DO NOTHING;

-- Módulo Terapia Intensiva (I1 a I4)
INSERT INTO gtt_triggers (module_id, code, name, description) VALUES
('b1000000-0000-0000-0000-000000000004', 'I1', 'Pneumonia com início no hospital', 'Pneumonia associada à ventilação mecânica desenvolvida após 48h na UTI.'),
('b1000000-0000-0000-0000-000000000004', 'I2', 'Readmissão em unidade de cuidados intensivos/terapia intensiva', 'Retorno do paciente à UTI decorrente de recaída ou complicação da enfermagem/clínica.'),
('b1000000-0000-0000-0000-000000000004', 'I3', 'Procedimentos em unidade de cuidados intensivos/terapia intensiva', 'Complicações em punções profundas, traqueostomias percutâneas ou drenos na UTI.'),
('b1000000-0000-0000-0000-000000000004', 'I4', 'Intubação ou reintubação', 'Reintubação traqueal decorrente de extubação não planejada/acidental.')
ON CONFLICT (code) DO NOTHING;

-- Módulo Perinatal (P1 a P8)
INSERT INTO gtt_triggers (module_id, code, name, description) VALUES
('b1000000-0000-0000-0000-000000000005', 'P1', 'Uso de agentes tocolíticos', 'Complicações hemodinâmicas maternas decorrentes da tocolise farmacológica.'),
('b1000000-0000-0000-0000-000000000005', 'P2', 'Lacerações de 3º e 4º graus', 'Laceração perineal grave com lesão esfincteriana ou retal no parto vaginal.'),
('b1000000-0000-0000-0000-000000000005', 'P3', 'Contagem de plaquetas inferior a 50.000', 'Trombocitopenia aguda materna associada a sangramento puerperal patológico.'),
('b1000000-0000-0000-0000-000000000005', 'P4', 'Perda de sangue estimada superior a 500 mL para parto vaginal, ou 1.000 mL para parto cesariana', 'Hemorragia pós-parto materna anormal exigindo intervenção hemoterápica ou cirúrgica.'),
('b1000000-0000-0000-0000-000000000005', 'P5', 'Consulta com outra especialidade/interconsulta', 'Acionamento urgente de especialidades por injúria cirúrgica ou anestésica obstétrica.'),
('b1000000-0000-0000-0000-000000000005', 'P6', 'Administração de oxitocina/ocitocina e similares no período pós-parto', 'Administração de doses elevadas de uterotônicos por atonia ou hemorragia grave.'),
('b1000000-0000-0000-0000-000000000005', 'P7', 'Parto instrumentalizado', 'Lesão do canal de parto ou trauma materno derivado de fórceps/vácuo.'),
('b1000000-0000-0000-0000-000000000005', 'P8', 'Administração de anestesia geral', 'Conversão de emergência para anestesia geral por falha de raqui ou choque puerperal.')
ON CONFLICT (code) DO NOTHING;

-- Módulo Emergência (E1 e E2)
INSERT INTO gtt_triggers (module_id, code, name, description) VALUES
('b1000000-0000-0000-0000-000000000006', 'E1', 'Readmissão no serviço de urgência/pronto atendimento nas 48 horas após a alta', 'Retorno precoce em até 48 horas devido a complicação não diagnosticada na liberação.'),
('b1000000-0000-0000-0000-000000000006', 'E2', 'Tempo de permanência no serviço de urgência/pronto atendimento superior a 6 horas', 'Permanência na urgência > 6h associada ao desenvolvimento de escaras, quedas ou eventos adversos.')
ON CONFLICT (code) DO NOTHING;
