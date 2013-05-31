Jzy3d - A Java API for 3d charts
================================

This project is part of a <a href="https://github.com/jzy3d/jzy3d-master">multi-modules project</a>

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

License
--------------
New BSD

More information
--------------
http://www.jzy3d.org

