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

package com.github.sevntu.checkstyle.checks.coding;

import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * This check verifies the name of JUnit4 test class for compliance with user
 * defined naming convention(by default Check expects test classes names
 * matching
 * ".+Test\\d*|.+Tests\\d*|Test.+|Tests.+|.+IT|.+ITs|.+TestCase\\d*|.+TestCases\\d*"
 * regex).
 * </p>
 * <p>
 * Class is considered to be a test if its definition or one of its method
 * definitions annotated with user defined annotations. By default Check looks
 * for classes which contain methods annotated with "Test" or "org.junit.Test".
 * </p>
 * <p>
 * Check has following options:
 * </p>
 * <p>
 * "expectedClassNameRegex" - regular expression which matches expected test
 * class names. If test class name does not matches this regex then Check will
 * log violation. This option defaults to
 * ".+Test\\d*|.+Tests\\d*|Test.+|Tests.+|.+IT|.+ITs|.+TestCase\\d*|.+TestCases\\d*".
 * </p>
 * <p>
 * "classAnnotationNameRegex" - regular expression which matches test annotation
 * names on classes. If class annotated with matching annotation, it is
 * considered to be a test. This option defaults to empty regex(one that matches
 * nothing). If for example this option set to "RunWith", then class "SomeClass"
 * is considered to be a test:
 * </p>
 *
 * <pre>
 * <code>
 * {@literal @}RunWith(Parameterized.class)
 * class SomeClass
 * {
 * }
 * </code>
 * </pre>
 *
 * <p>
 * "methodAnnotationNameRegex" - regular expression which matches test
 * annotation names on methods. If class contains a method annotated with
 * matching annotation, it is considered to be a test. This option defaults to
 * "Test|org.junit.Test". For example, if this option set to "Test", then class
 * "SomeClass" is considered to be a test.
 * </p>
 *
 * <pre>
 * <code>
 * class SomeClass
 * {
 *      {@literal @}Test
 *      void method() {
 *
 *      }
 * }
 * </code>
 * </pre>
 *
 * <p>
 * Annotation names must be specified exactly the same way it specified in code,
 * thus if Check must match annotation with fully qualified name, corresponding
 * options must contain qualified annotation name and vice versa. For example,
 * if annotation regex is "org.junit.Test" Check will recognize "{@literal @}
 * org.junit.Test" annotation and will skip "{@literal @}Test" annotation and
 * vice versa if annotation regex is "Test" Check will recognize "{@literal @}
 * Test" annotation and skip "{@literal @}org.junit.Test" annotation.
 * </p>
 * <p>
 * Following configuration will adjust Check to look for classes annotated with
 * annotation "RunWith" or classes with methods annotated with "Test" and verify
 * that classes names end with "Test" or "Tests".
 * </p>
 *
 * <pre>
 *     &lt;module name="NameConventionForJUnit4TestClassesCheck"&gt;
 *       &lt;property name="expectedClassNameRegex" value=".+Tests|.+Test"/&gt;
 *       &lt;property name="classAnnotationNameRegex" value="RunWith"/&gt;
 *       &lt;property name="methodAnnotationNameRegex" value="Test"/&gt;
 *     &lt;/module&gt;
 * </pre>
 *
 * @author <a href="mailto:zuy_alexey@mail.ru">Zuy Alexey</a>
 * @since 1.13.0
 */
public class NameConventionForJunit4TestClassesCheck extends AbstractCheck {

    /**
     * Violation message key.
     */
    public static final String MSG_KEY = "name.convention.for.test.classes";

    /**
     * <p>
     * Regular expression which matches expected names of JUnit test classes.
     * </p>
     * <p>
     * Default value is
     * ".+Test\\d*|.+Tests\\d*|Test.+|Tests.+|.+IT|.+ITs|.+TestCase\\d*|.+TestCases\\d*".
     * </p>
     */
    private Pattern expectedClassNameRegex =
            Pattern.compile(".+Test\\d*|.+Tests\\d*|Test.+|Tests.+|.+IT|.+ITs|.+TestCase\\d*"
                    + "|.+TestCases\\d*");

    /**
     * <p>
     * Regular expression which matches JUnit class test annotation names.
     * </p>
     * <p>
     * By default this regex is empty.
     * </p>
     */
    private Pattern classAnnotationNameRegex;

    /**
     * <p>
     * Regular expression which matches JUnit method test annotation names.
     * </p>
     * <p>
     * Default value is "Test|org.junit.Test".
     * </p>
     */
    private Pattern methodAnnotationNameRegex =
            Pattern.compile("Test|org.junit.Test");

    /**
     * Sets regexp to match 'expected' class names for JUnit tests.
     *
     * @param expectedClassNameRegex
     *        regexp to match 'correct' JUnit test class names.
     */
    public void setExpectedClassNameRegex(String expectedClassNameRegex) {
        if (expectedClassNameRegex == null || expectedClassNameRegex.isEmpty()) {
            this.expectedClassNameRegex = null;
        }
        else {
            this.expectedClassNameRegex = Pattern.compile(expectedClassNameRegex);
        }
    }

    /**
     * Sets class test annotation name regexp for JUnit tests.
     *
     * @param annotationNameRegex
     *        regexp to match annotations for unit test classes.
     */
    public void setClassAnnotationNameRegex(String annotationNameRegex) {
        if (annotationNameRegex == null || annotationNameRegex.isEmpty()) {
            classAnnotationNameRegex = null;
        }
        else {
            classAnnotationNameRegex = Pattern.compile(annotationNameRegex);
        }
    }

    /**
     * Sets method test annotation name regexp for JUnit tests.
     *
     * @param annotationNameRegex
     *        regexp to match annotations for unit test classes.
     */
    public void setMethodAnnotationNameRegex(String annotationNameRegex) {
        if (annotationNameRegex == null || annotationNameRegex.isEmpty()) {
            methodAnnotationNameRegex = null;
        }
        else {
            methodAnnotationNameRegex = Pattern.compile(annotationNameRegex);
        }
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.CLASS_DEF,
        };
    }

    @Override
    public int[] getAcceptableTokens() {
        return getDefaultTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return getDefaultTokens();
    }

    @Override
    public void visitToken(DetailAST classDefNode) {
        if ((isClassDefinitionAnnotated(classDefNode) || isAtleastOneMethodAnnotated(classDefNode))
                && hasUnexpectedName(classDefNode)) {
            logUnexpectedClassName(classDefNode);
        }
    }

    /**
     * Checks whether class definition annotated with user defined annotation.
     *
     * @param classDefNode
     *        a class definition node
     * @return true, if class definition annotated with user defined annotation
     */
    private boolean isClassDefinitionAnnotated(DetailAST classDefNode) {
        return hasAnnotation(classDefNode, classAnnotationNameRegex);
    }

    /**
     * Checks whether class contains at least one method annotated with user
     * defined annotation.
     *
     * @param classDefNode
     *        a class definition node
     * @return true, if class contains at least one method annotated with user
     *         defined annotation
     */
    private boolean isAtleastOneMethodAnnotated(DetailAST classDefNode) {
        boolean result = false;
        DetailAST classMemberNode =
                classDefNode.findFirstToken(TokenTypes.OBJBLOCK).getFirstChild();

        while (classMemberNode != null) {
            if (classMemberNode.getType() == TokenTypes.METHOD_DEF
                    && hasAnnotation(classMemberNode, methodAnnotationNameRegex)) {
                result = true;
                break;
            }

            classMemberNode = classMemberNode.getNextSibling();
        }

        return result;
    }

    /**
     * Returns true, if class has unexpected name.
     *
     * @param classDefNode
     *        a class definition node
     * @return true, if class has unexpected name
     */
    private boolean hasUnexpectedName(final DetailAST classDefNode) {
        final String className = getIdentifierName(classDefNode);
        return !isMatchesRegex(expectedClassNameRegex, className);
    }

    /**
     * Returns true, if class or method has annotation with name specified in
     * regexp.
     *
     * @param methodOrClassDefNode
     *        the node of type TokenTypes.METHOD_DEF or TokenTypes.CLASS_DEF
     * @param annotationNamesRegexp
     *        regexp contains annotation names
     * @return true, if the class or method contains one of the annotations,
     *         specified in the regexp
     */
    private static boolean hasAnnotation(DetailAST methodOrClassDefNode,
            Pattern annotationNamesRegexp) {
        DetailAST modifierNode =
                methodOrClassDefNode.findFirstToken(TokenTypes.MODIFIERS).getFirstChild();

        boolean result = false;

        while (modifierNode != null) {
            if (modifierNode.getType() == TokenTypes.ANNOTATION) {
                final String annotationName = getIdentifierName(modifierNode);

                if (isMatchesRegex(annotationNamesRegexp, annotationName)) {
                    result = true;
                    break;
                }
            }

            modifierNode = modifierNode.getNextSibling();
        }

        return result;
    }

    /**
     * Logs unexpected class name.
     *
     * @param classDef
     *        the node of type TokenTypes.CLASS_DEF
     */
    private void logUnexpectedClassName(DetailAST classDef) {
        log(classDef.findFirstToken(TokenTypes.IDENT), MSG_KEY, expectedClassNameRegex);
    }

    /**
     * Returns name of identifier contained in specified node.
     *
     * @param identifierNode
     *        a node containing identifier or qualified identifier.
     * @return identifier name for specified node. If node contains qualified
     *         name then method returns its text representation.
     */
    private static String getIdentifierName(DetailAST identifierNode) {
        final DetailAST identNode = identifierNode.findFirstToken(TokenTypes.IDENT);
        String result;

        if (identNode == null) {
            result = "";

            DetailAST node = identifierNode.findFirstToken(TokenTypes.DOT);

            while (node.getType() == TokenTypes.DOT) {
                result = "." + node.getLastChild().getText() + result;

                node = node.getFirstChild();
            }

            result = node.getText() + result;
        }
        else {
            result = identNode.getText();
        }

        return result;
    }

    /**
     * Matches string against regexp.
     *
     * @param regexPattern
     *        regex to match string with. May be null.
     * @param str
     *        a string to match against regex.
     * @return false if regex is null, otherwise result of matching string
     *         against regex.
     */
    private static boolean isMatchesRegex(Pattern regexPattern, String str) {
        final boolean result;
        if (regexPattern == null) {
            result = false;
        }
        else {
            result = regexPattern.matcher(str).matches();
        }
        return result;
    }

}
