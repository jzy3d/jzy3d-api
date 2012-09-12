These are the example programs which are featured in the OpenGL
Programming Guide, Version 1.2.  To compile these programs, you
need OpenGL development libraries for your machine using the 
Java language and platform. Namely, JOGL/JSR-231 from 
http://jogl-dev.java.net.


All programs has keyboard listener to exit on ESC press.
 
* OpenGL 1.1 to 1.2 compatiblity issues

Most of the programs included in this distribution will work with
OpenGL 1.1.

* Notes for programs demonstrating the ARB Imaging Subset

With the introduction of OpenGL 1.2, the OpenGL Architecture Review
Board added the ARB Imaging Subset.  The imaging subset is not part of
the core OpenGL functionality, and as such, your implementation may not
support it. Some vendors implement only choose a subset of the Imaging
functions, so some program here will not display the expected results.
These programs check for the Imaging extension as well as the functions
it needs to call inorder to run; otherwise, the program just display
the original image.

These programs are: 
colormatrix.c, 
colortable.c,
convolution.c, 
histogram.c, 
minmax.c, and 
blendeqn.c.  	   

Thank you.

Kiet Le - ak.kiet.le@gmail.com (200607)
Red Book Porter