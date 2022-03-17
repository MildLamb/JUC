package com.mildlamb.juc.pool;

import java.util.concurrent.*;

public class ThreadPoolExectorDemo {
    public static void main(String[] args) {
        // 阿里巴巴推荐使用底层 ThreadPoolExecutor 来创建线程池
        // 获取CPU核心数  Runtime.getRuntime().availableProcessors()

        ExecutorService executorService = new ThreadPoolExecutor(2, Runtime.getRuntime().availableProcessors(),
                3, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy());  // 单线程线程池
        try {
            for (int i = 0; i < 8; i++) {
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
