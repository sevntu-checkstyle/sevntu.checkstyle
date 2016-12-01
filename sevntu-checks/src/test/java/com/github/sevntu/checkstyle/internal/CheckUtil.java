////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2016 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.internal;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.AutomaticBean;
import com.puppycrawl.tools.checkstyle.api.BeforeExecutionFileFilter;
import com.puppycrawl.tools.checkstyle.api.Filter;

public final class CheckUtil {
    private CheckUtil() {
    }

    public static Set<String> getPackages(Set<Class<?>> modules) throws IOException {
        final Set<String> result = new HashSet<>();

        for (Class<?> module : modules) {
            result.add(module.getPackage().getName()
                    .replace("com.github.sevntu.checkstyle.checks.", ""));
        }

        return result;
    }

    public static Set<Class<?>> getModulesInPackage(Set<Class<?>> modules, String packge) {
        final Set<Class<?>> result = new HashSet<>();

        for (Class<?> module : modules) {
            if (module.getPackage().getName().endsWith(packge)) {
                result.add(module);
            }
        }

        return result;
    }

    /**
     * Gets the checkstyle's modules. Checkstyle's modules are nonabstract
     * classes from com.puppycrawl.tools.checkstyle package which names end with
     * 'Check', do not contain the word 'Input' (are not input files for UTs),
     * checkstyle's filters and SuppressWarningsHolder class.
     *
     * @return a set of checkstyle's modules names.
     * @throws IOException if the attempt to read class path resources failed.
     */
    public static Set<Class<?>> getCheckstyleModules() throws IOException {
        final Set<Class<?>> checkstyleModules = new HashSet<>();

        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final ClassPath classpath = ClassPath.from(loader);
        final String packageName = "com.github.sevntu.checkstyle.checks";
        final ImmutableSet<ClassPath.ClassInfo> checkstyleClasses = classpath
                .getTopLevelClassesRecursive(packageName);

        for (ClassPath.ClassInfo clazz : checkstyleClasses) {
            final Class<?> loadedClass = clazz.load();
            if (isCheckstyleModule(loadedClass)) {
                checkstyleModules.add(loadedClass);
            }
        }
        return checkstyleModules;
    }

    /**
     * Get's the check's messages.
     * @param module class to examine.
     * @return a set of checkstyle's module message fields.
     * @throws ClassNotFoundException if the attempt to read a protected class fails.
     */
    public static Set<Field> getCheckMessages(Class<?> module) throws ClassNotFoundException {
        final Set<Field> checkstyleMessages = new HashSet<>();

        // get all fields from current class
        final Field[] fields = module.getDeclaredFields();

        for (Field field : fields) {
            if (field.getName().startsWith("MSG_")) {
                checkstyleMessages.add(field);
            }
        }

        // deep scan class through hierarchy
        final Class<?> superModule = module.getSuperclass();

        if (superModule != null) {
            checkstyleMessages.addAll(getCheckMessages(superModule));
        }

        return checkstyleMessages;
    }

    /**
     * Checks whether a class may be considered as the checkstyle module.
     * Checkstyle's modules are nonabstract classes which names end with
     * 'Check', do not contain the word 'Input' (are not input files for UTs),
     * checkstyle's filters, checkstyle's file filters and
     * SuppressWarningsHolder class.
     *
     * @param loadedClass class to check.
     * @return true if the class may be considered as the checkstyle module.
     */
    private static boolean isCheckstyleModule(Class<?> loadedClass) {
        final String className = loadedClass.getSimpleName();
        return isCheckstyleNonAbstractCheck(loadedClass, className)
                || isFilterModule(loadedClass, className)
                || isFileFilterModule(loadedClass, className)
                || "SuppressWarningsHolder".equals(className)
                || "FileContentsHolder".equals(className);
    }

    /**
     * Checks whether a class may be considered as the checkstyle check.
     * Checkstyle's checks are nonabstract classes which names end with 'Check',
     * do not contain the word 'Input' (are not input files for UTs), and extend
     * Check.
     *
     * @param loadedClass class to check.
     * @param className class name to check.
     * @return true if a class may be considered as the checkstyle check.
     */
    private static boolean isCheckstyleNonAbstractCheck(Class<?> loadedClass, String className) {
        return !Modifier.isAbstract(loadedClass.getModifiers()) && className.endsWith("Check")
                && !className.contains("Input")
                && AbstractCheck.class.isAssignableFrom(loadedClass);
    }

    /**
     * Checks whether a class may be considered as the checkstyle filter.
     * Checkstyle's filters are classes which are subclasses of AutomaticBean,
     * implement 'Filter' interface, and which names end with 'Filter'.
     *
     * @param loadedClass class to check.
     * @param className class name to check.
     * @return true if a class may be considered as the checkstyle filter.
     */
    private static boolean isFilterModule(Class<?> loadedClass, String className) {
        return Filter.class.isAssignableFrom(loadedClass)
                && AutomaticBean.class.isAssignableFrom(loadedClass)
                && className.endsWith("Filter");
    }

    /**
     * Checks whether a class may be considered as the checkstyle file filter.
     * Checkstyle's file filters are classes which are subclasses of
     * AutomaticBean, implement 'BeforeExecutionFileFilter' interface, and which
     * names end with 'FileFilter'.
     *
     * @param loadedClass class to check.
     * @param className class name to check.
     * @return true if a class may be considered as the checkstyle file filter.
     */
    private static boolean isFileFilterModule(Class<?> loadedClass, String className) {
        return BeforeExecutionFileFilter.class.isAssignableFrom(loadedClass)
                && AutomaticBean.class.isAssignableFrom(loadedClass)
                && className.endsWith("FileFilter");
    }
}
