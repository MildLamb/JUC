package com.mildlamb.juc.pool;

import java.util.concurrent.*;

// Executors 工具类，3大方法
// 使用了线程池后，使用线程池来创建线程
public class Demo1 {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();  // 单线程线程池
        // 阿里巴巴推荐使用底层 ThreadPoolExecutor 来创建线程池
        ExecutorService executorService2 = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());  // 单线程线程池

//        ExecutorService executorService = Executors.newFixedThreadPool(5);     // 固定线程池大小的线程池
//        ExecutorService executorService = Executors.newCachedThreadPool();   // 可伸缩的线程池
//        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);  // 可执行周期任务
        try {
            for (int i = 0; i < 10; i++) {
                executorService.execute(()->{
                    System.out.println(Thread.currentThread().getName() + "ok");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 线程池用完，程序结束，关闭线程池
            executorService.shutdown();
        }
    }
}
