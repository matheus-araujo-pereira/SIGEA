---
name: gtt-audit
description: >-
  Runbook operacional para auditoria clínica e validação da metodologia IHI Global Trigger Tool (GTT).
  Use esta skill quando o usuário solicitar execução ou testes de auditoria de prontuários,
  verificação de gatilhos clínicos (53 triggers), classificação de dano NCC MERP (A a I)
  ou cálculo de taxas epidemiológicas (EA por 1000 dias-paciente, EA por 100 admissões, % com dano).
---

# Skill: GTT Audit (Metodologia IHI Global Trigger Tool)

Este procedimento orienta a validação e execução de auditorias clínicas retrospectivas segundo a metodologia do *Institute for Healthcare Improvement (IHI)* no SIGEA-GTT.

---

## 1. Fluxo Operacional da Auditoria GTT

A auditoria divide-se rigorosamente em duas etapas sequenciais:

```
[ Prontuário Simulado ]
        │
        ▼
[ Fase 1: Revisão Primária ] ── (Tempo Limite: 20 min)
   • Auditor Discente / Enfermeiro
   • Rastreamento dos 53 Gatilhos nos 6 módulos
   • Apontamento de suspeita de dano
        │
        ▼
   Gatilhos Positivos?
      ├── NÃO ──► Conclusão (Sem Dano / Auditoria Finalizada)
      └── SIM ──► Encaminhar para Revisão de Consenso
                    │
                    ▼
[ Fase 2: Revisão de Consenso ]
   • Professor / Auditor Sênior
   • Análise de Causalidade (O dano decorreu do cuidado de saúde?)
   • Atribuição da Categoria NCC MERP (A a I)
   • Conclusão e Consolidação no Prontuário
```

---

## 2. Checklist dos 6 Módulos e 53 Gatilhos

Ao auditar prontuários, rastreie os seguintes códigos padronizados:

- **Módulo C (Cuidados Gerais - 19 gatilhos)**:
  - `C-01` Parada cardiorrespiratória | `C-02` Queda do leito | `C-03` Lesão por Pressão estágio II+
  - `C-04` Readmissão em até 30 dias | `C-05` Transferência não planejada de setor
  - `C-06` a `C-19` Outras complicações de internação geral.
- **Módulo M (Medicamentos - 13 gatilhos)**:
  - `M-01` Uso de Naloxona | `M-02` Uso de Flumazenil | `M-03` Uso de Vitamina K / PFC
  - `M-04` Glicemia < 50 mg/dL | `M-05` Sulfato de Protamina | `M-06` a `M-13` Outros antídotos.
- **Módulo S (Cirúrgico - 11 gatilhos)**:
  - `S-01` Retorno não planejado ao centro cirúrgico | `S-02` Mudança de procedimento
  - `S-03` Cirurgia > 6h | `S-04` Queda brusca de Hb/Ht | `S-05` a `S-11` Complicações pós-operatórias.
- **Módulo I (UTI - 4 gatilhos)**:
  - `I-01` Reintubação < 48h | `I-02` Transferência não programada para UTI
  - `I-03` Permanência em UTI > 7 dias | `I-04` PCR em UTI.
- **Módulo P (Perinatal - 4 gatilhos)**:
  - `P-01` Apgar 5º min < 6 | `P-02` Trauma obstétrico | `P-03` Admissão inesperada em UTIN | `P-04` Hemorragia pós-parto.
- **Módulo E (Emergência - 2 gatilhos)**:
  - `E-01` Retorno ao PS em 48h | `E-02` Tempo no PS > 24h.

---

## 3. Classificação de Danos NCC MERP

O auditor sênior deve categorizar o desfecho:
- **Sem Dano**: Categorias A, B, C, D (não pontuam como Evento Adverso).
- **Com Dano (Eventos Adversos GTT)**:
  - `E`: Dano temporário com intervenção necessária.
  - `F`: Dano temporário com prolongamento de internação.
  - `G`: Dano permanente.
  - `H`: Intervenção para manutenção da vida.
  - `I`: Óbito relacionado ao cuidado.

---

## 4. Validação das Métricas Estatísticas

Execute a verificação das fórmulas calculadas pelo backend:
1. **Taxa de EA / 1000 dias-paciente**: `(Total EA E-I / Total Dias de Internação) * 1000`
2. **Taxa de EA / 100 admissões**: `(Total EA E-I / Total Prontuários) * 100`
3. **% de Admissões com Dano**: `(Prontuários com >=1 EA / Total Prontuários) * 100`
