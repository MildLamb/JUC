package com.mildlamb.juc.cas;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

public class CASDemo {
    //CAS
    public static void main(String[] args) {
        // CAS：Compare and Swap，即比较再交换。

        AtomicInteger atomicInteger = new AtomicInteger(2022);

        // 期望，更新
        // public final boolean compareAndSet(int expect, int update)
        // 如果我期望的值达到了，就更新；否则，不更新
        atomicInteger.compareAndSet(2022,114514);
        System.out.println(atomicInteger.get());
        atomicInteger.compareAndSet(114514,2022);
        System.out.println(atomicInteger.get());
        atomicInteger.compareAndSet(2022,6666);
        System.out.println(atomicInteger.get());
    }
}
