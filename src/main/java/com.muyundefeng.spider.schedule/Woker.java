package com.muyundefeng.spider.schedule;

import com.muyundefeng.spider.downloader.Downloader;
import com.muyundefeng.spider.entity.Page;
import com.muyundefeng.spider.entity.Request;
import com.muyundefeng.spider.pageprocessor.PageProcessor;
import com.muyundefeng.spider.utils.RmDuplicateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by lisheng on 17-4-20.
 */
public class Woker {

    public Downloader downloader;

    public PageProcessor pageProcessor;

    public volatile MyRequestCollection requestsPool;

    public Logger logger = LoggerFactory.getLogger(Woker.class);

    public Woker(Downloader downloader, PageProcessor pageProcessor, MyRequestCollection collection) {
        this.downloader = downloader;
        this.pageProcessor = pageProcessor;
        requestsPool = collection;
    }

    public void execute() {
        Page page = downloader.download(requestsPool.popRequest());
        if (page == null) {
            logger.info("page is null");
            return;
        }
        pageProcessor.processor(page);
        List<Request> requests = page.getRequestList();
        if (requests != null)
            for (Request request : requests) {
                if (!RmDuplicateUtils.containElement(request.getUrl())) {
                    requestsPool.addRequest(request);
                    RmDuplicateUtils.add(request.getUrl());
                }
            }
    }
}
