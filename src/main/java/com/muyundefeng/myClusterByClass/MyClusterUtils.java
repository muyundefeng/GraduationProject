//package com.muyundefeng.myClusterByClass;
//
//import org.apache.commons.io.IOUtils;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.config.CookieSpecs;
//import org.apache.http.client.config.RequestConfig;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.util.EntityUtils;
//
//import java.io.BufferedReader;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.URL;
//import java.util.Arrays;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///**
// * 根据从现有的聚类器进行相关的聚类，根据html标签以及属性定义聚类规则
// * 阈值定义为0.5
// * Created by lisheng on 17-5-8.
// */
//public class MyClusterUtils {
//
//    public static final double THRESH_HOLD = 0.5;
//
//    public static String download(String str) {
//        HttpClient httpClient = HttpClients.createDefault();
//        HttpGet httpGet = new HttpGet(str);
////        HttpHost host = new HttpHost("proxy2.asec.buptnsrc.com", 8001);
//        RequestConfig requestConfig = RequestConfig.custom()
//                .setConnectTimeout(10000)
//                .setConnectionRequestTimeout(3000)
//                .setSocketTimeout(10000)
////                .setProxy(host)
//                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
//                .build();
//        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.79 Safari/537.1");
//        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
//        httpGet.setConfig(requestConfig);
//        HttpResponse response = null;
//        String content = null;
//        try {
//            response = httpClient.execute(httpGet);
//            content = IOUtils.toString(response.getEntity().getContent(), "utf-8");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                EntityUtils.consume(response.getEntity());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return content;
//    }
//
//    public static void readUrlFromFile(String path) throws IOException {
//        FileInputStream is = new FileInputStream(path);
//        InputStreamReader isr = new InputStreamReader(is);
//        BufferedReader in = new BufferedReader(isr);
//        String line = null;
//        while ((line = in.readLine()) != null) {
//            URL url = new URL(line);
//            String content = download(line);
//            extraDivFromPage(content);
//            break;
//        }
//    }
//
//
//    public static String extraDivFromPage(String url) {
//        String content = download(url);
//        String patternStr = "<div([^>]*)>";
//        String resStr = "";
//        Pattern pattern = Pattern.compile(patternStr);
//        Matcher matcher = pattern.matcher(content);
//        while (matcher.find()) {
//            resStr += matcher.group(1);
//        }
//        return resStr;
//    }
//
//    public static String extraClass(String property) {
//        String patternStr = "class=([^\\s]*)\\s";
//        Pattern pattern = Pattern.compile(patternStr);
//        Matcher matcher = pattern.matcher(property);
//        String resStr = "";
//        while (matcher.find()) {
//            resStr += matcher.group(1) + " ";
//        }
//        return resStr;
//    }
//    //判断两个类别的网页是否属于一个类别
//    //计算两个url的html结构相似性
//    public static boolean calculateSimilar(String str1, String str) {
//        String arry[] = str.split("\\s");
//        String array1[] = str1.split("\\s");
//        List<String> list = Arrays.asList(arry);
//        List<String> list1 = Arrays.asList(array1);
//        int count = 0;
//        for (String str2 : list) {
//            if (list1.contains(str2)) {
//                count++;
//            }
//        }
//        int length = array1.length + arry.length;
//        length = length / 2;
//        double similar = (double) count / (double) length;
//        if (similar >= THRESH_HOLD) {
//            //两个类别的网页属于同一个聚类
//            return true;
//        }else{
//            return false;
//        }
//    }
//
//
//    public static void main(String[] args) {
////        try {
////            readUrlFromFile("/home/lisheng/work/ExperData/preProcessData/clusterUrl/digitech");
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//        //String url = "http://finance.qq.com/a/20170508/004802.htm";
//        String url = "http://health.qq.com/a/20170508/012149.htm";
//        String url1 = "http://digi.tech.qq.com/a/20170508/000951.htm";
//        String urlStr = extraDivFromPage(url);
//        String urlStr1 = extraDivFromPage(url1);
//        String classes = extraClass(urlStr);
//        String classes1 = extraClass(urlStr1);
//        calculateSimilar(classes, classes1);
////        System.out.println(urlStr);
////        System.out.println("=============================");
////        System.out.println(urlStr1);
////        System.out.println(urlStr.equals(urlStr1));
//
//    }
//}
