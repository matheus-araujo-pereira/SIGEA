---
name: latex-dcomp
description: >-
  Instruções e comandos para compilação, formatação e validação do documento acadêmico em LaTeX (DCOMP/UFS).
  Use esta skill sempre que o usuário solicitar alterações na monografia, geração do PDF,
  ajuste de tabelas, imagens, referências bibliográficas (BibTeX) ou relatórios de TCC.
---

# Skill: LaTeX DCOMP (Compilação e Normas Acadêmicas UFS)

Este procedimento orienta a manutenção e compilação do documento acadêmico do SIGEA sob as normas do DCOMP/UFS e ABNT.

---

## 1. Estrutura do Documento Acadêmico

O projeto reside em `projeto_departamental/`:
- `projeto_departamento.tex`: Arquivo principal (mestre).
- `dcomp-abntex2.cls`: Classe LaTeX oficial customizada para o DCOMP/UFS.
- `referencias.bib`: Base de dados bibliográfica BibTeX.
- `figuras/`: Diagramas e imagens vetoriais/rasterizadas.
- `Cap1-Introducao.tex` a `Cap5-Conclusao.tex`: Capítulos modulares.
- `Makefile`: Automação da compilação com `pdflatex` e `bibtex`.

---

## 2. Passo a Passo de Compilação

1. Navegue até o diretório da monografia:
   ```bash
   cd projeto_departamental
   ```
2. Limpe os arquivos temporários e compile o PDF:
   ```bash
   make clean && make
   ```
3. Verifique a ausência de erros graves no log de compilação:
   - Certifique-se de que não haja `Fatal error occurred` ou referências indefinidas (`LaTeX Warning: Reference '...' undefined`).
4. Verifique a integridade do PDF gerado:
   ```bash
   pdfinfo projeto_departamento.pdf
   ```

---

## 3. Diretrizes de Diagramação e Referências
- **Tabelas e Figuras**:
  - Toda tabela larga deve utilizar `\begin{table}[htbp]` com `\begin{adjustbox}{max width=\textwidth}` ou ambiente `tabularx` para não estourar as margens laterais da página A4.
  - Figuras devem ser centralizadas com `\centering` e ter legendas descritivas na parte superior e fonte na parte inferior, conforme ABNT.
- **Citações e Referências**:
  - Todas as entradas em `referencias.bib` devem conter campos essenciais (`author`, `title`, `year`, `publisher` ou `journal`).
  - Não adicionar referências fantasmas que não sejam citadas explicitamente no texto com `\cite` ou `\citeonline`.
