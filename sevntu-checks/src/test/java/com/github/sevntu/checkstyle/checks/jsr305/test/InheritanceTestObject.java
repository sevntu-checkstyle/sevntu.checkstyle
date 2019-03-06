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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;

import com.github.sevntu.checkstyle.checks.jsr305.test.InheritanceTestObject.Inherited;

@ParametersAreNullableByDefault
public class InheritanceTestObject {

    // anonymous classes can't be annotated, but this should inherit the
    // annotations from the superclass
    public void testAnonClasses() {
        final Inherited anonymousClassInstance = new Inherited() {

            // no error
            @Override
            public void getUnannotated(final String unannotated) {
            }

            // no error
            @Override
            public void getNullable(final String nullable) {
            }

            // error
            public void getAnotherUnannotated(final String getAnotherUnannotated) {
            }
        };
    }

    @ParametersAreNonnullByDefault
    interface Inherited {

        // no error
        void getUnannotated(String unannotated);

        // no error
        void getNullable(@Nullable String nullable);
    }

    // no error
    static class UnannotatedInheritor implements Inherited {

        // no error, since we're overriding @NonnullByDefault from the interface
        @Override
        public void getUnannotated(@Nullable final String unannotated) {
        }

        // no error
        @Override
        public void getNullable(final String nullable) {
        }

        // error
        public void getAnotherUnannotated(final String unannotated) {
        }

    }

    // no error
    @ParametersAreNullableByDefault
    static class AnnotatedInheritor implements Inherited {

        // this should not be an error, we are inheriting via the @Override
        // annotation
        @Override
        public void getUnannotated(final String unannotated) {
        }

        // no error
        @Override
        public void getNullable(final String nullable) {
        }

        // no error
        public void getAnotherUnannotated(final String unannotated) {
        }

    }

}
