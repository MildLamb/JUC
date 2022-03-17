package com.mildlamb.juc.PC;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程之间的通信问题  等待，唤醒
 */

public class ProductJUC {
    public static void main(String[] args) {
        Data2 data = new Data2();
        new Thread(()->{ for (int i = 0; i < 10; i++) data.increment();},"Product").start();
        new Thread(()->{ for (int i = 0; i < 10; i++) data.decrement();},"Customer").start();
        new Thread(()->{ for (int i = 0; i < 10; i++) data.increment();},"Product2").start();
        new Thread(()->{ for (int i = 0; i < 10; i++) data.decrement();},"Customer2").start();
    }
}

class Data2{
    private int num = 0;

    Lock lock = new ReentrantLock();
    Condition condition = lock.newCondition();

    // +1
    public void increment(){
        lock.lock();
        try {
            while (num != 0){
                condition.await();
            }
            num++;
            System.out.println(Thread.currentThread().getName() + " => " + num);
            condition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void decrement(){
        lock.lock();
        try {
            while (num == 0){
                condition.await();
            }
            num--;
            System.out.println(Thread.currentThread().getName() + " => " + num);
            condition.signalAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
