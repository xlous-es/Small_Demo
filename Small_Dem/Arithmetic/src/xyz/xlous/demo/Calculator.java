package xyz.xlous.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
    题目：根据输入的算式，得出结果并输出。
    * 注意：<br>
        1、输入为字符串类型，且输出结果保留两位小数；<br>
        2、输入的字符串中可能含有括号。<br>
        3、运算符只有：“+ - * / ( )”六种
 */
public class Calculator {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("请输入算式：");
        String str = sc.nextLine();
        // 去除字符串中的空格、table等空白字符
        str = str.replaceAll("\\s*", "");
        // 调用运算方法
        System.out.println(getCalculatorAll(str));
    }

    /**
     * 右括号运算方法
     *
     * @param str 输入的算式
     * @return 最终运算结果
     */
    private static String getCalculatorAll(String str) {
        // 寻找左括号，lastIndexOf方法返回-1，表明字符串中没有该子串
        // 否则返回最后一次出现的索引位置
        int lindex = str.lastIndexOf("(");
        // lindex为-1，说明不存在左括号
        if (lindex == -1) {
            // 判断右括号的存在，如果右括号存在，则表明式子有误，抛出异常
            if (str.indexOf(")") != -1) {
                throw new RuntimeException("输入的算式有误，请重新输入");
            }
            // 没有右括号存在，式子正常，调用无括号运算方法
            return getCalculator(str);
        }
        // 左括号存在
        else {
            // 寻找右括号，indexOf方法，如果存在该子串，返回第一次出现的索引，否则返回-1
            int rindex = str.indexOf(")");
            // 左括号存在，而右括号不存在，输入有误，抛出异常
            if (rindex == -1) {
                throw new RuntimeException("输入的算式有误，请重新输入");
            }
            // 式子正常
            else {
                // 截取括号内的子字符串
                String sub = str.substring(lindex + 1, rindex);
                // 用该子串调用无括号运算方法直接运算，返回的是运算的结果
                String result = getCalculator(sub);
                // 将括号内的子字符串（包括该对括号）替换为运算的结果
                str = str.substring(0, lindex) + result + str.substring(rindex + 1);
                // 递归调用
                return getCalculatorAll(str);
            }
        }
    }

    /**
     * 无括号运算方法
     *
     * @param str 输入的算式
     * @return 运算结果
     */
    private static String getCalculator(String str) {
        // 获取输入的算式中的数据列表
        List<Double> numlist = getNumberList(str);
        // 获取输入的算式中的运算符列表
        List<Character> operlist = getOperList(str);
        // 首先计算乘除，遍历运算符列表，如果存在乘除，则先运算
        for (int i = 0; i < operlist.size(); i++) {
            Character oper = operlist.get(i);
            // 存在乘除运算符
            if (oper == '*' || oper == '/') {
                // 要使用该运算符，所以应该在运算后将该运算符从operlist列表中移出
                operlist.remove(i);
                // 取出该运算符左右两边的参加运算的数据
                // 第一个参与运算的额数据与运算符在operlist中的索引位置一致
                // 获取i索引位置的数据，remove方法是返回i索引位置的值，同时在列表中删除该索引位置的元素
                double lnum = numlist.remove(i);
                // 第二个数据原来的位置应该在索引为i+1的位置，但是第一个数据是remove，所以i之后的元素都要向左移动一位，所以还是remove取出i索引位置的元素
                double rnum = numlist.remove(i);
                // 将运算之后的值重新添加至i位置，替换参与运算的式子
                numlist.add(i, oper == '*' ? lnum * rnum : lnum / rnum);
                // 现在的operlist中i索引位置的运算符是原来i+1处的，所以要i--来让该运算符参与遍历
                i--;
            }
        }
        // 每一次的运算都是要使用一个运算符，如果运算符列表不为空，说明运算未结束，否则运算结束
        // 此时剩下的运算符只有加减运算符，所以可以从左到右顺序运算
        while (!operlist.isEmpty()) {
            double lnum = numlist.remove(0);
            double rnum = numlist.remove(0);
            Character oper = operlist.remove(0);
            numlist.add(0, oper == '+' ? lnum + rnum : lnum - rnum);
        }
        // numlist.get(0)，因为运算到最后，只有0索引处是最终运算的结果
        // 使用String类的format方法，是为了规范格式
        return String.format("%.2f", numlist.get(0));
    }

    /**
     * 获取运算符列表
     *
     * @param str 算式
     * @return 返回运算符列表
     */
    private static List<Character> getOperList(String str) {
        // 清除干扰字符：负数前面的负号：‘-’替换为‘@’
        str = setSymbol(str);
        List<Character> operlist = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            char oper = str.charAt(i);
            if (oper == '+' || oper == '-' || oper == '*' || oper == '/') {
                operlist.add(oper);
            }
        }
        return operlist;
    }

    /**
     * 获取运算数据列表
     *
     * @param str 算式
     * @return 返回运算数列表
     */
    private static List<Double> getNumberList(String str) {
        // 清除干扰字符：负数前面的负号：‘-’替换为‘@’
        str = setSymbol(str);
        // 将运算符替换为‘#’，是为了下一步的根据‘#’分割
        str = str.replace("+", "#");
        str = str.replace("-", "#");
        str = str.replace("*", "#");
        str = str.replace("/", "#");
        // 根据‘#’分割字符串，得到运算数列表
        String[] num = str.split("#");
        List<Double> numlist = new ArrayList<>();
        // 将分割后的字符串数组使用Double包装类的parseDouble方法，转化为Double类型数据
        for (String s : num) {
            // 如果字符串的第一个字符为‘@’，表明是负数，此时要将‘@’替换回‘-’
            if (s.charAt(0) == '@') {
                s = '-' + s.substring(1);
            }
            // Double包装类的parseDouble方法，将字符串转化为Double类型，并将其存入列表中
            numlist.add(Double.parseDouble(s));
        }
        return numlist;
    }

    /**
     * 清除干扰字符：负数前面的负号：‘-’替换为‘@’
     *
     * @param str 运算式
     * @return 返回将负数前的负号‘-’替换为‘@’之后的字符串
     */
    private static String setSymbol(String str) {
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            // 如果字符是负号
            if (ch == '-') {
                // 如果该字符的索引是第一个，即0索引处，则第一个数据是负数
                if (i == 0) {
                    str = '@' + str.substring(1);
                } else {
                    char ch2 = str.charAt(i - 1);
                    // 如果负号的前面还有其他运算符，则说明该运算符表示负数前的负号
                    if (ch2 == '+' || ch2 == '-' || ch2 == '*' || ch2 == '/') {
                        str = str.substring(0, i) + '@' + str.substring(i + 1);
                    }
                }
            }
        }
        return str;
    }

}
