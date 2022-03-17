package com.mildlamb.juc.PC;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OrderSign {
    public static void main(String[] args) {

        Data3 data = new Data3();

        new Thread(()->{for(int i = 1;i <= 10;i++) data.printA();},"TaskA").start();
        new Thread(()->{for(int i = 1;i <= 10;i++) data.printB();},"TaskB").start();
        new Thread(()->{for(int i = 1;i <= 10;i++) data.printC();},"TaskC").start();
    }
}


class Data3{
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private Condition condition2 = lock.newCondition();
    private Condition condition3 = lock.newCondition();
    private int num = 1;

    public void printA(){
        lock.lock();
        try {
            while (num != 1){
                // 等待
                condition.await();
            }
            // 业务代码
            System.out.println(Thread.currentThread().getName() + "=> Kindred是第一位");
            num = 2;
            condition2.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    public void printB(){
        lock.lock();
        try {
            while (num != 2){
                condition2.await();
            }
            // 业务代码
            System.out.println(Thread.currentThread().getName() + "=> Gnar是第二位");
            num = 3;
            condition3.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void printC(){
        lock.lock();
        try {
            while (num != 3){
                condition3.await();
            }
            // 业务代码
            System.out.println(Thread.currentThread().getName() + "=> Neeko是第三位");
            num = 1;
            condition.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}