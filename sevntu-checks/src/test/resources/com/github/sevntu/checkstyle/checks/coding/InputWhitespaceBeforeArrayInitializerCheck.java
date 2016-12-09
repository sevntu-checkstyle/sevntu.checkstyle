package com.github.sevntu.checkstyle.checks.coding;

public class InputWhitespaceBeforeArrayInitializerCheck {
    public void check(String[] args) {
        calculate(new int[]{1,2,3,4});
        int[] ints0 = {1,2,3,4};
        int[] ints1 = new int[] {0, 1, 2, 3};
        int[] ints2 = new int[] {
            0, 1, 2, 3
        };
        int[] ints3 = new int[]
            {0, 1, 2, 3};
        int[] ints4 = new int[]{
            0, 1, 2, 3
        };
        int[] ints5 = new int[]{0, 1, 2, 3};
        int ints6[]={1,2,3};
        int[] ints7 = new int[]
                               {0, 1, 2, 3};
        int[][] nested_ints = {
                {1, 2, 3, 4, 5},
                {6, 7, 8, 9, 10}
        };
        int[][] nestedIntOneLineViolation = new int[][]{{1}};
        int[][] nestedIntOneLine = new int[][] {{1}};
    }
    private int calculate(int[] tab) {
        return tab.length;
    }
}
