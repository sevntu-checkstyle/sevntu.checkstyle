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
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;

import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;

public class ClassTestObject {

    // error, double annotation
    @ParametersAreNonnullByDefault
    @ParametersAreNullableByDefault
    class BothParameterAnnotationsClass {

    }

    // no error
    @ParametersAreNullableByDefault
    interface ParameterAnnotationsInterface {

        // no error
        void setUnannotated(String unannotated);

        // error, redundant
        void setNullable(@Nullable String nullable);

        // no error
        void setNonnull(@Nonnull String nonnull);

    }

    interface NoParameterAnnotationsInterface {

        // error, missing
        void setUnannotated(String unannotated);

        // no error
        void setNullable(@Nonnull String nullable);

        // no error
        void setNonnull(@Nullable String nonnull);

    }

    @ParametersAreNonnullByDefault
    class ParameterAnnotationsClass {

        // no error
        public void setUnannotated(final String unannotated) {
        }

        // no error
        public void setNullable(@Nullable final String nullable) {
        }

        // error, redundant
        public void setNonnull(@Nonnull final String nonnull) {
        }

    }

    class NoParameterAnnotationsClass {

        // error, missing
        public void setUnannotated(final String unannotated) {
        }

        // no error
        public void setNullable(@Nullable final String nullable) {
        }

        // no error
        public void setNonnull(@Nonnull final String nonnull) {
        }

    }

    @ParametersAreNonnullByDefault
    static class DefaultNullableParameterInheritingClass implements ParameterAnnotationsInterface {

        @Override
        // no error
        public void setUnannotated(final String unannotated) {
        }

        @Override
        // no error
        public void setNullable(final String nullable) {
        }

        @Override
        // no error
        public void setNonnull(final String nonnull) {
        }

        // no error
        public void setAnotherUnannotated(final String unannotated) {
        }
    }

    static class NullableParameterInheritingClass implements ParameterAnnotationsInterface {

        @Override
        // no error
        public void setUnannotated(final String unannotated) {
        }

        @Override
        // no error
        public void setNullable(final String nullable) {
        }

        @Override
        // no error
        public void setNonnull(final String nonnull) {
        }

        // error
        public void setAnotherUnannotated(final String unannotated) {
        }
    }

    @ReturnValuesAreNonnullByDefault
    interface ReturnValueAnnotationInterface {
        // error, redundant
        @Nonnull
        String getNonnull();

        // error, disallowed
        @ReturnValuesAreNonnullByDefault
        String getNonnullByDefault();

        // no error
        @CheckForNull
        String getCheckForNull();
    }

    @ReturnValuesAreNonnullByDefault
    static class ReturnValueAnnotationClass {
        // error, redundant
        @Nonnull
        public String getNonnull() {
            return "";
        }

        // error, disallowed
        @ReturnValuesAreNonnullByDefault
        public String getNonnullByDefault() {
            return "";
        }

        // no error
        @CheckForNull
        public String getCheckForNull() {
            return "";
        }
    }

    interface NoReturnValueAnnotationInterface {
        // no error
        @Nonnull
        String getNonnull();

        // error, disallowed
        @ReturnValuesAreNonnullByDefault
        String getNonnullByDefault();

        // no error
        @CheckForNull
        String getCheckForNull();
    }

}
