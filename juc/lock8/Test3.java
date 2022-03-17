package com.mildlamb.juc.lock8;

import java.util.concurrent.TimeUnit;

/**
 * 8锁问题
 * 5. 将同步方法添加static关键字，谁先执行? 先输出发短信，再是打电话，锁的是Phone.class,全局唯一
 * 6. 用两个对象，将同步方法添加static关键字，谁先执行? 先输出发短信，再是打电话，锁的是Phone.class,全局唯一
 */
public class Test3 {
    public static void main(String[] args) {
        Phone3 phone = new Phone3();
        Phone3 phone2 = new Phone3();
        new Thread(()->{phone.sendMsg();},"TaskA").start();

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(()->{phone2.call();},"TaskB").start();
    }
}

class Phone3{

    // synchronized 锁的对象是方法的调用者
    // 两个方法用的是同一个锁，谁先拿到谁就会先执行
    public static synchronized void sendMsg(){
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("发短信");
    }
    public static synchronized void call(){
        System.out.println("打电话");
    }
}
