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
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;

import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;

public class InputJsr305AnnotationsCheckWithEnum {

    @ParametersAreNonnullByDefault
    enum ParameterAnnotationsEnum {
        TEST;

        public void setUnannotated(final String foo) { // ok
        }

        public void setNullable(@Nullable final String foo) { // ok
        }

        public void setNonnull(@Nonnull final String foo) { // violation, redundant
        }

    }

    enum NoParameterAnnotationsEnum {
        TEST;

        public void setUnannotated(final String foo) { // violation, missing
        }

        public void setNullable(@Nullable final String foo) { // ok
        }

        public void setNonnull(@Nonnull final String foo) { // ok
        }

    }

    @ReturnValuesAreNonnullByDefault
    enum ReturnValueAnnotationEnum {
        TEST;
        @Nonnull // violation, redundant
        public String getNonnull() {
            return "";
        }

        @ReturnValuesAreNonnullByDefault // violation, disallowed
        public String getNonnnullByDefault() {
            return "";
        }

        @CheckForNull // ok
        public String getCheckForNull() {
            return "";
        }
    }

    @ParametersAreNonnullByDefault // violation
    @ParametersAreNullableByDefault
    enum BothAnnotations {
        TEST;
    }

}
