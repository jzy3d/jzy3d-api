jzy3d-swt
=========

This project is part of a <a href="https://github.com/jzy3d/jzy3d-master">multi-modules project</a>

Tools to run jzy3d-api in SWT

The SWT jar files listed as a dependency are actually not deployed in Maven Central so you need to add the following repository to the pom.xml file:
swt-repo
https://swt-repo.googlecode.com/svn/repo/
Otherwise, you're going to get an error message like the following when trying to build the project:
Failure to find org.eclipse.swt:org.eclipse.swt.gtk.linux.x86_64:jar:4.2.1

