package com.mildlamb.juc.forkjoin;

import java.util.concurrent.RecursiveTask;

/**
 * 求和计算的任务
 * 如何使用ForkJoin
 * 1. ForkJoinPool 通过它来执行
 * 2. 计算任务 ForkJoinPool.execute(ForkJoinTask task)
 * 3. 计算类要继承 ForkJoinTask
 */

public class ForkJoinDemo extends RecursiveTask<Long> {

    private Long start;
    private Long end;

    // 临界值
    private Long temp = 10000L;

    public ForkJoinDemo(Long start, Long end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        if ((end - start) < temp){
            // 走分支合并计算
            Long sum = 0L;
            for (Long i = start; i < end; i++) {
                sum += i;
            }
            return sum;
        } else {
            // forkjoin
            Long middle = (start + end) / 2;
            ForkJoinDemo task = new ForkJoinDemo(start,middle);
            task.fork();
            ForkJoinDemo task2 = new ForkJoinDemo(middle + 1,end);
            task2.fork();
            return task.join() + task2.join();
        }
    }
}
