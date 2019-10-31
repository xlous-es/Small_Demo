## 简单四则运算
题目：根据输入的算式，得出结果并输出。
* 注意：<br>
    1、输入为字符串类型，且输出结果保留两位小数；<br>
    2、输入的字符串中可能含有括号。<br>
    3、运算符只有：“+ - * / ( )”六种
    
### 解决方案：
* 使用Scanner中的nextLine方法输入字符串
    * ```String str = sc.nextLine();```
* 去除字符串中的空格、table等空白字符
    * ```str = str.replaceAll("\\s*", "");```
* 完成运算方法，运算得到结果
    * ```String getCalculatorAll(String str)```
        * 使用String类中的lastIndexOf方法，找到最内侧的左括号
            * ```int lindex = str.lastIndexOf("(");```
        * 如果不存在左括号，则：
            * ```if (lindex == -1)```
                * 判断是否存在右括号，则应抛出异常
                * 如果不存在右括号，则直接运算
                    ```java
                    if (str.indexOf(")") != -1) {
                        throw new RuntimeException("输入的算式有误，请重新输入");
                    }
                    return getCalculator(str);
                    ```
        * 如果存在左括号，则：
            * 使用String类中的indexOf方法，判断是否存在右括号
                * ```int rindex = str.indexOf(")");```
            * 如果不存在，则应抛出异常（如上述抛出异常代码）
            * 如果存在，即rindex的值就是第一次出现的索引位置
                * 使用String类中的substring方法截取括号内的子字符串内容
                    * ```String sub = str.substring(lindex + 1, rindex);```
                * 调用直接运算方法
                    * String result = getCalculator(sub);
                * 将算式字符串中的本次运算的括号内的子字符串用调用直接运算方法返回的字符串替代
                    * ```str = str.substring(0, lindex) + result + str.substring(rindex + 1);```
                * 递归调用getCalculatorAll(String str)（即本身）
                    * ```return getCalculatorAll(str);```
    * 无括号运算方法：```String getCalculator(String str)```
        * 获取算式字符串中的数据列表和运算符列表，使用List
            ```java
            // 获取输入的算式中的数据列表
            List<Double> numlist = getNumberList(str);
            // 获取输入的算式中的运算符列表
            List<Character> operlist = getOperList(str);
            ```
        * 先计算存在的乘除
            * 遍历运算符列表operlist
            ```java
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
            ```
        * 再计算加减
            ```java
            // 每一次的运算都是要使用一个运算符，如果运算符列表不为空，说明运算未结束，否则运算结束
            // 此时剩下的运算符只有加减运算符，所以可以从左到右顺序运算
            while (!operlist.isEmpty()) {
                double lnum = numlist.remove(0);
                double rnum = numlist.remove(0);
                Character oper = operlist.remove(0);
                numlist.add(0, oper == '+' ? lnum + rnum : lnum - rnum);
            }
            ```
        * 返回值：
            ```java
            // numlist.get(0)，因为运算到最后，只有0索引处是最终运算的结果
            // 使用String类的format方法，是为了规范格式
            return String.format("%.2f", numlist.get(0));
            ```
    * 获取运算符列表：```List<Character> getOperList(String str)```
        * 清除干扰字符：负数前面的负号：‘-’替换为‘@’
            * 调用替换负数负号方法：```str = setSymbol(str);```
        * 遍历算式字符串，如果是运算符就添加至operlist列表中
            ```java
            List<Character> operlist = new ArrayList<>();
            for (int i = 0; i < str.length(); i++) {
                char oper = str.charAt(i);
                if (oper == '+' || oper == '-' || oper == '*' || oper == '/') {
                    operlist.add(oper);
                }
            }
            ```
    * 获取运算符列表：```List<Double> getNumberList(String str)```
        * 清除干扰字符：负数前面的负号：‘-’替换为‘@’
            * 调用替换负数负号方法：```str = setSymbol(str);```
        * 将所有的“+-*/”运算符替换为‘#’
            ```java
            // 将运算符替换为‘#’，是为了下一步的根据‘#’分割
            str = str.replace("+","#");
            str = str.replace("-","#");
            str = str.replace("*","#");
            str = str.replace("/","#");
            ```
        * 然后根据‘#’切割，得到数据列表numlist
            ```java
            String[] num = str.split("#");
            ```
        * 将原来变为‘@’的‘-’，即负数变为正常，使用Double包装类的parseDouble方法，将字符串转化为double类型数据
            ```java
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
            ```
    * 清除干扰字符：负数前面的负号：‘-’替换为‘@’：```String setSymbol(String str)```
        ```java
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
        ```