package com.muyundefeng.spider.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by lisheng on 17-4-20.
 */
public class MyThreadPool {

    public int size;

    public ExecutorService executor;

    public AtomicInteger aliveSize;

    public final Lock lock = new ReentrantLock();

    public final Condition fullLoad = lock.newCondition();

    public Logger logger = LoggerFactory.getLogger(MyThreadPool.class);

    public MyThreadPool(int size) {
        this.size = size;
        executor = Executors.newFixedThreadPool(size);
        aliveSize = new AtomicInteger(0);
    }

    public int getAliveThread() {
        return aliveSize.get();
    }

    public void execute(final Woker worker) {
        if (aliveSize.get() >= size) {
            lock.lock();
            try {
                while (aliveSize.get() >= size)
                    fullLoad.await();
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            } finally {
                lock.unlock();
            }
        }
        aliveSize.incrementAndGet();
        executor.execute(new Runnable() {
            public void run() {
                try {
                    worker.execute();
                } finally {
                    try {
                        lock.lock();
                        aliveSize.decrementAndGet();
                        fullLoad.signal();
                    } finally {
                        lock.unlock();
                    }
                }
            }
        });
    }

    public void shudown() {
        if (executor != null) {
            logger.info("close thread pool!");
            executor.shutdown();
        }
    }
}
