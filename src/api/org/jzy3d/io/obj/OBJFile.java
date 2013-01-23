package org.jzy3d.io.obj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jzy3d.maths.BoundingBox3d;

/**
 * Translated from C++ Version: nvModel.h - Model support class
 * 
 * The nvModel class implements an interface for a multipurpose model object.
 * This class is useful for loading and formatting meshes for use by OpenGL. It
 * can compute face normals, tangents, and adjacency information. The class
 * supports the obj file format.
 * 
 * Author: Evan Hart Email: sdkfeedback@nvidia.com
 * 
 * Copyright (c) NVIDIA Corporation. All rights reserved.
 */
public class OBJFile {
    /** Enumeration of primitive types */
    public enum PrimType {
        eptNone(0x0), eptPoints(0x1), eptEdges(0x2), eptTriangles(0x4), eptTrianglesWithAdjacency(0x8), eptAll(0xf);
        PrimType(int val) {
            m_iVal = val;
        }

        int m_iVal = 0;
    };

    public static final int NumPrimTypes = 4;

    /*
     * public Model CreateModel() { return new Model(); }
     */

    public OBJFile() {
        posSize_ = 0;
        pOffset_ = -1;
        nOffset_ = -1;
        vtxSize_ = 0;
        openEdges_ = 0;
    }

    public class IdxSet {
        Integer pIndex = 0;
        Integer nIndex = 0;

        boolean lessThan(IdxSet rhs) {
            if (pIndex < rhs.pIndex)
                return true;
            else if (pIndex == rhs.pIndex) {
                if (nIndex < rhs.nIndex)
                    return true;
            }
            return false;
        }
    };

    /**
     * This function attempts to determine the type of the filename passed as a
     * parameter. If it understands that file type, it attempts to parse and
     * load the file into its raw data structures. If the file type is
     * recognized and successfully parsed, the function returns true, otherwise
     * it returns false.
     */
    public boolean loadModelFromFile(String file) {
        URL fileURL = getClass().getClassLoader().getResource(File.separator + file);
        if (fileURL != null) {
            BufferedReader input = null;
            try {

                input = new BufferedReader(new InputStreamReader(fileURL.openStream()));
                String line = null;
                float[] val = new float[4];
                int[][] idx = new int[3][3];
                boolean hasNormals = false;

                while ((line = input.readLine()) != null) {
                    switch (line.charAt(0)) {
                    case '#':
                        break;
                    case 'v':
                        switch (line.charAt(1)) {

                        case ' ':
                            line = line.substring(line.indexOf(" ") + 1);
                            // vertex, 3 or 4 components
                            val[0] = Float.valueOf(line.substring(0, line.indexOf(" ")));
                            line = line.substring(line.indexOf(" ") + 1);
                            val[1] = Float.valueOf(line.substring(0, line.indexOf(" ")));
                            line = line.substring(line.indexOf(" ") + 1);
                            val[2] = Float.valueOf(line);
                            positions_.add(val[0]);
                            positions_.add(val[1]);
                            positions_.add(val[2]);
                            break;

                        case 'n':
                            // normal, 3 components
                            line = line.substring(line.indexOf(" ") + 1);
                            val[0] = Float.valueOf(line.substring(0, line.indexOf(" ")));
                            line = line.substring(line.indexOf(" ") + 1);
                            val[1] = Float.valueOf(line.substring(0, line.indexOf(" ")));
                            line = line.substring(line.indexOf(" ") + 1);
                            val[2] = Float.valueOf(line);
                            normals_.add(val[0]);
                            normals_.add(val[1]);
                            normals_.add(val[2]);
                            break;
                        }
                        break;

                    case 'f':
                        // face
                        line = line.substring(line.indexOf(" ") + 2);

                        idx[0][0] = Integer.valueOf(line.substring(0, line.indexOf("//"))).intValue();
                        line = line.substring(line.indexOf("//") + 2);
                        idx[0][1] = Integer.valueOf(line.substring(0, line.indexOf(" "))).intValue();

                        {
                            // This face has vertex and normal indices

                            // in .obj, f v1 .... the vertex index used start
                            // from 1, so -1 here
                            // remap them to the right spot
                            idx[0][0] = (idx[0][0] > 0) ? (idx[0][0] - 1) : ((int) positions_.size() - idx[0][0]);
                            idx[0][1] = (idx[0][1] > 0) ? (idx[0][1] - 1) : ((int) normals_.size() - idx[0][1]);

                            // grab the second vertex to prime
                            line = line.substring(line.indexOf(" ") + 1);
                            idx[1][0] = Integer.valueOf(line.substring(0, line.indexOf("//")));
                            line = line.substring(line.indexOf("//") + 2);
                            idx[1][1] = Integer.valueOf(line.substring(0, line.indexOf(" ")));

                            // remap them to the right spot
                            idx[1][0] = (idx[1][0] > 0) ? (idx[1][0] - 1) : ((int) positions_.size() - idx[1][0]);
                            idx[1][1] = (idx[1][1] > 0) ? (idx[1][1] - 1) : ((int) normals_.size() - idx[1][1]);

                            // grab the third vertex to prime
                            line = line.substring(line.indexOf(" ") + 1);
                            idx[2][0] = Integer.valueOf(line.substring(0, line.indexOf("//")));
                            line = line.substring(line.indexOf("//") + 2);
                            idx[2][1] = Integer.valueOf(line);
                            {
                                // remap them to the right spot
                                idx[2][0] = (idx[2][0] > 0) ? (idx[2][0] - 1) : ((int) positions_.size() - idx[2][0]);
                                idx[2][1] = (idx[2][1] > 0) ? (idx[2][1] - 1) : ((int) normals_.size() - idx[2][1]);

                                // add the indices
                                for (int ii = 0; ii < 3; ii++) {
                                    pIndex_.add(idx[ii][0]);
                                    nIndex_.add(idx[ii][1]);
                                }

                                // prepare for the next iteration, the num 0
                                // does not change.
                                idx[1][0] = idx[2][0];
                                idx[1][1] = idx[2][1];
                            }
                            hasNormals = true;
                        }
                        break;

                    default:
                        break;
                    }
                    ;
                }
                // post-process data
                // free anything that ended up being unused
                if (!hasNormals) {
                    normals_.clear();
                    nIndex_.clear();
                }

                posSize_ = 3;
                return true;

            } catch (FileNotFoundException kFNF) {
                System.err.println("Unable to find the shader file " + file);
            } catch (IOException kIO) {
                System.err.println("Problem reading the shader file " + file);
            } catch (NumberFormatException kIO) {
                System.err.println("Problem reading the shader file " + file);
            } finally {
                try {
                    if (input != null) {
                        input.close();
                    }
                } catch (IOException closee) {
                }
            }
        }
        return false;
    }

    /**
     * This function takes the raw model data in the internal
     * structures, and attempts to bring it to a format directly
     * accepted for vertex array style rendering. This means that
     * a unique compiled vertex will exist for each unique
     * combination of position, normal, tex coords, etc that are
     * used in the model. The prim parameter, tells the model
     * what type of index list to compile. By default it compiles
     * a simple triangle mesh with no connectivity.
     */
    public void compileModel() {
        boolean needsTriangles = true;

        HashMap<IdxSet, Integer> pts = new HashMap<IdxSet, Integer>();
        vertices_ = FloatBuffer.allocate((pIndex_.size() + nIndex_.size()) * 3);
        indices_ = IntBuffer.allocate(pIndex_.size());
        for (int i = 0; i < pIndex_.size(); i++) {
            IdxSet idx = new IdxSet();
            idx.pIndex = pIndex_.get(i);

            if (normals_.size() > 0)
                idx.nIndex = nIndex_.get(i);
            else
                idx.nIndex = 0;

            if (!pts.containsKey(idx)) {
                if (needsTriangles)
                    indices_.put(pts.size());

                pts.put(idx, pts.size());

                // position,
                vertices_.put(positions_.get(idx.pIndex * posSize_));
                vertices_.put(positions_.get(idx.pIndex * posSize_ + 1));
                vertices_.put(positions_.get(idx.pIndex * posSize_ + 2));

                // normal
                if (normals_.size() > 0) {
                    vertices_.put(normals_.get(idx.nIndex * 3));
                    vertices_.put(normals_.get(idx.nIndex * 3 + 1));
                    vertices_.put(normals_.get(idx.nIndex * 3 + 2));
                }

            } else {
                if (needsTriangles)
                    indices_.put(pts.get(idx));
            }
        }

        // create selected prim

        // set the offsets and vertex size
        pOffset_ = 0; // always first
        vtxSize_ = posSize_;
        if (hasNormals()) {
            nOffset_ = vtxSize_;
            vtxSize_ += 3;
        } else {
            nOffset_ = -1;
        }
        vertices_.rewind();
        indices_.rewind();
    }

    /**
     * Returns the points defining the axis-aligned bounding box containing the model.
     */
    public BoundingBox3d computeBoundingBox() {
        float[] minVal = new float[3];
        float[] maxVal = new float[3];
        
        if (positions_.isEmpty())
            return null;

        for (int i = 0; i < 3; i++) {
            minVal[i] = 1e10f;
            maxVal[i] = -1e10f;
        }

        for (int i = 0; i < positions_.size(); i += 3) {
            float x = positions_.get(i);
            float y = positions_.get(i + 1);
            float z = positions_.get(i + 2);
            minVal[0] = Math.min(minVal[0], x);
            minVal[1] = Math.min(minVal[1], y);
            minVal[2] = Math.min(minVal[2], z);
            maxVal[0] = Math.max(maxVal[0], x);
            maxVal[1] = Math.max(maxVal[1], y);
            maxVal[2] = Math.max(maxVal[2], z);
        }
        
        return new BoundingBox3d(minVal[0], maxVal[0], minVal[1], maxVal[1], minVal[2], maxVal[2]);
    }

    public void clearNormals() {
        normals_.clear();
        nIndex_.clear();
    }

    public boolean hasNormals() {
        return normals_.size() > 0;
    }

    public int getPositionSize() {
        return posSize_;
    }

    public int getNormalSize() {
        return 3;
    }

    public List<Float> getPositions() {
        return (positions_.size() > 0) ? positions_ : null;
    }

    public List<Float> getNormals() {
        return (normals_.size() > 0) ? normals_ : null;
    }

    public List<Integer> getPositionIndices() {
        return (pIndex_.size() > 0) ? pIndex_ : null;
    }

    public List<Integer> getNormalIndices() {
        return (nIndex_.size() > 0) ? nIndex_ : null;
    }

    public int getPositionCount() {
        return (posSize_ > 0) ? (int) positions_.size() / posSize_ : 0;
    }

    public int getNormalCount() {
        return (int) normals_.size() / 3;
    }

    public int getIndexCount() {
        return pIndex_.size();
    }

    public FloatBuffer getCompiledVertices() {
        return vertices_;
    }

    public IntBuffer getCompiledIndices() {
        return indices_;
    }

    public int getCompiledPositionOffset() {
        return pOffset_;
    }

    public int getCompiledNormalOffset() {
        return nOffset_;
    }

    public int getCompiledVertexSize() {
        return vtxSize_;
    }

    public int getCompiledVertexCount() {
        return ((pIndex_.size() + nIndex_.size()) * 3);
    }

    public int getOpenEdgeCount() {
        return openEdges_;
    }

    protected List<Float> positions_ = new ArrayList<Float>();
    protected List<Float> normals_ = new ArrayList<Float>();
    protected int posSize_;

    protected List<Integer> pIndex_ = new ArrayList<Integer>();
    protected List<Integer> nIndex_ = new ArrayList<Integer>();

    // data structures optimized for rendering, compiled model
    protected IntBuffer indices_ = null;
    protected FloatBuffer vertices_ = null;
    protected int pOffset_;
    protected int nOffset_;
    protected int vtxSize_ = 0;
    protected int openEdges_;
};
