# JUC
## 准备环境  
- 确保项目的Project Structure中的Project，Model都是8版本
- Settting中找Java Compiler也改成8

## 进程和线程
- 进程：是程序在执行过程当中CPU资源分配的最小单位，并且进程都有自己的地址空间  
- 线程：线程是CPU调度的最小单位，它可以和属于同一个进程的其他线程共享这个进程的全部资源

Java默认有几个线程? 2个 mian、GC  

## 并发和并行
- 并行：指在同一时刻，有多条指令在多个处理器上同时执行。
- 并发：指在同一时刻只能有一条指令执行，但多个进程指令被快速的轮换执行，使得在宏观上具有多个进程同时执行的效果，但在微观上并不是同时执行的，只是把时间分成若干段，使多个进程快速交替的执行。

## 线程的状态
```java
    public enum State {
        // 新生
        NEW,
        
        // 可运行
        RUNNABLE,

        // 阻塞
        BLOCKED,
        
        // 等待
        WAITING,
        
        // 超时等待
        TIMED_WAITING,
        
        // 终止
        TERMINATED;
    }
```

## wait/sleep的区别
1. 来自不同的类: wait ==> Object,sleep ==> Thread
2. 关于锁的释放：wait会释放锁，sleep不会释放锁
3. 使用的范围不同：wait要在同步代码块中，sleep可以在任何地方使用
4. 使用场景不同：wait多用于线程间的通信
5. 是否需要被唤醒：wait需要被唤醒，sleep不需要
