package com.github.sevntu.checkstyle.checks.coding;
import java.lang.reflect.Method;
import java.util.Iterator;

public abstract class InputOverridableMethodInConstructorCheck24 {

	protected abstract String buildGetter(String component, String prop);

    protected InputOverridableMethodInConstructorCheck24(String component, String prop) {

          buildGetter(component, prop);

    }

}
