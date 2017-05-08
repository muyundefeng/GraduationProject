package com.muyundefeng.process;

import java.io.*;
import java.net.URL;

/**
 * 处理url将url做一个大体的聚类分析
 * http://sports.sohu.com/20170505/n491847969.shtml
 * 将上面的url sohu.com移除掉，并根据类别存放到不同的文件中
 * Created by lisheng on 17-5-5.
 */
public class URLCluster {

    public static final String urlFile = "/home/lisheng/work/ExperData/preProcessData/output1/part-r-00000";//存放url的文件路径

    public static final String HOST_NAME = "qq.com";//主机名，将部分主机名剔除掉

    public static final String URL_SAVE_PATH = "/home/lisheng/work/ExperData/preProcessData/clusterUrl/";

    public static void readUrlFromFile() throws IOException {
        FileInputStream is = new FileInputStream(urlFile);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader in = new BufferedReader(isr);
        String line = null;
        while ((line = in.readLine()) != null) {
            URL url = new URL(line);
            String fileName = url.getHost().replace(HOST_NAME, "").replace(".", "");
            writeUrlToFile(fileName,line);
        }
    }

    public static void writeUrlToFile(String fileName, String line) {
        try {
            String path = URL_SAVE_PATH + fileName;
            System.out.println(path+"======");
            File file = new File(path);
            if (!file.exists())
                file.createNewFile();
            FileOutputStream out = new FileOutputStream(file, true); //如果追加方式用true
            StringBuffer sb = new StringBuffer();
            sb.append(line + "\n");
            out.write(sb.toString().getBytes("utf-8"));//注意需要转换对应的字符集
            out.close();
        } catch (IOException ex) {
            System.out.println(fileName+"+++++++++++++++++");
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            readUrlFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
