package com.yunchuan.tingyanwu.ad.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by tingyanwu on 16/9/13.
 */
public class FileHelper {

    public static boolean mk(String name) {

        Log.e("mk", name);
        File f = new File(name);
        Log.e("mk", f.toString());

//        if (!f.isDirectory())
        f.mkdirs();
        return true;

    }


    public static boolean ifUpdate(String name, long size) {
        File f = new File(name);
        return f.length() != size;
    }


    public static boolean exist(String name) {
        File f = new File(name);
        return f.exists();
    }


    public static String getName(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }


    public static String getExt(String path) {
        return path.substring(path.lastIndexOf(".") + 1);
    }


    public static boolean isImage(String path) {
        String ext = getExt(path);


        if ("jpg_png_jpeg".contains(ext.toLowerCase()))
            return true;
        else
            return false;


    }


    public static void copyFileStream(File dest, Uri uri, Context context) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);

            }
        } catch (Exception e) {

            Log.e("exception", e.toString());
        } finally {

            try {
                is.close();
                os.close();
            } catch (IOException e) {
                Log.e("exception", e.toString());
            }

        }
    }



    //检查目录文件大小
    public long getVideoDirectorySize() {
        Log.e("filehelper",CrashApplication.videoHome);

        File f = new File(CrashApplication.videoHome);

        File[] files = f.listFiles();
        long total = 0;
        for (File file : files
                ) {
            if (file.isFile()) {

                Log.e("filehelper",file.getName());

                total += file.length();
            }
        }
        return total;
    }

    public void deleteLastFile()
    {

        File f = new File(CrashApplication.videoHome);
        File[] files = f.listFiles();
        long total = 0;

        for (File a:files
             ) {

            Log.e("filehelper",a.getName()+"_"+f.lastModified());
        }
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File file, File t1) {
                if (file.lastModified()>t1.lastModified())
                    return 1;
                    else
                return -1;
            }
        });

        for (File a:files
                ) {

            Log.e("filehelper sort ",a.getName()+"_"+f.lastModified());
        }


        for (File file : files
                ) {
            if (file.isFile()) {
            file.delete();
            }
        }
    }

public void checkVideoSpace()
{
    long space=getVideoDirectorySize();
    Log.e("space ",space+"");
    while (space>3*1024*1024*1024l)
    {
        deleteLastFile();
        space=getVideoDirectorySize();
    }
}







}
