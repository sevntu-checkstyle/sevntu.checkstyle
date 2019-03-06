////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2019 the original author or authors.
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

package com.github.sevntu.checkstyle.checks.jsr305.test;

import java.io.Serializable;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ArraysTestObject {

    private final Object obj;

    // ok
    ArraysTestObject(@Nonnull final int[] nonnull) {
        obj = nonnull;
    }

    // ok
    ArraysTestObject(@Nullable final long[] nullable) {
        obj = nullable;
    }

    // error
    ArraysTestObject(final short[] array) {
        obj = array;
    }

    // ok
    ArraysTestObject(@Nonnull final byte... varargs) {
        obj = varargs;
    }

    // ok
    ArraysTestObject(@Nullable final float... varargs) {
        obj = varargs;
    }

    // error
    ArraysTestObject(final double... varargs) {
        obj = varargs;
    }

    // ok
    ArraysTestObject(@Nonnull final String... varargs) {
        obj = varargs;
    }

    // ok
    ArraysTestObject(@Nullable final Object... varargs) {
        obj = varargs;
    }

    // error
    ArraysTestObject(final Serializable... varargs) {
        obj = varargs;
    }

    // ok
    ArraysTestObject(@Nonnull final Class<?>[] clazz) {
        obj = clazz;
    }

    // ok
    ArraysTestObject(@Nullable final Iterable<?>[] iterable) {
        obj = iterable;
    }

    // error
    ArraysTestObject(final Comparable<?> comparable) {
        obj = comparable;
    }

    @Nonnull
    int[] retNonnul() {
        return new int[] {};
    }

    @CheckForNull
    int[] retCheckForNull() {
        return null;
    }

    int[] retNoAnnotation() {
        return new int[] {};
    }

    // ok
    void testNonnullArray(@Nonnull final int[] array) {
        array[1] = 0;
    }

    // ok
    void testNullableArray(@Nullable final int[] array) {
        array[1] = 0;
    }

    // error
    void testNoAnnotationArray(final int[] array) {
        array[1] = 0;
    }

    // ok
    void testNonnullVarargs(@Nonnull final int... varargs) {
        varargs[1] = 0;
    }

    // ok
    void testNullableVarargs(@Nullable final int... varargs) {
        varargs[1] = 0;
    }

    // error
    void testNoAnnotationVarargs(final int... varargs) {
        varargs[1] = 0;
    }

    @Override
    public String toString() {
        return String.valueOf(obj);
    }
}
