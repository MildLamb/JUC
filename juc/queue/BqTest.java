package com.mildlamb.juc.queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class BqTest {
    public static void main(String[] args) throws InterruptedException {
        BqTest.test4();
    }

    /**
     * 抛出异常
     */
    public static void test1(){
        ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue(3);
        System.out.println(blockingQueue.add("kindred"));
        System.out.println(blockingQueue.add("gnar"));
        System.out.println(blockingQueue.add("neeko"));

        System.out.println("=========================");

        System.out.println(blockingQueue.remove());
        System.out.println(blockingQueue.remove());
        System.out.println(blockingQueue.remove());
    }

    /**
     * 不抛出异常，有返回值
     */
    public static void test2(){
        ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue(3);
        System.out.println(blockingQueue.offer("kindred"));
        System.out.println(blockingQueue.offer("gnar"));
        System.out.println(blockingQueue.offer("neeko"));
//        System.out.println(blockingQueue.offer("qsj"));   // 返回false
        System.out.println(blockingQueue.element());   //查看队列的队首元素是谁

        System.out.println("=========================");

        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());     // 返回null
    }

    /**
     * 等待，阻塞(一直阻塞)
     */
    public static void test3() throws InterruptedException {
        ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue(3);
        blockingQueue.put("kindred");
        blockingQueue.put("gnar");
        blockingQueue.put("neeko");
        // 不够加入到队列中，就会一直等待
//        blockingQueue.put("qsj");

        System.out.println(blockingQueue.take());
        System.out.println(blockingQueue.take());
        System.out.println(blockingQueue.take());
        // 取不到就一直等着
        System.out.println(blockingQueue.take());
    }

    /**
     * 超时等待
     */
    public static void test4() throws InterruptedException {
        ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue(3);
        System.out.println(blockingQueue.offer("kindred",2, TimeUnit.SECONDS));
        System.out.println(blockingQueue.offer("gnar",2, TimeUnit.SECONDS));
        System.out.println(blockingQueue.offer("neeko",2, TimeUnit.SECONDS));
        System.out.println(blockingQueue.offer("qsj",2, TimeUnit.SECONDS));   // 返回false

        System.out.println("=========================");

        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll(2,TimeUnit.SECONDS));     // 返回null
    }
}
