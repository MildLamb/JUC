package com.mildlamb.juc.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 异步调用
 */

public class FutureDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 无返回值的 runAsync 异步回调
//        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(()->{
//            try {
//                TimeUnit.SECONDS.sleep(2);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println(Thread.currentThread().getName() + "runAsync => Void");
//        });
//
//        System.out.println("kindred");
//        completableFuture.get();


        // 有返回值的 runAsync 异步回调
        CompletableFuture<Integer> completableFuture2 = CompletableFuture.supplyAsync(()->{
//            try {
//                TimeUnit.SECONDS.sleep(2);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            System.out.println(Thread.currentThread().getName() + "runAsync => Integer");
            return 1024;
        });

        System.out.println("gnar");
        System.out.println(completableFuture2.whenComplete((t,u) -> {
            System.out.println("t=>" + t);  // 正常的返回结果
            System.out.println("u=>" + u);  // 错误信息
        }).exceptionally((e) -> {
            System.out.println(e.getMessage());
            return 233;  // 出现错误后执行的返回结果
        }));
    }
}
