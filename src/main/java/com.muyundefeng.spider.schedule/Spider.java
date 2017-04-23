package com.muyundefeng.spider.schedule;


import com.muyundefeng.spider.constant.SPIDER_CONSTANT;
import com.muyundefeng.spider.downloader.Downloader;
import com.muyundefeng.spider.downloader.MyHttpClinetDownloader;
import com.muyundefeng.spider.entity.Request;
import com.muyundefeng.spider.pageprocessor.PageProcessor;
import com.muyundefeng.spider.utils.ChannelsDom;
import com.muyundefeng.spider.utils.RmDuplicateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by lisheng on 17-4-20.
 */
public class Spider {

    public Downloader downloader;

    public MyThreadPool pool;

    public PageProcessor pageProcessor;

    public  MyRequestCollection seedRequest;

    public Woker woker;

    public ChannelsDom channelsDom;

    public Logger logger = LoggerFactory.getLogger(getClass());

    public final Lock lock = new ReentrantLock();

    public final Condition waitFoRequest = lock.newCondition();

    public final Condition waitForComplete = lock.newCondition();


    public Spider() {
        downloader = new MyHttpClinetDownloader();
        pool = new MyThreadPool(SPIDER_CONSTANT.THREAD_POOL_SIZE);
        seedRequest = new MyRequestCollection();
    }

    public Spider initWoker() {
        this.woker = buildWorker(downloader, pageProcessor, seedRequest);
        return this;
    }

    public Spider initChannelProcessor(String channelId) {
        channelsDom = new ChannelsDom(channelId);
        ChannelsDom.getChannelsDomById(channelId);
        this.pageProcessor = ChannelsDom.getChannelsDomById(channelId).getPagePro();
        String seed = ChannelsDom.getChannelsDomById(channelId).getHomeUrl();
        addSeedUrl(seed);
        return this;
    }

    public Spider addSeedUrl(String... urls) {
        for (String url : urls) {
            Request request = new Request();
            request.setUrl(url);
            RmDuplicateUtils.add(url);
            logger.info("add seed url:" + url);
            seedRequest.addRequest(request);
        }
        return this;
    }

    public void start() {
        logger.info("start crawling");
        while (true) {
            if (seedRequest.isEmpty()&&pool.getAliveThread() == 0) {
                if(waitForNewUrl())
                    break;
            } else {
                if(!seedRequest.isEmpty()) {
                    pool.execute(this.woker);
                }
            }
        }
        logger.info("end crawling");
        pool.shudown();
    }

    public boolean waitForNewUrl() {
        boolean isOver = false;
        lock.lock();
        try {
            waitFoRequest.await(5, TimeUnit.SECONDS);
            if (seedRequest.getSize() == 0)
                isOver = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return isOver;
    }

    public void waitForComplete(){
        lock.lock();
        try {
            waitForComplete.await(300,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }
    }

    public Woker buildWorker(Downloader downloader, PageProcessor pageProcessor, MyRequestCollection collection) {
        Woker woker = new Woker(downloader, pageProcessor, collection);
        return woker;
    }

    public static void main(String[] args) {
        String channelId = "1";
        Spider spider = new Spider();
        spider.initChannelProcessor(channelId).initWoker().start();
    }
}
