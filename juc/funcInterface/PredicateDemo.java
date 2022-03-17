package com.mildlamb.juc.funcInterface;

import java.util.function.Predicate;

/**
 * Predicate 断定型函数接口
 */

public class PredicateDemo {
    public static void main(String[] args) {
        Predicate<String> predicate = (String str) -> {
            return str.equals("kindred");
        };

        System.out.println(predicate.test("kindred"));
    }
}
