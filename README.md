jzy3d-main
==========

This is a main Git repository for Jzy3d providing multiple maven modules.

Travis build status : [![Build Status](https://travis-ci.org/jzy3d/jzy3d-api.svg?branch=master)](https://travis-ci.org/jzy3d/jzy3d-api)




API and modules
-----------------------------------
- <a href="https://github.com/jzy3d/jzy3d-api/blob/master/jzy3d-tutorials">jzy3d-tutorials</a> : few examples for building main chart families (surfaces, scatters, etc).

Application will require <a href="https://github.com/jzy3d/jzy3d-api/blob/master/jzy3d-api/src/api">jzy3d-api</a> <i>plus</i> classes to address a specific windowing environement (AWT, SWT, Swing). The API itself has no dependency to AWT, making it buildable for Android environement.

Code specific to a target windowing environement is made available through modules (or sometime source folder separation):
- <a href="https://github.com/jzy3d/jzy3d-api/blob/master/jzy3d-api/src/awt">jzy3d-awt</a> : provides AWT canvases (source folder separation but part of jzy3d-api build)
- <a href="https://github.com/jzy3d/jzy3d-api/blob/master/jzy3d-api/src/swing">jzy3d-swing</a> : provides Swing canvases  (source folder separation but jzy3d-api build)
- <a href="https://github.com/jzy3d/jzy3d-api/blob/master/jzy3d-swt">jzy3d-swt</a> : provides a wrapper on AWT canvas to embed a chart in a SWT application.
- <a href="https://github.com/jzy3d/jzy3d-api/blob/master/jzy3d-jdt-core">jzy3d-jdt-core</a> : a clone of JDT, for Java Delaunay Triangulation

The API has optional extensions bundled by a parent and several children module stored in separated Git repositories:
- <a href="https://github.com/jzy3d/jzy3d-extensions">jzy3d-extensions</a> : gather 5 optional modules that may be checkout separately as well (Depth peeling, Graphs, G2D, )


Jzy3d Maven Repository
-----------------------------------
- Release versions WILL stand on maven central
- Snapshot versions stand on Jzy3d maven repository:
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
	 &lt;name&gt;Jzy3d Snapshots&lt;/name&gt;
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
