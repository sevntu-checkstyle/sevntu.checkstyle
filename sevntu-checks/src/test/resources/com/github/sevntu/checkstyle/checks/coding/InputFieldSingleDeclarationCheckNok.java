package com.github.sevntu.checkstyle.checks.coding;

import java.io.File;
import java.util.logging.Logger;

public class InputFieldSingleDeclarationCheckNok {

    // both FQN and import'ed type names work:
    Logger logger1;
    org.slf4j.Logger logger2; // <= Checkstyle violation raised here!

    // some field of another type (req. for test coverage)
    File file;

    // a method, so that there are not only fields here (req. for test coverage)
    void foo() { }

}
