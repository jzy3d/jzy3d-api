# Makefile for jGL Examples

# jGL 3D Graphics Library for Java
# Version:  2.4
# Copyright (C) 1996-2001  Robin Bing-Yu Chen

### Local Setting ###

LOCALCLASSPATH = jgl.jar

# LOCALCLASSPATH = jgl.jar:$(CLASSPATH)

BACKUPDIR = Example_BAK

FILES = bezcurve bezmesh bezsurf checker clip colormat cube doublebuffer \
	light lines list material mipmap model movelight pickdepth \
	picksquare planet polys robot select simple smooth stroke surface \
	tea teapots texgen texturesurf
# drawf

### RULES ###

.SUFFIXES: .java

.java:
	@echo "Compile" $< "and Convert" $*.html
	@$(JAVAC) $(FLAGS) -classpath $(LOCALCLASSPATH) $<
	@$(CP) $*.html $*-pi.html
	@$(HTMLCONV) $*-pi.html
#	@$(RM) converter.props
#	@$(JAVA) -jar $(HTMLCONV) $*.html -backup $(BACKUPDIR) -progress false
#	@$(MV) $*.html $*-pi.html
#	@$(MV) $(BACKUPDIR)/$*.html .

### TARGETS ###

all:	jgl.jar $(FILES)
	@$(RM) converter.props
#	@$(RM) convertlog.txt
	@cd .. ; $(RM) $(BACKUPDIR)
	@echo "Compile Examples of jGL ok."

jgl.jar:
	@cd ..; $(MAKE) update

clean:
	@$(RM) jgl.jar
	@$(RM) *.class core
	@$(RM) *-pi.html
	@$(RM) converter.props
#	@$(RM) convertlog.txt
	@cd .. ; $(RM) $(BACKUPDIR)

include Make-config
