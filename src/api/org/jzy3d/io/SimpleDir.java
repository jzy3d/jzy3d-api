// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SimpleDir.java

package org.jzy3d.io;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SimpleDir
{

    public SimpleDir()
    {
    }

    public static List getAllFolders(File file)
        throws IOException
    {
        if(!file.exists())
            throw new IOException((new StringBuilder()).append("File does not exist:").append(file).toString());
        List output = new ArrayList();
        File folders[] = listDir(file);
        File arr$[] = folders;
        int len$ = arr$.length;
        for(int i$ = 0; i$ < len$; i$++)
        {
            File f = arr$[i$];
            output.add(f);
        }

        return output;
    }

    public static File[] listFile(File dir)
    {
        FileFilter fileFilter = new FileFilter() {

            public boolean accept(File file)
            {
                return !file.isDirectory();
            }

        }
;
        return dir.listFiles(fileFilter);
    }

    public static File[] listDir(File dir)
    {
        FileFilter fileFilter = new FileFilter() {

            public boolean accept(File file)
            {
                return file.isDirectory();
            }

        }
;
        return dir.listFiles(fileFilter);
    }

    public static List getAllFiles(List file)
        throws IOException
    {
        if(file.size() == 0)
            return new ArrayList(0);
        if(file.size() == 1)
            return getAllFiles((File)file.get(0));
        List out = new ArrayList();
        File f;
        for(Iterator i$ = file.iterator(); i$.hasNext(); out.addAll(getAllFiles(f)))
            f = (File)i$.next();

        return out;
    }

    public static List getAllFiles(File file)
        throws IOException
    {
        if(!file.exists())
            throw new IOException((new StringBuilder()).append("File does not exist:").append(file).toString());
        File files[] = listFile(file);
        File folders[] = listDir(file);
        List out = new ArrayList();
        File arr$[] = files;
        int len$ = arr$.length;
        for(int i$ = 0; i$ < len$; i$++)
        {
            File f = arr$[i$];
            out.add(f);
        }

        arr$ = folders;
        len$ = arr$.length;
        for(int i$ = 0; i$ < len$; i$++)
        {
            File f = arr$[i$];
            out.addAll(getAllFiles(f));
        }

        return out;
    }
}
