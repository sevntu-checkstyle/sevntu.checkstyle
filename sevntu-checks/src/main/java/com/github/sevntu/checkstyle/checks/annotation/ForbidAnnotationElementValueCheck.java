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

package com.github.sevntu.checkstyle.checks.annotation;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.CharMatcher;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * Forbids specific
 * <a href= 'https://docs.oracle.com/javase/specs/jls/se7/html/jls-9.html#jls-9.6.1'>element
 * value</a> for specific annotation. You can configure this check using following options:
 * </p>
 * <ul>
 * <li>Annotation name</li>
 * <li>Annotation element name</li>
 * <li>Forbidden annotation element value pattern</li>
 * </ul>
 * Example of usage:<br>
 * <p>
 * Here is XML configs and according code samples needed to forbid.
 * </p>
 * <p>To configure the check to forbid junit Test annotations with the element name "expected":</p>
 * <p>
 * Config
 * </p>
 *
 * <pre>
 * &lt;module name="ForbidAnnotationElementValue"&gt;
 * &lt;/module&gt;
 * </pre>
 * <p>
 * Code
 * </p>
 *
 * <pre>
 *  &#64;Test(expected = Exception.class)
 * </pre>
 * <p>
 * To configure the check to forbid
 * <a href= 'https://docs.oracle.com/javase/specs/jls/se7/html/jls-9.html#jls-9.7.3'>single-element
 * </a> annotation element value, like 'SuppressWarnings', elementName option should be specified
 * as "value".
 * </p>
 * <p>
 * Config
 * </p>
 *
 * <pre>
 * &lt;module name="ForbidAnnotationElementValue"&gt;
 * &lt;property name="annotationName" value="SuppressWarnings"/&gt;
 * &lt;property name="elementName" value="value"/&gt;
 * &lt;property name="forbiddenElementValueRegexp" value="unchecked"/&gt;
 * &lt;/module&gt;
 * </pre>
 * <p>
 * Code
 * </p>
 *
 * <pre>
 * &#64;SuppressWarnings("unchecked")
 * </pre>
 * <p>
 * To forbid any array-valued element, forbiddenElementValueRegexp option should be: "\{.*\}".
 * </p>
 * <p>
 * Config
 * </p>
 *
 * <pre>
 * &lt;module name="ForbidAnnotationElementValue"&gt;
 * &lt;property name="annotationName" value="SuppressWarnings"/&gt;
 * &lt;property name="elementName" value="value"/&gt;
 * &lt;property name="forbiddenElementValueRegexp" value="\{.*\}"/&gt;
 * &lt;/module&gt;
 * </pre>
 *
 * <p>
 * Code
 * </p>
 *
 * <pre>
 * &#64;SuppressWarnings({"unused", "unchecked"})
 * </pre>
 *
 * @author <a href="mailto:drozzds@gmail.com"> Sergey Drozd </a>
 * @author Richard Veach
 * @since 1.22.0
 */
public class ForbidAnnotationElementValueCheck extends AbstractCheck {

    /** Message key. */
    public static final String MSG_KEY = "annotation.forbid.element.value";

    /** CharMatcher using to trimming quotes from Strings. */
    private static final CharMatcher QUOTE_MATCHER = CharMatcher.is('\"');

    /** Default annotation element name when none specified. */
    private static final String ELEMENT_NAME_DEFAULT = "value";

    /** Forbidden annotation name. */
    private String annotationName = "Test";

    /** Forbidden annotation element name. */
    private String elementName = "expected";

    /** Precompiled forbidden element value pattern. */
    private Pattern forbiddenElementValueRegexp = Pattern.compile(".*");

    /**
     * Sets Annotation Name Check property.
     *
     * @param annotationName The annotation name.
     */
    public void setAnnotationName(String annotationName) {
        this.annotationName = annotationName;
    }

    /**
     * Sets Annotation Element Check property.
     *
     * @param elementName The annotation element name.
     */
    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    /**
     * Sets Forbidden Element Value Pattern Check property.
     *
     * @param forbiddenElementValueRegexp
     *        The forbidden element value pattern to set.
     */
    public void setForbiddenElementValueRegexp(String forbiddenElementValueRegexp) {
        this.forbiddenElementValueRegexp = Pattern.compile(forbiddenElementValueRegexp);
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.ANNOTATION,
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
    public void visitToken(DetailAST ast) {
        if (getAnnotationName(ast).equals(annotationName)) {
            if (ELEMENT_NAME_DEFAULT.equals(elementName) && isSingleElementAnnotation(ast)) {
                final DetailAST forbiddenElement = getSingleElementWithForbiddenValue(ast);

                log(forbiddenElement, MSG_KEY, elementName, annotationName);
            }
            else {
                for (DetailAST forbiddenElement : getForbiddenElements(ast)) {
                    log(forbiddenElement, MSG_KEY, elementName, annotationName);
                }
            }
        }
    }

    /**
     * Determining that annotation is single-element.
     *
     * @param annotation
     *        DetailAST node of type {@link TokenTypes#ANNOTATION}
     * @return True if the annotation is a single-element.
     */
    private boolean isSingleElementAnnotation(DetailAST annotation) {
        return getSingleElementWithForbiddenValue(annotation) != null;
    }

    /**
     * Returns single element of specified annotation that matches forbidden element value.
     *
     * @param annotation
     *        DetailAST node of type {@link TokenTypes#ANNOTATION}
     * @return DetailAST node of type {@link TokenTypes#EXPR}
     */
    private DetailAST getSingleElementWithForbiddenValue(DetailAST annotation) {
        DetailAST singleElement = null;
        DetailAST currentNode = annotation.getFirstChild();

        while (currentNode != null) {
            if (currentNode.getType() == TokenTypes.EXPR
                    || currentNode.getType() == TokenTypes.ANNOTATION_ARRAY_INIT) {
                final String elementValue = getSingleElementValue(currentNode);

                if (forbiddenElementValueRegexp.matcher(elementValue).find()) {
                    singleElement = currentNode;
                    break;
                }
            }

            currentNode = currentNode.getNextSibling();
        }

        return singleElement;
    }

    /**
     * Gets all forbidden children one level below on the current DetailAST parent node.
     *
     * @param annotation
     *        DetailAST node of type {@link TokenTypes#ANNOTATION}
     * @return List of forbidden elements.
     */
    private List<DetailAST> getForbiddenElements(DetailAST annotation) {
        final List<DetailAST> forbiddenElements = new LinkedList<>();
        DetailAST currentNode = annotation.getFirstChild();

        while (currentNode != null) {
            if (currentNode.getType() == TokenTypes.ANNOTATION_MEMBER_VALUE_PAIR
                    && isElementForbidden(currentNode)) {
                forbiddenElements.add(currentNode);
            }

            currentNode = currentNode.getNextSibling();
        }

        return forbiddenElements;
    }

    /**
     * Checks a member-value pair in AST tree for forbidden element.
     *
     * @param memberValuePair
     *        DetailAST node of type {@link TokenTypes#ANNOTATION_MEMBER_VALUE_PAIR}
     * @return True if element is forbidden.
     */
    private boolean isElementForbidden(DetailAST memberValuePair) {
        final String elementValue = getElementValue(memberValuePair);
        final Matcher elementValueMatcher = forbiddenElementValueRegexp.matcher(elementValue);

        return getElementName(memberValuePair).equals(elementName) && elementValueMatcher.find();
    }

    /**
     * Gets annotation element value as String from member-value pair node of syntax tree.
     *
     * @param memberValuePair
     *        DetailAST node of type {@link TokenTypes#ANNOTATION_MEMBER_VALUE_PAIR}
     * @return String-represented element value
     */
    private static String getElementValue(DetailAST memberValuePair) {
        final String elementValue;
        DetailAST elementValueAst = memberValuePair.findFirstToken(TokenTypes.EXPR);

        if (elementValueAst == null) {
            elementValueAst = memberValuePair.findFirstToken(TokenTypes.ANNOTATION_ARRAY_INIT);
            elementValue = getListOfValuesAsText(elementValueAst);
        }
        else {
            elementValue = getExpressionText(elementValueAst);
        }

        return elementValue;
    }

    /**
     * Returns parameter value for single-element annotation.
     *
     * @param parameter
     *        DetailAST node of type {@link TokenTypes#ANNOTATION}
     * @return String-represented parameter value
     */
    private static String getSingleElementValue(DetailAST parameter) {
        final String parameterValue;

        if (parameter.getType() == TokenTypes.EXPR) {
            parameterValue = getExpressionText(parameter);
        }
        else {
            parameterValue = getListOfValuesAsText(parameter);
        }

        return parameterValue;
    }

    /**
     * Returns expression text.
     *
     * @param expression
     *        DetailAST node of type {@link TokenTypes#EXPR}
     * @return String-represented expression
     */
    private static String getExpressionText(DetailAST expression) {
        final DetailAST expressionValue = expression.getFirstChild();
        final String elementValue;

        if (expressionValue.getType() == TokenTypes.DOT) {
            final FullIdent fullExpression = FullIdent.createFullIdent(expressionValue);
            elementValue = fullExpression.getText();
        }
        else {
            elementValue = expressionValue.getText();
        }

        return trimQuotes(elementValue);
    }

    /**
     * Gets String-represented array from provided left brace.
     *
     * @param brace
     *        DetailAST node of type {@link TokenTypes#ANNOTATION_ARRAY_INIT}
     * @return String-represented array. For example "{1,2,3,4}"
     */
    private static String getListOfValuesAsText(DetailAST brace) {
        String fullText = "{";
        DetailAST currentNode = brace.getFirstChild();

        while (currentNode != null) {
            if (currentNode.getType() == TokenTypes.EXPR) {
                fullText += currentNode.getFirstChild().getText();
            }
            else {
                fullText += currentNode.getText();
            }

            currentNode = currentNode.getNextSibling();
        }

        return fullText;
    }

    /**
     * Trims quotes from input string.
     *
     * @param input
     *        string with quotes
     * @return quotes trimmed
     */
    private static String trimQuotes(String input) {
        return QUOTE_MATCHER.trimFrom(input);
    }

    /**
     * Gets annotation name as String value from annotation node of syntax tree.
     *
     * @param annotation
     *        DetailAST node of type {@link TokenTypes#ANNOTATION}
     * @return String-represented annotation name
     */
    private static String getAnnotationName(DetailAST annotation) {
        DetailAST annotationName = annotation.findFirstToken(TokenTypes.IDENT);

        if (annotationName == null) {
            // full classpath
            annotationName = annotation.findFirstToken(TokenTypes.DOT).getLastChild();
        }

        return annotationName.getText();
    }

    /**
     * Gets annotation element name as String value from member-value pair node of syntax tree.
     *
     * @param memberValuePair
     *        DetailAST node of type {@link TokenTypes#ANNOTATION_MEMBER_VALUE_PAIR}
     * @return String-represented parameter name
     */
    private static String getElementName(DetailAST memberValuePair) {
        final DetailAST elementName = memberValuePair.findFirstToken(TokenTypes.IDENT);
        return elementName.getText();
    }

}
