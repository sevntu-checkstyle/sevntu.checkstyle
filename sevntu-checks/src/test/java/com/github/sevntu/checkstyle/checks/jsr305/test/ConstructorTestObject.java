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

import javax.annotation.CheckForNull;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;

public class ConstructorTestObject {

    @Nonnull
    private final Object nonnull;

    @Nullable
    private final Object nullable;

    // error
    public ConstructorTestObject(@Nonnull @Nullable final Enum<?> value) {
        nullable = value;
        nonnull = new Object();
    }

    // error
    public ConstructorTestObject(final Class<?> clz) {
        nonnull = new Object();
        nullable = clz;
    }

    // ok
    @ParametersAreNonnullByDefault
    public ConstructorTestObject(final String string1, @Nullable final String string2) {
        nonnull = string1;
        nullable = string2;
    }

    // error
    @ParametersAreNonnullByDefault
    public ConstructorTestObject(@Nonnull final String string1, final Integer string2) {
        nonnull = string1;
        nullable = string2;
    }

    // ok
    @ParametersAreNullableByDefault
    public ConstructorTestObject(@Nonnull final Integer int1, final Integer int2) {
        nonnull = int1;
        nullable = int2;
    }

    // error
    @ParametersAreNullableByDefault
    public ConstructorTestObject(final Integer int1, @Nullable final String int2) {
        if (int1 != null) {
            nonnull = int1;
        }
        else {
            nonnull = Integer.valueOf(1);
        }
        nullable = int2;
    }

    // ok
    public ConstructorTestObject(@Nullable final String obj) {
        nonnull = new Object();
        nullable = obj;
    }

    // ok
    public ConstructorTestObject(@Nonnull final Integer obj) {
        nonnull = obj;
        nullable = null;
    }

    // ok
    @CheckForNull
    public Object getNullable() {
        return nullable;
    }

    // ok
    @CheckReturnValue
    @Nonnull
    public Object getNonnull() {
        return nonnull;
    }

}
