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

public class InputJsr305AnnotationsCheckWithClass {

    @ParametersAreNonnullByDefault // violation, double annotation
    @ParametersAreNullableByDefault
    class BothParameterAnnotationsClass {

    }

    @ParametersAreNullableByDefault // ok
    interface ParameterAnnotationsInterface {

        void setUnannotated(String unannotated); // ok

        void setNullable(@Nullable String nullable); // violation, redundant

        void setNonnull(@Nonnull String nonnull); // ok

    }

    interface NoParameterAnnotationsInterface {

        void setUnannotated(String unannotated); // violation, missing

        void setNullable(@Nonnull String nullable); // ok

        void setNonnull(@Nullable String nonnull); // ok

    }

    @ParametersAreNonnullByDefault
    class ParameterAnnotationsClass {

        public void setUnannotated(final String unannotated) { // ok
        }

        public void setNullable(@Nullable final String nullable) { // ok
        }

        public void setNonnull(@Nonnull final String nonnull) { // violation, redundant
        }

    }

    class NoParameterAnnotationsClass {

        public void setUnannotated(final String unannotated) { // violation, missing
        }

        public void setNullable(@Nullable final String nullable) { // ok
        }

        public void setNonnull(@Nonnull final String nonnull) { // ok
        }

    }

    @ParametersAreNonnullByDefault
    static class DefaultNullableParameterInheritingClass implements ParameterAnnotationsInterface {

        @Override
        public void setUnannotated(final String unannotated) { // ok
        }

        @Override
        public void setNullable(final String nullable) { // ok
        }

        @Override
        public void setNonnull(final String nonnull) { // ok
        }

        public void setAnotherUnannotated(final String unannotated) { // ok
        }
    }

    static class NullableParameterInheritingClass implements ParameterAnnotationsInterface {

        @Override
        public void setUnannotated(final String unannotated) { // ok
        }

        @Override
        public void setNullable(final String nullable) { // ok
        }

        @Override
        public void setNonnull(final String nonnull) { // ok
        }

        public void setAnotherUnannotated(final String unannotated) { // violation
        }
    }

    @ReturnValuesAreNonnullByDefault
    interface ReturnValueAnnotationInterface {
        @Nonnull // violation, redundant
        String getNonnull();

        @ReturnValuesAreNonnullByDefault // violation, disallowed
        String getNonnullByDefault();

        @CheckForNull // ok
        String getCheckForNull();
    }

    @ReturnValuesAreNonnullByDefault
    static class ReturnValueAnnotationClass {
        @Nonnull // violation, redundant
        public String getNonnull() {
            return "";
        }

        @ReturnValuesAreNonnullByDefault // violation, disallowed
        public String getNonnullByDefault() {
            return "";
        }

        @CheckForNull // ok
        public String getCheckForNull() {
            return "";
        }
    }

    interface NoReturnValueAnnotationInterface {
        @Nonnull // ok
        String getNonnull();

        @ReturnValuesAreNonnullByDefault // violation, disallowed
        String getNonnullByDefault();

        @CheckForNull // ok
        String getCheckForNull();
    }

}
