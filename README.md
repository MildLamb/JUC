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

## Callable
1. 可以有返回值
2. 可以抛出异常
3. 方法不同，call()

```java
public class CallableTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //new Thread().start();  //如何启动Callable
        // new Thread() 中的参数只能传递Runnable，想要传递Callable就要使用Runnable的适配类FutureTask
        FutureTask<String> stringFutureTask = new FutureTask<>(new MyThread());
        new Thread(stringFutureTask,"TaskA").start();
        // 获取Callable的返回结果
        String s = stringFutureTask.get();
        System.out.println(s);
    }
}

// Callable的泛型为call方法的返回值类型
class MyThread implements Callable<String> {
    @Override
    public String call() {
        System.out.println("进入call方法");
        return "kindred";
    }
}
```

## 辅助工具类
### CountDownLatch（闭锁，用于等待事件）
原理：  
- countDownLatch.countDown(); //数量-1
- countDownLatch.await();  //等待计数器归零，然后再向下执行

```java
public class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        // 总数是5
        CountDownLatch countDownLatch = new CountDownLatch(5);

        for (int i = 0; i < 5; i++) {
            new Thread(()->{
                System.out.println(Thread.currentThread().getName() + "Go out");
                countDownLatch.countDown();  //数量-1
            },"Task" + String.valueOf(i)).start();
        }

        countDownLatch.await();  //等待计数器归零，然后向下执行

        System.out.println("计数器已经归零");
    }
}
```

### CyclicBarrier
```java
public class CyclicBarrierDemo {
    public static void main(String[] args) {
        /**
         * 集齐7龙珠，召唤神龙
         */
        CyclicBarrier cyclicBarrier = new CyclicBarrier(7,()->{
            System.out.println("你已经召唤出了神龙");
        });

        for (int i = 0; i < 7; i++) {
            final int temp = i;
            new Thread(()->{
                System.out.println(Thread.currentThread().getName() + "收集" + (temp+1) + "个龙珠");
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
```
### Semaphore 信号量
- semaphore.acquire();  //获取，假设信号量满了，等待其他线程释放
- semaphore.release();  //释放当前信号量
作用：多个共享资源的互斥使用！并发限流，控制最大线程数

```java
public class SemaphoreDemo {
    public static void main(String[] args) {
        // 用于限流
        Semaphore semaphore = new Semaphore(3);

        for (int i = 0; i < 6; i++) {
            new Thread(()->{
                // acquire()  获取
                try {
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() + "拿到了车位");
                    TimeUnit.SECONDS.sleep(2);
                    System.out.println(Thread.currentThread().getName() + "离开了车位");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // release()  释放
                    semaphore.release();
                }
            },"Car"+String.valueOf(i+1)).start();
        }
    }
}
```

## ReadWriteLock
```java
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
```

## 阻塞队列

| 操作      | 抛出异常 | 不抛出异常，但有返回值 | 阻塞等待(一直等) | 超时等待(等一段时间) | 
| ----------- | ----------- | ----------- | ----------- | ----------- |
| 添加     |   add()     |  offer()  |  put()  |  offer(E e, long timeout, TimeUnit unit) |
| 移除     |    remove()    |  poll()  |  take() | poll(long timeout, TimeUnit unit) |
| 返回队首元素 |    element()    | peek()  |        |     |

## 线程池(重点)
### 池化技术
提前准备好资源，当程序需要使用资源时，从池中获取，使用完毕后再归还池中，避免重复创建带来的资源消耗，和性能减少。  

### 线程池的好处
1. 避免反复创建/销毁带来的降低资源的消耗
2. 当需要使用线程时，直接从池中获取，提高响应的速度
3. 方便资源的统一管理

### 三大方法，7大参数，4种拒绝策列
- 三大方法
```java
// Executors 工具类，3大方法
// 使用了线程池后，使用线程池来创建线程
public class Demo1 {
    public static void main(String[] args) {
//        ExecutorService executorService = Executors.newSingleThreadExecutor();  // 单线程线程池
        ExecutorService executorService = Executors.newFixedThreadPool(5);     // 固定线程池大小的线程池
//        ExecutorService executorService = Executors.newCachedThreadPool();   // 可伸缩的线程池
//        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);  // 可执行周期任务
        try {
            for (int i = 0; i < 10; i++) {
                executorService.execute(()->{
                    System.out.println(Thread.currentThread().getName() + "ok");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 线程池用完，程序结束，关闭线程池
            executorService.shutdown();
        }
    }
}
```

- 7大参数
```java
    public ThreadPoolExecutor(int corePoolSize,   // 核心线程数大小
                              int maximumPoolSize,  // 最大线程池大小
                              long keepAliveTime,   // 存活时间
                              TimeUnit unit,     //  超时单位
                              BlockingQueue<Runnable> workQueue,   // 任务队列(阻塞队列)
                              ThreadFactory threadFactory,   //  线程工厂，创建线程的
                              RejectedExecutionHandler handler) {  // 拒绝策略
        if (corePoolSize < 0 ||
            maximumPoolSize <= 0 ||
            maximumPoolSize < corePoolSize ||
            keepAliveTime < 0)
            throw new IllegalArgumentException();
        if (workQueue == null || threadFactory == null || handler == null)
            throw new NullPointerException();
        this.acc = System.getSecurityManager() == null ?
                null :
                AccessController.getContext();
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.workQueue = workQueue;
        this.keepAliveTime = unit.toNanos(keepAliveTime);
        this.threadFactory = threadFactory;
        this.handler = handler;
    }
```

- 4种拒绝策略
```java
ThreadPoolExecutor.AbortPolicy()   // 拒绝任务，抛出异常
ThreadPoolExecutor.DiscardPolicy()  // 拒绝任务，不抛出异常
ThreadPoolExecutor.DiscardOldestPolicy()  // 将任务队列最老的任务丢弃，并尝试再次提交新的任务,不抛出异常
ThreadPoolExecutor.CallerRunsPolicy()  // 谁调用的，谁来执行
```

- 自定义线程池
```java
public class ThreadPoolExectorDemo {
    public static void main(String[] args) {
        // 阿里巴巴推荐使用底层 ThreadPoolExecutor 来创建线程池
        
        // 自定义线程池
        ExecutorService executorService = new ThreadPoolExecutor(2, 5,
                3, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());  // 单线程线程池
                
        try {
            for (int i = 0; i < 8; i++) {
                executorService.execute(()->{
                    System.out.println(Thread.currentThread().getName() + "ok");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 线程池用完，程序结束，关闭线程池
            executorService.shutdown();
        }
    }
}
```

### 最大线程数应该如何定义
- CPU 密集型,几核，最大就设置几
- I/O 密集型，判断程序中十分消耗I/O的线程

## 四大函数式接口
- 函数式接口：有且只有一个方法的接口

### Function 函数型接口
```java
public class FunctionDemo {
    public static void main(String[] args) {

        /**
         * Function<T, R>  传入参数类型 T ， 返回值类型 R
         * R apply(T t);
         */

        Function<String,Integer> function = (String str) -> {
            return 10;
        };

        System.out.println(function.apply("kindred"));
    }
}
```
### Predicate 断定型接口
```java
public class PredicateDemo {
    public static void main(String[] args) {
    
        /**
         * public interface Predicate<T>
         * boolean test(T t);
         */
    
        Predicate<String> predicate = (String str) -> {
            return str.equals("kindred");
        };

        System.out.println(predicate.test("kindred"));
    }
}
```
### Supplier 供给型接口
```java
/**
 * 供给型接口 Supplier
 */

public class SupplierDemo {
    public static void main(String[] args) {
        /**
         * public interface Supplier<T>
         * T get();
         */
        Supplier<String> supplier = () -> {
          return "kindred";
        };

        System.out.println(supplier.get());
    }
}
```
### 消费型接口
```java
/**
 * 消费型接口 Consumer
 */

public class ConsumerDemo {
    public static void main(String[] args) {
        /**
         * public interface Consumer<T>
         * void accept(T t);
         */
        Consumer<String> consumer = (str) -> {
            System.out.println("Hello,我是" + str);
        };

        consumer.accept("kindred");
    }
}
```
