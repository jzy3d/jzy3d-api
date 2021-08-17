# Makefile for jGL render extension codes

# jGL 3D Graphics Library for Java
# Version:  2.5
# Copyright (C) 1996-2002  Robin Bing-Yu Chen

### Local Setting ###

LOCALCLASSPATH = ../../..

FILES = gle_phong gle_phong_z

### RULES ###

.SUFFIXES: .java

.java:
	@echo "Compile" $<
	@$(JAVAC) $(FLAGS) -classpath $(LOCALCLASSPATH) $<

### TARGETS ###

all:	$(FILES)
	@echo "Compile the RENDER EXTENSION of jGL ok."

clean:
	@$(RM) *.class core

update:
	@cd .. ; $(MAKE) $@

include ../../Make-config

