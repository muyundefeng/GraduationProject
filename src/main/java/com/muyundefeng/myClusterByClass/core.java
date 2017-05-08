package com.muyundefeng.myClusterByClass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 根据html网页的的div class属性进行聚类
 * Created by lisheng on 17-5-8.
 */
public class core {

    public Queue<String> queue = new LinkedBlockingQueue<String>();

    public String hostname = "qq.com";

    public double thresh_hold = 0.5;

    public String save_path = null;

    public FileNameGenerateUtils fileNameGenerateUtils;

    public Logger logger = LoggerFactory.getLogger(this.getClass());

    public Set<String> set = new HashSet<String>();

    public core(int bound, String hostName, double thresh_hold, String savePath) {
        fileNameGenerateUtils = new FileNameGenerateUtils(bound);
        this.hostname = hostName;
        this.thresh_hold = thresh_hold;
        this.save_path = savePath;
    }

    /**
     * @param path 存放初始的url聚类
     */
    public void startProcess(String path) throws IOException {
        logger.info("start to process path");
        File catalog = new File(path);
        File subFiles[] = catalog.listFiles();//得到目录中所有文件
        for (File file : subFiles) {
            String path1 = file.getPath();//得到文件的路径
            queue.add(path1);
        }
        while (!queue.isEmpty()) {
            //两个文件进行比较
            String sourceFile = queue.poll();//弹出第一个文件
            String fileName = fileNameGenerateUtils.getFileName();
            for (String path1 : queue) {
                logger.info("start processing file:" + path1);
                String srcUrl = readUrlFromFile(sourceFile, true);
                String targetUrl = readUrlFromFile(path1, true);
                String src = extraDivFromPage(srcUrl);
                String target = extraDivFromPage(targetUrl);
                if (src != null && target != null) {
                    String srcClassProperty = extraClass(src);
                    String targetClassProperty = extraClass(target);
                    //如果返回为true,需要将这个两类文件聚类为一起
                    if (calculateSimilar(srcClassProperty, targetClassProperty)) {
                        if (set.add(sourceFile)) {
                            System.out.println("sourcefile is "+sourceFile+"----------------");
                            String file1Content = readUrlFromFile(sourceFile, false);
                            writeUrlToFile(fileName, file1Content);
                        }
                        if (set.add(path1)) {
                            String file2Content = readUrlFromFile(path1, false);
                            writeUrlToFile(fileName, file2Content);
                            delFile(path1);
                        }
//                        set.add(path1);
//                        set.add(sourceFile);
//                        String file1Content = readUrlFromFile(sourceFile, false);
//                        String file2Content = readUrlFromFile(path1, false);
//                        String totalUrl = file1Content + "\n" + file2Content;
//                        writeUrlToFile(fileName, totalUrl);
                    }
                }
            }
            delFile(sourceFile);
            for (String url : set) {
                if (queue.contains(url)) {
                    queue.remove(url);
                }
            }
        }
    }

    public String extraDivFromPage(String url) {
        String content = DownloadUtils.download(url.trim());
        if (content == null)
            return null;
        String patternStr = "<div([^>]*)>";
        String resStr = "";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            resStr += matcher.group(1);
        }
        return resStr;
    }

    public String extraClass(String property) {
        String patternStr = "class=([^\\s]*)\\s";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(property);
        String resStr = "";
        while (matcher.find()) {
            resStr += matcher.group(1) + " ";
        }
        return resStr;
    }

    //判断两个类别的网页是否属于一个类别
    //计算两个url的html结构相似性
    public boolean calculateSimilar(String str1, String str) {
        logger.info("calculate similar between " + str1 + "and" + str);
        String arry[] = str.split("\\s");
        String array1[] = str1.split("\\s");
        List<String> list = Arrays.asList(arry);
        List<String> list1 = Arrays.asList(array1);
        int count = 0;
        for (String str2 : list) {
            if (list1.contains(str2)) {
                count++;
            }
        }
        int length = array1.length + arry.length;
        length = length / 2;
        double similar = (double) count / (double) length;
        logger.info("similar between two urls is " + similar);
        if (similar >= thresh_hold) {
            //两个类别的网页属于同一个聚类
            logger.info("these two cluster is similar!");
            return true;
        } else {
            logger.info("these two cluster is difference!");
            return false;
        }
    }

    /**
     * @param path 文件路径
     * @param flag 是否要返回一个url，还是所有的url
     * @return
     * @throws IOException
     */
    public String readUrlFromFile(String path, boolean flag) throws IOException {
        logger.info("read url from file=" + path);
        String content = "";
        FileInputStream is = new FileInputStream(path);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader in = new BufferedReader(isr);
        String line = null;
        while ((line = in.readLine()) != null) {
            if (isDetail(line) && flag) {
                content = line;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }
            if (flag == false) {
                content += line + "\n";
            }
        }
        isr.close();
        is.close();
        return content;
    }

    @SuppressWarnings("Duplicates")
    public void writeUrlToFile(String fileName, String line) {
        FileOutputStream out = null;
        StringBuffer sb = null;
        try {
            String path = save_path + fileName;
            logger.info("write to file=" + path + "=========");
            File file = new File(path);
            if (!file.exists())
                file.createNewFile();
            out = new FileOutputStream(file, true); //如果追加方式用true
            sb = new StringBuffer();
            sb.append(line + "\n");
            out.write(sb.toString().getBytes("utf-8"));//注意需要转换对应的字符集
            out.close();
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param url 判断该url否是详情页面
     * @return
     */
    public boolean isDetail(String url) {
        String patternStr = ".*" + hostname + "(/.*){2,}.+";
        if (url.matches(patternStr)) {
            return true;
        } else {
            return false;
        }
    }

    public void delFile(String fileName) {
        File file = new File(fileName);
        file.delete();
    }

    public static void main(String[] args) {
        core core = new core(1000, "qq.com", 0.5, "/home/lisheng/work/ExperData/preProcessData/output1/");
//        try {
//            String str = core.readUrlFromFile("/home/lisheng/work/ExperData/preProcessData/clusterUrl/astrolady",true);
//            System.out.println(str);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        try {
            core.startProcess("/home/lisheng/work/ExperData/preProcessData/clusterUrl");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
