package com.mildlamb.juc.supclass;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        // 总数是5
        CountDownLatch countDownLatch = new CountDownLatch(5);

        for (int i = 0; i < 5; i++) {
            new Thread(()->{
                System.out.println(Thread.currentThread().getName() + "Go out");
                countDownLatch.countDown();  //数量-1
            },"Task" + String.valueOf(i)).start();
        }

        countDownLatch.await();  //等待计数器归零，然后向下执行

        System.out.println("计数器已经归零");
    }
}
