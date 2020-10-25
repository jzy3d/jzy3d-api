Jzy3d - Core
================================

DEPLOY SOURCE AND JAVADOC :
```
mvn clean source:jar javadoc:jar deploy
 ```

To install a lib locally:

```
mvn install:install-file -DgroupId=org.jzyio
 -DartifactId=jzyio -Dversion=0.1 -Dpackaging=jar -Dfile=./lib/misc/org.jzyio-0.1.jar
```


To download javadocs for dependencies

```
mvn dependency:resolve -Dclassifier=javadoc
```



DONE ==============

animator moved to native chart
screenshot remove texture data
Rename ViewMouseController to NewtViewCameraController

BROKE ==============

Test : les images screenshot sont toutes grisées!! 0 couleur
Toutes les book demo ne s'ouvrent plus
JavaFX chart - attention devrait utiliser Offscreen
Guide tests generating picture
WireSurfaceOffscreenDemo ne s'ouvre plus
ShaderMandelbrotDemo
DepthPeelingChart
TrialFaceOrdering
PickableGraphDemo

LOG CHARTS
- ANNOTATIONS DE POINT MAL PLACEES (DebugGL_LogCharts)
- AXIS LABEL DE LOG SCATTER MAL PLACEES (DemoLogScatter)
- TOUTES LES SURFACE DEMO N'ONT PAS DE AXIS LABEL NI TICK LABEL

TextureDemoOffscreen génère des screenshot super pales
SymbolPointOffscreenDemo n'a pas généré d'image

TODO ==============

IGLLoader

Setting : move to Native

TestDemos.test a besoin de modifier les factory en offscreen

Delete ChartScene that is not used (intention : spawn multiple views. Moreover Scene already hold a list of views

Renderer3d to be moved to JOGL, extract CanvasListener interface?

Disk object : GLUNewQuadric wrap with jGL and JOGl

View
- public GL getCurrentGL()
- public GLContext getCurrentContext()
- protected GLAutoDrawable getCanvasAsGLAutoDrawable()


SEPARATE 
- IO MODULE TO AVOID MIXING WITH CLEAN API FOR TS etc GENERATION?
- Offscreen in a dedicated maven artifact?
- native-jogl-core pour le painter GL2, embedded, les VBO


Ajouter un test U pour chaque type de factory en utilisant ChartTester

Fix and move TestCoord3dRotate



FINAL CLEANUP ===========

DELETE ColorMapperUpdater
CHART.add/removeDrawable

Remplacer ChartLauncher par méthode open



GUIDE UPDATE =====

Squarify
Volume
VBO
Factories