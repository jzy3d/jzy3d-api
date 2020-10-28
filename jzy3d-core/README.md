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

JavaFX chart - attention devrait utiliser Offscreen
Guide tests generating picture
WireSurfaceOffscreenDemo ne s'ouvre plus
ShaderMandelbrotDemo
DepthPeelingChart
TrialFaceOrdering
PickableGraphDemo -> le click selectionne une zone décallée
ChartTester NPE

ObjFileVBODemo : Caused by: java.lang.NoSuchMethodError: java.nio.FloatBuffer.rewind()Ljava/nio/FloatBuffer;

LOG CHARTS
- ANNOTATIONS DE POINT MAL PLACEES (DebugGL_LogCharts)
- AXIS LABEL DE LOG SCATTER MAL PLACEES (DemoLogScatter)

TextureDemoOffscreen génère des screenshot super pales
SymbolPointOffscreenDemo n'a pas généré d'image

GUIDE : SWT in classpath prevent an AWT application from opening -> document this in SWT module

TODO ==============

IGLLoader

Dive in
- AWTImageViewport

Design
- Setting : s'assurer plus utilités
- TestDemos.test a besoin de modifier les factory en offscreen
- Renderer3d to be moved to JOGL, extract CanvasListener interface?
- Disk object : GLUNewQuadric wrap with jGL and JOGl
- Quand on utilise GLMock, il faut désactiver manuellement la mise à jour du GL dans Renderer3d. Trouver un meilleur mécanisme

View
- public GL getCurrentGL()
- public GLContext getCurrentContext()
- protected GLAutoDrawable getCanvasAsGLAutoDrawable()


Maven modules
- IO MODULE TO AVOID MIXING WITH CLEAN API FOR TS etc GENERATION?
- Offscreen in a dedicated maven artifact?
- native-jogl-core pour le painter GL2, embedded, les VBO

Ajouter un test U 
- sur ChartTester.compare 
- pour chaque type de factory en utilisant ChartTester
- ChartTest a une NPE quand la taille de l'image d'origine et comparées ne sont pas similaire ChartTester.TEST_IMG_SIZE
- Must test CameraDistanceAnnotation is properly applying Log transform
- Déplacer vers chart-tester : CameraEyeOverlayAnnotation, CameraDistanceAnnotation, BarycenterAnnotation, CameraPathAnnotation. PLUS tester la capacité à appliquer space transform 
- Déplacer vers guide example tester : with example with FaceOrderingProblem in the GUIDE example


Tous les utilisateur de getCurrentContext ne devraient pas le faire explicitement, idéalement c'est à l'intérieur du Painter
  AbstractCameraController -> Mouse pickers




FINAL CLEANUP ===========

DELETE
- ColorMapperUpdater
- ProjectionUtils
- ChartScene that is not used (intention : spawn multiple views. Moreover Scene already hold a list of views

CHART.add/removeDrawable

Remplacer ChartLauncher par méthode open


GUIDE UPDATE =====

Squarify
Volume
VBO
Factories

POST IMPROVEMENT ==
EnlightableBar is not englightable!