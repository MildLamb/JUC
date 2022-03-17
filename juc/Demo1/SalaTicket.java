package com.mildlamb.juc.Demo1;

// 卖票例子
public class SalaTicket {
    public static void main(String[] args) {
        Ticket ticket = new Ticket();
        //多线程操作
        new Thread(()->{
                for (int i = 0; i < 40; i++) {
                    ticket.sale();
                }
            },"TaskA").start();
        new Thread(()->{
                for (int i = 0; i < 40; i++) {
                    ticket.sale();
                }
            },"TaskB").start();
        new Thread(()->{
                for (int i = 0; i < 40; i++) {
                    ticket.sale();
                }
            },"TaskC").start();
    }
}


class Ticket{
    // 属性，方法
    private int ticket_num = 30;

    // 卖票的方法
    public synchronized void sale(){
        if (ticket_num > 0){
            System.out.println(Thread.currentThread().getName() + "卖出了第" + (ticket_num--) + "张票,还剩" + ticket_num + "张票");
        }
    }
}