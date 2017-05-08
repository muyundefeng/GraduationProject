package com.muyundefeng.myClusterByClass;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by lisheng on 17-5-7.
 */
public class DownloadUtils {

    public static HttpClient httpClient = HttpClientManager.getHttpClient();

    public static Logger logger = LoggerFactory.getLogger(DownloadUtils.class);

    public static String download(String str) {
        logger.info("download url:" + str + "======");
        if (str == null || str.length() == 0)
            return null;
        HttpGet httpGet = new HttpGet(str);

        //        HttpHost host = new HttpHost("proxy1.asec.buptnsrc.com", 8001);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(10000)
                .setConnectionRequestTimeout(3000)
                .setSocketTimeout(10000)
//                .setProxy(host)
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .build();
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.79 Safari/537.1");
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        httpGet.setConfig(requestConfig);
        HttpResponse response = null;
        String content = null;
        try {
            response = httpClient.execute(httpGet);
            content = IOUtils.toString(response.getEntity().getContent(), "utf-8");
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            try {
                EntityUtils.consume(response.getEntity());
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return content;
    }

    public static void main(String[] args) {
        download("http://fj.qq.com/");
    }
}
