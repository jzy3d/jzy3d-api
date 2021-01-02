jzy3d-api
=========

Jzy3d is a framework for easily drawing 3d and 2d charts in Java, using either fast native GPU rendering or CPU based rendering. The framework targets simplicity and portability (AWT, SWT, NEWT, Swing, GPU/CPU, etc).


# How to use

Refer to the [tutorial README](jzy3d-tutorials/README.md) file to get help on creating your first chart project with the help of example code.

# Architecture

<img src="doc/Components.png"/>

<img src="doc/Factories.png"/>

<img src="doc/Interop.png"/>



# Extensions

Additional modules kept separated demonstrate side works on Jzy3d
- <a href="https://github.com/jzy3d/bigpicture">jzy3d-bigpicture</a> : drivers to few big data storage to draw massive amount of points
- <a href="https://github.com/jzy3d/jzy3d-graphs">jzy3d-graph</a> : 3d graphs layout and rendering using Gephi toolkit
- <a href="https://github.com/jzy3d/jzy3d-spectro">jzy3d-spectro</a> : 3d spectrogram






# Build

```
mvn install
```

# License

New BSD

# More information

http://www.jzy3d.org

Travis build status : [![Build Status](https://travis-ci.org/jzy3d/jzy3d-api.svg?branch=master)](https://travis-ci.org/jzy3d/jzy3d-api)
