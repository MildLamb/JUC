package com.mildlamb.juc.unsafe;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MapTest {
    public static void main(String[] args) {
        Map<String,Object> map = new ConcurrentHashMap<>();
        // 初始容量，加载因子  public HashMap(int initialCapacity, float loadFactor)
        for (int i = 0; i < 10; i++) {
            new Thread(
                    ()->{
                        map.put(Thread.currentThread().getName(), UUID.randomUUID().toString().substring(0,5));
                        System.out.println(map);
                    }
            ,String.valueOf(i)).start();
        }
    }
}
