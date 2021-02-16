package com.github.sevntu.checkstyle.checks.coding;

import static com.google.common.truth.Truth.*;

import com.google.common.truth.Truth;
import org.junit.jupiter.api.Test;

public class InputRequireFailForTryCatchInJunitCheck11 {

    Object obj;

    @Test
    public void valid() {
        try {
            obj.toString();
            Truth.assert_().fail();
        }
        catch (Exception expected) {
            assertThat(expected).isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    public void valid2() {
        try {
            obj.toString();
            com.google.common.truth.Truth.assert_().fail();
        }
        catch (Exception expected) {
            assertThat(expected).isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    public void validStatic() {
        try {
            obj.toString();
            assert_().fail();
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
