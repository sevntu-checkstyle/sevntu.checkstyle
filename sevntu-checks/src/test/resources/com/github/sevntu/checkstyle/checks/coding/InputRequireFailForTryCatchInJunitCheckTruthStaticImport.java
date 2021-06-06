package com.github.sevntu.checkstyle.checks.coding;

import static com.google.common.truth.Truth.assert_;
import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

import com.google.common.truth.Truth;
import org.junit.jupiter.api.Test;

public class InputRequireFailForTryCatchInJunitCheckTruthStaticImport {

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

    @Test
    public void validAssertWithMessage() {
        try {
            obj.toString();
            assertWithMessage("simple message").fail();
        }
        catch (Exception expected) {
            assertThat(expected).isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    public void validAssertWithFormattedMessage() {
        try {
            obj.toString();
            Truth.assertWithMessage("formatted message: %s %s", "string", 42).fail();
        }
        catch (Exception expected) {
            assertThat(expected).isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    public void validAssertWithMessageChained() {
        try {
            obj.toString();
            com.google.common.truth.Truth.assert_().withMessage("simple message").fail();
        }
        catch (Exception expected) {
            assertThat(expected).isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    public void validAssertWithMessageChainedFormatted() {
        try {
            obj.toString();
            assertWithMessage("First line")
                .withMessage("Formatted second line: %s %s", "string", 42)
                .withMessage("Third line")
                .fail();
        }
        catch (Exception expected) {
            assertThat(expected).isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    public void violationAssertWithoutFail() {
        try { // violation
            obj.toString();
            assertWithMessage("message").that(0).isLessThan(1);
        }
        catch (Exception expected) {
            assertThat(expected).isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    public void violationChainedFail() {
        try { // violation
            obj.toString();
            chain().chain().fail();
        }
        catch (Exception expected) {
            assertThat(expected).isInstanceOf(NullPointerException.class);
        }
    }

    private InputRequireFailForTryCatchInJunitCheckTruthStaticImport chain() {
        return this;
    }

    private void fail() {
        // This method does nothing
    }

}
