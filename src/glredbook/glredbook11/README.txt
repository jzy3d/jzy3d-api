This README is for the glredbook11 package.

These are port of the examples programs which are featured
in the OpenGL Programming Guide, 2nd Edition corresponding
to OpenGL version 1.1.

- All examples requires JDK 1.4.2 and javax.media.opengl (jogl)
  and a system dependent jogl-natives-*.jar to compile.
- No ant build file is yet available.
    
New in the second edition demostrating new features of OpenGL 1.1:

1. polyoff.c	--> polyoff.java
2. texbind.c	--> texbind.java
3. texprox.c	--> texprox.java
4. texsub.c		--> texsub.java
5. varray.c		--> varray.java

These may not be able to run in OpenGL 1.1, unless modified 
to support extensions.

OpenGL 1.0 to 1.1 compatibility issues:
There are nine programs that are modify for 1.1 features 
but they will also run in 1.0.

Four programs--checker.c, mipmap.c, texgen.c, and wrap.c--using
texture objects have been modified so that they will avoid
the use of texture objects on OpenGL 1.0 machines.

Best regards,

Kiet Le (Java port 200604)
ak.kiet.le '@' gmail.com
http://ak.kiet.le.googlepages.com/theredbookinjava.html


/* ALL PROGRAMS IN PACKAGE HAS THE FOLLOWING LICENSE.
 *
 * Copyright (c) 1993-1997, Silicon Graphics, Inc.
 * ALL RIGHTS RESERVED 
 * Permission to use, copy, modify, and distribute this software for 
 * any purpose and without fee is hereby granted, provided that the above
 * copyright notice appear in all copies and that both the copyright notice
 * and this permission notice appear in supporting documentation, and that 
 * the name of Silicon Graphics, Inc. not be used in advertising
 * or publicity pertaining to distribution of the software without specific,
 * written prior permission. 
 *
 * THE MATERIAL EMBODIED ON THIS SOFTWARE IS PROVIDED TO YOU "AS-IS"
 * AND WITHOUT WARRANTY OF ANY KIND, EXPRESS, IMPLIED OR OTHERWISE,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTY OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE.  IN NO EVENT SHALL SILICON
 * GRAPHICS, INC.  BE LIABLE TO YOU OR ANYONE ELSE FOR ANY DIRECT,
 * SPECIAL, INCIDENTAL, INDIRECT OR CONSEQUENTIAL DAMAGES OF ANY
 * KIND, OR ANY DAMAGES WHATSOEVER, INCLUDING WITHOUT LIMITATION,
 * LOSS OF PROFIT, LOSS OF USE, SAVINGS OR REVENUE, OR THE CLAIMS OF
 * THIRD PARTIES, WHETHER OR NOT SILICON GRAPHICS, INC.  HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS, HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, ARISING OUT OF OR IN CONNECTION WITH THE
 * POSSESSION, USE OR PERFORMANCE OF THIS SOFTWARE.
 * 
 * US Government Users Restricted Rights 
 * Use, duplication, or disclosure by the Government is subject to
 * restrictions set forth in FAR 52.227.19(c)(2) or subparagraph
 * (c)(1)(ii) of the Rights in Technical Data and Computer Software
 * clause at DFARS 252.227-7013 and/or in similar or successor
 * clauses in the FAR or the DOD or NASA FAR Supplement.
 * Unpublished-- rights reserved under the copyright laws of the
 * United States.  Contractor/manufacturer is Silicon Graphics,
 * Inc., 2011 N.  Shoreline Blvd., Mountain View, CA 94039-7311.
 *
 * OpenGL(R) is a registered trademark of Silicon Graphics, Inc.
 */
