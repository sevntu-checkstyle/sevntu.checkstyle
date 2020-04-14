package com.github.sevntu.checkstyle.checks.coding;

public class InputFieldSingleDeclarationCheckFqnNok {

    // even twice a FQN type names work:
    org.slf4j.Logger logger1;
    org.slf4j.Logger logger2; // <= Checkstyle violation raised here!

}
