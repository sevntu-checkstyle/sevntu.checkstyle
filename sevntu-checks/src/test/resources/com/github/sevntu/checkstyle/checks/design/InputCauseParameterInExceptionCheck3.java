package com.github.sevntu.checkstyle.checks.design;
@SuppressWarnings("serial")
public class InputCauseParameterInExceptionCheck3 extends RuntimeException {

    public InputCauseParameterInExceptionCheck3() {
        super();
    }

    public InputCauseParameterInExceptionCheck3(String message) {
        super(message);
    }

    public InputCauseParameterInExceptionCheck3(Throwable cause) {
        super(cause);
    }

    public InputCauseParameterInExceptionCheck3(String message, Throwable cause) {
        super(message, cause);
    }

}
