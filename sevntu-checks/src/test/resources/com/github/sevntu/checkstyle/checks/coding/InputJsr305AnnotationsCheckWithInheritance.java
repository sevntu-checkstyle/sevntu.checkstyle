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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;

@ParametersAreNullableByDefault
public class InputJsr305AnnotationsCheckWithInheritance {

    // anonymous classes can't be annotated, but this should inherit the
    // annotations from the superclass
    public void testAnonClasses() {
        final Inherited anonymousClassInstance = new Inherited() {

            @Override
            public void getUnannotated(final String unannotated) {
            }

            @Override
            public void getNullable(final String nullable) {
            }

            public void getAnotherUnannotated(final String getAnotherUnannotated) { // violation
            }

            @Override
            public Object nonnull() {
                return "";
            }
        };
    }

    @ParametersAreNonnullByDefault
    interface Inherited {

        void getUnannotated(String unannotated); // ok

        void getNullable(@Nullable String nullable); // ok

        @Nonnull
        Object nonnull();
    }

    // ok
    static class UnannotatedInheritor implements Inherited {

        @Override // ok, since we're overriding @NonnullByDefault from the interface
        public void getUnannotated(@Nullable final String unannotated) {
        }

        @Override // ok
        public void getNullable(final String nullable) {
        }

        public void getAnotherUnannotated(final String unannotated) { // violation
        }

        @Override
        public Object nonnull() {
            return "";
        }

    }

    @ParametersAreNullableByDefault // ok
    static class AnnotatedInheritor implements Inherited {

        // this should not be an violation, we are inheriting via the @Override
        // annotation
        @Override
        public void getUnannotated(final String unannotated) {
        }

        @Override // ok
        public void getNullable(final String nullable) {
        }

        public void getAnotherUnannotated(final String unannotated) { // ok
        }

        @Override
        @Nonnull // ok
        public Object nonnull() {
            return "";
        }
    }

}
