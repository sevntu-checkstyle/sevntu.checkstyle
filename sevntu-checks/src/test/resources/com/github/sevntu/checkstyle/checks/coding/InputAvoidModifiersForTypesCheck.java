package com.github.sevntu.checkstyle.checks.coding;

import java.util.logging.Logger;

import org.junit.Rule;

import com.google.common.annotations.Beta;

/**
 * <p>
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 * @author <a href="mailto:yasser.aziza@gmail.com">Yasser Aziza</a>
 * </p>
 */
public class InputAvoidModifiersForTypesCheck
{

    @Rule private static java.io.File k = new java.io.File("");
    private final InputAvoidModifiersForTypesCheck i = new InputAvoidModifiersForTypesCheck();
    private static final InputAvoidModifiersForTypesCheck m = new InputAvoidModifiersForTypesCheck();
    protected transient InputAvoidModifiersForTypesCheck a = new InputAvoidModifiersForTypesCheck();
    public volatile InputAvoidModifiersForTypesCheck d = new InputAvoidModifiersForTypesCheck();
    InputAvoidModifiersForTypesCheck e = new InputAvoidModifiersForTypesCheck();
    final InputAvoidModifiersForTypesCheck f = new InputAvoidModifiersForTypesCheck();

    public static void method()
    {
        InputAvoidModifiersForTypesCheck b = new InputAvoidModifiersForTypesCheck();
        final InputAvoidModifiersForTypesCheck c = new InputAvoidModifiersForTypesCheck();
        final int [] a = new int [10]; // primitive type array
        int []xxx = new int [6];
    }
    
    public class Check {
        private Logger log1 = Logger.getLogger(getClass().getName()); // OK
        protected Logger log2 = Logger.getLogger(getClass().getName()); // Violation
        public Logger log3 = Logger.getLogger(getClass().getName()); // Violation
        Logger log4 = Logger.getLogger(getClass().getName()); // Violation
    }

}
