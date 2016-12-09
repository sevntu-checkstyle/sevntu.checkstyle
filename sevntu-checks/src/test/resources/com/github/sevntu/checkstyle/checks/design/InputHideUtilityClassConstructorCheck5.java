package com.github.sevntu.checkstyle.checks.design;

import java.awt.Dimension;
import javax.swing.JPanel;

/**
 * Not a util class because it's not directly derived from java.lang.Object.
 */
public class InputHideUtilityClassConstructorCheck5 extends JPanel
{
    /** HideUtilityClassConstructorCheck should not report this */
    public InputHideUtilityClassConstructorCheck5()
    {
	this.setPreferredSize(new Dimension(100, 100));
    }

    public static void utilMethod()
    {
	System.out.println("I'm a utility method");
    }
}
