package com.muyundefeng.spider.downloader;

import com.muyundefeng.spider.constant.SPIDER_CONSTANT;
import com.muyundefeng.spider.entity.Page;
import com.muyundefeng.spider.entity.Request;
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
import java.io.InputStream;

/**
 * Created by lisheng on 17-4-20.
 */
public class MyHttpClinetDownloader implements Downloader {

    static HttpClient httpClient = MyHttpClientPool.getHttpClient();

    public Logger logger = LoggerFactory.getLogger(getClass());

    public Page download(Request request) {
        if(request == null)
            return null;
        logger.info("start downloading url:" + request.getUrl());
        Page page = null;
        if (request.getDefaultMethod().equals(SPIDER_CONSTANT.GET)) {
            HttpGet httpGet = null;
            try {
                httpGet = new HttpGet(request.getUrl());
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            if (httpGet != null) {
                HttpHost httpHost = new HttpHost(SPIDER_CONSTANT.PROXY_HOST, SPIDER_CONSTANT.PROXY_PORT);
                RequestConfig config = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                        .setProxy(httpHost)
                        .setConnectionRequestTimeout(SPIDER_CONSTANT.MAX_CONNECT_TIME_OUT)
                        .setSocketTimeout(SPIDER_CONSTANT.MAX_READ_TIME_OUT)
                        .build();
                httpGet.setConfig(config);
                HttpResponse response = null;
                try {
                    response = httpClient.execute(httpGet);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        logger.info("download page successfully");
                        InputStream inputStream = response.getEntity().getContent();
                        String content = IOUtils.toString(inputStream, "utf-8");
                        page = buildPage(content, request.getUrl());
                    }
                } catch (IOException e) {
                    logger.error("donwload error " + e.getMessage());
                } finally {
                    try {
                        if (response != null)
                            EntityUtils.consume(response.getEntity());
                    } catch (IOException e) {
                        logger.info(e.getMessage());
                    }
                }
            }
        }
        return page;
    }

    public Page buildPage(String content, String url) {
        Page page = new Page();
        page.setContent(content);
        Request request = new Request();
        request.setUrl(url);
        page.setRequest(request);
        return page;
    }

    public static void main(String[] args) {
        MyHttpClinetDownloader downloader = new MyHttpClinetDownloader();
        Request request = new Request();
        request.setUrl("http://shouji.baidu.com/");
        System.out.println(downloader.download(request));
    }
}
