ARGS	:= -interaction=nonstopmode

all: pdf clean

pdf: *.tex *.bib
	@pdflatex $(ARGS) *.tex > /dev/null 2>&1
	@bibtex *.aux > /dev/null 2>&1
	@pdflatex $(ARGS) *.tex > /dev/null 2>&1
	@pdflatex $(ARGS) *.tex > /dev/null 2>&1
	@xdg-open *.pdf > /dev/null 2>&1

clean:
	@rm -f *.aux *.bbl *.blg *.out > /dev/null 2>&1
