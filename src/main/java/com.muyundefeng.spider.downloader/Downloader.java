package com.muyundefeng.spider.downloader;

import com.muyundefeng.spider.entity.Page;
import com.muyundefeng.spider.entity.Request;

/**
 * Created by lisheng on 17-4-20.
 */
public interface Downloader {
    public Page download(Request request);
}
