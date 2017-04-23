package com.muyundefeng.spider.entity;

import com.muyundefeng.spider.constant.SPIDER_CONSTANT;

/**
 * Created by lisheng on 17-4-20.
 */
public class Request {
    public String url;
    public String defaultMethod = SPIDER_CONSTANT.DEFAULT_METHOD;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDefaultMethod() {
        return defaultMethod;
    }

    public void setDefaultMethod(String defaultMethod) {
        this.defaultMethod = defaultMethod;
    }
}
