package com.mildlamb.juc.lock8;

import java.util.concurrent.TimeUnit;

/**
 * 8锁问题
 * 3. 增加一个普通方法后，先执行哪个方法，注意各个方法的延迟时间
 * 4. 两个对象，调用方法，哪个先执行 (不同锁，两把锁)
 */
public class Test2 {
    public static void main(String[] args) {
        Phone2 phone = new Phone2();
        Phone2 phone2 = new Phone2();

        new Thread(()->{phone.sendMsg();},"TaskA").start();

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(()->{phone2.call();},"TaskB").start();
    }
}

class Phone2{

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

    public void hello(){
        System.out.println("hello");
    }
}
