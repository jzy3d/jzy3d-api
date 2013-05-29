Jzy3d - A Java API for 3d charts
================================

Project layout
--------------
Source folders organisation:
- api holds the API sources
- bridge holds classes to ease injection of components from a given windowing toolkit to another one (awt, swing, swt, newt) 
- tests holds
 - ChartTest, a tool to compare a chart with a previously saved screenshot
 - Replay, a utility to record and validate a sequence of mouse and key interactions results on a chart (work in progress)
- awt and swing hold Windowing toolkit dependent classes. 
 - Will be moved soon to external module as jzy3d-swt. 

Build
- Eclipse: .project & .classpath files
- Ant: build.xml
- Maven: pom.xml
- Javadoc: javadoc.xml

Library dependencies
- <a href="http://jogamp.org/jogl/www/">jogl2</a>
- <a href="https://github.com/yonatang/JDT">jdt</a> (currently copied in API but will be externalized soon)
- opencsv
- log4j
- junit

Windowing toolkit dependent modules
-----------------------------------
These modules extends Jzy3d with components dedicated to a windowing toolkit (awt, swt, swing, etc)
- <a href="https://github.com/jzy3d/jzy3d-swt">jzy3d-swt</a>
- jzy3d-awt (coming soon)
- jzy3d-swing (coming soon)


Satellite projects depending on Jzy3d
--------------
Satellite projects are extensions of the framework that remain external to the API.

- <a href="https://github.com/jzy3d/jzy3d-graphs">jzy3d-graphs</a>: extends Gephi toolkit and Jzy3d to allow 3d graph layouts 
- <a href="https://github.com/jzy3d/jzy3d-depthpeeling">jzy3d-depthpeeling</a>: an extension allowing visually perfect transparency
- <a href="https://github.com/jzy3d/jzy3d-svm-mapper">jzy3d-svm-mapper</a>:  building tesselated surfaces out of a SVM regression model (also depends on <a href="https://github.com/jzy3d/jzy3d-tools-libsvm">jzy3d-tools-libsvm</a>, a clone of libsvm of utility wrappers and refactors)
- glredbook

License
--------------
New BSD

More information
--------------
http://www.jzy3d.org

