package com.github.sevntu.checkstyle.checks.coding;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class InputRequireFailForTryCatchInJunitCheck10 {

    Object obj;

    @Test
    public void valid() {
        try {
            obj.toString();
            Assertions.fail("");
        }
        catch (Exception expected) {
            assertThat(expected).isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    public void valid2() {
        try {
            obj.toString();
            org.assertj.core.api.Assertions.fail("");
        }
        catch (Exception expected) {
            assertThat(expected).isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    public void valid3() {
        try {
            obj.toString();
            Assertions.failBecauseExceptionWasNotThrown(NullPointerException.class);
        }
        catch (Exception expected) {
            assertThat(expected).isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    public void valid4() {
        try {
            obj.toString();
            org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown(NullPointerException.class);
        }
        catch (Exception expected) {
            assertThat(expected).isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    public void validStatic() {
        try {
            obj.toString();
            fail("");
        }
        catch (Exception expected) {
            assertThat(expected).isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    public void validStatic1() {
        try {
            obj.toString();
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        }
        catch (Exception expected) {
            assertThat(expected).isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    public void violation() {
        try { // violation
            System.out.println("");
        }
        catch (Exception expected) {
            assertThat(expected).isInstanceOf(NullPointerException.class);
        }
    }
}
