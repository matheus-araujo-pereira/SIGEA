-- ====================================================================
-- SIGEA: Sistema Inteligente de Gestão de Eventos Adversos
-- Script DML: Carga de Dados Oficiais da Metodologia IHI-GTT
-- Módulos, Gatilhos Clínicos (53), Gravidades NCC MERP e Administradores
-- Base Normativa: docs/referencias/metodologia_global_trigger_tool.pdf
-- ====================================================================

-- 1. Usuários Administradores Iniciais (1º Acesso Obrigatório: must_change_password = TRUE)
INSERT INTO users (id, full_name, email, password_hash, role, registration_number, is_active, must_change_password)
VALUES
(
    'a1000000-0000-0000-0000-000000000001',
    'Matheus Araujo Pereira',
    'matheusaraujopereira@academico.ufs.br',
    '$2a$12$e2gg/066sAz11ugh4tdsBu9z4hakyHQqxafgz.N8wfcmYrW98xY.2',
    'ADMIN',
    NULL,
    TRUE,
    TRUE
),
(
    'a2000000-0000-0000-0000-000000000001',
    'Profª. Drª. Ana Waleska de Menezes Seixas Souza',
    'anawaleska@academico.ufs.br',
    '$2a$12$e2gg/066sAz11ugh4tdsBu9z4hakyHQqxafgz.N8wfcmYrW98xY.2',
    'ADMIN',
    NULL,
    TRUE,
    TRUE
)
ON CONFLICT (email) DO UPDATE SET
    full_name = EXCLUDED.full_name,
    role = EXCLUDED.role,
    password_hash = EXCLUDED.password_hash,
    must_change_password = EXCLUDED.must_change_password,
    is_active = EXCLUDED.is_active;

-- 2. Classificações de Gravidade do Dano NCC MERP (Categorias A a I)
INSERT INTO harm_severities (category_letter, name, description, is_harm, is_active) VALUES
('A', 'Categoria A', 'Circunstâncias ou eventos com capacidade para causar erros.', FALSE, TRUE),
('B', 'Categoria B', 'Um erro que não atingiu o paciente.', FALSE, TRUE),
('C', 'Categoria C', 'Um erro que atingiu o paciente, mas não causou danos.', FALSE, TRUE),
('D', 'Categoria D', 'Um erro que atingiu o paciente e exigiu monitoramento ou intervenção para confirmar que não resultou em nenhum dano ao paciente.', FALSE, TRUE),
('E', 'Categoria E', 'Dano temporário ao paciente e necessidade de intervenção.', TRUE, TRUE),
('F', 'Categoria F', 'Dano temporário ao paciente e necessidade de iniciar ou prolongar hospitalização.', TRUE, TRUE),
('G', 'Categoria G', 'Dano permanente ao paciente.', TRUE, TRUE),
('H', 'Categoria H', 'Necessidade de intervenção para manter a vida.', TRUE, TRUE),
('I', 'Categoria I', 'Morte do paciente.', TRUE, TRUE)
ON CONFLICT (category_letter) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    is_harm = EXCLUDED.is_harm,
    is_active = EXCLUDED.is_active;

-- 3. Módulos Oficiais da Metodologia IHI-GTT (6 Módulos Especializados)
INSERT INTO gtt_modules (id, code, name, description, is_active) VALUES
('b1000000-0000-0000-0000-000000000001', 'CUIDADOS', 'Cuidados', 'Triggers do Módulo Cuidados para detecção de eventos adversos na prestação de cuidados gerais de saúde.', TRUE),
('b1000000-0000-0000-0000-000000000002', 'MEDICACAO', 'Medicação', 'Triggers do Módulo Medicação para identificação de danos e eventos adversos relacionados a medicamentos.', TRUE),
('b1000000-0000-0000-0000-000000000003', 'CIRURGICO', 'Cirúrgico', 'Triggers do Módulo Cirúrgico para avaliação de complicações intra e pós-operatórias.', TRUE),
('b1000000-0000-0000-0000-000000000004', 'UTI', 'Cuidados Intensivos/Terapia Intensiva', 'Triggers do Módulo Cuidados Intensivos/Terapia Intensiva para detecção de danos em pacientes críticos.', TRUE),
('b1000000-0000-0000-0000-000000000005', 'PERINATAL', 'Perinatal', 'Apenas os processos/prontuários clínicos maternos serão selecionados para revisão quando utilizar o Global Trigger Tool do IHI. Os eventos adversos relacionados aos recém-nascidos não são medidos com esta ferramenta.', TRUE),
('b1000000-0000-0000-0000-000000000006', 'URGENCIA', 'Serviço de Urgência/Pronto Atendimento', 'Triggers do Módulo Serviço de Urgência/Pronto Atendimento para identificação de eventos adversos na admissão emergencial.', TRUE)
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    is_active = EXCLUDED.is_active;

-- 4. Gatilhos Oficiais do IHI-GTT (53 Gatilhos Clínicos Padronizados - Nomenclatura 100% Oficial)

-- Módulo Cuidados (C)
INSERT INTO gtt_triggers (module_id, code, name, description, is_active) VALUES
('b1000000-0000-0000-0000-000000000001', 'C1', 'Transfusão de sangue, hemocomponentes ou hemoderivados', 'Os procedimentos podem exigir transfusão intraoperatório de produtos sanguíneos para reposição de perda estimada de sangue. Qualquer transfusão de concentrado de hemácias ou de sangue total deve ter sua causa investigada, incluindo sangramento excessivo (relacionado a cirurgias ou ao uso de anticoagulantes), trauma não intencional de um vaso sanguíneo, etc. Transfusão de muitas unidades ou além da perda de sangue esperada nas primeiras 24 horas de cirurgia, incluindo intraoperatório e pósoperatório, provavelmente estará relacionada a um evento adverso peri-operatória. Os casos em que a perda excessiva de sangue ocorreu no pré-operatório tipicamente não são eventos adversos. Pacientes recebendo anticoagulantes que necessitam de transfusão de plasma fresco congelado e plaquetas provavelmente experimentaram um evento adverso relacionado ao uso desses medicamentos.', TRUE),
('b1000000-0000-0000-0000-000000000001', 'C2', 'Paragem/parada cardíaca ou respiratória ou ativação de equipa/time de resposta rápida', 'Todos os “códigos”, paragens/paradas cardíacas ou respiratórias e a ativação de equipas/times de resposta rápida devem ser cuidadosamente revistos, pois isso pode ser o resultado de um evento adverso (verifique se há problemas relacionados a medicamentos). No entanto, algumas dessas situações podem estar associadas à progressão de uma doença. Por exemplo, paragem/parada cardíaca ou respiratória intraoperatória ou na unidade de recuperação pós-anestésica sempre deve ser considerada um evento adverso. Nas primeiras 24 horas de pós-operatório, também é muito provável que seja um evento adverso. Por outro lado, uma arritmia cardíaca súbita resultando em paragem/parada cardíaca pode não ser um evento adverso, mas estar relacionada a uma doença cardíaca. Falha no reconhecimento de sinais e sintomas é um exemplo de erro de omissão e não deve ser contada como um evento adverso, a menos que as alterações na condição do paciente sejam o resultado de alguma intervenção médica.', TRUE),
('b1000000-0000-0000-0000-000000000001', 'C3', 'Diálise aguda', 'Uma nova necessidade de diálise pode ser o curso de um processo de doença ou o resultado de um evento adverso. Exemplos de eventos adversos podem ser insuficiência renal induzida por drogas ou reação à administração de um contraste para procedimentos radiológicos.', TRUE),
('b1000000-0000-0000-0000-000000000001', 'C4', 'Hemocultura positiva', 'Uma hemocultura positiva a qualquer momento durante a hospitalização deve ser investigada como um indicador de um evento adverso, especificamente uma infecção relacionada com os cuidados de saúde. Geralmente, os eventos adversos associados a esse trigger incluem infecções diagnosticadas 48 horas ou mais após a admissão, como infecções da corrente sanguínea, sépsis/sepse por infecções em outros dispositivos (por exemplo, infecção do trato urinário associada a cateter) ou qualquer outra infecção hospitalar. Pacientes com hemoculturas positivas relacionadas a outras doenças, como pneumonia adquirida na comunidade que evolui para sépsis/sepse, não são considerados como tendo eventos adversos.', TRUE),
('b1000000-0000-0000-0000-000000000001', 'C5', 'Exame de imagem para detecção de embolia pulmonar ou trombose venosa profunda', 'O desenvolvimento de uma trombose venosa profunda (TVP) ou embolia pulmonar (EP) durante uma hospitalização será um evento adverso na maioria dos casos. Exceções raras podem ser aquelas relacionadas a processos de doença, como câncer ou distúrbios de coagulação. No entanto, na maioria dos pacientes este é um dano relacionado aos cuidados de saúde, mesmo que todas as medidas preventivas apropriadas pareçam ter sido tomadas. Se a hospitalização ocorrer devido a uma TVP ou EP, procure uma causa antes da admissão que possa ser atribuída aos cuidados de saúde, como um procedimento cirúrgico prévio. A falta de profilaxia para tromboembolismo venoso não é um evento adverso; é um erro de omissão.', TRUE),
('b1000000-0000-0000-0000-000000000001', 'C6', 'Queda superior a 25% nos valores de hemoglobina ou hematócrito', 'Qualquer redução de 25% ou mais nos níveis de hemoglobina (Hg) ou hematócrito (Hct) deve ser investigada, especialmente quando ocorre em um período relativamente curto de tempo, como 72 horas ou menos. Os eventos de hemorragia são comumente identificados por esse trigger e podem estar relacionados ao uso de anticoagulantes ou ácido acetil salicílico ou a uma complicação cirúrgica. A queda da Hg ou do Hct em si não é um evento adverso, a menos que esteja relacionada a algum tratamento em saúde. Uma redução associada a um processo de doença não é um evento adverso.', TRUE),
('b1000000-0000-0000-0000-000000000001', 'C7', 'Queda do paciente', 'Uma queda em um ambiente de cuidados representa uma falha de cuidado e pode ser resultado do uso de medicamentos, falha de equipamento ou falha de pessoal. Qualquer queda em um ambiente de cuidado que cause danos, independentemente da causa, é um evento adverso; uma queda sem ferimentos não é um evento adverso. Quedas resultando em ferimentos e admissão no hospital devem ser revistas quanto à causalidade. Uma queda que é resultado de tratamento em saúde (como de medicamentos) deve ser considerada um evento adverso, mesmo se a queda ocorreu fora do hospital.', TRUE),
('b1000000-0000-0000-0000-000000000001', 'C8', 'Lesões por pressão', 'As lesões por pressão são eventos adversos se ocorridos durante uma hospitalização. Caso as lesões tenham acontecido em ambiente ambulatorial, considere a etiologia (sedação excessiva, etc.) para avaliar se um evento adverso ocorreu.', TRUE),
('b1000000-0000-0000-0000-000000000001', 'C9', 'Readmissão em até 30 dias após a alta', 'Qualquer readmissão, particularmente até 30 dias após a alta, pode ser um evento adverso. Um evento adverso pode não se manifestar até que o paciente tenha recebido alta do hospital, especialmente se a duração da permanência for mínima. Exemplos de eventos adversos incluem infecção do local/sítio cirúrgico, trombose venosa profunda ou embolia pulmonar.', TRUE),
('b1000000-0000-0000-0000-000000000001', 'C10', 'Uso de contenção física no leito', 'Sempre que contenções forem usadas, reveja as razões documentadas e avalie a possível relação entre o uso das contenções e confusão mental por uso de medicamentos, etc., que indicariam um evento adverso.', TRUE),
('b1000000-0000-0000-0000-000000000001', 'C11', 'Infecções relacionadas com os cuidados de saúde', 'Qualquer infecção que ocorra após a admissão no hospital é, provavelmente, um evento adverso, especialmente aquelas relacionadas a procedimentos ou a uso de dispositivos. As infecções que causam admissão no hospital devem ser revistas para determinar se estão relacionadas com os cuidados de saúde (por exemplo, procedimento anterior, uso de cateter urinário em casa ou em instituição de longa permanência) versus doenças que ocorrem naturalmente (por exemplo, pneumonia adquirida na comunidade).', TRUE),
('b1000000-0000-0000-0000-000000000001', 'C12', 'Acidente Vascular Cerebral (AVC) no hospital', 'Avaliar a causa do AVC para determinar se está associado a um procedimento (por exemplo, procedimento cirúrgico, cardioversão de fibrilação atrial) ou anticoagulação. Quando procedimentos ou tratamentos provavelmente contribuíram para um AVC, trata-se de um evento adverso.', TRUE),
('b1000000-0000-0000-0000-000000000001', 'C13', 'Transferência para unidade de maior complexidade', 'Transferências para unidades de maior complexidade de cuidado dentro da própria instituição, para outra instituição, ou para a sua instituição proveniente de outra, devem ser revistas. Todas as transferências têm probabilidade de serem resultado de um evento adverso e a condição clínica do paciente pode ter se deteriorado devido a um evento adverso. Procure as razões para a transferência. Por exemplo, no caso de hospitalização em cuidados intensivos/terapia intensiva após paragem/parada respiratória e intubação, se a paragem/parada respiratória for resultado da progressão natural de uma exacerbação de doença pulmonar obstrutiva crônica (DPOC), então não seria um evento adverso; se for causada por uma embolia pulmonar que se desenvolveu no pós-operatório ou resultado de sedação excessiva de um paciente com DPOC, seria um evento adverso. Uma unidade de maior complexidade de cuidado pode incluir unidade de monitorização contínua ou de cuidados intermédios/intermediários se o paciente foi transferido de uma unidade de internamento/internação geral.', TRUE),
('b1000000-0000-0000-0000-000000000001', 'C14', 'Qualquer complicação de procedimentos', 'Uma complicação resultante de qualquer procedimento é um evento adverso. As notas de descrição dos procedimentos frequentemente não indicam as complicações, especialmente se elas ocorrerem horas ou dias após a nota ter sido redigida. Portanto, observe as complicações relatadas na codificação, no relatório de alta ou nas outras notas de evolução.', TRUE),
('b1000000-0000-0000-0000-000000000001', 'C15', 'Outros', 'Frequentemente, quando o processo/protocolo clínico é revisto, é descoberto um evento adverso que não se encaixa em um trigger. Qualquer evento desse tipo pode ser colocado sob o trigger “Outro”.', TRUE)
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    module_id = EXCLUDED.module_id,
    is_active = EXCLUDED.is_active;

-- Módulo Medicação (M)
INSERT INTO gtt_triggers (module_id, code, name, description, is_active) VALUES
('b1000000-0000-0000-0000-000000000002', 'M1', 'Resultado positivo para Clostridium difficile em fezes', 'Uma pesquisa positiva para C. difficile é um evento adverso se houver histórico de uso de antibióticos.', TRUE),
('b1000000-0000-0000-0000-000000000002', 'M2', 'Tempo de tromboplastina parcial ativado (aPTT/PTTa) maior que 100 segundos', 'Níveis elevados de aPTT/PTTa ocorrem quando os pacientes estão em uso de heparina. Procure por evidências de hemorragia para determinar se um evento adverso ocorreu. O aPTT/PTTa elevado em si não é um evento adverso — deve haver manifestações como sangramento, hematomas ou queda de Hg ou Hct.', TRUE),
('b1000000-0000-0000-0000-000000000002', 'M3', 'Razão Normalizada Internacional (INR/RNI) maior que 6', 'Procure por evidências de sangramento para determinar se um evento adverso ocorreu. Um INR/RNI elevado em si não é um evento adverso.', TRUE),
('b1000000-0000-0000-0000-000000000002', 'M4', 'Glicemia menor que 50 mg/dL', 'Procure por sintomas como letargia e tremores documentados em anotações da enfermagem e pela administração de glicose, sumo/suco de laranja ou outra intervenção. Se houver sintomas, procure pelo uso associado de insulina ou hipoglicemiantes orais. Se o paciente não apresentar sintomas, não há nenhum evento adverso.', TRUE),
('b1000000-0000-0000-0000-000000000002', 'M5', 'Elevação de ureia ou creatinina sérica para valor duas vezes superior ao basal', 'Faça a revisão dos dados laboratoriais buscando por níveis crescentes de ureia ou creatinina sérica. Se uma elevação superior a duas vezes o valor basal for encontrada, reveja as notas de administração de medicamentos para identificar aqueles que podem causar toxicidade renal. Analise as notas de evolução do médico, a história e o exame físico para outras causas de insuficiência renal, como doença renal préexistente ou diabetes, que poderiam colocar o paciente em maior risco de insuficiência renal; isso não seria um evento adverso, mas sim a progressão da doença.', TRUE),
('b1000000-0000-0000-0000-000000000002', 'M6', 'Administração de vitamina K (fitomenadiona)', 'Se a vitamina K foi utilizada como resposta a um INR/RNI alargado, avalie o processo/prontuário clínico em busca de evidências de sangramento. Um evento adverso provavelmente ocorreu se há dados laboratoriais indicando uma queda no hematócrito ou presença de sangue nas fezes. Verifique as notas de evolução quanto a evidências de equimoses, hemorragia gastrointestinal, acidente vascular cerebral hemorrágico ou hematomas grandes como exemplos de eventos adversos.', TRUE),
('b1000000-0000-0000-0000-000000000002', 'M7', 'Administração de anti-histamínico', 'A difenidramina, dexclorferinamina, clemastina e hidroxizina são exemplos de anti-histamínicos frequentemente utilizados para reações alérgicas a medicamentos, mas também podem ser prescritos como indutores de sono, no pré-operatório/pré-procedimento ou para alergias sazonais. Se o medicamento tiver sido administrado, reveja o processo/prontuário clínico para determinar se ele foi solicitado para sintomas de uma reação alérgica a um medicamento ou a uma transfusão de sangue administrados durante a hospitalização ou antes da admissão — esses seriam eventos adversos.', TRUE),
('b1000000-0000-0000-0000-000000000002', 'M8', 'Administração de flumazenil', 'Flumazenil reverte o efeito dos medicamentos benzodiazepínicos. Determine porque o medicamento foi utilizado. Exemplos de eventos adversos são hipotensão grave ou sedação acentuada e prolongada.', TRUE),
('b1000000-0000-0000-0000-000000000002', 'M9', 'Administração de naloxona', 'A naloxona é um potente antagonista de opioides. O uso provavelmente representa um evento adverso, exceto em casos de abuso de drogas ou overdose auto infligida.', TRUE),
('b1000000-0000-0000-0000-000000000002', 'M10', 'Administração de antieméticos', 'Náuseas e vômitos comumente são o resultado da administração de medicamentos em ambientes cirúrgicos e não cirúrgicos. Antieméticos são comumente administrados. Náuseas e vômitos que interferem na alimentação, recuperação pós-operatória ou atrasam a alta sugerem um evento adverso. Um ou dois episódios tratados com sucesso com antieméticos não sugerem nenhum evento adverso. O julgamento do revisor é necessário para determinar se o dano ocorreu.', TRUE),
('b1000000-0000-0000-0000-000000000002', 'M11', 'Hipotensão/sedação excessiva', 'Faça a revisão das evoluções médicas e de enfermagem ou as anotações multidisciplinares para obter evidências de sedação excessiva e letargia. Reveja notas ou gráficos de sinais vitais para episódios de hipotensão relacionados a administração de um sedativo, analgésico ou relaxante muscular. A overdose intencional não é considerada um evento adverso.', TRUE),
('b1000000-0000-0000-0000-000000000002', 'M12', 'Suspensão abrupta de medicamentos', 'Embora a descontinuação de medicamentos seja um achado comum no processo/prontuário clínico, parar abruptamente os medicamentos é um trigger que requer investigação adicional para avaliação da causa. Uma mudança súbita na condição do paciente que requer ajuste de medicamentos está frequentemente relacionada com a ocorrência de um evento adverso. “Abrupto” é melhor descrito como uma suspensão inesperada ou desvio da prática usual de prescrição; por exemplo, a descontinuação de um antibiótico intravenoso associada à mudança para a sua forma oral não é inesperada.', TRUE),
('b1000000-0000-0000-0000-000000000002', 'M13', 'Outros', 'Use esse trigger para eventos adversos relacionados a medicamentos identificados, mas não associados a nenhum dos triggers de medicamentos listados acima.', TRUE)
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    module_id = EXCLUDED.module_id,
    is_active = EXCLUDED.is_active;

-- Módulo Cirúrgico (S)
INSERT INTO gtt_triggers (module_id, code, name, description, is_active) VALUES
('b1000000-0000-0000-0000-000000000003', 'S1', 'Reintervenção cirúrgica', 'Uma reintervenção cirúrgica pode ser planejada ou não e ambas podem ser resultado de um evento adverso. Um exemplo de um evento adverso seria um paciente que teve hemorragia interna após a primeira cirurgia e precisou de uma segunda cirurgia para explorar a causa e cessar o sangramento. Mesmo que a segunda cirurgia seja exploratória e não revele nenhuma complicação, isso deve ser considerado um evento adverso.', TRUE),
('b1000000-0000-0000-0000-000000000003', 'S2', 'Mudança de procedimento', 'Quando o procedimento indicado nas anotações pós-operatórias for diferente do procedimento planeado/planejado nas anotações pré-operatórias ou documentado no consentimento cirúrgico, o revisor deve procurar detalhes sobre o motivo dessa mudança ter ocorrido. Uma mudança inesperada no procedimento devido a complicações ou falhas em dispositivos ou equipamentos deve ser considerada um evento adverso, particularmente se o tempo de permanência hospitalar aumentar ou se houver danos evidentes.', TRUE),
('b1000000-0000-0000-0000-000000000003', 'S3', 'Admissão em unidade de cuidados intensivos/terapia intensiva no pós-operatório', 'A admissão em uma unidade de cuidados intensivos/terapia intensiva pode ser um destino pós-operatório normal ou inesperado. As admissões inesperadas frequentemente estão relacionadas com eventos adversos operatórios. Por exemplo, a admissão em unidade de cuidados intensivos/terapia intensiva após o reparo de um aneurisma aórtico pode ser esperada, mas a admissão após uma cirurgia de prótese de joelho seria incomum. O revisor precisa determinar por que a admissão em cuidados intensivos/terapia intensiva ocorreu.', TRUE),
('b1000000-0000-0000-0000-000000000003', 'S4', 'Intubação ou reintubação ou uso de BiPap na unidade de recuperação pósanestésica', 'Anestesia, sedativos ou analgésicos podem resultar em depressão respiratória, exigindo o uso de BiPap ou reintubação no pós-operatório, o que seria um evento adverso.', TRUE),
('b1000000-0000-0000-0000-000000000003', 'S5', 'Raio X intraoperatório ou na unidade de recuperação pós-anestésica', 'Realização de exame de imagem de qualquer tipo que não seja rotina para o procedimento requer investigação. Uma radiografia obtida devido a suspeita de itens retidos ou de contagem incorreta de instrumentos ou compressas seria um trigger positivo. A identificação de um item retido que requer um procedimento adicional é um evento adverso. Se o item retido for identificado e removido sem qualquer evidência adicional de danos para o paciente ou reintervenção cirúrgica, isso não é considerado um evento adverso.', TRUE),
('b1000000-0000-0000-0000-000000000003', 'S6', 'Morte intra ou no pós-operatório', 'Todas as mortes que ocorrem no intraoperatório devem ser consideradas eventos adversos, a menos que a morte seja claramente esperada e a cirurgia tenha sido de natureza heroica. Os óbitos no pós-operatório exigirão revisão do processo/prontuário para especificidades, mas em geral todas as mortes pósoperatórias serão eventos adversos.', TRUE),
('b1000000-0000-0000-0000-000000000003', 'S7', 'Ventilação mecânica por tempo superior a 24 horas no pós-operatório', 'A ventilação mecânica de curto prazo no pós-operatório pode estar prevista para determinados procedimentos cardíacos, torácicos e abdominais. Se o paciente necessitar de ventilação mecânica após 24 horas, um evento adverso intraoperatório ou pós-operatório deve ser considerado. Pacientes com doença pulmonar ou muscular preexistente podem ter mais dificuldade em desmamar rapidamente de um ventilador no pós-operatório, mas isso não deve excluir automaticamente a possibilidade de um evento adverso. Os revisores devem usar o julgamento clínico para determinar se os cuidados intraoperatórios e pós-operatórios estão relacionados a eventos ou se são parte da doença.', TRUE),
('b1000000-0000-0000-0000-000000000003', 'S8', 'Administração intraoperatória de adrenalina, noradrenalina, naloxona ou flumazenil', 'Esses medicamentos não são administrados rotineiramente no intraoperatório. Faça a revisão das notas anestésicas e cirúrgicas para determinar o motivo da administração. Hipotensão causada por sangramento ou sedação excessiva são exemplos de eventos adversos que podem ser tratados com esses medicamentos.', TRUE),
('b1000000-0000-0000-0000-000000000003', 'S9', 'Aumento do nível de troponina superior a 1,5 nanograma/mL no pós-operatório', 'Um aumento pós-operatório do nível de troponina pode indicar um evento cardíaco. Os revisores precisarão usar o julgamento clínico para saber se um evento cardíaco ocorreu.', TRUE),
('b1000000-0000-0000-0000-000000000003', 'S10', 'Lesão, reparação ou remoção de órgão durante o procedimento cirúrgico', 'Reveja as notas operatórias e pós-operatórias para obter evidências de que o procedimento incluiu a reparação ou remoção de qualquer órgão. A remoção ou reparação deve fazer parte do procedimento planeado/planejado; caso contrário, deve ser considerado um evento adverso e provavelmente foi resultado de uma complicação cirúrgica, como uma lesão acidental.', TRUE),
('b1000000-0000-0000-0000-000000000003', 'S11', 'Ocorrência de qualquer complicação cirúrgica', 'Refere-se a qualquer uma das várias complicações, incluindo, mas não se limitando a EP, TVP, lesão por pressão, infarto do miocárdio, insuficiência renal, etc.', TRUE)
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    module_id = EXCLUDED.module_id,
    is_active = EXCLUDED.is_active;

-- Módulo Cuidados Intensivos/Terapia Intensiva (I)
INSERT INTO gtt_triggers (module_id, code, name, description, is_active) VALUES
('b1000000-0000-0000-0000-000000000004', 'I1', 'Pneumonia com início no hospital', 'Qualquer pneumonia diagnosticada na terapia intensiva precisa ser analisada com cuidado. Se as evidências sugerem que a pneumonia começou antes da hospitalização, não há evento adverso; mas se a revisão sugere o seu início no hospital, é um evento adverso. Em geral, qualquer infecção que comece não apenas na unidade de cuidados intensivos/terapia intensiva, mas em qualquer setor do hospital, será considerada relacionada com os cuidados de saúde. As readmissões no hospital ou na unidade de cuidados intensivos/terapia intensiva podem representar uma infecção relacionada com a assistência de uma hospitalização anterior.', TRUE),
('b1000000-0000-0000-0000-000000000004', 'I2', 'Readmissão em unidade de cuidados intensivos/terapia intensiva', 'Consulte o trigger S3–Admissão em unidade de cuidados intensivos/terapia intensiva no pós-operatório.', TRUE),
('b1000000-0000-0000-0000-000000000004', 'I3', 'Procedimentos em unidade de cuidados intensivos/terapia intensiva', 'Qualquer procedimento que ocorra em um paciente na unidade de cuidados intensivos/terapia intensiva requer investigação. Veja todos os procedimentos à beira do leito e outros procedimentos realizados enquanto o paciente estava na unidade. As complicações geralmente não estão nas notas que descrevem o procedimento, mas podem se tornar evidentes pelos cuidados requeridos, o que pode indicar que um evento ocorreu.', TRUE),
('b1000000-0000-0000-0000-000000000004', 'I4', 'Intubação ou reintubação', 'Consulte o trigger S4–Intubação ou reintubação ou uso de BiPap na unidade de recuperação pósanestésica.', TRUE)
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    module_id = EXCLUDED.module_id,
    is_active = EXCLUDED.is_active;

-- Módulo Perinatal (P)
INSERT INTO gtt_triggers (module_id, code, name, description, is_active) VALUES
('b1000000-0000-0000-0000-000000000005', 'P1', 'Uso de agentes tocolíticos', 'O uso de agentes tocolíticos como atosiban, indometacina, terbutalina, nifedipina ou sulfato de magnésio pode resultar em uma intervenção desnecessária de uma cesariana devido à administração de um medicamento. Procure por fatores complicadores. O uso desses agentes no trabalho de parto prematuro não é um trigger positivo.', TRUE),
('b1000000-0000-0000-0000-000000000005', 'P2', 'Lacerações de 3o e 4o graus', 'Por definição, lacerações de 3o ou 4o graus são eventos adversos. Procure também eventos adicionais para a mãe ou para a criança associados à laceração, como parte de uma cascata, de modo que a gravidade apropriada possa ser avaliada.', TRUE),
('b1000000-0000-0000-0000-000000000005', 'P3', 'Contagem de plaquetas inferior a 50.000', 'Procure por eventos adversos relacionados a sangramentos, como acidente vascular cerebral, hematomas e hemorragia, que requeiram transfusões de sangue. Procure informações sobre a causa da diminuição da contagem de plaquetas para avaliar se foi resultado do uso de um medicamento. Geralmente a transfusão de plaquetas indica de que o paciente tem uma baixa contagem de plaquetas. Eventos relacionados à transfusão ou sangramento podem indicar que um evento adverso pode ter ocorrido.', TRUE),
('b1000000-0000-0000-0000-000000000005', 'P4', 'Perda de sangue estimada superior a 500 mL para parto vaginal, ou 1.000 mL para parto cesariana', 'O limite aceite/aceito como “normal” para a perda de sangue após o parto vaginal é de 500 mL, e uma perda de sangue de 1.000 mL é considerada dentro dos limites normais após o parto cesariana.', TRUE),
('b1000000-0000-0000-0000-000000000005', 'P5', 'Consulta com outra especialidade/interconsulta', 'Pode ser um indicador de lesão ou danos.', TRUE),
('b1000000-0000-0000-0000-000000000005', 'P6', 'Administração de oxitocina/ocitocina e similares no período pós-parto', 'Agentes usados para controlar a hemorragia pós-parto, definida como perda de sangue superior a 500 mL para um parto vaginal e superior a 1.000 mL para uma cesariana. Se a administração padrão da oxitocina/ocitocina ocorrer no pós-parto, atentar para administração de quantidades superiores a 20 unidades no período imediatamente após o parto.', TRUE),
('b1000000-0000-0000-0000-000000000005', 'P7', 'Parto instrumentalizado', 'Os instrumentos podem causar lesões na mãe, incluindo hematomas, traumas e lacerações perineais.', TRUE),
('b1000000-0000-0000-0000-000000000005', 'P8', 'Administração de anestesia geral', 'Pode ser um indicador de danos resultantes de falha de planeamento/planejamento ou outras fontes de danos.', TRUE)
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    module_id = EXCLUDED.module_id,
    is_active = EXCLUDED.is_active;

-- Módulo Serviço de Urgência/Pronto Atendimento (E)
INSERT INTO gtt_triggers (module_id, code, name, description, is_active) VALUES
('b1000000-0000-0000-0000-000000000006', 'E1', 'Readmissão no serviço de urgência/pronto atendimento nas 48 horas após a alta', 'Procure por reações a medicamentos, infecções ou outras razões pelas quais os eventos podem ter trazido o paciente de volta ao serviço de urgência/pronto atendimento e, em seguida, requerido hospitalização.', TRUE),
('b1000000-0000-0000-0000-000000000006', 'E2', 'Tempo de permanência no serviço de urgência/pronto atendimento superior a 6 horas', 'Longa permanência no serviço de urgência/pronto atendimento, em alguns casos, pode representar falhas no cuidado ideal. Procure complicações como quedas, hipotensão ou complicações relacionadas a procedimentos.', TRUE)
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    module_id = EXCLUDED.module_id,
    is_active = EXCLUDED.is_active;
