jzy3d-depthpeeling
==================

An extension of Jzy3d allowing to deploy depth peeling based charts for scene graph order independent transparency.

See http://www.jzy3d.org/plugins-depthpeeling.php


# Known problem on MacOSX 10.8.5 :
can not find an existing GLProfile compatible with GL2 and supporting ARB buffer:

/* IF TOO LOW PROFILE ON MAC */
        // ERROR : GLSLProgram: ERROR: 0:10: '' : extension 'ARB_draw_buffers'
        // is not supported

        // GLProfile.getDefault(); (isGL2 true)
        // GLProfile.getMinimum(true);
        // GLProfile.getMaxFixedFunc(true);

        /* IF TOO HIGH PROFILE ON MAC */
        // ERROR : jogamp.opengl.gl4.GL4bcImpl.getGL2(GL4bcImpl.java:40488) :
        // Not a GL2 implementation

        // GLProfile.getMaxProgrammable(true/false);
        // GLProfile.getMaxProgrammableCore(true/false);
        // GLProfile.getGL2GL3();
        // GLProfile.getGL3();

        /* IF TOO HIGH PROFILE ON MAC */

        // Profile XXXX is not available on
        // MacOSXGraphicsDevice[type .macosx, connection decon, unitID 0, handle
        // 0x0, owner false, NullToolkitLock[obj 0x5f375618]], but:
        // [GLProfile[GL2ES1/GL2.hw], GLProfile[GL2ES2/GL3.hw],
        // GLProfile[GL2/GL2.hw], GLProfile[GL2/GL2.hw], GLProfile[GL3/GL3.hw],
        // GLProfile[GL2GL3/GL3.hw]]

        // GLProfile.get(GLProfile.GL3bc);
        // GLProfile.get(GLProfile.GL4bc);
