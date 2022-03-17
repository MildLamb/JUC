package com.mildlamb.juc.supclass;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SemaphoreDemo {
    public static void main(String[] args) {
        // 用于限流
        Semaphore semaphore = new Semaphore(3);

        for (int i = 0; i < 6; i++) {
            new Thread(()->{
                // acquire()  获取
                try {
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() + "拿到了车位");
                    TimeUnit.SECONDS.sleep(2);
                    System.out.println(Thread.currentThread().getName() + "离开了车位");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // release()  释放
                    semaphore.release();
                }
            },"Car"+String.valueOf(i+1)).start();
        }
    }
}
