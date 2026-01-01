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
package com.github.sevntu.checkstyle.checks.naming;

/**
 * Test case for
 * {@link com.puppycrawl.tools.checkstyle.checks.naming.EnumValueNameTest} unit
 * test.
 *
 * @author Pavel Baranchikov
 */
public class InputEnumValueNameCheck
{
    /**
     * Simple enum. It has neither fiels nor methods.
     */
    enum SimpleProperEnum
    {
        FirstSimple, SECOND_SIMPLE, DB2, V1;
    }

    /**
     * Complex enum, having both fields and methods.
     */
    enum ComplexProperEnum
    {
        FirstComplex(1), SECOND_COMPLEX(2), V2(3);

        private final int intValue;

        private ComplexProperEnum(int intValue)
        {
            this.intValue = intValue;
        }

        public int getIntValue()
        {
            return intValue;
        }

        public int getLongValue()
        {
            return intValue;
        }

    }

    enum MethodOnlyEnum
    {
        MO_FIRST, MoSecond;

        public String toString()
        {
            return name();
        }
    }

    enum FieldOnlyEnum
    {
        FO_FIRST, FoSecond;

        private final int someField = 0;
    }
}
