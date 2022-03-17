package com.mildlamb.juc.lock8;

import java.util.concurrent.TimeUnit;

/**
 * 8锁问题
 *
 * 1. 标准情况下，两个线程哪个先打印? 先输出发短信，再是打电话；synchronized 锁的对象是方法的调用者
 * 2. 发短信的方法sleep 4s，谁先打印? 先输出发短信，再是打电话
 */
public class Test {
    public static void main(String[] args) {
        Phone phone = new Phone();
        new Thread(()->{phone.sendMsg();},"TaskA").start();

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(()->{phone.call();},"TaskB").start();
    }
}

class Phone{

    // synchronized 锁的对象是方法的调用者
    // 两个方法用的是同一个锁，谁先拿到谁就会先执行
    public synchronized void sendMsg(){
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("发短信");
    }
    public synchronized void call(){
        System.out.println("打电话");
    }
}
