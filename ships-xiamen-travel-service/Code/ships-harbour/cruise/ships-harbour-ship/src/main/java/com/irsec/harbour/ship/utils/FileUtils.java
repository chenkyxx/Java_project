package com.irsec.harbour.ship.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: Jethro
 * @Date: 2019/9/30 16:40
 * @Description:
 */
public class FileUtils {

    public static byte[] readFileToByteArray(File file){
        byte[] buffer = null;
        try
        {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1)
            {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return buffer;
    }


    public static void writeTXT(String path,String title,String content){
        try {
            // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw
            /* 写入Txt文件 */

            File writename = new File(path);// 相对路径，如果没有则要建立一个新的output。txt文件
            if(!writename.exists()){
                writename.mkdirs();
            }

            writename = new File(path+File.separator+title+".txt");// 相对路径，如果没有则要建立一个新的output。txt文件
            writename.createNewFile(); // 创建新文件
            FileOutputStream fos = new FileOutputStream(path+File.separator+title+".txt");
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            osw.write(content);
            osw.flush();
            osw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static List<File> loopFile(String path){
        List<File> fileList = new ArrayList<>();
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null == files || files.length == 0) {
                return null;
            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        List<File> tempFileList = loopFile(file2.getAbsolutePath());
                        if(tempFileList != null && tempFileList.size() != 0){
                            fileList.addAll(tempFileList);
                        }
                    } else {
                        fileList.add(file2);
                    }
                }
            }
        } else {
            return null;
        }
        return fileList;
    }

    public static String readFileContent(File file) {
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr);
            }
            reader.close();
            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sbf.toString();
    }

}
