package com.mildlamb.juc.lock;

// Lock
/**
 * 可重入就是说某个线程已经获得某个锁，可以再次获取锁而不会出现死锁
 */

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Demo2 {
    public static void main(String[] args) {
        Phone2 phone2 = new Phone2();
        Phone2 phone3 = new Phone2();
        new Thread(()->{
            phone2.sms();
        },"TaskA").start();

        new Thread(()->{
            phone3.tall();
        },"TaskB").start();
    }
}

class Phone2 {

    Lock lock = new ReentrantLock();

    public void sms(){

        lock.lock();

        try {
            System.out.println(Thread.currentThread().getName() + "sms --> " + lock.toString());
            call();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void call(){
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "call --> " + lock.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void tall(){
        lock.lock();
        try {
            call();
            System.out.println(Thread.currentThread().getName() + "tall --> " + lock.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
