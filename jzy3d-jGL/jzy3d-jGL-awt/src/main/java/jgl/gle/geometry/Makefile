# Makefile for jGL geometry extension codes

# jGL 3D Graphics Library for Java
# Version:  2.5
# Copyright (C) 2002  Robin Bing-Yu Chen

### Local Setting ###

LOCALCLASSPATH = ../../..

FILES = gle_phong_geo gle_phong_z_geo

### RULES ###

.SUFFIXES: .java

.java:
	@echo "Compile" $<
	@$(JAVAC) $(FLAGS) -classpath $(LOCALCLASSPATH) $<

### TARGETS ###

all:	$(FILES)
	@echo "Compile the GEOMETRY EXTENSION of jGL ok."

clean:
	@$(RM) *.class core

update:
	@cd .. ; $(MAKE) $@

include ../../Make-config

