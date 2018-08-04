package com.huaweisoft.ousy.helpers;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ousy on 2016-03-30.
 */
public class FileHelper
{

    /**
     * 分隔符.
     */
    public final static String FILE_EXTENSION_SEPARATOR = ".";

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return
     */
    public static Boolean isFileExist(String filePath)
    {
        File file = new File(filePath);
        if (file.exists() && file.isFile())
        {
            return true;
        }
        return false;
    }

    public static boolean createFolder(String path)
    {
        boolean ret = true;
        File file = new File(path);
        if (!file.exists())
        {
           ret = file.mkdirs();
        }

        return ret;
    }

    /**
     * 判断SD卡是否可用
     *
     * @return SD卡可用返回true
     */
    public static boolean hasSdcard()
    {
        String status = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(status);
    }

    /**
     * 判断文件夹是否存在
     *
     * @param dirPath 文件路径
     * @return
     */
    public static Boolean isDirExist(String dirPath)
    {
        File file = new File(dirPath);
        if (file.exists() && file.isDirectory())
        {
            return true;
        }
        return false;
    }

    /**
     * 创建一个文件
     *
     * @param fileName 文件名
     * @return
     * @throws IOException
     */
    public static Boolean createFile(String fileName) throws IOException
    {
        File file = new File(fileName);
        return file.createNewFile();
    }

    public static String setFileName()
    {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
        String name = "";
        // 设置图片的名字
        name = sDateFormat.format(new java.util.Date()) + ".jpg";

        return name;
    }

    /**
     * 设置文件的uri
     *
     * @return
     */
    public static Uri getUri(String mPhotoName)
    {
        Uri uri = null;
        File file = new File(Environment.getExternalStorageDirectory() + "/rdCamera", mPhotoName);
        try
        {
            file.createNewFile();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        uri = Uri.fromFile(file);

        return uri;
    }

    /**
     * 根据Uri获取图片文件的绝对路径
     */
    public static String getRealFilePath(Context context, final Uri uri)
    {
        if (null == uri)
        {
            return null;
        }

        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
        {
            data = uri.getPath();
        }
        else if (ContentResolver.SCHEME_FILE.equals(scheme))
        {
            data = uri.getPath();
        }
        else if (ContentResolver.SCHEME_CONTENT.equals(scheme))
        {
            Cursor cursor = context.getContentResolver().query(uri,
                    new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor)
            {
                if (cursor.moveToFirst())
                {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1)
                    {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 删除文件
     *
     * @param filePath
     */
    public static void deleteFile(String filePath)
    {
        File file = new File(filePath);
        file.delete();
    }

    /**
     * 删除文件
     *
     * @param filePath
     */
    public static void delete(String filePath)
    {
        File file = new File(filePath);
        file.delete();
    }

    /**
     * 删除指定目录中特定的文件
     *
     * @param dir
     * @param filter
     */
    public static void delete(String dir, FilenameFilter filter)
    {
        if (TextUtils.isEmpty(dir))
        {
            return;
        }
        File file = new File(dir);
        if (!file.exists())
        {
            return;
        }
        if (file.isFile())
        {
            file.delete();
        }
        if (!file.isDirectory())
        {
            return;
        }

        File[] lists = null;
        if (filter != null)
        {
            lists = file.listFiles(filter);
        }
        else
        {
            lists = file.listFiles();
        }

        if (lists == null)
        {
            return;
        }
        for (File f : lists)
        {
            if (f.isFile())
            {
                f.delete();
            }
        }
    }

    /**
     * 获得不带扩展名的文件名称
     *
     * @param filePath 文件路径
     * @return
     */
    public static String getFileNameWithoutExtension(String filePath)
    {
        if (TextUtils.isEmpty(filePath))
        {
            return filePath;
        }
        int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosi = filePath.lastIndexOf(File.separator);
        if (filePosi == -1)
        {
            return (extenPosi == -1 ? filePath : filePath.substring(0,
                    extenPosi));
        }
        if (extenPosi == -1)
        {
            return filePath.substring(filePosi + 1);
        }
        return (filePosi < extenPosi ? filePath.substring(filePosi + 1,
                extenPosi) : filePath.substring(filePosi + 1));
    }

    /**
     * 拷贝SdCard卡上的文件
     *
     * @param fromFile 源文件
     * @param toFile   目标文件
     * @param rewrite  是否可重写
     * @throws IOException 拷贝文件时的IO异常
     */
    public static void copyfile(File fromFile, File toFile, Boolean rewrite) throws IOException
    {
        if (!fromFile.exists())
        {
            return;
        }

        if (!fromFile.isFile())
        {
            return;
        }

        if (!fromFile.canRead())
        {
            return;
        }

        if (!toFile.getParentFile().exists())
        {
            toFile.getParentFile().mkdirs();
        }

        if (toFile.exists() && rewrite)
        {
            toFile.delete();
        }
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try
        {
            inputStream = new FileInputStream(fromFile);
            outputStream = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = inputStream.read(bt)) > 0)
            {
                outputStream.write(bt, 0, c); // 将内容写到新文件当中
            }
        } finally
        {
            if (inputStream != null)
            {
                inputStream.close();
            }
            if (outputStream != null)
            {
                outputStream.close();
            }
        }

    }

    /**
     * 将文件转化成字节数组
     *
     * @param file
     * @return
     */
    public static byte[] FileToBytes(File file)
    {
        int byte_size = 1024;
        byte[] b = new byte[byte_size];
        try
        {
            FileInputStream fileInputStream = new FileInputStream(file);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(byte_size);
            for (int length; (length = fileInputStream.read(b)) != -1; )
            {
                outputStream.write(b, 0, length);
            }
            fileInputStream.close();
            outputStream.close();
            return outputStream.toByteArray();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 拼装文件路径
     *
     * @param dir      文件所在的目录
     * @param fileName 文件名
     * @return 返回文件完成路径
     */
    public static String concatFilePath(String dir, String fileName)
    {
        String filePath = "";
        if (dir.endsWith("\\") || dir.endsWith("/"))
        {
            filePath = dir + fileName;
        }
        else
        {
            filePath = dir + "\\" + fileName;
        }
        return filePath;
    }

    /**
     * 获取文件大小 单位KB
     *
     * @param path 路径
     * @return
     */
    public static int getFileSize(String path)
    {
        int size = 0;
        File f = new File(path);
        if (f.exists() && f.isFile())
        {
            size = (int) (f.length() / 1024);
        }

        return size;
    }

    /**
     * 获取目录下所有文件(按时间排序)
     *
     * @param path
     * @return
     */
    public static List<File> getFileSort(String path)
    {
        File root = new File(path);
        List<File> list = new ArrayList<>();
        if (root.isDirectory())
        {
            File[] files = root.listFiles();
            if (null != files && files.length > 0)
            {
                Arrays.sort(files, new Comparator<File>()
                {
                    // 旧到新排
                    public int compare(File file, File newFile)
                    {
                        if (file.lastModified() < newFile.lastModified())
                        {
                            return -1;
                        }
                        else if (file.lastModified() == newFile.lastModified())
                        {
                            return 0;
                        }
                        else
                        {
                            return 1;
                        }

                    }
                });
                for (File i : files)
                {
                    list.add(i);
                }
            }

        }

        return list;
    }

    /**
     * 获取目录下所有文件
     *
     * @param realpath
     * @param files
     * @return
     */
    public static List<File> getFiles(String realpath, List<File> files)
    {

        File realFile = new File(realpath);
        if (realFile.isDirectory())
        {
            File[] subfiles = realFile.listFiles();
            for (File file : subfiles)
            {
                if (file.isDirectory())
                {
                    getFiles(file.getAbsolutePath(), files);
                }
                else
                {
                    files.add(file);
                }
            }
        }
        return files;
    }

    //根据文件修改时间进行比较的内部类
    static class CompratorByLastModified implements Comparator<File>
    {

        public int compare(File f1, File f2)
        {
            long diff = f1.lastModified() - f2.lastModified();
            if (diff > 0)
            {
                return 1;
            }
            else if (diff == 0)
            {
                return 0;
            }
            else
            {
                return -1;
            }
        }
    }
}
