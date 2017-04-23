package com.muyundefeng.spider.schedule;

import com.muyundefeng.spider.entity.Request;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**定义request的一个共享池
 * Created by lisheng on 17-4-20.
 */
public class MyRequestCollection {

    public BlockingQueue<Request> blockingQueue;

    public MyRequestCollection(){
        this.blockingQueue = new LinkedBlockingQueue<Request>();
    }

    public void addRequest(Request request){
        blockingQueue.add(request);
    }

    public  Request popRequest(){
        Request request = null;
        try {
            request = blockingQueue.poll();//take取元素并不是并发的
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  request;
    }
    public boolean isEmpty(){
        return blockingQueue.isEmpty();
    }

    public int getSize(){return blockingQueue.size();}
}
