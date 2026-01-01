///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2026 the original author or authors.
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
///////////////////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.checks.design;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InputConstructorWithoutParamsCheck {

    public void inputToTestDefaultConfig() {

        // the default config prohibits constructors without parameters if a class name
        // matches ".*Exception$"
        final RuntimeException ex = new RuntimeException(); // violation expected

        // the default config applies only to classes that have the word "Exception" at the end
        // of the class name
        final MockExceptionHandler mockExceptionHandler = new MockExceptionHandler(); // no violation expected

        // the default config ignores UnsupportedOperationException
        final UnsupportedOperationException ex2 = new UnsupportedOperationException(); // no violation expected

        // the check allows empty String parameters
        final RuntimeException ex1 = new RuntimeException(""); // no violation expected

        // the check ignores the comments
        // RuntimeException ex = new RuntimeException();

       /* the check ignores the comments
        * RuntimeException ex = new RuntimeException();
        */

        // the case where the "new" keyword declares an array, but does not create its elements
        // hence it is not a violation, even though it matches the regexp in the default config
        final Exception[] arrayOfExceptions = new Exception[1]; // no violation expected

        // ELIST may occur within array declaration, e.g., in size()
        final List<Exception> dummyList = Arrays.asList(arrayOfExceptions);
        final Exception[] arrayOfExceptions2 = new Exception[dummyList.size()]; // no violation expected

    }

    public void inputToTestCustomConfig() {

        // the test custom config prohibits constructors without parameters
        // if a class name matches "Clazz[1-9]"

        // the check can prohibit default parameterless constructors
        final Clazz1 o1 = new Clazz1(); // violation expected

        // the check can prohibit user-defined parameterless constructors
        final Clazz2 o2 = new Clazz2(); // violation expected

        // the check can ignore certain classes
        // no violation expected, configured via ignoredClassNameFormat in
        // ConstructorWithoutParamsCheckTest
        final Clazz4 o4 = new Clazz4();

        // the check allows empty String parameters
        final Clazz3 o3 = new Clazz3(""); // no violation expected

        // the check ignores the comments
        // Clazz3 o3 = new Clazz3();

        /* the check ignores the comments
         * Clazz3 o3 = new Clazz3();
         */

    }

    class Clazz1 {

    }

    class Clazz2 {
       public Clazz2(){

       }
    }

    class Clazz3 {

        public Clazz3(){

        }

        public Clazz3(final String str){

        }
    }

    class Clazz4 {

    }

    class MockExceptionHandler {

    }

    private static final Set<String> TEST = Collections.unmodifiableSortedSet(Stream.of("test")
            .collect(Collectors.toCollection(TreeSet::new)));
}
