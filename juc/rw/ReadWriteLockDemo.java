package com.mildlamb.juc.rw;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockDemo {
    public static void main(String[] args) {
        MyCacheLock myCache = new MyCacheLock();
        // 写入
        for (int i = 0; i < 5; i++) {
            final int temp = i;
            new Thread(()->{
                myCache.put(temp+"",temp);
            }).start();
        }

        // 读取
        for (int i = 0; i < 5; i++) {
            final int temp = i;
            new Thread(()->{
                myCache.get(temp+"");
            }).start();
        }
    }
}

class MyCache{
    private volatile Map<String,Object> map = new HashMap<>();

    // 存
    public void put(String key,Object value){
        System.out.println(Thread.currentThread().getName() + "-> 写入:" + key);
        map.put(key, value);
        System.out.println(Thread.currentThread().getName() + "写入完毕");
    }

    // 取
    public void get(String key){
        System.out.println(Thread.currentThread().getName() + "-> 读取:" + key);
        Object o = map.get(key);
        System.out.println(Thread.currentThread().getName() + "读取完毕");
    }
}

// 加锁处理
class MyCacheLock{
    private volatile Map<String,Object> map = new HashMap<>();
    // 读写锁
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    // 存，写入的时候，只希望只有一个线程写
    public void put(String key,Object value){
        readWriteLock.writeLock().lock();
        System.out.println(Thread.currentThread().getName() + "-> 写入:" + key);
        map.put(key, value);
        System.out.println(Thread.currentThread().getName() + "写入完毕");
        readWriteLock.writeLock().unlock();
    }

    // 取,读取的时候，读的顺序可以随意，但不允许有写
    public void get(String key){
        readWriteLock.readLock().lock();
        System.out.println(Thread.currentThread().getName() + "-> 读取:" + key);
        Object o = map.get(key);
        System.out.println(Thread.currentThread().getName() + "读取完毕");
        readWriteLock.readLock().unlock();
    }
}
