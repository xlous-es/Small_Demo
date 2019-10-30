package xyz.xlous.demo;


import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
    在终端输入一个算式，输出最终结果
 */
public class Calculator {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("请输入运算式：");
        String str = sc.nextLine();
        // 去除字符串中的所有空格
        str = str.replaceAll("\\s*", "");
        System.out.println(getOperation(str));
    }
    /**
     * 存在括号计算方法
     *
     * @param str
     * @return
     */
    public static String getOperation(String str) {
        int lindex = str.lastIndexOf('(');
        if (lindex == -1) {
            if (str.indexOf(')') != -1) {
                throw new RuntimeException("运算式输入有误，请重新输入！");
            }
            return getCal(str);
        } else {
            int rindex = str.indexOf(')', lindex);
            if (rindex == -1) {
                throw new RuntimeException("运算式输入有误，请重新输入！");
            } else {
                String sub = str.substring(lindex + 1, rindex);
                String result = getCal(sub);
                str = str.substring(0, lindex) + result + str.substring(rindex + 1);
                return getOperation(str);
            }
        }
    }
    /**
     * 无括号计算方法
     *
     * @param str
     * @return
     */
    public static String getCal(String str) {
        List<Double> numlist = getNumList(str);
        List<Character> operlist = getOper(str);

        // 遍历运算符list，先计算乘除
        for (int i = 0; i < operlist.size(); i++) {
            Character ch = operlist.get(i);
            if (ch == '*' || ch == '/') {
                // 乘除运算之后，该运算符应该取出
                operlist.remove(i);
                // 取出运算符对应位置和之后一个位置的数进行运算
                Double left = numlist.remove(i);
                // 上面取出i之后，原本列表中i之后的数都左移一位
                // 所以i+1变为了i
                Double right = numlist.remove(i);

                numlist.add(i, ch == '*' ? left * right : left / right);
                // 因为operlist中将原来在i位置的乘除移出，i+1开始的数据左移一位
                // 所以此时的i位置的运算符是尚未经过判断的原来属于i+1位置的运算符
                i--;
            }
        }
        while (!operlist.isEmpty()) {
            Character ch = operlist.remove(0);
            Double left = numlist.remove(0);
            Double right = numlist.remove(0);
            numlist.add(0, ch == '+' ? left + right : left - right);
        }
        return String.format("%.2f", numlist.get(0));
    }


    /**
     * 获取输入的字符串中的运算符，存入operater列表中
     *
     * @param str
     * @return
     */
    public static List<Character> getOper(String str) {
        str = setSymbol(str);
        List<Character> operater = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '+' || c == '-' || c == '*' || c == '/') {
                operater.add(c);
            }
        }
        return operater;
    }

    /**
     * 获取输入的字符串中的数值，存入list列表中
     *
     * @param str
     * @return
     */
    public static List<Double> getNumList(String str) {
        str = setSymbol(str);

        str = str.replace("+", "#");
        str = str.replace("-", "#");
        str = str.replace("*", "#");
        str = str.replace("/", "#");

        String[] nums = str.split("#");
        List<Double> list = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            if (nums[i].charAt(0) == '@') {
                nums[i] = '-' + nums[i].substring(1);
            }
            list.add(Double.parseDouble(nums[i]));
        }
        return list;
    }

    /**
     * 将输入的四则运算式中的负数的‘-’号变为‘@’
     *
     * @param str 要进行运算的式子
     * @return str 返回改变后的式子
     */
    public static String setSymbol(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            // 改变负数前的负号
            if (c == '-') {
                // 当第一个数是负数时
                if (i == 0) {
                    str = '@' + str.substring(1);
                } else {
                    char ch = str.charAt(i - 1);
                    if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                        // 第一个substring的范围是[0,i)，第二个只有一个参数，表示从i+1开始，到字符串结尾
                        str = str.substring(0, i) + '@' + str.substring(i + 1);
                    }
                }
            }
        }
        return str;
    }


}
