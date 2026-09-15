############################################################################
# Makefile - Documento de Diagrama de Classes DCOMP/UFS
# SIGEA-GTT - Matheus Araujo Pereira
############################################################################

FILENAME = documento_classes

all: $(FILENAME).pdf

$(FILENAME).pdf: $(FILENAME).tex
	pdflatex -interaction=nonstopmode $(FILENAME).tex
	bibtex $(FILENAME)
	pdflatex -interaction=nonstopmode $(FILENAME).tex
	pdflatex -interaction=nonstopmode $(FILENAME).tex

clean:
	rm -f *.ps *.dvi *.aux *.toc *.idx *.ind *.ilg *.log *.out *.brf *.blg *.bbl *.loa *.loc *.lof *.lot *.loq Conteudo/*.aux Pre_Textual/*.aux $(FILENAME).pdf

clean-build:
	rm -f *.ps *.dvi *.aux *.toc *.idx *.ind *.ilg *.log *.out *.brf *.blg *.bbl *.loa *.loc *.lof *.lot *.loq Conteudo/*.aux Pre_Textual/*.aux
