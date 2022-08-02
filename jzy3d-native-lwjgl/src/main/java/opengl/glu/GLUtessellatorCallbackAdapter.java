/*
 * Copyright (c) 2002-2008 LWJGL Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
* Portions Copyright (C) 2003-2006 Sun Microsystems, Inc.
* All rights reserved.
*/

package opengl.glu;

/**
 * The <b>GLUtessellatorCallbackAdapter</b> provides a default implementation of
 * {@link GLUtessellatorCallback GLUtessellatorCallback}
 * with empty callback methods.  This class can be extended to provide user
 * defined callback methods.
 *
 * @author Eric Veach, July 1994
 * @author Java Port: Pepijn Van Eechhoudt, July 2003
 * @author Java Port: Nathan Parker Burg, August 2003
 */

public class GLUtessellatorCallbackAdapter implements GLUtessellatorCallback {
    public void begin(int type) {}
    public void edgeFlag(boolean boundaryEdge) {}
    public void vertex(Object vertexData) {}
    public void end() {}
//  public void mesh(com.sun.opengl.impl.tessellator.GLUmesh mesh) {}
    public void error(int errnum) {}
    public void combine(double[] coords, Object[] data,
                            float[] weight, Object[] outData) {}
    public void beginData(int type, Object polygonData) {}
    public void edgeFlagData(boolean boundaryEdge,
                                 Object polygonData) {}
    public void vertexData(Object vertexData, Object polygonData) {}
    public void endData(Object polygonData) {}
    public void errorData(int errnum, Object polygonData) {}
    public void combineData(double[] coords, Object[] data,
                                float[] weight, Object[] outData,
                                Object polygonData) {}
}