package com.github.jiuzhuan.domain.repository.example.common.utils;

public class MathUtil {

    public static int max(int... num){
        int max = num[0];
        for (int i = 1; i < num.length; i++) {
            max = Math.max(max, num[i]);
        }
        return max;
    }
}
