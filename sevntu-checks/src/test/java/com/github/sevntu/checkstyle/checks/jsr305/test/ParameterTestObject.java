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

public class ParameterTestObject
        implements Comparable<ParameterTestObject>, Serializable {

    private static final long serialVersionUID = 1L;

    // error
    @Nonnull
    public String missingNullnessParameter(final Object obj) {
        return obj.toString();
    }

    // 2x error
    public boolean missingNullnessParameter(final Object obj1, final Object obj2) {
        return obj1.equals(obj2);
    }

    // ok
    @Nonnull
    public String nonnullParameter(@Nonnull final Object obj) {
        return obj.toString();
    }

    // ok
    public boolean nonnullParameter(@Nonnull final Object obj1,
            @Nonnull final Object obj2) {
        return obj1.equals(obj2);
    }

    // ok
    @Nonnull
    public String nullableParameter(@Nullable final Object obj) {
        return String.valueOf(obj);
    }

    // ok
    public boolean nullableParameter(@Nullable final Object obj1,
            @Nullable final Object obj2) {
        if (obj1 != null) {
            System.out.println("bonk");
        }
        return obj2 == null;
    }

    // error
    @Nonnull
    public String checkForNullParameter(@CheckForNull final Object obj) {
        return String.valueOf(obj);
    }

    // error
    @Override
    public boolean equals(@Nonnull final Object obj) {
        return super.equals(obj);
    }

    // ok
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    // ok
    @Override
    public int compareTo(@Nullable final ParameterTestObject obj) {
        return 0;
    }

    // error
    @Nonnull
    public String nonnullNullable(@Nonnull @Nullable final Object obj) {
        return String.valueOf(obj);
    }

}
