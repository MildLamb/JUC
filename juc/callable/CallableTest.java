package com.mildlamb.juc.callable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class CallableTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //new Thread().start();  //如何启动Callable
        // new Thread() 中的参数只能传递Runnable，想要传递Callable就要使用Runnable的适配类FutureTask
        FutureTask<String> stringFutureTask = new FutureTask<>(new MyThread());
        FutureTask<String> stringFutureTask2 = new FutureTask<>(new MyThread());

        new Thread(stringFutureTask,"TaskA").start();
        new Thread(stringFutureTask2,"TaskB").start();


        // 获取Callable的返回结果
        String s = stringFutureTask.get();
        System.out.println(s);
    }
}

// Callable的泛型为call方法的返回值类型
class MyThread implements Callable<String> {
    @Override
    public String call() {
        System.out.println(Thread.currentThread().getName() + ":进入call方法");
        return "kindred";
    }
}