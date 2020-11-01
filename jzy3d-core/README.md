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



