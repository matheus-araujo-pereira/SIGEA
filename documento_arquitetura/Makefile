############################################################################
# Makefile - Documento de Arquitetura de Software 4+1 DCOMP/UFS
# SIGEA-GTT - Matheus Araujo Pereira
############################################################################

FILENAME = documento_arquitetura

all: $(FILENAME).pdf

$(FILENAME).pdf: $(FILENAME).tex
	pdflatex -interaction=nonstopmode $(FILENAME).tex
	bibtex $(FILENAME)
	pdflatex -interaction=nonstopmode $(FILENAME).tex
	pdflatex -interaction=nonstopmode $(FILENAME).tex

clean:
	rm -f *.ps *.dvi *.aux *.toc *.idx *.ind *.ilg *.log *.out *.brf *.blg *.bbl *.loa *.loc *.lof *.lot *.loq Conteudo/*.aux Pre_Textual/*.aux $(FILENAME).pdf
