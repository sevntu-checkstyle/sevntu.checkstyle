////////////////////////////////////////////////////////////////////////////////
//checkstyle: Checks Java source code for adherence to a set of rules.
//Copyright (C) 2001-2012  Oliver Burn
//
//This library is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public
//License as published by the Free Software Foundation; either
//version 2.1 of the License, or (at your option) any later version.
//
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public
//License along with this library; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.checks.design;

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * Test class for ForbidWildcardInReturnTypeCheck.
 * @author <a href='mailto:barataliba@gmail.com'>Baratali Izmailov</a>
 */
public class ForbidWildcardAsReturnTypeCheckTest extends BaseCheckTestSupport
{
    /**
     * Name of file with messages.
     */
    public static final String PROP_FILE_NAME = "messages.properties";
    /**
     * Message for this check.
     */
    private static final String MESSAGE =
            getMessage(ForbidWildcardAsReturnTypeCheck.MSG_KEY);
    /**
     * Line numbers with methods which have wildcard in return type.
     */
    private static final SortedSet<Integer> LINES = new TreeSet<Integer>();
    /**
     * Line numbers with public methods which have wildcard in return type.
     */
    private static final SortedSet<Integer> LINES_WITH_PUBLIC_METHODS =
            newSetOfLines(new Integer[] { 9, 29, 54, 59, 64, 104, 109, 114,
                119, 124, 129, 134, });
    /**
     * Line numbers with private methods which have wildcard in return type.
     */
    private static final SortedSet<Integer> LINES_WITH_PRIVATE_METHODS =
            newSetOfLines(new Integer[] { 24, 44, 49, });
    /**
     * Line numbers with protected methods which have wildcard in return type.
     */
    private static final SortedSet<Integer> LINES_WITH_PROTECTED_METHODS =
            newSetOfLines(new Integer[] { 19, 39, });
    /**
     * Line numbers with package methods which have wildcard in return type.
     */
    private static final SortedSet<Integer> LINES_WITH_PACKAGE_METHODS =
            newSetOfLines(new Integer[] { 14, 34, 147, 152, 157, 162, 164, 169,
                174, 179, 189, 202, 207, 214, 218, 221, });
    /**
     * Line numbers with methods which have upper bounded wildcard in return
     * type.
     */
    private static final SortedSet<Integer> LINES_WITH_EXTENDS =
            newSetOfLines(new Integer[] { 29, 34, 39, 44, 49, 54, 104, 114,
                129, 134, 164, 169, 174, 179, 189, 202, 214, 218, });
    /**
     * Line numbers with methods which have lower bounded wildcard in return
     * type.
     */
    private static final SortedSet<Integer> LINES_WITH_SUPER =
            newSetOfLines(new Integer[] { 9, 14, 19, 24, 59, 119, 189, 207, });
    /**
     * Ignore list for class names. Regexp.
     */
    private static final String IGNORE_LIST_PATTERN = "(Compar.+)|Collection";

    /**
     * Initialize value of LINES.
     */
    public ForbidWildcardAsReturnTypeCheckTest()
    {
        LINES.addAll(LINES_WITH_PACKAGE_METHODS);
        LINES.addAll(LINES_WITH_PRIVATE_METHODS);
        LINES.addAll(LINES_WITH_PROTECTED_METHODS);
        LINES.addAll(LINES_WITH_PUBLIC_METHODS);
    }

    /**
     * Main test.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public final void testDefault()
        throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(ForbidWildcardAsReturnTypeCheck.class);
        checkConfig.addAttribute("checkPublicMethods", "true");
        checkConfig.addAttribute("checkPrivateMethods", "true");
        checkConfig.addAttribute("checkProtectedMethods", "true");
        checkConfig.addAttribute("checkPackageMethods", "true");
        checkConfig.addAttribute("allowReturnWildcardWithSuper", "false");
        checkConfig.addAttribute("allowReturnWildcardWithExtends", "false");
        checkConfig.addAttribute("classNamesIgnoreList", "");
        final String[] expected = createExpectedMessages(LINES);

        verify(checkConfig,
                getPath("InputForbidWildcardAsReturnType.java"),
                expected);
    }

    /**
     * Test only public methods.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public final void testOnlyPublicMethods()
        throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(ForbidWildcardAsReturnTypeCheck.class);
        checkConfig.addAttribute("checkPublicMethods", "true");
        checkConfig.addAttribute("checkPrivateMethods", "false");
        checkConfig.addAttribute("checkProtectedMethods", "false");
        checkConfig.addAttribute("checkPackageMethods", "false");
        checkConfig.addAttribute("allowReturnWildcardWithSuper", "false");
        checkConfig.addAttribute("allowReturnWildcardWithExtends", "false");
        checkConfig.addAttribute("classNamesIgnoreList", "");
        final String[] expected =
                createExpectedMessages(LINES_WITH_PUBLIC_METHODS);

        verify(checkConfig,
                getPath("InputForbidWildcardAsReturnType.java"),
                expected);
    }

    /**
     * Test only private methods.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public final void testOnlyPrivateMethods()
        throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(ForbidWildcardAsReturnTypeCheck.class);
        checkConfig.addAttribute("checkPublicMethods", "false");
        checkConfig.addAttribute("checkPrivateMethods", "true");
        checkConfig.addAttribute("checkProtectedMethods", "false");
        checkConfig.addAttribute("checkPackageMethods", "false");
        checkConfig.addAttribute("allowReturnWildcardWithSuper", "false");
        checkConfig.addAttribute("allowReturnWildcardWithExtends", "false");
        checkConfig.addAttribute("classNamesIgnoreList", "");
        final String[] expected =
                createExpectedMessages(LINES_WITH_PRIVATE_METHODS);

        verify(checkConfig,
                getPath("InputForbidWildcardAsReturnType.java"),
                expected);
    }

    /**
     * Test only protected methods.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public final void testOnlyProtectedMethods()
        throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(ForbidWildcardAsReturnTypeCheck.class);
        checkConfig.addAttribute("checkPublicMethods", "false");
        checkConfig.addAttribute("checkPrivateMethods", "false");
        checkConfig.addAttribute("checkProtectedMethods", "true");
        checkConfig.addAttribute("checkPackageMethods", "false");
        checkConfig.addAttribute("allowReturnWildcardWithSuper", "false");
        checkConfig.addAttribute("allowReturnWildcardWithExtends", "false");
        checkConfig.addAttribute("classNamesIgnoreList", "");
        final String[] expected =
                createExpectedMessages(LINES_WITH_PROTECTED_METHODS);

        verify(checkConfig,
                getPath("InputForbidWildcardAsReturnType.java"),
                expected);
    }

    /**
     * Test only package methods.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public final void testOnlyPackageMethods()
        throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(ForbidWildcardAsReturnTypeCheck.class);
        checkConfig.addAttribute("checkPublicMethods", "false");
        checkConfig.addAttribute("checkPrivateMethods", "false");
        checkConfig.addAttribute("checkProtectedMethods", "false");
        checkConfig.addAttribute("checkPackageMethods", "true");
        checkConfig.addAttribute("allowReturnWildcardWithSuper", "false");
        checkConfig.addAttribute("allowReturnWildcardWithExtends", "false");
        checkConfig.addAttribute("classNamesIgnoreList", "");
        final String[] expected =
                createExpectedMessages(LINES_WITH_PACKAGE_METHODS);

        verify(checkConfig,
                getPath("InputForbidWildcardAsReturnType.java"),
                expected);
    }

    /**
     * Allow wildcard with super.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public final void testAllowSuper()
        throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(ForbidWildcardAsReturnTypeCheck.class);
        checkConfig.addAttribute("checkPublicMethods", "true");
        checkConfig.addAttribute("checkPrivateMethods", "true");
        checkConfig.addAttribute("checkProtectedMethods", "true");
        checkConfig.addAttribute("checkPackageMethods", "true");
        checkConfig.addAttribute("allowReturnWildcardWithSuper", "true");
        checkConfig.addAttribute("allowReturnWildcardWithExtends", "false");
        checkConfig.addAttribute("classNamesIgnoreList", "");
        final SortedSet<Integer> exceptSuper = new TreeSet<Integer>(LINES);
        exceptSuper.removeAll(LINES_WITH_SUPER);
        exceptSuper.addAll(LINES_WITH_EXTENDS);
        final String[] expected = createExpectedMessages(exceptSuper);

        verify(checkConfig,
                getPath("InputForbidWildcardAsReturnType.java"),
                expected);
    }

    /**
     * Allow wildcard with extends.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public final void testAllowExtends()
        throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(ForbidWildcardAsReturnTypeCheck.class);
        checkConfig.addAttribute("checkPublicMethods", "true");
        checkConfig.addAttribute("checkPrivateMethods", "true");
        checkConfig.addAttribute("checkProtectedMethods", "true");
        checkConfig.addAttribute("checkPackageMethods", "true");
        checkConfig.addAttribute("allowReturnWildcardWithExtends", "true");
        checkConfig.addAttribute("allowReturnWildcardWithSuper", "false");
        checkConfig.addAttribute("classNamesIgnoreList", "");
        final SortedSet<Integer> exceptExtends = new TreeSet<Integer>(LINES);
        exceptExtends.removeAll(LINES_WITH_EXTENDS);
        exceptExtends.addAll(LINES_WITH_SUPER);
        final String[] expected = createExpectedMessages(exceptExtends);

        verify(checkConfig,
                getPath("InputForbidWildcardAsReturnType.java"),
                expected);
    }

    /**
     * Allow wildcard with extends and super.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public final void testAllowExtendsAndSuper()
        throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(ForbidWildcardAsReturnTypeCheck.class);
        checkConfig.addAttribute("checkPublicMethods", "true");
        checkConfig.addAttribute("checkPrivateMethods", "true");
        checkConfig.addAttribute("checkProtectedMethods", "true");
        checkConfig.addAttribute("checkPackageMethods", "true");
        checkConfig.addAttribute("allowReturnWildcardWithExtends", "true");
        checkConfig.addAttribute("allowReturnWildcardWithSuper", "true");
        checkConfig.addAttribute("classNamesIgnoreList", "");
        final SortedSet<Integer> exceptSuperAndExtends =
                new TreeSet<Integer>(LINES);
        exceptSuperAndExtends.removeAll(LINES_WITH_SUPER);
        exceptSuperAndExtends.removeAll(LINES_WITH_EXTENDS);
        final String[] expected = createExpectedMessages(exceptSuperAndExtends);

        verify(checkConfig,
                getPath("InputForbidWildcardAsReturnType.java"),
                expected);
    }

    /**
     * Allow certain types in ignoreListForClassNames.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public final void testWithIgnoreList()
        throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(ForbidWildcardAsReturnTypeCheck.class);
        checkConfig.addAttribute("checkPublicMethods", "true");
        checkConfig.addAttribute("checkPrivateMethods", "true");
        checkConfig.addAttribute("checkProtectedMethods", "true");
        checkConfig.addAttribute("checkPackageMethods", "true");
        checkConfig.addAttribute("allowReturnWildcardWithExtends", "false");
        checkConfig.addAttribute("allowReturnWildcardWithSuper", "false");
        checkConfig.addAttribute("classNamesIgnoreList",
                IGNORE_LIST_PATTERN);
        final SortedSet<Integer> exceptSuperAndExtends =
                new TreeSet<Integer>(LINES);
        final Collection<Integer> linesToIgnore = Arrays.asList(214, 218, 221);
        exceptSuperAndExtends.removeAll(linesToIgnore);
        final String[] expected = createExpectedMessages(exceptSuperAndExtends);

        verify(checkConfig,
                getPath("InputForbidWildcardAsReturnType.java"),
                expected);
    }

    /**
     * Get error message from property file.
     * @param aKey
     *        key for error message
     * @return error message
     */
    private static String getMessage(final String aKey)
    {
        final Properties prop = new Properties();
        try {
            prop.load(ForbidWildcardAsReturnTypeCheck.class
                    .getResourceAsStream(PROP_FILE_NAME));
        }
        catch (Exception e) {
            return null;
        }
        return prop.getProperty(aKey);
    }

    /**
     * Create new set of line numbers.
     * @param aLines
     *        arrays of line numbers
     * @return sorted set of line numbers.
     */
    private static SortedSet<Integer> newSetOfLines(Integer[] aLines)
    {
        return new TreeSet<Integer>(Arrays.asList(aLines));
    }

    /**
     * Create array of expected messages.
     * @param aLines sorted set of line numbers.
     * @return array of messages.
     */
    private static String[] createExpectedMessages(SortedSet<Integer> aLines)
    {
        final String[] expected = new String[aLines.size()];
        int i = 0;
        for (Integer element : aLines) {
            expected[i] = element + ": " + MESSAGE;
            i++;
        }
        return expected;
    }
}
