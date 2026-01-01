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

package com.github.sevntu.checkstyle.checks.design;

import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

/**
 * This check prohibits usage of parameterless constructors,
 * including the default ones.
 *
 * <p><b>Rationale:</b> constructors of certain classes must always take arguments
 * to properly instantiate objects. Exception classes are the primary example:
 * their objects must contain enough info to find out why an exception occurred
 * (see "Effective Java", item 63). Constructing an exception without a cause exception
 * or an exception message leaves out such info and thus should be prohibited.
 * </p>
 *
 * <p>This check prohibits classes which simple names match the RegExp
 *     defined in 'classNameFormat' property.
 * </p>
 *
 * <p><b>Default configuration:</b>
 *     <pre>
 *         &lt;module name="ConstructorWithoutParamsCheck"&gt;
 *           &lt;property name="classNameFormat" value=".*Exception$"/&gt;
 *           &lt;property name="ignoredClassNameFormat" value="UnsupportedOperationException"/&gt;
 *         &lt;/module&gt;
 *     </pre>
 *
 * <p><b>Examples:</b>
 * <pre>
 * // Assume a RegExp in classNameFormat catches example class names
 * // the check can prohibit default constructors of built-in classes
 * RuntimeException ex = new RuntimeException(); // violation expected
 *
 * // the check ignores classes which names match ignoredClassNameFormat
 * // the default config ignores UnsupportedOperationException
 * UnsupportedOperationException ex2 = new UnsupportedOperationException(); // no violation expected
 *
 * // the check allows constructors with empty arguments
 * RuntimeException ex = new RuntimeException(""); // no violation expected
 *
 * // the check can prohibit default constructors of user-defined classes
 * public class Clazz1 {
 * }
 *
 * Clazz1 o1 = new Clazz1(); // violation expected
 *
 * // the check can prohibit  user-defined parameterless constructors
 * public class Clazz2 {
 *
 *   Clazz2() {
 *     foobar();
 *   }
 *
 * }
 *
 * Clazz2 o2 = new Clazz2(); // violation expected
 * </pre>
 * <p>For more examples, see InputConstructorWithoutParamsCheck.
 * For discussion, see the sevntu-checkstyle
 * <a href=https://github.com/sevntu-checkstyle/sevntu.checkstyle/issues/412> issue 412</a></p>.
 *
 * @author <a href="mailto:Sergey.Dudoladov@gmail.com">Sergey Dudoladov</a>
 * @since 1.20.0
 */
public class ConstructorWithoutParamsCheck extends AbstractCheck {

    /**
     * This key points to the warning message in the  "messages.properties" file.
     */
    public static final String MSG_KEY = "constructor.without.params";

    /**
     * The format string of the regexp for a check to apply to.
     */
    private String classNameFormat = ".*Exception$";

    /**
     * The format string of the regexp of class names to ignore.
     */
    private String ignoredClassNameFormat = "UnsupportedOperationException";

    /**
     * The regexp to match against.
     */
    private Pattern regexp = CommonUtil.createPattern(classNameFormat);

    /**
     * The regexp to select class names to ignore.
     */
    private Pattern ignoredRegexp = CommonUtil.createPattern(ignoredClassNameFormat);

    /**
     * Sets the classNameFormat based on the XML configuration value.
     *
     * @param classNameFormat the regexp pattern
     */
    public void setClassNameFormat(String classNameFormat) {
        this.classNameFormat = classNameFormat;
        regexp = CommonUtil.createPattern(classNameFormat);
    }

    /**
     * Sets the ignoredClassNameFormat based on the XML configuration value.
     *
     * @param ignoredClassNameFormat the regexp pattern
     */
    public void setIgnoredClassNameFormat(String ignoredClassNameFormat) {
        this.ignoredClassNameFormat = ignoredClassNameFormat;
        ignoredRegexp = CommonUtil.createPattern(this.ignoredClassNameFormat);
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {TokenTypes.LITERAL_NEW};
    }

    @Override
    public int[] getRequiredTokens() {
        return getDefaultTokens();
    }

    @Override
    public int[] getAcceptableTokens() {
        return getDefaultTokens();
    }

    @Override
    public void visitToken(DetailAST ast) {
        final DetailAST firstChild = ast.getFirstChild();
        if (firstChild != null) {
            final String className = firstChild.getText();

            // The "new" keyword either creates objects or declares arrays.
            // In the case of arrays, no objects (array elements) are automatically created,
            // and this check does not apply.
            if (regexp.matcher(className).find()
                && !ignoredRegexp.matcher(className).find()
                && ast.findFirstToken(TokenTypes.ARRAY_DECLARATOR) == null) {
                final DetailAST parameterListAST = ast.findFirstToken(TokenTypes.ELIST);
                final int numberOfParameters = parameterListAST.getChildCount();

                if (numberOfParameters == 0) {
                    log(ast, MSG_KEY, className);
                }
            }
        }
    }

}
