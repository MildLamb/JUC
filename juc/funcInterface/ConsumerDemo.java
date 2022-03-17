package com.mildlamb.juc.funcInterface;

import java.util.function.Consumer;

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
