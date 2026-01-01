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

import javax.annotation.CheckForNull;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;

public class InputJsr305AnnotationsCheckWithConstructor {

    @Nonnull
    private final Object nonnull;

    @Nullable
    private final Object nullable;

    public InputJsr305AnnotationsCheckWithConstructor(@Nonnull @Nullable final Enum<?> value) { // violation
        nullable = value;
        nonnull = new Object();
    }

    public InputJsr305AnnotationsCheckWithConstructor(final Class<?> clz) { // violation
        nonnull = new Object();
        nullable = clz;
    }

    @ParametersAreNonnullByDefault
    public InputJsr305AnnotationsCheckWithConstructor(final String string1, @Nullable final String string2) { // ok
        nonnull = string1;
        nullable = string2;
    }

    @ParametersAreNonnullByDefault
    public InputJsr305AnnotationsCheckWithConstructor(@Nonnull final String string1, final Integer string2) { // violation
        nonnull = string1;
        nullable = string2;
    }

    @ParametersAreNullableByDefault
    public InputJsr305AnnotationsCheckWithConstructor(@Nonnull final Integer int1, final Integer int2) { // ok
        nonnull = int1;
        nullable = int2;
    }

    @ParametersAreNullableByDefault
    public InputJsr305AnnotationsCheckWithConstructor(final Integer int1, @Nullable final String int2) { // violation
        if (int1 != null) {
            nonnull = int1;
        }
        else {
            nonnull = Integer.valueOf(1);
        }
        nullable = int2;
    }

    public InputJsr305AnnotationsCheckWithConstructor(@Nullable final String obj) { // ok
        nonnull = new Object();
        nullable = obj;
    }

    public InputJsr305AnnotationsCheckWithConstructor(@Nonnull final Integer obj) { // ok
        nonnull = obj;
        nullable = null;
    }

    @CheckForNull
    public Object getNullable() { // ok
        return nullable;
    }

    @CheckReturnValue
    @Nonnull
    public Object getNonnull() { // ok
        return nonnull;
    }

}
