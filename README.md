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

## Lock锁(重点)
![image](https://user-images.githubusercontent.com/92672384/157355271-12f06cdb-2153-418d-b84b-e8919a30c4c5.png)

![image](https://user-images.githubusercontent.com/92672384/157355553-6b8cde30-b1d1-4452-b6c5-06db670ef98a.png)

- 公平锁：十分公平，先来后到
- 非公平锁：可以插队

### Lock和Synchronized的区别
1. Synchronized 内置的关键字，Lock是一个Java类
2. Synchronized 法判断获取锁的状态，Lock可以判断是否获取到了锁
3. Synchronized 会自动释放锁，Lock必须要手动释放锁，不然会死锁
4. Synchronized 可重入锁，不可以中断的，非公平；Lock，可重入锁，可以判断锁，可以设置是否为公平锁
5. Synchronized 适合锁少量的代码同步问题，Lock适合锁大量的同步代码

## 生产者消费者问题
### 传统生产者消费者问题，while防止虚假唤醒
```java
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
```

### JUC实现生产者消费者问题
```java
public class ProductJUC {
    public static void main(String[] args) {
        Data2 data = new Data2();
        new Thread(()->{ for (int i = 0; i < 10; i++) data.increment();},"Product").start();
        new Thread(()->{ for (int i = 0; i < 10; i++) data.decrement();},"Customer").start();
        new Thread(()->{ for (int i = 0; i < 10; i++) data.increment();},"Product2").start();
        new Thread(()->{ for (int i = 0; i < 10; i++) data.decrement();},"Customer2").start();
    }
}

class Data2{
    private int num = 0;

    Lock lock = new ReentrantLock();
    Condition condition = lock.newCondition();

    // +1
    public void increment(){
        lock.lock();
        try {
            while (num != 0){
                condition.await();
            }
            num++;
            System.out.println(Thread.currentThread().getName() + " => " + num);
            condition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void decrement(){
        lock.lock();
        try {
            while (num == 0){
                condition.await();
            }
            num--;
            System.out.println(Thread.currentThread().getName() + " => " + num);
            condition.signalAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
```
