package com.github.sevntu.checkstyle.checks.design;

public class InputCauseParameterInException4 extends ParentException {

public InputCauseParameterInException4(Exception cause) {
            super(cause);
        }
public class ParentException {

    public ParentException(String str, Exception e) {
    }

    public ParentException(Exception cause) {
    }

}
}

