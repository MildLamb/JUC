package com.mildlamb.juc.funcInterface;

import java.util.function.Function;

/**
 * Function 函数型接口
 */

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
