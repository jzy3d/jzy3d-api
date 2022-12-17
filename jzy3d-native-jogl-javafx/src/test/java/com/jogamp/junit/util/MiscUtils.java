/**
 * Copyright 2014 JogAmp Community. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY JogAmp Community ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JogAmp Community OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of JogAmp Community.
 */
package com.jogamp.junit.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.common.util.IOUtil;

public class MiscUtils {
    public static class CopyStats {
        public int totalBytes = 0;
        public int totalFiles = 0;
        public int totalFolders = 0;
        public int currentDepth = 0;
        public int maxDepth = 0;
        public boolean trackFiles;
        public final List<File> srcFiles = new ArrayList<File>();
        public final List<File> dstFiles = new ArrayList<File>();

        public void dump(final String prefix, final boolean folderOnly) {
            System.err.println(prefix+"Total bytes: "+totalBytes);
            System.err.println(prefix+"Total files: "+totalFiles);
            System.err.println(prefix+"Total folder: "+totalFolders);
            System.err.println(prefix+"Depth: "+currentDepth/maxDepth);
            System.err.println(prefix+"Tracking: "+trackFiles);
            if( trackFiles ) {
                for(int i=0; i<srcFiles.size(); i++) {
                    final File src = srcFiles.get(i);
                    final File dst = dstFiles.get(i);
                    if( !folderOnly || src.isDirectory() ) {
                        System.err.printf("%s\t src %4d: %s%n", prefix, i, src.toString());
                        System.err.printf("%s\t dst %4d: %s%n%n", prefix, i, dst.toString());
                    }
                }
            }
        }
    }
    public static CopyStats copy(final File src, final File dest, final int maxDepth, final boolean trackFiles) throws IOException {
        final CopyStats cs = new CopyStats();
        cs.maxDepth = maxDepth;
        cs.trackFiles = trackFiles;
        copy(src, dest, cs);
        return cs;
    }
    private static void copy(final File src, final File dest, final CopyStats stats) throws IOException {
        if(src.isDirectory()){
            if( stats.maxDepth >= 0 && stats.currentDepth >= stats.maxDepth ) {
                return;
            }
            stats.totalFolders++;
            if( stats.trackFiles ) {
                stats.srcFiles.add(src);
                stats.dstFiles.add(dest);
            }
            stats.currentDepth++;
            if(!dest.exists()){
                dest.mkdirs();
            }
            final String fileNames[] = src.list();
            for (int i=0; i<fileNames.length; i++) {
                final String fileName = fileNames[i];
                final File srcFile = new File(src, fileName);
                final File destFile = new File(dest, fileName);
                copy(srcFile, destFile, stats);
            }
            stats.currentDepth--;
        } else {
            stats.totalFiles++;
            if( stats.trackFiles ) {
                stats.srcFiles.add(src);
                stats.dstFiles.add(dest);
            }
            final InputStream in = new BufferedInputStream(new FileInputStream(src));
            try {
                stats.totalBytes += IOUtil.copyStream2File(in, dest, 0);
            } finally {
                in.close();
            }
        }
    }
}
