package com.github.sevntu.checkstyle.checks.coding;

public class InputEnumTrailingCommaCheck {

    enum MovieType {
        GOOD,
        BAD,
        UGLY,
        ;
    }

    enum Gender {
        FEMALE,
        MALE // Violation
    }

    enum Language {
        ENGLISH,
        GERMAN // Violation
        ;
    }

    // Violation
    enum ErrorMessage {
        USER_DOES_NOT_EXIST,
        INVALID_USER_PASS,
        EVERYTHING_ELSE; // Violation
    }

    enum BinType {ZERO, ONE
    ;}

    enum SameLine {FIRST, SECOND, THIRD;}

    enum LineBreak {FIRST, SECOND,
        THIRD;}

    enum LastLineBreak {FIRST, SECOND,
        THIRD; // Violation
    }

    enum LastBreakSemicolon {FIRST, SECOND, THIRD
        ;
    }

    enum RgbColor {
        RED,
        GREEN,
        BLUE
        ,
    }

    enum OperatingSystem {
        LINUX,
        MAC
        ,}

    enum EMPTY {E
    }

}
