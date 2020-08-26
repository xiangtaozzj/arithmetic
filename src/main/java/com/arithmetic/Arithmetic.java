package com.arithmetic;

import java.text.MessageFormat;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Arithmetic {

    /** 被演算子キュー*/
    private Queue<String> numberQueue = new ArrayDeque<>();

    /** 演算子スタック */
    private Stack<String> opreatorStack = new Stack<>();

    public Arithmetic(String expression) throws ExpressionException {
        LinkedList<String> expList = checkExpression(expression);
        changeToReversePolish(expList);
    }

    /**
     * 
     * 逆ポーランド式に変換する
     * <blockquote>
     * <li>
     * 演算子スタックに既存の演算子より優先順位:
     *      1.同じの場合、topの演算子をpopし、被演算子キューにaddします。
     *      2.低い場合、既存の演算子をpopして、被演算子キューにaddします。
     *      3.演算子スタックに'('がある場合、'('までpopして、被演算子キューにaddします。
     * </li><li>
     * 式：2.1 / 0.7 +4 *3-(4+ 2 *2)-1
     * </li><li>
     * 逆ポーランド式： 2.1 0.7 / 4 3 * + 4 2 2 * + - 1 -
     * </li>
     * </blockquote>
     * @param arithmetic
     * @author t_kou
     */
    private void changeToReversePolish(final LinkedList<String> expList) {
        // 前回のデータを削除する。
        numberQueue.clear();
        opreatorStack.clear();

        // 文字列の式を一文字ずつ解析する。
        for (String str : expList) {
            switch (str) {
            case ("("):
                opreatorStack.push(String.valueOf(str));
                break;
            // "+" "-"は同じの優先順位。
            case ("+"):
            case ("-"):
                // 演算子スタックにtopの演算子より優先順位：
                // 同じの場合、topの演算子をpopし、被演算子キューにaddします。
                // 低い場合、既存の演算子を全部popし、被演算子キューにaddします。
                // 演算子スタックに'('がある場合、'('までpopして、被演算子キューにaddします。
                if (!opreatorStack.isEmpty() && !"(".equals(opreatorStack.peek())) {
                    while (!opreatorStack.isEmpty() && !"(".equals(opreatorStack.peek())) {
                        numberQueue.add(opreatorStack.pop());
                    }
                }
                // 演算子スタックは空又は演算子スタックにtopの演算子は'('の場合、そのまま演算子キューにaddします。
                opreatorStack.push(String.valueOf(str));
                break;
            // "*" "/"は同じの優先順位。
            case ("*"):
            case ("/"):
                if (!opreatorStack.isEmpty() && !"(".equals(opreatorStack.peek())
                        && ("*".equals(opreatorStack.peek()) || "/".equals(opreatorStack.peek()))) {
                    numberQueue.add(opreatorStack.pop());
                }
                opreatorStack.push(String.valueOf(str));
                break;
            case (")"):
                while (!opreatorStack.isEmpty() && !"(".equals(opreatorStack.peek())) {
                    numberQueue.add(opreatorStack.pop());
                }
                opreatorStack.pop();
                break;
            default:
                numberQueue.add(String.valueOf(str));
                break;
            }
        }

        // 全部解析した後、演算子ススタックにまだ演算子があると、全部被演算子スタックにpushします。
        while (!opreatorStack.isEmpty()) {
            numberQueue.add(opreatorStack.pop());
        }

        // 逆ポーランド式への変換結果
        //        System.out.println(numberQueue);
    }

    /**
     * 
     * 	変換した逆ポーランド式で計算する
     * <blockquote>
     * メソッド詳細。
     * </blockquote>
     * @param opreatorStack
     * @return
     * @author t_kou
     */
    public double calculate() {
        Stack<String> resultStack = new Stack<>();
        double num1;
        double num2;
        double result;
        while (!numberQueue.isEmpty()) {
            String str = numberQueue.poll();
            switch (str) {
            case ("+"):
                num2 = Double.parseDouble(resultStack.pop());
                num1 = Double.parseDouble(resultStack.pop());
                resultStack.push(String.valueOf(num1 + num2));
                break;
            case ("-"):
                num2 = Double.parseDouble(resultStack.pop());
                num1 = Double.parseDouble(resultStack.pop());
                resultStack.push(String.valueOf(num1 - num2));
                break;
            case ("*"):
                num2 = Double.parseDouble(resultStack.pop());
                num1 = Double.parseDouble(resultStack.pop());
                resultStack.push(String.valueOf(num1 * num2));
                break;
            case ("/"):
                num2 = Double.parseDouble(resultStack.pop());
                if (num2 == 0d) {
                    // TODO throw exception for 0 dividing 
                }
                num1 = Double.parseDouble(resultStack.pop());
                resultStack.push(String.valueOf(num1 / num2));
                break;
            default:
                resultStack.push(str);
            }
        }
        result = Double.parseDouble(resultStack.pop());
        return result;
    }

    /**
     * 入力した計算式のチェック
     * <blockquote>
     * </blockquote>
     * @param expression
     * @return
     * @throws ExpressionException
     * @author t_kou
     */
    private LinkedList<String> checkExpression(String expression) throws ExpressionException {
        final char[] expChar = expression.toCharArray();
        StringBuilder str = new StringBuilder();
        LinkedList<String> list = new LinkedList<>();

        int leftBracketCnt = 0;
        int rightBracketCnt = 0;
        
        int lastTokenType = 0; // 0:オペランド, 1:オペランド
        int operatorCnt = 0;

        for (int i = 0; i < expChar.length; i++) {
            char ch = expChar[i];
            if ('0' <= ch && ch <= '9' || ch == '.' || ch == ' ') {
                str.append(ch);
            } else {
                String strNoSpace = str.toString().trim();
                if (strNoSpace.contains(" ")) {
                    String message = MessageFormat.format("被演算子に半角スペースが入っています。被演算子：第[{0}]番目の[{1}]",
                            (i - str.length() + 1), str);
                    throw new ExpressionException(message);
                } else if (strNoSpace.length() != 0) {
                    if (!list.isEmpty() && ")".equals(list.getLast())) {
                        list.add("*");
                    }
                    list.add(strNoSpace);
                    str.delete(0, str.length());
                }
                switch (ch) {
                case ('+'):
                case ('-'):
                case ('*'):
                case ('/'):
                    if (i == 0 || "+-*/(".contains((list.getLast()))) {
                        throw new ExpressionException("演算式が間違いました。演算子：第["
                                + (i + 1) + "]番目の[" + ch + "]");
                    } else {
                        list.add(String.valueOf(ch));
                    }
                    break;
                case ('('):
                    leftBracketCnt++;
                    if (!list.isEmpty() && !"+-*/(".contains(list.getLast())) {
                        list.add("*");
                    }
                    list.add(String.valueOf(ch));
                    break;
                case (')'):
                    rightBracketCnt++;
                    // "("、")"はペアーになっているかどうかの確認。
                    if (rightBracketCnt > leftBracketCnt) {
                        throw new ExpressionException("左括弧と右の括弧はペアーになっていません。右括弧が多かったです。");
                    }
                    list.add(String.valueOf(ch));
                    break;
                default:
                    throw new ExpressionException("演算子が間違いました、或いは全角スペースを入っています。演算子：第["
                            + (i + 1) + "]番目の[" + ch +"]");
                }
            }
        }

        // "("、")"はペアーになっているかどうかの確認。
        if (leftBracketCnt != rightBracketCnt) {
            throw new ExpressionException("左括弧と右の括弧はペアーになっていません。右括弧が足りないです。");
        }
        
        String strNoSpace = str.toString().trim();
        if (strNoSpace.contains(" ")) {
            throw new ExpressionException("被演算子に半角スペースが入っています。最後の被演算子：[" + str + "]");
        } else if (strNoSpace.length() != 0) {
            if (!list.isEmpty() && ")".equals(list.getLast())) {
                list.add("*");
            }
            list.add(strNoSpace);
        }

        if ("+-*/".contains((list.getLast()))) {
            throw new ExpressionException("演算式が間違いました。式の最後は四則演算式になっています。[" + list.getLast() + "]");
        }
        return list;
    }
}