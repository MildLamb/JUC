package com.mildlamb.juc.stream;

import java.util.Arrays;
import java.util.List;

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
        User u4 = new User(4,"e",24);
        User u5 = new User(6,"d",25);
        User u3 = new User(3,"c",23);
        // 集合是用来存储的
        List<User> users = Arrays.asList(u1, u2, u3, u4, u5);

        // 计算交给流 stream
        users.stream()
                .filter((u) -> {return u.getId() % 2 == 0;})
                .filter((u) -> {return u.getAge() > 23;})
                .map((u) -> {u.setName(u.getName().toUpperCase()); return u;})
                .sorted((user1,user2) -> { return (int)(user2.getName().toCharArray()[0]) - (int)user1.getName().toCharArray()[0];})
                .limit(1)
                .forEach(System.out::println);
    }
}
