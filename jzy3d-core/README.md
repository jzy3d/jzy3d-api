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

JavaFX chart
Guide tests generating picture
WireSurfaceOffscreenDemo ne s'ouvre plus
ShaderMandelbrotDemo
DepthPeelingChart
TrialFaceOrdering
PickableGraphDemo

Toutes les book demo ne s'ouvrent plus

Les graphes du plugin graphe ne sont plus colorié, les edges ont disparu


TextureDemoOffscreen génère des screenshot super pales
SymbolPointOffscreenDemo n'a pas généré d'image

TODO ==============

Chart : remove GLCapabilities to be passed to Canvas.
Setting : move to Native

ChartComponentFactory.newFrame : a desactivé CanvasNewtAWt + CLEAN REFLECTION

AWTChartComponentFactory : remove Swing invoke + empêcher constructeur "awt", "newt", "swing", "offscreen"
SwingChartComponentFactory
OffscreenChartFactory
NewtChartFactory : rework newFrameSwing
Move SwingChartLauncher to Swing Core

TestDemos.test a besoin de modifier les factory en offscreen


SEPARATE 
- IO MODULE TO AVOID MIXING WITH CLEAN API FOR TS etc GENERATION?
- Offscreen in a dedicated maven artifact?
- native-jogl-core pour le painter GL2, embedded, les VBO

Nettoyer l'analyse

Ajouter un test U pour chaque type de factory en utilisant ChartTester


FINAL CLEANUP ===========

DELETE ColorMapperUpdater
CHART.add/removeDrawable

Remplacer ChartLauncher par méthode open

GUIDE UPDATE =====

Squarify
Volume
VBO
Factories