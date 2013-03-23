Jzy3d - A Java API for 3d charts
================================

Project layout
--------------
Source organisation
- src/api holds the API sources
- src/bridge holds classes to ease injection of components from a given windowing toolkit to another one (awt, swing, swt, newt) 
- src/tests holds
 - ChartTest, a tool to compare a chart with a previously saved screenshot
 - Replay, a utility to record and validate a sequence of mouse and key interactions results on a chart (work in progress)

Library dependencies
- jogl2
- jdt (currently copied in API but will be externalized soon)
- opencsv
- swt
- log4j
- junit

Project dependencies
These project dependencies are set through eclipse .classpath file.
- jzy3d-tools-convexhull

Build files
- build.xml
- javadoc.xml


Satellite projects depending on Jzy3d
--------------
Satellite projects are extensions of the framework that remain external to the API.

- jzy3d-graphs
- jzy3d-depthpeeling
- jzy3d-svm-mapper (also depends on jzy3d-tools-libsvm)
- glredbook

License
--------------
New BSD

More information
--------------
http://www.jzy3d.org

