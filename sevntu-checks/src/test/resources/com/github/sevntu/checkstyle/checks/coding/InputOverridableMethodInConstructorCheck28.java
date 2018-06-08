package com.github.sevntu.checkstyle.checks.coding;

import java.io.IOException;

public class InputOverridableMethodInConstructorCheck28 {
    class InputStream extends java.io.InputStream {
        InputStream() {
            method();
        }

        @Override
        public int read() throws IOException {
            return 0;
        }
    }

    public void method() {
    };
}
