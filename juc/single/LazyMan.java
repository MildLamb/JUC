package com.mildlamb.juc.single;

public class LazyMan {

    private LazyMan(){
        System.out.println(Thread.currentThread().getName() + "ok");
    }

    private volatile static LazyMan lazyMan;

    public synchronized static LazyMan getInstance(){
        if (lazyMan == null) {
            lazyMan = new LazyMan();
        }
        return lazyMan;
    }

    // 多线程并发
    public static void main(String[] args){
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                LazyMan.getInstance();
            }).start();
        }
    }
}
