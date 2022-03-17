package com.mildlamb.juc.PC;


/**
 * 线程之间的通信问题  等待，唤醒
 */

public class Product {
    public static void main(String[] args) {
        Data data = new Data();
        new Thread(()->{ for (int i = 0; i < 10; i++) data.increment();},"Product").start();
        new Thread(()->{ for (int i = 0; i < 10; i++) data.decrement();},"Customer").start();
        new Thread(()->{ for (int i = 0; i < 10; i++) data.increment();},"Product2").start();
        new Thread(()->{ for (int i = 0; i < 10; i++) data.decrement();},"Customer2").start();
    }
}

class Data{
    private int num = 0;

    // +1
    public synchronized void increment(){
        while (num != 0){
            // 等待
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        num++;
        System.out.println(Thread.currentThread().getName() + " => " + num);
        // 通知消费者，我生产了
        this.notifyAll();
    }

    public synchronized void decrement(){
        while (num == 0){
            // 等待
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        num--;
        System.out.println(Thread.currentThread().getName() + " => " + num);
        // 通知生产者，我已经消费完了
        this.notifyAll();
    }
}
