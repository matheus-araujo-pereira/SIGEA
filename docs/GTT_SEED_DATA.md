# Definições Oficiais de Dados do IHI Global Trigger Tool e Metodologia da Qualidade

## 1. Classificações de Gravidade (Índice NCC MERP adaptado)
- **Categoria A**: Circunstâncias ou eventos com capacidade para causar erros. (Sem dano / Capacidade)
- **Categoria B**: Um erro que não atingiu o paciente. (Sem dano / Quase-falha - Near miss)
- **Categoria C**: Um erro que atingiu o paciente, mas não causou danos. (Sem dano / Atingiu o paciente)
- **Categoria D**: Um erro que atingiu o paciente e exigiu monitoramento ou intervenção para confirmar que não resultou em nenhum dano ao paciente. (Sem dano / Necessidade de monitoramento)
- **Categoria E**: Dano temporário ao paciente e necessidade de intervenção. (Dano temporário / Intervenção)
- **Categoria F**: Dano temporário ao paciente e necessidade de iniciar ou prolongar hospitalização. (Dano temporário / Prolongamento de permanência)
- **Categoria G**: Dano permanente ao paciente. (Dano permanente)
- **Categoria H**: Necessidade de intervenção para manter a vida (intervenção necessária em até 1 hora para evitar o óbito).
- **Categoria I**: Morte do paciente (o evento adverso contribuiu para a morte do paciente).

---

## 2. Módulos e Gatilhos do GTT

### MÓDULO 1: CUIDADOS (Código: C)
1. **C1 - Transfusão de sangue, hemocomponentes ou hemoderivados**  
   *Descrição*: Os procedimentos podem exigir transfusão intraoperatória de produtos sanguíneos para reposição de perda estimada de sangue. Qualquer transfusão de concentrado de hemácias ou sangue total deve ter sua causa investigada, incluindo sangramento excessivo (cirúrgico ou por anticoagulantes) ou trauma não intencional de vaso. Transfusões volumosas nas primeiras 24h pós-cirúrgicas frequentemente decorrem de eventos adversos. Pacientes em uso de anticoagulantes que necessitam de plasma fresco congelado ou plaquetas provavelmente sofreram eventos adversos.
2. **C2 - Paragem/parada cardíaca ou respiratória ou ativação de equipa/time de resposta rápida**  
   *Descrição*: Todas as paradas ou acionamentos do time de resposta rápida devem ser analisados quanto a causas medicamentosas ou intervenções. Paradas intraoperatórias, na RPA ou nas primeiras 24h são prioritariamente eventos adversos. Arritmias por progressão natural de doença cardíaca sem nexo causal médico não são eventos adversos.
3. **C3 - Diálise aguda**  
   *Descrição*: Nova necessidade de diálise decorrente de injúria renal aguda induzida por medicamentos nefrotóxicos ou por administração de contraste radiológico.
4. **C4 - Hemocultura positiva**  
   *Descrição*: Hemocultura positiva obtida 48 horas ou mais após a internação indicando infecção de corrente sanguínea associada a cateter vascular, procedimentos ou foco hospitalar.
5. **C5 - Exame de imagem para detecção de embolia pulmonar ou trombose venosa profunda**  
   *Descrição*: Solicitação de Doppler ou angiotomografia que diagnostica TVP ou Embolia Pulmonar após admissão ou decorrente de procedimento cirúrgico prévio.
6. **C6 - Queda superior a 25% nos valores de hemoglobina ou hematócrito**  
   *Descrição*: Queda brusca de 25% ou mais de Hb/Hct em até 72 horas decorrente de hemorragia cirúrgica ou iatrogênica (anticoagulantes, antiagregantes).
7. **C7 - Queda do paciente**  
   *Descrição*: Queda ocorrida no hospital que resulte em lesão mensurável física (fratura, laceração, hematoma). Queda sem dano físico não é evento adverso.
8. **C8 - Lesões por pressão**  
   *Descrição*: Lesão por pressão (qualquer estágio) desenvolvida após a internação do paciente decorrente de falha de alívio de decúbito ou imobilização.
9. **C9 - Readmissão em até 30 dias após a alta**  
   *Descrição*: Retorno hospitalar em 30 dias motivado por complicação do tratamento, infecção de ferida operatória, TVP pós-alta ou efeitos adversos prévios.
10. **C10 - Uso de contenção física no leito**  
    *Descrição*: Avaliar agitação secundária a erro medicamentoso, sedação inadequada ou delírio induzido que exigiu imobilização física no leito.
11. **C11 - Infecções relacionadas com os cuidados de saúde**  
    *Descrição*: Qualquer processo infeccioso manifestado após 48h da internação (ITU associada a cateter, infecção do sítio cirúrgico, broncoaspiração assistencial).
12. **C12 - Acidente Vascular Cerebral (AVC) no hospital**  
    *Descrição*: AVC isquêmico ou hemorrágico ocorrido durante o internamento associado a procedimentos intervencionistas ou falha no manejo de anticoagulação.
13. **C13 - Transferência para unidade de maior complexidade**  
    *Descrição*: Transferência não planejada da enfermaria para semi-intensiva ou UTI em decorrência de deterioração clínica provocada por evento adverso assistencial.
14. **C14 - Qualquer complicação de procedimentos**  
    *Descrição*: Danos agudos ou subagudos decorrentes de punção venosa central (pneumotórax), endoscopia (perfuração), biópsias ou drenagens.
15. **C15 - Outros**  
    *Descrição*: Qualquer evento adverso relacionado ao cuidado geral que não possua gatilho específico definido nos itens C1 a C14.

### MÓDULO 2: MEDICAÇÃO (Código: M)
1. **M1 - Resultado positivo para Clostridium difficile em fezes**  
   *Descrição*: Colite pseudomembranosa diagnosticada por toxina ou cultura positiva associada ao uso recente de antibióticos na internação.
2. **M2 - Tempo de tromboplastina parcial ativado (aPTT/PTTa) maior que 100 segundos**  
   *Descrição*: Alargamento de PTTa > 100s associado ao uso de heparina, exigindo busca por hemorragias ativas ou hematomas.
3. **M3 - Razão Normalizada Internacional (INR/RNI) maior que 6**  
   *Descrição*: RNI > 6 em vigência de varfarina/cumarínicos com sangramento evidente ou necessidade de intervenção terapêutica.
4. **M4 - Glicemia menor que 50 mg/dL**  
   *Descrição*: Hipoglicemia sintomática (letargia, sudorese, convulsão) causada por insulina ou hipoglicemiantes com necessidade de infusão de glicose.
5. **M5 - Elevação de ureia ou creatinina sérica para valor duas vezes superior ao basal**  
   *Descrição*: Toxicidade renal provocada por drogas nefrotóxicas (aminoglicosídeos, vancomicina, AINEs ou contraste).
6. **M6 - Administração de vitamina K (fitomenadiona)**  
   *Descrição*: Prescrição de fitomenadiona para reverter coagulopatia grave associada a sangramento ativo.
7. **M7 - Administração de anti-histamínico**  
   *Descrição*: Uso de anti-histamínicos (difenidramina, dexclorfeniramina) para tratamento de reação anafilactoide ou cutânea medicamentosa/transfusional.
8. **M8 - Administração de flumazenil**  
   *Descrição*: Uso de antagonista para reverter sedação excessiva, hipotensão severa ou coma induzido por benzodiazepínicos.
9. **M9 - Administração de naloxona**  
   *Descrição*: Antagonismo imediato de depressão respiratória aguda provocada por opioides prescritos.
10. **M10 - Administração de antieméticos**  
    *Descrição*: Vômitos incoercíveis ou persistentes (>24h) com desidratação e atraso na alta decorrentes de quimioterapia ou medicação anestésica.
11. **M11 - Hipotensão/sedação excessiva**  
    *Descrição*: Rebaixamento sensorial grave ou choque distributivo causado por doses inadvertidas de sedativos, anestésicos ou anti-hipertensivos.
12. **M12 - Suspensão abrupta de medicamentos**  
    *Descrição*: Descontinuação imediata e não rotineira de um fármaco devido a desenvolvimento súbito de toxicidade, rash ou choque.
13. **M13 - Outros**  
    *Descrição*: Outros danos comprovadamente associados ao uso de medicamentos não cobertos pelos itens M1 a M12.

### MÓDULO 3: CIRÚRGICO (Código: S)
1. **S1 - Reintervenção cirúrgica**  
   *Descrição*: Retorno de urgência à sala cirúrgica para hemostasia, desbridamento, correção de fístula ou remoção de corpo estranho.
2. **S2 - Mudança de procedimento**  
   *Descrição*: Conversão de procedimento (ex.: videolaparoscopia para laparotomia exploradora aberta) provocada por lesão iatrogênica acidental ou complicação técnica.
3. **S3 - Admissão em unidade de cuidados intensivos/terapia intensiva no pós-operatório**  
   *Descrição*: Encaminhamento não programado para UTI no pós-operatório imediato devido a choque cirúrgico, sangramento ou falência orgânica.
4. **S4 - Intubação ou reintubação ou uso de BiPap na unidade de recuperação pós-anestésica**  
   *Descrição*: Falência respiratória ou bloqueio neuromuscular residual exigindo retorno à ventilação mecânica na sala de recuperação.
5. **S5 - Raio X intraoperatório ou na unidade de recuperação pós-anestésica**  
   *Descrição*: Radiografia de emergência solicitada em sala cirúrgica ou RPA por suspeita de compressas ou instrumentais retidos na cavidade.
6. **S6 - Morte intra ou no pós-operatório**  
   *Descrição*: Óbito ocorrido na mesa de cirurgia ou durante o período de recuperação cirúrgica imediata atribuível ao ato anestésico-cirúrgico.
7. **S7 - Ventilação mecânica por tempo superior a 24 horas no pós-operatório**  
   *Descrição*: Dependência ventilatória não programada excedendo 24h pós-cirurgia em paciente previamente hígido pulmonar.
8. **S8 - Administração intraoperatória de adrenalina, noradrenalina, naloxona ou flumazenil**  
   *Descrição*: Uso emergencial de vasopressores ou antagonistas para choque anafilático anestésico ou parada cardiorrespiratória em sala.
9. **S9 - Aumento do nível de troponina superior a 1,5 nanograma/mL no pós-operatório**  
   *Descrição*: Marcador de infarto agudo do miocárdio perioperatório decorrente de hipóxia ou instabilidade cirúrgica.
10. **S10 - Lesão, reparação ou remoção de órgão durante o procedimento cirúrgico**  
    *Descrição*: Trauma não intencional durante a cirurgia (laceração hepática, perfuração vesical, secção de ureter) exigindo reparo no mesmo ato.
11. **S11 - Ocorrência de qualquer complicação cirúrgica**  
    *Descrição*: Deiscência de sutura, fístula digestiva, queimadura provocada por placa eletrocirúrgica ou hematoma intracavitário infectado.

### MÓDULO 4: CUIDADOS INTENSIVOS/TERAPIA INTENSIVA (Código: I)
1. **I1 - Pneumonia com início no hospital**  
   *Descrição*: Pneumonia desenvolvida após 48h de ventilação mecânica invasiva (PAV) na UTI.
2. **I2 - Readmissão em unidade de cuidados intensivos/terapia intensiva**  
   *Descrição*: Retorno à UTI antes da alta hospitalar devido à instabilidade gerada por complicação prévia ou liberação precoce.
3. **I3 - Procedimentos em unidade de cuidados intensivos/terapia intensiva**  
   *Descrição*: Complicação grave em traqueostomia à beira do leito, punção arterial ou passagem de cateter de hemodiálise na UTI.
4. **I4 - Intubação ou reintubação**  
   *Descrição*: Reintubação decorrente de extubação acidental provocada por agitação ou manejo inadequado na UTI.

### MÓDULO 5: PERINATAL (Código: P)
1. **P1 - Uso de agentes tocolíticos**  
   *Descrição*: Efeito adverso materno grave (edema pulmonar, hipotensão severa) decorrente de tocolíticos (terbutalina, sulfato de magnésio).
2. **P2 - Lacerações de 3º e 4º graus**  
   *Descrição*: Rotura perineal severa com lesão do esfíncter anal ou mucosa retal ocorrida durante parto vaginal.
3. **P3 - Contagem de plaquetas inferior a 50.000**  
   *Descrição*: Plaquetopenia materna grave decorrente de síndrome HELLP iatrogênica ou transfusional com sangramento associado.
4. **P4 - Perda de sangue estimada superior a 500 mL para parto vaginal, ou 1.000 mL para parto cesariana**  
   *Descrição*: Hemorragia pós-parto patológica exigindo ressuscitação volêmica, suturas hemostáticas ou histerectomia puerperal.
5. **P5 - Consulta com outra especialidade/interconsulta**  
   *Descrição*: Chamada de urgência de cirurgião geral ou urologista por lesão cirúrgica materna intra-cesárea.
6. **P6 - Administração de oxitocina/ocitocina e similares no período pós-parto**  
   *Descrição*: Uso superior a 20 UI de ocitocina ou derivados de ergot para conter atonia uterina pós-trauma de parto.
7. **P7 - Parto instrumentalizado**  
   *Descrição*: Lesão genital severa ou hematoma perineal extenso decorrente do emprego de fórceps ou vácuo-extrator.
8. **P8 - Administração de anestesia geral**  
   *Descrição*: Conversão de raquianestesia para anestesia geral devido a bloqueio alto inadequado ou broncoaspiração materna.

### MÓDULO 6: SERVIÇO DE URGÊNCIA/PRONTO ATENDIMENTO (Código: E)
1. **E1 - Readmissão no serviço de urgência/pronto atendimento nas 48 horas após a alta**  
   *Descrição*: Retorno à emergência em menos de 48h por agravamento de quadro decorrente de erro diagnóstico ou toxicidade medicamentosa inicial.
2. **E2 - Tempo de permanência no serviço de urgência/pronto atendimento superior a 6 horas**  
   *Descrição*: Espera de leito superior a 6h associada ao desenvolvimento de lesão por pressão de maca, queda no setor ou sepse não reconhecida.

---

## 3. Ferramentas da Qualidade Integradas
1. **Diagrama de Ishikawa (Espinha de Peixe)**: Mapeamento causal em 6 categorias (Método, Mão de Obra, Material, Máquina, Meio Ambiente, Medida).
2. **Ciclo PDCA/PDSA**: Fases: Plan (Problema e Ação), Do (Execução simulada), Check/Study (Métricas de impacto), Act (Padronização institucional).
3. **Matriz GUT**: Gravidade (1 a 5) × Urgência (1 a 5) × Tendência (1 a 5) = Prioridade (1 a 125).
4. **Matriz SWOT (FOFA)**: Forças, Oportunidades, Fraquezas e Ameaças relativas à segurança assistencial.
5. **Método 5W2H**: What (O quê), Why (Por quê), Where (Onde), When (Quando), Who (Quem), How (Como), How Much (Quanto custa).
6. **Brainstorming Estruturado**: Ideação coletiva sem críticas prévias para mapear barreiras de segurança.