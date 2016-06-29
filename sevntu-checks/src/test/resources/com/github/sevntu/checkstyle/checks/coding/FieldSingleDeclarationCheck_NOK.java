package com.github.sevntu.checkstyle.checks.coding;

import java.io.File;
import org.slf4j.Logger;

public class FieldSingleDeclarationCheck_NOK {

    // both FQN and import'ed type names work:
    Logger logger1;
    org.slf4j.Logger logger2; // <= Checkstyle violation raised here!

    // some field of another type (req. for test coverage)
    File file;

    // a method, so that there are not only fields here (req. for test coverage)
    void foo() { }
}
