////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2016 the original author or authors.
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

package com.github.sevntu.checkstyle.internal;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

public class AllChecksTest {
    @Test
    public void testAllChecksAreReferencedInConfigFile() throws Exception {
        final Set<String> checksReferencedInConfig = CheckUtil.getConfigCheckStyleChecks();
        final Set<String> checksNames = getFullNames(CheckUtil.getCheckstyleChecks());

        checksNames.stream().filter(check -> !checksReferencedInConfig.contains(check))
            .forEach(check -> {
                final String errorMessage = String.format(Locale.ROOT,
                    "%s is not referenced in sevntu-checks.xml", check);
                Assert.fail(errorMessage);
            });
    }

    /**
     * Removes 'Check' suffix from each class name in the set.
     * @param checks class instances.
     * @return a set of simple names.
     */
    private static Set<String> getFullNames(Set<Class<?>> checks) {
        return checks.stream().map(check -> check.getName().replace("Check", ""))
            .collect(Collectors.toSet());
    }
}
