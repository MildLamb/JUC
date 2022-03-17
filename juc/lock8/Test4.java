package com.mildlamb.juc.lock8;

import java.util.concurrent.TimeUnit;

/**
 * 8锁问题
 * 7. 一个静态同步方法，一个普通同步方法，一个对象，谁先执行?  先打电话，再发短信
 * 8. 一个静态同步方法，一个普通同步方法，两个对象，谁先执行?  先打电话，再发短信
 */
public class Test4 {
    public static void main(String[] args) {
        Phone4 phone = new Phone4();
        new Thread(()->{phone.sendMsg();},"TaskA").start();

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(()->{phone.call();},"TaskB").start();
    }
}

class Phone4{
    // 两个方法用的是同一个锁，谁先拿到谁就会先执行
    // static 锁的是类模板 class
    public static synchronized void sendMsg(){
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("发短信");
    }

    // synchronized 锁的对象是方法的调用者
    public synchronized void call(){
        System.out.println("打电话");
    }
}
