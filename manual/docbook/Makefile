OUTDIR   ?= ../html
XMLFILE  ?= jsampler.xml
XSLFILE  ?= html.xsl
XSLCHUNKEDFILE  ?= html-chunked.xsl
HTMLFILE ?= ../html/jsampler.html
CSSFILE	 ?= jsampler.css
XSLTPROC ?= xsltproc

html-chunked:: clean
	# creating output directory
	-@mkdir $(OUTDIR)
	# generating html
	$(XSLTPROC) -stringparam base.dir $(OUTDIR)/ -stringparam chunk.section.depth 1 $(XSLCHUNKEDFILE) $(XMLFILE)
	# copy css file to output directory
	-@cp $(CSSFILE) $(OUTDIR)/$(CSSFILE)
	# copy the image files to the output directory
	-@cp -r images  $(OUTDIR)/images
	# copy the license files to the output directory
	-@cp ../../licenses/gfdl.html  $(OUTDIR)

html:: clean
	# creating output directory
	-@mkdir $(OUTDIR)
	# generating html
	$(XSLTPROC) -o $(HTMLFILE) $(XSLFILE) $(XMLFILE)
	# copy css file to output directory
	-@cp $(CSSFILE) $(OUTDIR)/$(CSSFILE)
	# copy the image files to the output directory
	-@cp -r images  $(OUTDIR)/images
	# copy the license files to the output directory
	-@cp ../../licenses/gfdl.html  $(OUTDIR)

clean::
	@rm -rf $(OUTDIR)

