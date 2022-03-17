package com.mildlamb.juc.lock;

// Synchronized

public class Demo1 {
    public static void main(String[] args) {
        Phone phone = new Phone();
        new Thread(()->{
            phone.sms();
        },"TaskA").start();

        new Thread(()->{
            phone.tall();
        },"TaskB").start();
    }
}

class Phone {
    public synchronized void sms(){
        System.out.println(Thread.currentThread().getName() + "sms");
        call();
    }

    public synchronized void call(){
        System.out.println(Thread.currentThread().getName() + "call");
    }

    public synchronized void tall(){
        call();
        System.out.println(Thread.currentThread().getName() + "tall");
    }
}
