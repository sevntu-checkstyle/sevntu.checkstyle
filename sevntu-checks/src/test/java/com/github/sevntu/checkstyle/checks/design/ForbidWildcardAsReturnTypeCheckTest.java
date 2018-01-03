////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2018 the original author or authors.
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

import static com.github.sevntu.checkstyle.checks.design.ForbidWildcardAsReturnTypeCheck.MSG_KEY;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.utils.CommonUtils;

/**
 * Test class for ForbidWildcardInReturnTypeCheck.
 * @author <a href='mailto:barataliba@gmail.com'>Baratali Izmailov</a>
 */
public class ForbidWildcardAsReturnTypeCheckTest extends AbstractModuleTestSupport {

    /**
     * Line numbers with methods which have wildcard in return type.
     */
    private static final SortedSet<Integer> LINES = new TreeSet<>();
    /**
     * Line numbers with public methods which have wildcard in return type.
     */
    private static final SortedSet<Integer> LINES_WITH_PUBLIC_METHODS =
            newSetOfLines(new Integer[] {9, 29, 54, 59, 64, 104, 109, 114,
                119, 124, 129, 134, 228, 242, 280, 281, 282, 283, 284, 285,
                286, 294, 304, 307, 310, 313, 328, });
    /**
     * Line numbers with private methods which have wildcard in return type.
     */
    private static final SortedSet<Integer> LINES_WITH_PRIVATE_METHODS =
            newSetOfLines(new Integer[] {24, 44, 49, 234, 297, });
    /**
     * Line numbers with protected methods which have wildcard in return type.
     */
    private static final SortedSet<Integer> LINES_WITH_PROTECTED_METHODS =
            newSetOfLines(new Integer[] {19, 39, 231, 243, });
    /**
     * Line numbers with package methods which have wildcard in return type.
     */
    private static final SortedSet<Integer> LINES_WITH_PACKAGE_METHODS =
            newSetOfLines(new Integer[] {14, 34, 147, 152, 157, 162, 164, 169,
                174, 179, 189, 202, 207, 214, 218, 221, 244, 248, 251, 254, 264,
                267, 270, 291, });
    /**
     * Line numbers with methods which have upper bounded wildcard in return
     * type.
     */
    private static final SortedSet<Integer> LINES_WITH_EXTENDS =
            newSetOfLines(new Integer[] {29, 34, 39, 44, 49, 54, 104, 114,
                129, 134, 164, 169, 174, 179, 189, 202, 214, 218, 228, 242, 248,
                264, 282, 283, 286, 291, });
    /**
     * Line numbers with methods which have lower bounded wildcard in return
     * type.
     */
    private static final SortedSet<Integer> LINES_WITH_SUPER =
            newSetOfLines(new Integer[] {9, 14, 19, 24, 59, 119, 189, 207, 231,
                243, 251, 267, 284, 285, 286, 294, });
    /**
     * Line numbers with override methods.
     */
    private static final SortedSet<Integer> LINES_WITH_OVERRIDE =
            newSetOfLines(new Integer[] {307, 313, });
    /**
     * Line numbers with override methods.
     */
    private static final SortedSet<Integer> LINES_WITH_DEPRECATED =
            newSetOfLines(new Integer[] {304, 313, 328, });
    /**
     * Line numbers with ignore return type class names.
     */
    private static final SortedSet<Integer> LINES_WITH_IGNORE_CLASS_NAMES =
            newSetOfLines(new Integer[] {214, 218, 221, });
    /**
     * Message for this check.
     */
    private final String warningMessage = getCheckMessage(MSG_KEY);

    /**
     * Initialize value of LINES.
     */
    public ForbidWildcardAsReturnTypeCheckTest() {
        LINES.addAll(LINES_WITH_PACKAGE_METHODS);
        LINES.addAll(LINES_WITH_PRIVATE_METHODS);
        LINES.addAll(LINES_WITH_PROTECTED_METHODS);
        LINES.addAll(LINES_WITH_PUBLIC_METHODS);
    }

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/design";
    }

    /**
     * Main test.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public final void testDefault()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidWildcardAsReturnTypeCheck.class);
        checkConfig.addAttribute("checkPublicMethods", "true");
        checkConfig.addAttribute("checkPrivateMethods", "true");
        checkConfig.addAttribute("checkProtectedMethods", "true");
        checkConfig.addAttribute("checkPackageMethods", "true");
        checkConfig.addAttribute("checkOverrideMethods", "true");
        checkConfig.addAttribute("checkDeprecatedMethods", "true");
        checkConfig.addAttribute("allowReturnWildcardWithSuper", "false");
        checkConfig.addAttribute("allowReturnWildcardWithExtends", "false");
        checkConfig.addAttribute("returnTypeClassNamesIgnoreRegex", "");
        final String[] expected = createExpectedMessages(LINES);

        verify(checkConfig,
                getPath("InputForbidWildcardAsReturnTypeCheck.java"),
                expected);
    }

    /**
     * Test only public methods.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public final void testOnlyPublicMethods()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidWildcardAsReturnTypeCheck.class);
        checkConfig.addAttribute("checkPublicMethods", "true");
        checkConfig.addAttribute("checkPrivateMethods", "false");
        checkConfig.addAttribute("checkProtectedMethods", "false");
        checkConfig.addAttribute("checkPackageMethods", "false");
        checkConfig.addAttribute("checkOverrideMethods", "true");
        checkConfig.addAttribute("checkDeprecatedMethods", "true");
        checkConfig.addAttribute("allowReturnWildcardWithSuper", "false");
        checkConfig.addAttribute("allowReturnWildcardWithExtends", "false");
        checkConfig.addAttribute("returnTypeClassNamesIgnoreRegex", "");
        final String[] expected =
                createExpectedMessages(LINES_WITH_PUBLIC_METHODS);

        verify(checkConfig,
                getPath("InputForbidWildcardAsReturnTypeCheck.java"),
                expected);
    }

    /**
     * Test only private methods.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public final void testOnlyPrivateMethods()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidWildcardAsReturnTypeCheck.class);
        checkConfig.addAttribute("checkPublicMethods", "false");
        checkConfig.addAttribute("checkPrivateMethods", "true");
        checkConfig.addAttribute("checkProtectedMethods", "false");
        checkConfig.addAttribute("checkPackageMethods", "false");
        checkConfig.addAttribute("checkOverrideMethods", "true");
        checkConfig.addAttribute("checkDeprecatedMethods", "true");
        checkConfig.addAttribute("allowReturnWildcardWithSuper", "false");
        checkConfig.addAttribute("allowReturnWildcardWithExtends", "false");
        checkConfig.addAttribute("returnTypeClassNamesIgnoreRegex", "");
        final String[] expected =
                createExpectedMessages(LINES_WITH_PRIVATE_METHODS);

        verify(checkConfig,
                getPath("InputForbidWildcardAsReturnTypeCheck.java"),
                expected);
    }

    /**
     * Test only protected methods.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public final void testOnlyProtectedMethods()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidWildcardAsReturnTypeCheck.class);
        checkConfig.addAttribute("checkPublicMethods", "false");
        checkConfig.addAttribute("checkPrivateMethods", "false");
        checkConfig.addAttribute("checkProtectedMethods", "true");
        checkConfig.addAttribute("checkPackageMethods", "false");
        checkConfig.addAttribute("checkOverrideMethods", "true");
        checkConfig.addAttribute("checkDeprecatedMethods", "true");
        checkConfig.addAttribute("allowReturnWildcardWithSuper", "false");
        checkConfig.addAttribute("allowReturnWildcardWithExtends", "false");
        checkConfig.addAttribute("returnTypeClassNamesIgnoreRegex", "");
        final String[] expected =
                createExpectedMessages(LINES_WITH_PROTECTED_METHODS);

        verify(checkConfig,
                getPath("InputForbidWildcardAsReturnTypeCheck.java"),
                expected);
    }

    /**
     * Test only package methods.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public final void testOnlyPackageMethods()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidWildcardAsReturnTypeCheck.class);
        checkConfig.addAttribute("checkPublicMethods", "false");
        checkConfig.addAttribute("checkPrivateMethods", "false");
        checkConfig.addAttribute("checkProtectedMethods", "false");
        checkConfig.addAttribute("checkPackageMethods", "true");
        checkConfig.addAttribute("checkOverrideMethods", "true");
        checkConfig.addAttribute("checkDeprecatedMethods", "true");
        checkConfig.addAttribute("allowReturnWildcardWithSuper", "false");
        checkConfig.addAttribute("allowReturnWildcardWithExtends", "false");
        checkConfig.addAttribute("returnTypeClassNamesIgnoreRegex", "");
        final String[] expected =
                createExpectedMessages(LINES_WITH_PACKAGE_METHODS);

        verify(checkConfig,
                getPath("InputForbidWildcardAsReturnTypeCheck.java"),
                expected);
    }

    /**
     * Allow wildcard with super.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public final void testAllowSuper()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidWildcardAsReturnTypeCheck.class);
        checkConfig.addAttribute("checkPublicMethods", "true");
        checkConfig.addAttribute("checkPrivateMethods", "true");
        checkConfig.addAttribute("checkProtectedMethods", "true");
        checkConfig.addAttribute("checkPackageMethods", "true");
        checkConfig.addAttribute("checkOverrideMethods", "true");
        checkConfig.addAttribute("checkDeprecatedMethods", "true");
        checkConfig.addAttribute("allowReturnWildcardWithSuper", "true");
        checkConfig.addAttribute("allowReturnWildcardWithExtends", "false");
        checkConfig.addAttribute("returnTypeClassNamesIgnoreRegex", "");
        final SortedSet<Integer> exceptSuper = new TreeSet<>(LINES);
        exceptSuper.removeAll(LINES_WITH_SUPER);
        exceptSuper.addAll(LINES_WITH_EXTENDS);
        final String[] expected = createExpectedMessages(exceptSuper);

        verify(checkConfig,
                getPath("InputForbidWildcardAsReturnTypeCheck.java"),
                expected);
    }

    /**
     * Allow wildcard with extends.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public final void testAllowExtends()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidWildcardAsReturnTypeCheck.class);
        checkConfig.addAttribute("checkPublicMethods", "true");
        checkConfig.addAttribute("checkPrivateMethods", "true");
        checkConfig.addAttribute("checkProtectedMethods", "true");
        checkConfig.addAttribute("checkPackageMethods", "true");
        checkConfig.addAttribute("checkOverrideMethods", "true");
        checkConfig.addAttribute("checkDeprecatedMethods", "true");
        checkConfig.addAttribute("allowReturnWildcardWithExtends", "true");
        checkConfig.addAttribute("allowReturnWildcardWithSuper", "false");
        checkConfig.addAttribute("returnTypeClassNamesIgnoreRegex", "");
        final SortedSet<Integer> exceptExtends = new TreeSet<>(LINES);
        exceptExtends.removeAll(LINES_WITH_EXTENDS);
        exceptExtends.addAll(LINES_WITH_SUPER);
        final String[] expected = createExpectedMessages(exceptExtends);

        verify(checkConfig,
                getPath("InputForbidWildcardAsReturnTypeCheck.java"),
                expected);
    }

    /**
     * Allow wildcard with extends and super.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public final void testAllowExtendsAndSuper()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidWildcardAsReturnTypeCheck.class);
        checkConfig.addAttribute("checkPublicMethods", "true");
        checkConfig.addAttribute("checkPrivateMethods", "true");
        checkConfig.addAttribute("checkProtectedMethods", "true");
        checkConfig.addAttribute("checkPackageMethods", "true");
        checkConfig.addAttribute("checkOverrideMethods", "true");
        checkConfig.addAttribute("checkDeprecatedMethods", "true");
        checkConfig.addAttribute("allowReturnWildcardWithExtends", "true");
        checkConfig.addAttribute("allowReturnWildcardWithSuper", "true");
        checkConfig.addAttribute("returnTypeClassNamesIgnoreRegex", "");
        final SortedSet<Integer> exceptSuperAndExtends =
                new TreeSet<>(LINES);
        exceptSuperAndExtends.removeAll(LINES_WITH_SUPER);
        exceptSuperAndExtends.removeAll(LINES_WITH_EXTENDS);
        final String[] expected = createExpectedMessages(exceptSuperAndExtends);

        verify(checkConfig,
                getPath("InputForbidWildcardAsReturnTypeCheck.java"),
                expected);
    }

    /**
     * Allow certain types in ignoreListForClassNames.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public final void testWithIgnoreList()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidWildcardAsReturnTypeCheck.class);
        checkConfig.addAttribute("checkPublicMethods", "true");
        checkConfig.addAttribute("checkPrivateMethods", "true");
        checkConfig.addAttribute("checkProtectedMethods", "true");
        checkConfig.addAttribute("checkPackageMethods", "true");
        checkConfig.addAttribute("checkOverrideMethods", "true");
        checkConfig.addAttribute("checkDeprecatedMethods", "true");
        checkConfig.addAttribute("allowReturnWildcardWithExtends", "false");
        checkConfig.addAttribute("allowReturnWildcardWithSuper", "false");
        checkConfig.addAttribute("returnTypeClassNamesIgnoreRegex",
                "(Compar.+)|Collection");
        final SortedSet<Integer> exceptSuperAndExtends =
                new TreeSet<>(LINES);
        exceptSuperAndExtends.removeAll(LINES_WITH_IGNORE_CLASS_NAMES);
        final String[] expected = createExpectedMessages(exceptSuperAndExtends);

        verify(checkConfig,
                getPath("InputForbidWildcardAsReturnTypeCheck.java"),
                expected);
    }

    /**
     * Don't check override methods.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public final void testAllExceptOverrideMethods()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidWildcardAsReturnTypeCheck.class);
        checkConfig.addAttribute("checkPublicMethods", "true");
        checkConfig.addAttribute("checkPrivateMethods", "true");
        checkConfig.addAttribute("checkProtectedMethods", "true");
        checkConfig.addAttribute("checkPackageMethods", "true");
        checkConfig.addAttribute("checkOverrideMethods", "false");
        checkConfig.addAttribute("checkDeprecatedMethods", "true");
        checkConfig.addAttribute("allowReturnWildcardWithSuper", "false");
        checkConfig.addAttribute("allowReturnWildcardWithExtends", "false");
        checkConfig.addAttribute("returnTypeClassNamesIgnoreRegex", "");
        final SortedSet<Integer> exceptOverride =
                new TreeSet<>(LINES);
        exceptOverride.removeAll(LINES_WITH_OVERRIDE);
        final String[] expected = createExpectedMessages(exceptOverride);

        verify(checkConfig,
                getPath("InputForbidWildcardAsReturnTypeCheck.java"),
                expected);
    }

    /**
     * Don't check deprecated methods.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public final void testAllExceptDeprecatedMethods()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidWildcardAsReturnTypeCheck.class);
        checkConfig.addAttribute("checkPublicMethods", "true");
        checkConfig.addAttribute("checkPrivateMethods", "true");
        checkConfig.addAttribute("checkProtectedMethods", "true");
        checkConfig.addAttribute("checkPackageMethods", "true");
        checkConfig.addAttribute("checkOverrideMethods", "true");
        checkConfig.addAttribute("checkDeprecatedMethods", "false");
        checkConfig.addAttribute("allowReturnWildcardWithSuper", "false");
        checkConfig.addAttribute("allowReturnWildcardWithExtends", "false");
        checkConfig.addAttribute("returnTypeClassNamesIgnoreRegex", "");
        final SortedSet<Integer> exceptOverride =
                new TreeSet<>(LINES);
        exceptOverride.removeAll(LINES_WITH_DEPRECATED);
        final String[] expected = createExpectedMessages(exceptOverride);

        verify(checkConfig,
            getPath("InputForbidWildcardAsReturnTypeCheck.java"),
            expected);
    }

    @Test
    public final void testFullyQualifiedAnnotation()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidWildcardAsReturnTypeCheck.class);

        verify(checkConfig,
            getPath("InputForbidWildcardAsReturnTypeCheckQualifiedAnnotation.java"),
            CommonUtils.EMPTY_STRING_ARRAY);
    }

    /**
     * Create new set of line numbers.
     * @param aLines
     *        arrays of line numbers
     * @return sorted set of line numbers.
     */
    private static SortedSet<Integer> newSetOfLines(Integer[] aLines) {
        return new TreeSet<Integer>(Arrays.asList(aLines));
    }

    /**
     * Create array of expected messages.
     * @param aLines sorted set of line numbers.
     * @return array of messages.
     */
    private String[] createExpectedMessages(SortedSet<Integer> aLines) {
        final String[] expected = new String[aLines.size()];
        int index = 0;
        for (Integer element : aLines) {
            expected[index] = element + ": " + warningMessage;
            index++;
        }
        return expected;
    }

}
