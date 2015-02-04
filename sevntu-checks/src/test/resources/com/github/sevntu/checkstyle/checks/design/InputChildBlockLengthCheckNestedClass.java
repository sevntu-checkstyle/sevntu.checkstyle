package com.github.sevntu.checkstyle.checks.design;

public class InputChildBlockLengthCheckNestedClass {

    public static void main(String[] args)
    {
		if (isTrue()) {

			if (isTrue()) {


				class Inner {
					void method() {

						new Object() {


							int number = 0;



						};
					}
				}
			}
		}
	}

    public static boolean isTrue() {
        return Boolean.TRUE;
    }

}
