jzy3d-depthpeeling
==================

An extension of Jzy3d allowing to deploy depth peeling based charts for scene graph order independent transparency.

See http://www.jzy3d.org/plugins-depthpeeling.php


# Known problem on MacOSX 10.8.5 and beyond:

Can not find an existing GLProfile compatible with GL2 and supporting ARB buffer. Did comment ARB Buffer usage in shaders and it seams OK in most demos. Using GL2 instead of maxProgrammable(true) will make things "work" (start). 



# See also

https://forum.jogamp.org/Fail-to-run-a-depth-peeling-example-td3715480.html
https://coderedirect.com/questions/518899/writing-a-portable-java-application-using-jogl-and-android-opengl
https://stackoverflow.com/questions/35469370/opengl-performing-depth-peeling-in-a-scene-with-opaque-objects


