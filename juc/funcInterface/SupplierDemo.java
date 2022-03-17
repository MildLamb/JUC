package com.mildlamb.juc.funcInterface;

import java.util.function.Supplier;

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
