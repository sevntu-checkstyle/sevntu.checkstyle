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
 * {@link com.puppycrawl.tools.checkstyle.checks.naming.UniformEnumConstantNameCheck} unit
 * test.
 *
 * @author Pavel Baranchikov
 */
public class InputUniformEnumConstantNameCheck
{
    /**
     * Simple enum. It has neither fiels nor methods.
     */
    enum SimpleProperEnum
    {
        FirstSimple,
        DB2,
        SECOND_SIMPLE, // << failure
        V1;
    }

    /**
     * Complex enum, having both fields and methods.
     */
    enum ComplexProperEnum
    {
        FITST_COMPLEX(1),
        V2(2),
        SecondComplex(3); // << failure

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

    /**
     * Enum with upper case values.
     */
    enum UpperCaseEnum
    {
        UC_FIRST, UC_SECOND, DB2; // Ok
    }

    /**
     * Enum with camel case values.
     */
    enum CamelCaseEnum
    {
        CcFirst, CcSecond, DB2; // Ok
    }

    /**
     * Enum with wrong patterns in all the values.
     */
    enum WrongFormatterEnum
    {
        WF_First, // failure - wrong pattern
        WF_Second;
    }
}
