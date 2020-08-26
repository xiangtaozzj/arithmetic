package com.arithmetic;

import java.util.Scanner;

public class OperationScreen {

    public static void main(String[] args) {
        // 計算式
        //逆ポーランド式： 2.1 0.7 / 4 3 * + 4 2 2 * + - 1 -
        //        String expression = "2.1 / 0.7 +4 *3-(4+ 2 *2)-1";

        // 計算式を入力する
        System.out.println("四則演算式を入力してください：");
        Scanner scanner = new Scanner(System.in);
        String expression = scanner.nextLine();

        try {
            // 四則演算電卓を作る
            Arithmetic arithmetic = new Arithmetic(expression);

            // 計算して、結果を取得する
            double result = arithmetic.calculate();

            // 結果を出力する
            System.out.println("計算結果は：\n" + result);
        } catch (ExpressionException e) {
            System.out.println(e.getMessage());
            OperationScreen.main(null);
        }
        scanner.close();

    }

}
