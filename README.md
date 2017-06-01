jzy3d-main
==========

This is a main Git repository for Jzy3d providing multiple maven modules.

Travis build status : [![Build Status](https://travis-ci.org/jzy3d/jzy3d-api.svg?branch=master)](https://travis-ci.org/jzy3d/jzy3d-api)




API and modules
-----------------------------------
- <a href="https://github.com/jzy3d/jzy3d-api/blob/master/jzy3d-tutorials">jzy3d-tutorials</a> : few examples for building main chart families (surfaces, scatters, etc).

Application will require <a href="https://github.com/jzy3d/jzy3d-api/blob/master/jzy3d-api/src/api">jzy3d-api</a> <i>plus</i> classes to address a specific windowing environement (AWT, SWT, Swing). The API itself has no dependency to AWT, making it buildable for Android environement.

Code specific to a target windowing environement is made available through modules (or sometime source folder separation):
- <a href="https://github.com/jzy3d/jzy3d-api/blob/master/jzy3d-api/src/awt">jzy3d-api/awt</a> : provides AWT canvases (source folder separation but part of jzy3d-api build)
- <a href="https://github.com/jzy3d/jzy3d-api/blob/master/jzy3d-api/src/swing">jzy3d-api/swing</a> : provides Swing canvases  (source folder separation but jzy3d-api build)
- <a href="https://github.com/jzy3d/jzy3d-api/blob/master/jzy3d-swt">jzy3d-swt</a> : provides a wrapper on AWT canvas to embed a chart in a SWT application.
- <a href="https://github.com/jzy3d/jzy3d-api/blob/master/jzy3d-javafx">jzy3d-javafx</a> : render in Java FX applications.
- <a href="https://github.com/jzy3d/jzy3d-api/blob/master/jzy3d-jdt-core">jzy3d-jdt-core</a> : a clone of JDT, for Java Delaunay Triangulation
- <a href="https://github.com/jzy3d/jzy3d-api/blob/master/jzy3d-svm-mapper">jzy3d-svm-mapper</a> : fit a surface out of set of points using an SVM regression model
- <a href="https://github.com/jzy3d/jzy3d-api/blob/master/jzy3d-tools-libsvm">jzy3d-tools-libsvm</a> : a wrapper on LibSVM


Additional modules kept separated demonstrate side works on Jzy3d
- <a href="https://github.com/jzy3d/bigpicture">jzy3d-bigpicture</a> : drivers to few big data storage to draw massive amount of points
- <a href="https://github.com/jzy3d/jzy3d-graphs">jzy3d-graph</a> : 3d graphs layout and rendering using Gephi toolkit
- <a href="https://github.com/jzy3d/jzy3d-spectro">jzy3d-spectro</a> : 3d spectrogram



Jzy3d Maven Repository
-----------------------------------
- To add Jzy3d to your project

  release
  <pre>
  <code>
  &lt;dependency&gt;
    &lt;groupId&gt;org.jzy3d&lt;/groupId&gt;
    &lt;artifactId&gt;jzy3d-api&lt;/artifactId&gt;
    &lt;version&gt;1.0.0&lt;/version&gt;
  &lt;/dependency&gt;
  </code>
  </pre>
 snapshot
  <pre>
  <code>
  &lt;dependency&gt;
    &lt;groupId&gt;org.jzy3d&lt;/groupId&gt;
    &lt;artifactId&gt;jzy3d-api&lt;/artifactId&gt;
    &lt;version&gt;1.0.1-SNAPSHOT&lt;/version&gt;
  &lt;/dependency&gt;
  </code>
  </pre>


- Maven artifacts are stored there:
  <pre>
  <code>
  &lt;repositories&gt;
    &lt;repository&gt;
  	 &lt;id&gt;jzy3d-snapshots&lt;/id&gt;
  	 &lt;name&gt;Jzy3d Snapshots&lt;/name&gt;
  	 &lt;url&gt;http://maven.jzy3d.org/snapshots &lt;/url&gt;
    &lt;/repository&gt;
    &lt;repository&gt;
  	 &lt;id&gt;jzy3d-releases&lt;/id&gt;
  	 &lt;name&gt;Jzy3d Releases&lt;/name&gt;
  	 &lt;url&gt;http://maven.jzy3d.org/releases &lt;/url&gt;
    &lt;/repository&gt;
  &lt;/repositories&gt;
  </code>
  </pre>

Building the projects with Maven
-----------------------------------
Build all module from master repository by calling
- mvn install

To be friendly with Eclipse-but-non-Maven users, we add .project and .classpath files to the repositories. If you want to regenerate this files with maven and have the projects linked all together, simply run
- mvn eclipse:eclipse -Declipse.workspace=~[your current eclipse workspace folder]
- then edit jzy3d-api project properties to export all dependencies to other projects (Properties > Java Build Path > Order and Export > Select All. Then remove JRE System libraries).

Building the projects without Maven
-----------------------------------
We kept the repository easy to use for non-maven users.
- Eclipse project files (.project & .classpath) with inter-project relations are commited to the repositories
- Some modules have a lib/ directory containing required Jars. If you want to use these jars, simply edit the libraries dependencies of the Eclipse project to use them instead of the maven dependencies.

License
--------------
New BSD

More information
--------------
http://www.jzy3d.org
