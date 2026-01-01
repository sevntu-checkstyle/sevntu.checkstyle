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

package com.github.sevntu.checkstyle.checks.coding;

import java.io.Serializable;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InputJsr305AnnotationsCheckWithArrays {

    private final Object obj;

    InputJsr305AnnotationsCheckWithArrays(@Nonnull final int[] nonnull) { // ok
        obj = nonnull;
    }

    InputJsr305AnnotationsCheckWithArrays(@Nullable final long[] nullable) { // ok
        obj = nullable;
    }

    InputJsr305AnnotationsCheckWithArrays(final short[] array) { // violation
        obj = array;
    }

    InputJsr305AnnotationsCheckWithArrays(@Nonnull final byte... varargs) { // ok
        obj = varargs;
    }

    InputJsr305AnnotationsCheckWithArrays(@Nullable final float... varargs) { // ok
        obj = varargs;
    }

    InputJsr305AnnotationsCheckWithArrays(final double... varargs) { // violation
        obj = varargs;
    }

    InputJsr305AnnotationsCheckWithArrays(@Nonnull final String... varargs) { // ok
        obj = varargs;
    }

    InputJsr305AnnotationsCheckWithArrays(@Nullable final Object... varargs) { // ok
        obj = varargs;
    }

    InputJsr305AnnotationsCheckWithArrays(final Serializable... varargs) { // violation
        obj = varargs;
    }

    InputJsr305AnnotationsCheckWithArrays(@Nonnull final Class<?>[] clazz) { // ok
        obj = clazz;
    }

    InputJsr305AnnotationsCheckWithArrays(@Nullable final Iterable<?>[] iterable) { // ok
        obj = iterable;
    }

    InputJsr305AnnotationsCheckWithArrays(final Comparable<?> comparable) { // violation
        obj = comparable;
    }

    @Nonnull
    int[] retNonnul() { // ok
        return new int[] {};
    }

    @CheckForNull
    int[] retCheckForNull() { // ok
        return null;
    }

    int[] retNoAnnotation() { // violation
        return new int[] {};
    }

    void testNonnullArray(@Nonnull final int[] array) { // ok
        array[1] = 0;
    }

    void testNullableArray(@Nullable final int[] array) { // ok
        array[1] = 0;
    }

    void testNoAnnotationArray(final int[] array) { // violation
        array[1] = 0;
    }

    void testNonnullVarargs(@Nonnull final int... varargs) { // ok
        varargs[1] = 0;
    }

    void testNullableVarargs(@Nullable final int... varargs) { // ok
        varargs[1] = 0;
    }

    void testNoAnnotationVarargs(final int... varargs) { // violation
        varargs[1] = 0;
    }

    @Override
    public String toString() { // ok
        return String.valueOf(obj);
    }
}
