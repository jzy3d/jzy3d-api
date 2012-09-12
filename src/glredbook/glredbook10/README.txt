This ReadMe belongs to the glredbook10 package of glredbook project.

The Java code example in this directory are ports of C examples that 
accompany the examples printed in the _OpenGL Programming Guide_, 
published by Addison-Wesley; ISBN 0-201-63274-8.  
(Japanese language edition:  ISBN: 4-7952-9645-6)

The source code examples here need to be compiled with jogl.jar 
and the system dependent jogl-natives-*.jar 
(where * is either linux, win32, solsparc, solx86, and/or macosx).

- No ant build file is available yet.
- All programs require Java Runtime 1.4.2.
- All examples that includes jitter.h 
  has an internal copy.
- All examples extends from glskeleton.java

Note: Color Index examples are not support in javax.media.opengl (jogl), 
so they are not ported. NURBS are also not ported for the same reason.
LIST:
- antiindex.c
- antipindex.c
- fogindex.c
- nurbs.c
- surface.c
- trim.c

jfont.java is my replacement for xfont.c. jfont uses Java's GlyphVector
structure a string's geometry while xfont uses X Window specific code
to do the same.

Kiet Le (Java port) 
ak.kiet.le '@' gmail '.' com
ak.kiet.le.googlepages.com/theredbookinjava.html
These ports are (c) Copyright 2006, Kiet Le. 

/*
 *	For the software in this directory
 * (c) Copyright 1993, Silicon Graphics, Inc.
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
 * OpenGL(TM) is a trademark of Silicon Graphics, Inc.
 */
