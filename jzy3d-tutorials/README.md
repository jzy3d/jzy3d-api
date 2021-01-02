jzy3d-tutorials
===================

This module provides examples on how to create basic charts with Jzy3d.

For more example, refer to the complete [developer guide](http://jzy3d.org/documentation.php).

For more documentation about the framework design, see the API Readme file.


# Adding dependencies

See pom.xml file as an example.

## Add reference to Jzy3d Maven repository

```xml
<repositories>
  <repository>
    <id>jzy3d-snapshots</id>
    <name>Jzy3d Snapshots</name>
    <url>http://maven.jzy3d.org/snapshots/</url>
  </repository>
  <repository>
    <id>jzy3d-releases</id>
    <name>Jzy3d Releases</name>
    <url>http://maven.jzy3d.org/releases/</url>
  </repository>
</repositories>
```

## Add dependencies for native charts

```xml
<dependencies>
  <dependency>
    <groupId>org.jzy3d</groupId>
    <artifactId>jzy3d-native-jogl-awt</artifactId>
    <version>2.0.0-SNAPSHOT</version>
  </dependency>
  <dependency>
    <groupId>org.jzy3d</groupId>
    <artifactId>jzy3d-native-jogl-swing</artifactId>
    <version>2.0.0-SNAPSHOT</version>
  </dependency>
  <dependency>
    <groupId>org.jzy3d</groupId>
    <artifactId>jzy3d-native-jogl-newt</artifactId>
    <version>2.0.0-SNAPSHOT</version>
  </dependency>
  <dependency>
    <groupId>org.jzy3d</groupId>
    <artifactId>jzy3d-native-jogl-swt</artifactId>
    <version>2.0.0-SNAPSHOT</version>
  </dependency>

  <dependency>
    <groupId>org.jzy3d</groupId>
    <artifactId>jzy3d-tester-native</artifactId>
    <version>2.0.0-SNAPSHOT</version>
  </dependency>
</dependencies>
```

## Add dependencies for emulated charts

```xml
<dependencies>
  <dependency>
      <groupId>org.jzy3d</groupId>
      <artifactId>jzy3d-emul-gl</artifactId>
      <version>2.0.0-SNAPSHOT</version>
  </dependency>
</dependencies>
```

Note that Emulated charts and native charts can both appear in the path.

The below map better illustrate how the API is splitted into Maven modules.

<img src="doc/Maven.png"/>

# Scatter charts

See ```ScatterDemo```

<img src="doc/demo-scatter.png"/>

# Surface charts

See ```SurfaceDemoAWT```

<img src="doc/demo-surface.png"/>

# Volume charts

See ```LizardVolumeDemo```

<img src="doc/demo-volume.png"/>

# 2D Line charts

See ```Chart2DDemo```

<img src="doc/demo-line-2d.png"/>

# Debug charts

See ```DebugGL_Demo``` and modules ```jzy3d-tester```, ```jzy3d-tester-native```

<img src="doc/demo-debug-gl.png"/>
