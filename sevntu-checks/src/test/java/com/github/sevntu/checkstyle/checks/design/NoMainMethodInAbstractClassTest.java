////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2011  Oliver Burn
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

package com.github.sevntu.checkstyle.checks.design;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.design.NoMainMethodInAbstractClass;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.Test;

/**
 * Test class for NoMainMethodInAbstractClass check.
 * @author Vadym Chekrii
 *
 */
public class NoMainMethodInAbstractClassTest extends BaseCheckTestSupport
{

    @Test
    public void testDefault() throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(NoMainMethodInAbstractClass.class);

        final String[] expected = {
            "2: Avoid to have main() method in an abstract class.",
            "5: Avoid to have main() method in an abstract class.",
            "26: Avoid to have main() method in an abstract class.",
            "37: Avoid to have main() method in an abstract class.",
            "45: Avoid to have main() method in an abstract class.",
            "58: Avoid to have main() method in an abstract class.",
            "81: Avoid to have main() method in an abstract class."
        };
        verify(checkConfig, getPath("InputAvoidMainMethodInAbstractClass.java")
                , expected);
    }
}