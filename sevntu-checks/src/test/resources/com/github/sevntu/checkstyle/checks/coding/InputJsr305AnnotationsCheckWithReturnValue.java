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
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;

public class InputJsr305AnnotationsCheckWithReturnValue
        implements Comparable<InputJsr305AnnotationsCheckWithReturnValue>, Serializable, Runnable {

    private static final long serialVersionUID = 1L;

    @Nonnull // ok
    Object returnNonnull() {
        return new Object();
    }

    @CheckForNull // ok
    Object returnCheckForNull() {
        return null;
    }

    @Nullable // violation
    Object returnNullable() {
        return null;
    }

    @Nonnull // violation
    @CheckForNull
    Object returnNonnullCheckForNull() {
        return new Object();
    }

    @CheckReturnValue // ok
    @CheckForNull
    Object returnCheckReturnValueCheckForNull() {
        return new Object();
    }

    @CheckReturnValue // ok
    @Nonnull
    Object returnCheckReturnValueNonnull() {
        return new Object();
    }

    @Override // violation
    @Nonnull
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }

    @Override // violation
    @CheckForNull
    public int hashCode() {
        return super.hashCode();
    }

    @Override // ok
    public void run() {
        hashCode();
    }

    @Override // ok
    @Nonnull
    public String toString() {
        return super.toString();
    }

    @ParametersAreNonnullByDefault // violation
    @ParametersAreNullableByDefault
    @Nonnull
    public String concat(final String string1, final String string2) { // violation
        return string1 + string2;
    }

    @Override // violation
    @CheckReturnValue
    public int compareTo(@Nullable final InputJsr305AnnotationsCheckWithReturnValue obj) {
        return 0;
    }

    @CheckReturnValue // violation
    public void voidNoCheckReturnValue() {
        hashCode();
    }

    @Nonnull // violation
    public void voidNoNonnull() {
        hashCode();
    }

}
