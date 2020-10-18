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

BROKE ==============

JavaFX chart
Guide tests generating picture

TODO ==============

Chart : remove GLCapabilities to be passed to Canvas.
Setting : move to Native

ChartComponentFactory.newFrame : a desactivé CanvasNewtAWt + CLEAN REFLECTION

AWTChartComponentFactory : remove Swing invoke + empêcher constructeur "awt", "newt", "swing", "offscreen"
SwingChartComponentFactory
OffscreenChartFactory
NewtChartFactory : rework newFrameSwing
Move SwingChartLauncher to Swing Core


SEPARATE IO MODULE TO AVOID MIXING WITH CLEAN API FOR TS etc GENERATION?