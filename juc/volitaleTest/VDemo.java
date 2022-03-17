package com.mildlamb.juc.volitaleTest;

import sun.misc.Unsafe;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Volatile 不保证原子性
 * 如果不加 lock 和 synchronized ， 可以使用原子类来保证原子性
 */

public class VDemo {
    // Volatile 不保证原子性
    private volatile static AtomicInteger num = new AtomicInteger(0);

    public static void add(){
        // 1. 获得num的值
        // 2， +1
        // 3. 写回新的值
        num.getAndIncrement();  // AtomicInteger的加一操作
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int i1 = 0; i1 < 1000; i1++) {
                    add();
                }
            }).start();
        }


        while (Thread.activeCount() > 2){
            Thread.yield();
        }

        System.out.println(Thread.currentThread().getName() + " " + num);

    }
}
