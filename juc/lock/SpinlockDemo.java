package com.mildlamb.juc.lock;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 自旋锁
 */

public class SpinlockDemo {
    AtomicReference<Thread> atomicReference = new AtomicReference<>();
    // 加锁
    public void myLock(){
        Thread thread = Thread.currentThread();


        // 自旋锁
        while(!atomicReference.compareAndSet(null,thread)){
        }
        System.out.println(Thread.currentThread().getName() + "==> mylock");
    }

    // 解锁
    public void myUnLock(){
        Thread thread = Thread.currentThread();
        System.out.println(Thread.currentThread().getName() + "==> myUnlock");
        atomicReference.compareAndSet(thread,null);
    }
}
