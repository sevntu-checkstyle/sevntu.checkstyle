package com.github.sevntu.checkstyle.checks.coding;

public interface InputAvoidNotShortCircuitOperatorsForBooleanCheckNonClasses {
    int field = 4 << 16 | 0;
}
enum TestEnum {
    ;

    int field = 4 << 16 | 0;
}
@interface TestAnnotation {
    int field = 4 << 16 | 0;
}
