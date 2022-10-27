package com.github.sevntu.checkstyle.checks.coding;

public class InputLogicConditionNeedOptimizationCheck2 {
    String typeGuardAfterParenthesizedTrueSwitchStatement1(Object o) {
        switch (o) {
            case (Integer i)
                    && i == 0: o = String.valueOf(i); return "true";
            case ((Integer i)
                    && i == 2): o = String.valueOf(i); return "second";
            case Object x: return "any";
        }
    }

    String typeGuardAfterParenthesizedTrueSwitchExpression1(Object o) {
        return switch (o) {
            case (Integer i)
                    && i == 0: o = String.valueOf(i); yield "true";
            case ((Integer i)
                    && i == 2): o = String.valueOf(i); yield "second";
            case Object x: yield "any";
        };
    }

    String typeGuardAfterParenthesizedTrueIfStatement1(Object o) {
        if (o != null
                && o instanceof ((Integer i)
                && i == 0)) {
            return "true";
        } else if (o != null
                && o instanceof (((Integer i) 
                && i == 2)) 
                && (o = i) != null) { // violation
            return "second";
        } else {
            return "any";
        }
    }

    String typeGuardAfterParenthesizedTrueSwitchStatement2(Object o) {
        switch (o) {
            case (Integer i) &&
                    i == 0: o = String.valueOf(i); return "true";
            case ((Integer i) &&
                    i == 2): o = String.valueOf(i); return "second";
            case Object x: return "any";
        }
    }

    String typeGuardAfterParenthesizedTrueSwitchExpression2(Object o) {
        return switch (o) {
            case (Integer i) &&
                    i == 0: o = String.valueOf(i); yield "true";
            case ((Integer i) &&
                    i == 2): o = String.valueOf(i); yield "second";
            case Object x: yield "any";
        };
    }

    String typeGuardAfterParenthesizedTrueIfStatement2(Object o) {
        if (o != null &&
                o instanceof ((Integer i) &&
                        i == 0)) {
            return "true";
        } else if (o != null &&
                o instanceof (((Integer i) &&
                        i == 2)) && // violation
                (o = i) != null) {
            return "second";
        } else {
            return "any";
        }
    }
}
