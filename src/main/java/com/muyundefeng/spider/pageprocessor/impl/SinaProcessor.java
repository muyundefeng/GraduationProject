package com.muyundefeng.spider.pageprocessor.impl;

import com.muyundefeng.spider.entity.Page;
import com.muyundefeng.spider.pageprocessor.PageProcessor;
import com.muyundefeng.spider.selector.Html;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;

/**
 * Created by lisheng on 17-4-20.
 */
public class SinaProcessor implements PageProcessor {

    public Logger logger = LoggerFactory.getLogger(getClass());

    public static final String HOST_NAME = "http://.*qq.com[^#]*";

    public void processor(Page page) {
        String content = page.getContent();
        Html html = Html.create(content);
        List<String> urls = html.links().all();
        for(String url:urls){
            if(url.matches(HOST_NAME)){
                page.addRequest(url);
            }
        }
    }
}
