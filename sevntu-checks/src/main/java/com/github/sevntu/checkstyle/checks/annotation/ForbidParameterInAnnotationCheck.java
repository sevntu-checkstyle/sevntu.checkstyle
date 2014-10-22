////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2014  Oliver Burn
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
package com.github.sevntu.checkstyle.checks.annotation;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.CharMatcher;
import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Forbids specific parameter value for specific annotation. Example of usage:<br/>
 * In JUnit4 &#64;Test annotation has an "expected" parameter. It's disadvantage
 * is that it prints exceptions stack traces into sysout (<a
 * href='http://stackoverflow.com/questions/9375704/ignoring-stacktrace-when-
 * testing-for-exceptions-in-junit'>Like here</a>).
 * 
 * <pre>
 * &#064;Test(expected = IndexOutOfBoundsException.class)
 * public void outOfBounds()
 * {
 *     new ArrayList&lt;Object&gt;().get(1);
 * }
 * </pre>
 *
 * Using this check you could forbid parameter value
 * "IndexOutOfBoundsException.class" and so force user to write method with
 * usual Test annotation like this: <br/>
 *
 * <pre>
 * &#64;Test public void TestForException(){
 *     try{
 *         DoSomething();
 *         Fail();
 *     } catch(Exception e) {
 *       Assert.That(e.msg, Is("Bad thing happened"))
 *     }
 * }
 * </pre>
 * @author <a href="mailto:drozzds@gmail.com"> Sergey Drozd </a>
 */
public class ForbidParameterInAnnotationCheck extends Check
{
    /**
     * message key.
     */
    public static final String MSG_KEY = "annotation.forbid.parameter";

    /** Forbidden annotation name property */
    private String mAnnotationName;

    /** Forbidden annotation parameter name */
    private String mParameterName;

    /** Precompiled forbidden parameter regexp */
    private Pattern mForbiddenParameterPattern = Pattern.compile(".*");

    public void setAnnotationName(String aAnnotationName)
    {
        mAnnotationName = aAnnotationName;
    }

    public void setParameterName(String aParameterName)
    {
        mParameterName = aParameterName;
    }

    public void setParameterValueRegexp(String aParameterValueRegexp)
    {
        mForbiddenParameterPattern = Pattern.compile(aParameterValueRegexp);
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] { TokenTypes.ANNOTATION };
    }

    @Override
    public void visitToken(DetailAST aAnnotation)
    {
        DetailAST anonymousParameter = getAnonymousParameter(aAnnotation);
        if (anonymousParameter != null && mParameterName.equals("")) {
            log(anonymousParameter);
        } else if (getAnnotationName(aAnnotation).equals(mAnnotationName)) {
            for (DetailAST forbiddenParameter : getForbiddenParameters(aAnnotation)) {
                log(forbiddenParameter);
            }
        }
    }

    /**
     * Returns single unnamed parameter of specified annotation
     * @param aAnnotation
     *        - DetailAST node of type {@link TokenTypes#ANNOTATION}
     * @return DetailAST node of type {@link TokenTypes#EXPR}
     */
    private DetailAST getAnonymousParameter(DetailAST aAnnotation)
    {
        DetailAST singleParameter = null;
        DetailAST currentNode = aAnnotation.getFirstChild();
        while (currentNode != null) {
            if (currentNode.getType() == TokenTypes.EXPR) {
                singleParameter = currentNode;
                break;
            }
            currentNode = currentNode.getNextSibling();
        }
        return singleParameter;
    }

    /**
     * Logs forbidden member-value pair depending on the dot existence
     * @param aForbiddenParameter
     *        DetailAST node of type
     *        {@link TokenTypes#ANNOTATION_MEMBER_VALUE_PAIR
     *        TokenTypes.ANNOTATION_MEMBER_VALUE_PAIR}
     */
    private void log(DetailAST aForbiddenParameter)
    {
        if (aForbiddenParameter.getType() == TokenTypes.EXPR) {
            log(aForbiddenParameter, MSG_KEY, "value", mAnnotationName);
            return;
        }
        if (aForbiddenParameter.branchContains(TokenTypes.DOT)) {
            log(aForbiddenParameter, MSG_KEY, mParameterName, mAnnotationName);
        }
        else {
            DetailAST fullExpression = aForbiddenParameter.findFirstToken(TokenTypes.EXPR);
            log(fullExpression, MSG_KEY, mParameterName, mAnnotationName);
        }
    }

    /**
     * Gets all forbidden children one level below on the current DetailAST
     * parent node.
     * @param aAnnotation
     *        DetailAST node of type {@link TokenTypes#ANNOTATION}
     * @return Forbidden parameters list of type DetailAST
     */
    private List<DetailAST> getForbiddenParameters(DetailAST aAnnotation)
    {
        List<DetailAST> forbiddenParameters = new LinkedList<DetailAST>();
        DetailAST currentNode = aAnnotation.getFirstChild();
        while (currentNode != null) {
            if (currentNode.getType() == TokenTypes.ANNOTATION_MEMBER_VALUE_PAIR
                    && isParameterForbidden(currentNode)) {
                forbiddenParameters.add(currentNode);
            }
            currentNode = currentNode.getNextSibling();
        }
        return forbiddenParameters;
    }

    /**
     * Checks a member-value pair in AST tree for forbidden parameter
     * @param aMemberValuePair
     *        - DetailAST node of type
     *        {@link TokenTypes#ANNOTATION_MEMBER_VALUE_PAIR}
     * @return boolean result
     */
    private boolean isParameterForbidden(DetailAST aMemberValuePair)
    {
        String parameterValue = getParameterValue(aMemberValuePair);
        Matcher parameterValueMatcher = mForbiddenParameterPattern.matcher(parameterValue);
        return getParameterName(aMemberValuePair).equals(mParameterName)
                && parameterValueMatcher.matches();
    }

    /**
     * Gets annotation parameter value as String from member-value pair node of
     * syntax tree.
     * @param aMemberValuePair
     *        - DetailAST node of type
     *        {@link TokenTypes#ANNOTATION_MEMBER_VALUE_PAIR}
     * @return String-represented parameter value
     */
    private static String getParameterValue(DetailAST aMemberValuePair)
    {
        final DetailAST parameterValueAst = aMemberValuePair.findFirstToken(TokenTypes.EXPR)
                .getFirstChild();
        String parameterValue;
        if (parameterValueAst.getType() == TokenTypes.DOT) {
            FullIdent fullExpression = FullIdent.createFullIdent(parameterValueAst);
            parameterValue = fullExpression.getText();
        }
        else {
            parameterValue = parameterValueAst.getText();
        }
        return trimQuotes(parameterValue);
    }

    /**
     * trims quotes from input string
     * @param aInput
     *        - string with quotes
     * @return quotes trimmed
     */
    private static String trimQuotes(String aInput)
    {
        return CharMatcher.is('\"').trimFrom(aInput);
    }

    /**
     * Gets annotation name as String value from annotation node of syntax tree.
     * @param aAnnotation
     *        - DetailAST node of type {@link TokenTypes#ANNOTATION}
     * @return String-represented annotation name
     */
    private static String getAnnotationName(DetailAST aAnnotation)
    {
        DetailAST annotationName = aAnnotation.findFirstToken(TokenTypes.IDENT);
        return annotationName.getText();
    }

    /**
     * Gets annotation parameter name as String value from member-value pair
     * node of syntax tree.
     * @param aMemberValuePair
     *        - DetailAST node of type
     *        {@link TokenTypes#ANNOTATION_MEMBER_VALUE_PAIR}
     * @return String-represented parameter name
     */
    private static String getParameterName(DetailAST aMemberValuePair)
    {
        DetailAST parameterName = aMemberValuePair.findFirstToken(TokenTypes.IDENT);
        return parameterName.getText();
    }
}
