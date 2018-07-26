package com.github.sevntu.checkstyle.checks.coding;

public interface InputEnumTrailingCommaAndSemicolonCheck
{
    enum E1 {
        ONE,
        TWO,
        THREE,
    }

    enum E2 {
        ONE,
        TWO,
        THREE   // violation
    }

    enum E3 {
        ONE,
        TWO,
        THREE;  // violation
    }

    enum E4 {
        ONE,
        TWO,
        THREE,; // violation
    }

    enum E5 {
        ONE,
        TWO,
        THREE,
        ;
    }

    // enums below are ignored by the check, but were added for completenes
    // Please don't remove, they are necessary for full cobertura branch coverage

    // empty
    enum E6 {}

    // single enum const, single-line block
    enum E7_1 { ONE }
    enum E7_2 { ONE; }
    enum E7_3 { ONE, }
    enum E7_4 { ONE,; }

    // single enum const, multi-line block
    enum E8_1 {
        ONE
    }
    enum E8_2 {
        ONE;
    }
    enum E8_3 {
        ONE,
    }
    enum E8_4 {
        ONE,;
    }
}
