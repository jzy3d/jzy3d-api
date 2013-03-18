// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SimpleFile.java

package org.jzy3d.io;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class SimpleFile
{

    public SimpleFile()
    {
    }

    public static void write(String content, String file)
        throws Exception
    {
        createParentFoldersIfNotExist(file);
        Writer out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        out.write(content);
        out.close();
    }

    public static void createParentFoldersIfNotExist(String file)
    {
        File parent = (new File(file)).getParentFile();
        if(parent != null && !parent.exists())
            parent.mkdirs();
    }

    public static List read(String filename)
        throws IOException
    {
        List output = new ArrayList();
        File file = new File(filename);
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        DataInputStream dis;
        for(dis = new DataInputStream(bis); dis.available() != 0; output.add(dis.readLine()));
        fis.close();
        bis.close();
        dis.close();
        return output;
    }

    public static String readAsString(String filename)
        throws Exception
    {
        StringBuffer sb = new StringBuffer();
        File file = new File(filename);
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        DataInputStream dis;
        for(dis = new DataInputStream(bis); dis.available() != 0; sb.append((new StringBuilder()).append(dis.readLine()).append("\n").toString()));
        fis.close();
        bis.close();
        dis.close();
        return sb.toString();
    }

    /*public static List readFile(File file)
        throws IOException
    {
        List lines = new ArrayList();
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String s;
        while((s = br.readLine()) != null) 
            lines.add(s);
        fr.close();
        return lines;
    }*/

    public static boolean isYounger(String file1, String file2)
        throws Exception
    {
        File f1 = new File(file1);
        File f2 = new File(file2);
        if(!f2.exists())
            return false;
        if(!f1.exists())
            return true;
        else
            return f1.lastModified() > f2.lastModified();
    }
}
