////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2013  Oliver Burn
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
package com.puppycrawl.tools.checkstyle.checks.naming;

/**
 * Test case for
 * {@link com.puppycrawl.tools.checkstyle.checks.naming.EnumValueNameTest} unit
 * test.
 * @author Pavel Baranchikov
 */
public class InputEnumNamingCheck
{
    /**
     * Cammel notation simple enums.
     */
    enum SimpleProperEnum
    {
        FirstSimple, SecondSimple, ThirdSimple;
    }

    /**
     * Cammel notation complex enums.
     */
    enum ComplexProperEnum
    {
        FirstComplex(1), SecondComplex(2), ThirdComplex(3);

        private final int intValue;

        private ComplexProperEnum(int intValue)
        {
            this.intValue = intValue;
        }

        public int getIntValue()
        {
            return intValue;
        }
    }

    /**
     * uppercase notation simple enums.
     */
    enum SimpleErrorEnum
    {
        FIRST_SIMPLE, SECOND_SIMPLE, THIRD_SIMPLE;
    }

    /**
     * uppercase notation complex enums.
     */
    enum ComplexErrorEnum
    {
        FIRST_COMPLEX(1), SECOND_COMPLEX(2), THIRD_COMPLEX(3);

        private final int intValue;

        private ComplexErrorEnum(int intValue)
        {
            this.intValue = intValue;
        }

        public int getIntValue()
        {
            return intValue;
        }
    }

}
