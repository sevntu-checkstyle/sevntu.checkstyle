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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InputJsr305AnnotationsCheckWithPrimitives {

    public boolean nonnullParameter(@Nonnull final int param) { // violation
        return param == 0;
    }

    public boolean nullableParameter(@Nullable final int param) { // violation
        return param == 0;
    }

    public boolean checkForNullParameter(@CheckForNull final int param) { // violation
        return param == 0;
    }

    @Nonnull // violation
    public int returnNonnull() {
        return 0;
    }

    @CheckForNull // violation
    public int returnCheckForNull() {
        return 0;
    }

    @Nullable // violation
    public int returnNullable() {
        return 0;
    }

    @Override // violation
    @Nonnull
    public boolean equals(Object object) {
        return super.equals(object);
    }

    @Override // violation
    @Nonnull
    public int hashCode() {
        return super.hashCode();
    }

    public int primitivesInt(final int primitive) { // ok
        return primitive + 1;
    }

    public byte primitivesByte(final byte primitive) { // ok
        return primitive;
    }

    public boolean primitivesBoolean(final boolean primitive) { // ok
        return !primitive;
    }

    public float primitivesFloat(final float primitive) { // ok
        return primitive + 1.0F;
    }

    public double primitivesDouble(final double primitive) { // ok
        return primitive + 1.0D;
    }

    public long primitivesLong(final long primitive) { // ok
        return primitive + 1L;
    }

    public short primitivesShort(final short primitive) { // ok
        return primitive;
    }

    public char primitivesChar(final char primitive) { // ok
        return primitive;
    }

    public int primitivesNonnull(@Nonnull final int primitive) { // violation
        return primitive + 1;
    }

}
