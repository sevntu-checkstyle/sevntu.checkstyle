package com.github.sevntu.checkstyle.checks.coding;

public class InputNumericLiteralNeedsUnderscoreCheck {

    public void goodNumericLiterals() {
        int goodInt1 = 1234;
        int goodInt2 = 1_234_567;
        int goodInt3 = -1_234_567;
        int goodInt4 = -123_456_7;
        long goodLong1 = 1234L;
        long goodLong2 = 123_456_789_012_3L;
        long goodLong3 = 1234l;
        long goodLong4 = 123456l;
        float goodFloat1 = 1.234f;
        float goodFloat2 = 123456.123456f;
        float goodFloat3 = 123_456.789f;
        double goodDouble1 = 1.234;
        double goodDouble2 = 1.234d;
        double goodDouble3 = 123_456.789;
        double goodDouble4 = 123_456.789d;
        double goodDouble5 = -123_456.789;
        double goodDouble6 = -123_456.789d;
        double goodDouble7 = -123_456.123456D;
    }

    public void badNumericLiterals() {
        int badInt = 1234567;
        long badLong1 = 1234567L;
        long badLong2 = 1234567l;
        float badFloat = 1234567.89f;
        double badDouble = 1.23456789;
        if (badInt > 100000000) {
            badFloat -= 1.2345678f;
        }
    }

    public void scientificNotation() {
        float fgood1 = 123.456e2f;
        float fgood2 = 123_4.123_456_7e2f;
        float fgood3 = 123.456E2F;
        float fgood4 = 123_4.123_456_7E2F;
        double dgood1 = 123.456e2;
        double dgood2 = 123.456e2d;
        double dgood3 = 1234.123_456_7e2;
        double dgood4 = 1234.123_456_7e2d;
        double dgood5 = 1234.123_456_7e2D;
    }

    public void badScientificNotation() {
        float fbad1 = 1.2345678e2f;
        float fbad2 = 123_4.12_3456_7e2f;
        float fbad3 = 123.4567890E2F;
        double dbad1 = 123.1234567e2;
        double dbad2 = 1234567.56e1d;
        double dbad3 = 1234.12_3456_7e2;
        double dbad4 = 1234.12_3456_7e2d;
        double dbad5 = 1234.12_3456_7e2D;
    }

    public void goodHexLiterals() {
        int goodInt1 = 0xFF;
        int goodInt2 = 0xFFFF_FFFF;
        int goodInt3 = 0x1e3f;
        long goodLong1 = 0x0000L;
        long goodLong2 = 0xFFFF_FFFFL;
        long goodLong3 = 0xffff_ffffl;
        float goodFloat1 = 0xAAAA.BBBBp1f;
        float goodFloat2 = 0x12_345.6p-7F;
        double goodDouble1 = 0x1234.FFFFp7;
        double goodDouble2 = 0xCA_FE_DEED.DEAD_BEEFp0d;
        double goodDouble3 = 0x1.1234p0d;
    }

    public void badHexLiterals() {
        int badInt1 = 0x11111111;
        int badInt2 = 0xFFFEFFFE;
        int badInt3 = 0xfffefffe;
        int badInt4 = 0xFFFFF;
        int badInt5 = 0xFFFFD;
        long badLong1 = 0x0000FFFFL;
        long badLong2 = 0x0e0eFfFfl;
        float badFloat1 = 0x12345.6p-7F;
        float badFloat2 = 0x1.12345p2f;
        double badDouble1 = 0x12345.6p2;
        double badDouble2 = 0x1.12345p2d;
    }

    public void goodBinaryLiterals() {
        int goodInt1 = 0b00001111;
        int goodInt2 = 0b00001111_00001111;
        int goodInt3 = 0b0_00011110_000111_1;
        long goodLong1 = 0b00001111L;
        long goodLong2 = 0b00001111_00001111L;
        long goodLong3 = 0b00001111_00001111l;
        long goodLong4 = 0b00001111l;
    }

    public void badBinaryLiterals() {
        int badInt1 = 0b000011110;
        int badInt2 = 0b0000111_100001111;
        long badLong1 = 0b000011110L;
        long badLong2 = 0b00001111_00001111_000011111_0001111L;
        long badLong3 = 0b000011111l;
        long badLong4 = 0b00001111_00001111_000011111_0001111l;
    }

}
