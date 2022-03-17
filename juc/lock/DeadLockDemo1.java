package com.mildlamb.juc.lock;

import java.util.concurrent.TimeUnit;

public class DeadLockDemo1 {
    public static void main(String[] args) {

        String lock_A = "Alock";
        String lock_B = "Block";

        new Thread(new MyThread(lock_A,lock_B),"T1").start();
        new Thread(new MyThread(lock_B,lock_A),"T2").start();
    }
}

class MyThread implements Runnable {

    private String lockA;
    private String lockB;

    public MyThread(String lockA, String lockB) {
        this.lockA = lockA;
        this.lockB = lockB;
    }

    @Override
    public void run() {
        synchronized (lockA){
            System.out.println(Thread.currentThread().getName() + "lock:" + lockA + ",want:" + lockB);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (lockB){
                System.out.println(Thread.currentThread().getName() + "lock:" + lockB + ",want:" + lockA);
            }
        }
    }
}
