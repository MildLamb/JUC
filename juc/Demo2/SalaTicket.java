package com.mildlamb.juc.Demo2;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// 卖票例子
public class SalaTicket {
    public static void main(String[] args) {
        Ticket2 ticket = new Ticket2();
        //多线程操作
        new Thread(()->{ for (int i = 0; i < 40; i++) ticket.sale(); },"TaskA").start();
        new Thread(()->{ for (int i = 0; i < 40; i++) ticket.sale(); },"TaskB").start();
        new Thread(()->{ for (int i = 0; i < 40; i++) ticket.sale(); },"TaskC").start();
    }
}

// 使用Lock实现同步
class Ticket2{
    // 属性，方法
    private int ticket_num = 40;

    Lock lock = new ReentrantLock();

    // 卖票的方法
    public void sale(){
        // 加锁
        lock.lock();

        try {
            // 业务代码
            if (ticket_num > 0){
                System.out.println(Thread.currentThread().getName() + "卖出了第" + (ticket_num--) + "张票,还剩" + ticket_num + "张票");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 解锁
            lock.unlock();
        }
    }
}