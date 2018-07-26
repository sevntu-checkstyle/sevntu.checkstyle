package com.github.sevntu.checkstyle.checks.coding;

public class InputNumericLiteralNeedsUnderscoreCheck2 {
    
    public void goodNumericLiterals() {
        int goodInt1 = 12;
        int goodInt2 = 1_234;
        int goodInt3 = -1_234;
        int goodInt4 = -123_456_7;
        long goodLong1 = 123L;
        long goodLong2 = 123_456_789_012_3L;
        float goodFloat1 = 1.234f;
        float goodFloat2 = 123_456.789f;
        double goodDouble1 = 1.234;
        double goodDouble2 = 1.234d;
        double goodDouble3 = 123_4.789;
        double goodDouble4 = 12_34.1_234d;
        double goodDouble5 = -123_456.123_4;
        double goodDouble6 = -12_34_56.12_34d;
    }
    
    public void badNumericLiterals() {
        int badInt = 1234;
        long badLong = 1234L;
        float badFloat = 1_2345.89f;
        double badDouble = 1.2345;
        if (badInt > 100_000000) {
            badFloat -= 1.234_5678f;
        }
    }
    
    public void scientificNotation() {
        float fgood1 = 123.456e2f;
        float fgood2 = 123_4.123e2f;
        float fgood3 = 123.456E2F;
        float fgood4 = 123_4.123_456_7E2F;
        double dgood1 = 123.456e2;
        double dgood2 = 123.456e2d;
        double dgood3 = 1_234.123_456_7e2;
        double dgood4 = 1_234.123_456_7e2d;
    }
    
    public void badScientificNotation() {
        float fbad1 = 1.2345e2f;
        float fbad2 = 123_4.12_3456_7e2f;
        float fbad3 = 123.4560E2F;
        double dbad1 = 123.1234e2;
        double dbad2 = 1234.56e1d;
        double dbad3 = 1234.12_3456_7e2;
        double dbad4 = 1234.12_3456_7e2d;
    }
    
    public void goodHexLiterals() {
        int goodInt1 = 0xFF;
        int goodInt2 = 0xFF_FF_FF_FF;
        int goodInt3 = 0x1_e3_f;
        long goodLong1 = 0x00L;
        long goodLong2 = 0xFF_FF_FF_FFL;
        float goodFloat1 = 0xAA.BBp1f;
        float goodFloat2 = 0x1_23_45.AB_CDp-7F;
        double goodDouble1 = 0x1_23_4.FF_FFp7;
        double goodDouble2 = 0xCA_FE_DE_ED.DE_AD_BE_EFp0d;
        double goodDouble3 = 0x1.12_34p0d;
    }
    
    public void badHexLiterals() {
        int badInt1 = 0x111;
        int badInt2 = 0xFFFE;
        int badInt3 = 0xfffe;
        long badLong1 = 0x0000L;
        float badFloat1 = 0x123.0p-7F;
        float badFloat2 = 0x1.123p2f;
        double badDouble1 = 0x123.0p2;
        double badDouble2 = 0x1.123p2d;
    }
    
    public void goodByteLiterals() {
        int goodInt1 = 0b0011;
        int goodInt2 = 0b0000_1111_0000_1111;
        int goodInt3 = 0b0_000_1111_000_0111_1;
        long goodLong1 = 0b0011L;
        long goodLong2 = 0b0000_1111_0000_1111L;
    }
    
    public void badByteLiterals() {
        int badInt1 = 0b00110;
        int badInt2 = 0b00001_000;
        long badLong1 = 0b00110L;
        long badLong2 = 0b0000_1111_0000_1111_0000_11111_000_1111L;
    }
    
}
