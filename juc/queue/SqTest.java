package com.mildlamb.juc.queue;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * 同步队列
 *      和其他BlockingQueue不一样，SynchronousQueue 不存储多余元素
 *      put了一个元素，就必须从里面先take拿出来再能存储
 */

public class SqTest {
    public static void main(String[] args) {
        SynchronousQueue<String> synchronousQueue = new SynchronousQueue<>();  //同步队列
        new Thread(()->{
            try {
                System.out.println(Thread.currentThread().getName() + " put 1");
                synchronousQueue.put("kindred");
                System.out.println(Thread.currentThread().getName() + " put 2");
                synchronousQueue.put("gnar");
                System.out.println(Thread.currentThread().getName() + " put 3");
                synchronousQueue.put("neeko");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"TaskA").start();

        new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(3);
                System.out.println(Thread.currentThread().getName() + " : " + synchronousQueue.take());
                TimeUnit.SECONDS.sleep(3);
                System.out.println(Thread.currentThread().getName() + " : " + synchronousQueue.take());
                TimeUnit.SECONDS.sleep(3);
                System.out.println(Thread.currentThread().getName() + " : " + synchronousQueue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
