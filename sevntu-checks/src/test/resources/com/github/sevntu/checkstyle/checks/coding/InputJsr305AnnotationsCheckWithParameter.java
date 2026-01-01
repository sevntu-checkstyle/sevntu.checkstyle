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
import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InputJsr305AnnotationsCheckWithParameter
        implements Comparable<InputJsr305AnnotationsCheckWithParameter>, Serializable {

    private static final long serialVersionUID = 1L;

    @Nonnull
    public String missingNullnessParameter(final Object obj) { // violation
        return obj.toString();
    }

    public boolean missingNullnessParameter(final Object obj1, final Object obj2) { // 2x violation
        return obj1.equals(obj2);
    }

    @Nonnull
    public String nonnullParameter(@Nonnull final Object obj) { // ok
        return obj.toString();
    }

    public boolean nonnullParameter(@Nonnull final Object obj1,
            @Nonnull final Object obj2) { // ok
        return obj1.equals(obj2);
    }

    @Nonnull
    public String nullableParameter(@Nullable final Object obj) { // ok
        return String.valueOf(obj);
    }

    public boolean nullableParameter(@Nullable final Object obj1,
            @Nullable final Object obj2) { // ok
        if (obj1 != null) {
            System.out.println("bonk");
        }
        return obj2 == null;
    }

    @Nonnull
    public String checkForNullParameter(@CheckForNull final Object obj) { // violation
        return String.valueOf(obj);
    }

    @Override
    public boolean equals(@Nonnull final Object obj) { // violation
        return super.equals(obj);
    }

    @Override
    public int hashCode() { // ok
        return super.hashCode();
    }

    @Override
    public int compareTo(@Nullable final InputJsr305AnnotationsCheckWithParameter obj) { // ok
        return 0;
    }

    @Nonnull
    public String nonnullNullable(@Nonnull @Nullable final Object obj) { // violation
        return String.valueOf(obj);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public List<String> typeCast() { // ok
        final Object list = new ArrayList<>();
        final List<String> result = (List<String>) list;
        return result;
    }

}
