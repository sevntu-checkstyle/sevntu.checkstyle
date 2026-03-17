package com.github.sevntu.checkstyle.checks.coding;

public class InputLogicConditionNeedOptimizationCheck2 {
    String typeGuardAfterParenthesizedTrueSwitchStatement1(Object o) {
        switch (o) {
            case Integer i:
                if (i == 0) {
                    o = String.valueOf(i);
                    return "true";
                }
                if (i == 2) {
                    o = String.valueOf(i);
                    return "second";
                }
                break;

            case Object x:
                return "any";
        }
        return null;
    }

    String typeGuardAfterParenthesizedTrueSwitchExpression1(Object o) {
        return switch (o) {
            case Integer i -> {
                if (i == 0) {
                    o = String.valueOf(i);
                    yield "true";
                }
                if (i == 2) {
                    o = String.valueOf(i);
                    yield "second";
                }
                yield "any";
            }
            default -> "any";
        };
    }

    String typeGuardAfterParenthesizedTrueIfStatement1(Object o) {
        if (o != null && o instanceof Integer i && i == 0) {
            return "true";
        } else if (o != null && o instanceof Integer i && i == 2 && (o = i) != null) {
            return "second";
        } else {
            return "any";
        }
    }

    String typeGuardAfterParenthesizedTrueSwitchStatement2(Object o) {
        switch (o) {
            case Integer i:
                if (i == 0) {
                    o = String.valueOf(i);
                    return "true";
                }
                if (i == 2) {
                    o = String.valueOf(i);
                    return "second";
                }
                break;

            case Object x:
                return "any";
        }
        return null;
    }

    String typeGuardAfterParenthesizedTrueSwitchExpression2(Object o) {
        return switch (o) {
            case Integer i -> {
                if (i == 0) {
                    o = String.valueOf(i);
                    yield "true";
                }
                if (i == 2) {
                    o = String.valueOf(i);
                    yield "second";
                }
                yield "any";
            }
            case Object x -> "any";
        };
    }

    String typeGuardAfterParenthesizedTrueIfStatement2(Object o) {
        if (o != null && o instanceof Integer i && i == 0) {
            return "true";
        } else if (o != null && o instanceof Integer i && i == 2 && (o = i) != null) { // violation
            return "second";
        } else {
            return "any";
        }
    }

    void testParenthesisCase(boolean a, boolean b, boolean c) {
        if ((a && b) && c) {
            // test for parentheses coverage
        }
    }

    void testCoverage(boolean a, boolean b) {
        if (((a)) && b) {
        }
    }

    boolean getValue() {
        return true;
    }

    void testCoverageCase(boolean a) {
        if (getValue() && (a)) {
        }
    }

    String typeGuardAfterParenthesizedPatternWithMethodCall(Object o) {
        return switch (o) {
            case (String s) && s.isEmpty() -> "empty";
            default -> "any";
        };
    }
}
