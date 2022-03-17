package com.mildlamb.juc.cas;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicStampedReference;

public class ABADemo {
    public static void main(String[] args) {
        AtomicStampedReference<Integer> stampedReference = new AtomicStampedReference(1,1);

        new Thread(()->{
            // stampedReference.getStamp(); //获取版本号
            System.out.println("a1=>" + stampedReference.getStamp());
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            System.out.println("TaskA1 ==>" +stampedReference.compareAndSet(1, 2, stampedReference.getStamp(), stampedReference.getStamp() + 1));
            System.out.println("a2=>" + stampedReference.getStamp());

            System.out.println("TaskA2 ==>" +stampedReference.compareAndSet(2, 1, stampedReference.getStamp(), stampedReference.getStamp() + 1));
            System.out.println("a3=>" + stampedReference.getStamp());


        },"TaskA").start();


        new Thread(()->{
            System.out.println("b1=>" + stampedReference.getStamp());
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            System.out.println("TaskB ==>" + stampedReference.compareAndSet(1, 6, stampedReference.getStamp(), stampedReference.getStamp() + 1));
            System.out.println("b2=>" + stampedReference.getStamp());


        },"TaskB").start();
    }
}
