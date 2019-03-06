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

package com.github.sevntu.checkstyle.checks.jsr305;

import org.junit.Test;

import com.github.sevntu.checkstyle.checks.jsr305.Jsr305AnnotionsTestUtil.ExpectedWarning;
import com.github.sevntu.checkstyle.checks.jsr305.test.ClassTestObject;
import com.github.sevntu.checkstyle.checks.jsr305.test.DefaultParameterTestObject;
import com.github.sevntu.checkstyle.checks.jsr305.test.EnumTestObject;
import com.github.sevntu.checkstyle.checks.jsr305.test.InheritanceTestObject;
import com.github.sevntu.checkstyle.checks.jsr305.test.LambdaTestObject;
import com.github.sevntu.checkstyle.checks.jsr305.test.ParameterTestObject;
import com.github.sevntu.checkstyle.checks.jsr305.test.PrimitivesTestObject;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;

public class Jsr305AnnotationsCheckTest {

    @Test
    public void test() throws CheckstyleException {
        Jsr305AnnotionsTestUtil.check(
                new ExpectedWarning(ParameterTestObject.class, 35, 44),
                new ExpectedWarning(ParameterTestObject.class, 40, 45),
                new ExpectedWarning(ParameterTestObject.class, 40, 64),
                new ExpectedWarning(ParameterTestObject.class, 73, 41),
                new ExpectedWarning(ParameterTestObject.class, 79, 27),
                new ExpectedWarning(ParameterTestObject.class, 97, 35),

                new ExpectedWarning(PrimitivesTestObject.class, 29, 37),
                new ExpectedWarning(PrimitivesTestObject.class, 34, 38),
                new ExpectedWarning(PrimitivesTestObject.class, 39, 42),
                new ExpectedWarning(PrimitivesTestObject.class, 44, 5),
                new ExpectedWarning(PrimitivesTestObject.class, 50, 5),
                new ExpectedWarning(PrimitivesTestObject.class, 56, 5),
                new ExpectedWarning(PrimitivesTestObject.class, 62, 5),
                new ExpectedWarning(PrimitivesTestObject.class, 69, 5),
                new ExpectedWarning(PrimitivesTestObject.class, 116, 34),

                new ExpectedWarning(ClassTestObject.class, 33, 5),
                new ExpectedWarning(ClassTestObject.class, 47, 26),
                new ExpectedWarning(ClassTestObject.class, 57, 29),
                new ExpectedWarning(ClassTestObject.class, 79, 32),
                new ExpectedWarning(ClassTestObject.class, 87, 36),
                new ExpectedWarning(ClassTestObject.class, 141, 43),
                new ExpectedWarning(ClassTestObject.class, 148, 9),
                new ExpectedWarning(ClassTestObject.class, 152, 9),
                new ExpectedWarning(ClassTestObject.class, 163, 9),
                new ExpectedWarning(ClassTestObject.class, 169, 9),
                new ExpectedWarning(ClassTestObject.class, 187, 9),

                new ExpectedWarning(EnumTestObject.class, 45, 32),
                new ExpectedWarning(EnumTestObject.class, 54, 36),
                new ExpectedWarning(EnumTestObject.class, 71, 9),
                new ExpectedWarning(EnumTestObject.class, 77, 9),
                new ExpectedWarning(EnumTestObject.class, 90, 5),

                new ExpectedWarning(InheritanceTestObject.class, 47, 47),
                new ExpectedWarning(InheritanceTestObject.class, 76, 43),

                new ExpectedWarning(DefaultParameterTestObject.class, 37, 5),

                new ExpectedWarning(LambdaTestObject.class, 35, 10)

        );
    }
}
