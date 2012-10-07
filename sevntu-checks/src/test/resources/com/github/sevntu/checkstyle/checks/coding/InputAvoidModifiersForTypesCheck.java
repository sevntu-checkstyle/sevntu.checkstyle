package com.github.sevntu.checkstyle.checks.coding;

/**
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a> *
 */
public class InputAvoidModifiersForTypesCheck
{

    static java.io.File k = new java.io.File();
    final InputAvoidModifiersForTypesCheck i = new InputAvoidModifiersForTypesCheck();
    static final InputAvoidModifiersForTypesCheck m = new InputAvoidModifiersForTypesCheck();
    transient InputAvoidModifiersForTypesCheck a = new InputAvoidModifiersForTypesCheck();
    volatile InputAvoidModifiersForTypesCheck d = new InputAvoidModifiersForTypesCheck();

    public static void method()
    {
        InputAvoidModifiersForTypesCheck b = new InputAvoidModifiersForTypesCheck();
        final InputAvoidModifiersForTypesCheck c = new InputAvoidModifiersForTypesCheck();
        final int [] a = new int [10]; // primitive type array    
        int []xxx = new int [6];
    }

}
