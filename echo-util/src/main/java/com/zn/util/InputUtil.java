package com.zn.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * zhangning
 */
public class InputUtil {

    private static final BufferedReader key_info = new BufferedReader(new InputStreamReader(System.in));


    private InputUtil() {
    }


    /**
     * 实现键盘数据的输入操作 ，可以返回的数据类型为String
     * @param prompt 提示信息
     * @return 输入的数据返回
     */
    public static String getKeyInfoString(String prompt) {
        boolean falg = true;
        String str = null;
        while (falg) {
            System.out.print(prompt);
            try {
                str = key_info.readLine();
                if (str == null || "".equalsIgnoreCase(str)) {
                    System.out.println("输的的不符合规范或者没有输入！");
                } else {
                    System.out.println("您输入的是："+str);
                    falg = false;
                }
            } catch (IOException e) {
                System.out.println("输的的不符合规范或者没有输入！");
                e.printStackTrace();
            }
        }

        return str;
    }

    public static void main(String[] args) {
        InputUtil.getKeyInfoString("请输入！");
    }


}
