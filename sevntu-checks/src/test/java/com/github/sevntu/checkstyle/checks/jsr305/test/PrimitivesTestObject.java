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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PrimitivesTestObject {

    // error
    public boolean nonnullParameter(@Nonnull final int param) {
        return param == 0;
    }

    // error
    public boolean nullableParameter(@Nullable final int param) {
        return param == 0;
    }

    // error
    public boolean checkForNullParameter(@CheckForNull final int param) {
        return param == 0;
    }

    // error
    @Nonnull
    public int returnNonnull() {
        return 0;
    }

    // error
    @CheckForNull
    public int returnCheckForNull() {
        return 0;
    }

    // error
    @Nullable
    public int returnNullable() {
        return 0;
    }

    // error
    @Override
    @Nonnull
    public boolean equals(Object object) {
        return super.equals(object);
    }

    // error
    @Override
    @Nonnull
    public int hashCode() {
        return super.hashCode();
    }

    // ok
    public int primitivesInt(final int primitive) {
        return primitive + 1;
    }

    // ok
    public byte primitivesByte(final byte primitive) {
        return primitive;
    }

    // ok
    public boolean primitivesBoolean(final boolean primitive) {
        return !primitive;
    }

    // ok
    public float primitivesFloat(final float primitive) {
        return primitive + 1.0F;
    }

    // ok
    public double primitivesDouble(final double primitive) {
        return primitive + 1.0D;
    }

    // ok
    public long primitivesLong(final long primitive) {
        return primitive + 1L;
    }

    // ok
    public short primitivesShort(final short primitive) {
        return primitive;
    }

    // ok
    public char primitivesChar(final char primitive) {
        return primitive;
    }

    // error
    public int primitivesNonnull(@Nonnull final int primitive) {
        return primitive + 1;
    }

}
