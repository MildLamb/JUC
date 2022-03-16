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

## Stream流
```java
// User类
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User{
    private Integer id;
    private String name;
    private Integer age;
}



/**
 * 题目要求：一分钟内完成此题，只能用一行代码实现
 * 现在有5个用户！筛选
 * 1. ID必须是偶数
 * 2. 年龄必须要大于23岁
 * 3. 用户名转为大写字母
 * 4. 用户名字母倒着排序
 * 5. 只输出一个用户
 */

public class Test {
    public static void main(String[] args) {
        User u1 = new User(1,"a",21);
        User u2 = new User(2,"b",22);
        User u3 = new User(3,"c",23);
        User u4 = new User(4,"d",24);
        User u5 = new User(6,"e",25);
        // 集合是用来存储的
        List<User> users = Arrays.asList(u1, u2, u3, u4, u5);

        // 计算交给流 stream
        users.stream().filter((u) -> {return u.getId() % 2 == 0;})
                .filter((u) -> {return u.getAge() > 23;})
                .map((u) -> {u.setName(u.getName().toUpperCase()); return u;})
                .sorted((user1,user2) -> { return (int)(user2.getName().toCharArray()[0]) - (int)user1.getName().toCharArray()[0];})
                .limit(1)
                .forEach(System.out::println);
    }
}

```

## JMM
- JMM: Java内存模型，不是真实存在的东西，是一个概念


### 内存交互操作
- 内存交互操作有8种，虚拟机实现必须保证每一个操作都是原子的，不可在分的（对于double和long类型的变量来说，load、store、read和write操作在某些平台上允许例外）
  - lock     （锁定）：作用于主内存的变量，把一个变量标识为线程独占状态
  - unlock （解锁）：作用于主内存的变量，它把一个处于锁定状态的变量释放出来，释放后的变量才可以被其他线程锁定
  - read    （读取）：作用于主内存变量，它把一个变量的值从主内存传输到线程的工作内存中，以便随后的load动作使用
  - load     （载入）：作用于工作内存的变量，它把read操作从主存中变量放入工作内存中
  - use      （使用）：作用于工作内存中的变量，它把工作内存中的变量传输给执行引擎，每当虚拟机遇到一个需要使用到变量的值，就会使用到这个指令
  - assign  （赋值）：作用于工作内存中的变量，它把一个从执行引擎中接受到的值放入工作内存的变量副本中
  - store    （存储）：作用于主内存中的变量，它把一个从工作内存中一个变量的值传送到主内存中，以便后续的write使用
  - write 　（写入）：作用于主内存中的变量，它把store操作从工作内存中得到的变量的值放入主内存的变量中

- JMM对这八种指令的使用，制定了如下规则：
  - 不允许read和load、store和write操作之一单独出现。即使用了read必须load，使用了store必须write
  - 不允许线程丢弃他最近的assign操作，即工作变量的数据改变了之后，必须告知主存
  - 不允许一个线程将没有assign的数据从工作内存同步回主内存
  - 一个新的变量必须在主内存中诞生，不允许工作内存直接使用一个未被初始化的变量。就是怼变量实施use、store操作之前，必须经过assign和load操作
  - 一个变量同一时间只有一个线程能对其进行lock。多次lock后，必须执行相同次数的unlock才能解锁
  - 如果对一个变量进行lock操作，会清空所有工作内存中此变量的值，在执行引擎使用这个变量前，必须重新load或assign操作初始化变量的值
  - 如果一个变量没有被lock，就不能对其进行unlock操作。也不能unlock一个被其他线程锁住的变量
  - 对一个变量进行unlock操作之前，必须把此变量同步回主内存

### Volatile
- Volatile是Java虚拟机提供的轻量级的同步机制
1. 保证可见性
2. 不保证原子性
3. 禁止指令重排序

**保证可见性**  
```java
public class JMMDemo {

    // 不加volatile 程序会死循环
    private static volatile int num = 0;

    public static void main(String[] args) throws InterruptedException {
        new Thread(()->{
            while(num == 0){
            }
        }).start();

        TimeUnit.SECONDS.sleep(2);

        num = 1;
        System.out.println(num);
    }
}
```
**不保证原子性**
```java
/**
 * Volatile 不保证原子性
 * 如果不加 lock 和 synchronized ， 可以使用原子类来保证原子性
 */

public class VDemo {
    // Volatile 不保证原子性
    private volatile static AtomicInteger num = new AtomicInteger(0);

    public static void add(){
        // 1. 获得num的值
        // 2， +1
        // 3. 写回新的值
        num.getAndIncrement();  // AtomicInteger的加一操作
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int i1 = 0; i1 < 1000; i1++) {
                    add();
                }
            }).start();
        }


        while (Thread.activeCount() > 2){
            Thread.yield();
        }

        System.out.println(Thread.currentThread().getName() + " " + num);

    }
}
```
**禁止指令重排序**  

## 单例模式
### 饿汉式
```java
/**
 * 饿汉式单例
 */

public class Hungry {

    private Hungry(){

    }

    private final static Hungry HUNGRY = new Hungry();

    public static Hungry getInstance(){
        return HUNGRY;
    }
}
```

### 懒汉式
```java
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
```

## CAS
CAS机制当中使用了3个基本操作数：内存地址V，旧的预期值A，要修改的新值B。更新一个变量的时候，只有当变量的预期值A和内存地址V当中的实际值相同时，才会将内存地址V对应的值修改为B。

CAS的缺点：  
1. CPU开销较大  
在并发量比较高的情况下，如果许多线程反复尝试更新某一个变量，却又一直更新不成功，循环往复，会给CPU带来很大的压力。  

2. 不能保证代码块的原子性  
CAS机制所保证的只是一个变量的原子性操作，而不能保证整个代码块的原子性。比如需要保证3个变量共同进行原子性的更新，就不得不使用Synchronized了  


### ABA问题
- 概念：就是说一个线程把数据A变为了B，然后又重新变成了A。此时另外━个线程读取的时候，发现A没有变化，就误以为是原来的那个A。这就是有名的ABA问题。
- AtomicStampedReference
- 注意：Integer使用了对象缓存机制，默认范围是-128~127，推荐使用静态工厂方法valueof获取对象实例，而不是new，因为valueOf使用缓存，而new一定会创建新的对象分配新的内存空间;
```java
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
```

## 锁
### 可重入锁
- 可重入就是说某个线程已经获得某个锁，可以再次获取这个锁而不会出现死锁
```java
/**
 * 可重入就是说某个线程已经获得某个锁，可以再次获取锁而不会出现死锁
 */

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Demo2 {
    public static void main(String[] args) {
        Phone2 phone2 = new Phone2();
        Phone2 phone3 = new Phone2();
        new Thread(()->{
            phone2.sms();
        },"TaskA").start();

        new Thread(()->{
            phone3.tall();
        },"TaskB").start();
    }
}

class Phone2 {

    Lock lock = new ReentrantLock();

    public void sms(){

        lock.lock();

        try {
            System.out.println(Thread.currentThread().getName() + "sms --> " + lock.toString());
            call();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void call(){
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "call --> " + lock.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void tall(){
        lock.lock();
        try {
            call();
            System.out.println(Thread.currentThread().getName() + "tall --> " + lock.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
```

### 自旋锁
- 是指当一个线程在获取锁的时候，如果锁已经被其它线程获取，那么该线程将循环等待，然后不断的判断锁是否能够被成功获取，直到获取到锁才会退出循环
```java
public class TestSpinLock {
    public static void main(String[] args) throws InterruptedException {
        SpinlockDemo lock = new SpinlockDemo();

        new Thread(() -> {
            lock.myLock();
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                lock.myUnLock();
            }
        },"T1").start();

        TimeUnit.SECONDS.sleep(1);

        new Thread(() -> {
            lock.myLock();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                lock.myUnLock();
            }
        },"T2").start();
    }
}
```

### 死锁
死锁的4个必要条件：  
- 互斥条件：资源是独占的且排他使用，进程互斥使用资源，即任意时刻一个资源只能给一个进程使用，其他进程若申请一个资源，而该资源被另一进程占有时，则申请者等待直到资源被占有者释放。
- 不可剥夺条件：进程所获得的资源在未使用完毕之前，不被其他进程强行剥夺，而只能由获得该资源的进程资源释放。
- 请求和保持条件：进程每次申请它所需要的一部分资源，在申请新的资源的同时，继续占用已分配到的资源。
- 循环等待条件：在发生死锁时必然存在一个进程等待队列{P1,P2,…,Pn},其中P1等待P2占有的资源，P2等待P3占有的资源，…，Pn等待P1占有的资源，形成一个进程等待环路，环路中每一个进程所占有的资源同时被另一个申请，也就是前一个进程占有后一个进程所深情地资源。

### 死锁排查
- 编写一个死锁程序
```java
public class DeadLockDemo1 {
    public static void main(String[] args) {

        String lock_A = "Alock";
        String lock_B = "Block";

        new Thread(new MyThread(lock_A,lock_B),"T1").start();
        new Thread(new MyThread(lock_B,lock_A),"T2").start();
    }
}

class MyThread implements Runnable {

    private String lockA;
    private String lockB;

    public MyThread(String lockA, String lockB) {
        this.lockA = lockA;
        this.lockB = lockB;
    }

    @Override
    public void run() {
        synchronized (lockA){
            System.out.println(Thread.currentThread().getName() + "lock:" + lockA + ",want:" + lockB);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (lockB){
                System.out.println(Thread.currentThread().getName() + "lock:" + lockB + ",want:" + lockA);
            }
        }
    }
}
```
1. 使用 jps -l 定位进程号
```bash
C:\Code_Study\IDEA\juc>jps -l
15988 org.jetbrains.jps.cmdline.Launcher
12264 org.jetbrains.idea.maven.server.RemoteMavenServer
5512 sun.tools.jps.Jps
840
6524 com.mildlamb.juc.lock.DeadLockDemo1
```
2. 使用 jstack 进程号 ，查看进程信息
```bash
C:\Code_Study\IDEA\juc>jstack 6524

... ...
... ...
... ...

Java stack information for the threads listed above:
===================================================
"T2":
        at com.mildlamb.juc.lock.MyThread.run(DeadLockDemo1.java:36)
        - waiting to lock <0x000000076df1fde0> (a java.lang.String)
        - locked <0x000000076df1fe18> (a java.lang.String)
        at java.lang.Thread.run(Thread.java:748)
"T1":
        at com.mildlamb.juc.lock.MyThread.run(DeadLockDemo1.java:36)
        - waiting to lock <0x000000076df1fe18> (a java.lang.String)
        - locked <0x000000076df1fde0> (a java.lang.String)
        at java.lang.Thread.run(Thread.java:748)

Found 1 deadlock.
```
