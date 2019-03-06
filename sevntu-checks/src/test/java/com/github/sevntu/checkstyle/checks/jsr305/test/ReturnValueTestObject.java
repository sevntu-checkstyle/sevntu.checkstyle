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
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;

public class ReturnValueTestObject
        implements Comparable<ReturnValueTestObject>, Serializable, Runnable {

    private static final long serialVersionUID = 1L;

    // ok
    @Nonnull
    Object returnNonnull() {
        return new Object();
    }

    // ok
    @CheckForNull
    Object returnCheckForNull() {
        return null;
    }

    // error
    @Nullable
    Object returnNullable() {
        return null;
    }

    // error
    @Nonnull
    @CheckForNull
    Object returnNonnullCheckForNull() {
        return new Object();
    }

    // ok
    @CheckReturnValue
    @CheckForNull
    Object returnCheckReturnValueCheckForNull() {
        return new Object();
    }

    // ok
    @CheckReturnValue
    @Nonnull
    Object returnCheckReturnValueNonnull() {
        return new Object();
    }

    // error
    @Override
    @Nonnull
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }

    // error
    @Override
    @CheckForNull
    public int hashCode() {
        return super.hashCode();
    }

    // ok
    @Override
    public void run() {
        hashCode();
    }

    // ok
    @Override
    @Nonnull
    public String toString() {
        return super.toString();
    }

    // error + 2x parameter error
    @ParametersAreNonnullByDefault
    @ParametersAreNullableByDefault
    @Nonnull
    public String concat(final String string1, final String string2) {
        return string1 + string2;
    }

    // error
    @Override
    @CheckReturnValue
    public int compareTo(@Nullable final ReturnValueTestObject obj) {
        return 0;
    }

    // error
    @CheckReturnValue
    public void voidNoCheckReturnValue() {
        hashCode();
    }

    // error
    @Nonnull
    public void voidNoNonnull() {
        hashCode();
    }

}
