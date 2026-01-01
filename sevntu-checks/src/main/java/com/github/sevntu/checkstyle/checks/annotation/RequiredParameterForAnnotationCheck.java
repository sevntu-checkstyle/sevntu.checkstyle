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

import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * Check that annotation is used with all required parameters.
 * </p>
 * <p>
 * Parameters:<br>
 * <b>annotationName</b> - The name of the target annotation where enforcement of parameter
 * should happen.<br>
 * <b>requiredParameters</b> - Set of parameter names that are required on the target
 * annotation. Names can be specified on any order in target annotation.
 * </p>
 * <p>
 * <b>Example 1.</b><br>
 * Configuration:
 * </p>
 * <pre>
 * &lt;module name="RequiredParameterForAnnotation"&gt;
 *     &lt;property name="annotationName" value="TheAnnotation"/&gt;
 *     &lt;property name="requiredParameters" value="ThePropertyName1"/&gt;
 * &lt;/module&gt;
 * </pre>
 * <p>
 * Result:
 * </p>
 * <pre>
 * <code>
 * {@literal @}TheAnnotation() //Violation. ThePropertyName1 missing.
 * someMethod() {}
 *
 * {@literal @}TheAnnotation(ThePropertyName2=2) //Violation. ThePropertyName1 missing.
 * class SomeClass {}
 *
 * {@literal @}TheAnnotation(ThePropertyName1=1) //Correct.
 * class SomeClass {}
 *
 * {@literal @}TheAnnotation(ThePropertyName2=2, ThePropertyName3=3, ThePropertyName1=1) //Correct.
 * class SomeClass {}
 * </code>
 * </pre>
 * <p>
 * <b>Example 2.</b><br>
 * Configuration:
 * </p>
 * <pre>
 * &lt;module name="RequiredParameterForAnnotation"&gt;
 *     &lt;property name="annotationName" value="TheAnnotation"/&gt;
 *     &lt;property name="requiredParameters" value="ThePropertyName1,ThePropertyName2,
 *         ThePropertyName3"/&gt;
 * &lt;/module&gt;
 * </pre>
 * <p>
 * Result:
 * </p>
 * <pre>
 * <code>
 * {@literal @}TheAnnotation() //Violation. ThePropertyName 1, 2, 3 missing.
 * someMethod() {}
 *
 * {@literal @}TheAnnotation(ThePropertyName2=2) //Violation. ThePropertyName 1 and 3 missing.
 * class SomeClass {}
 *
 * {@literal @}TheAnnotation(ThePropertyName3=3, ThePropertyName2=2, ThePropertyName1=1) //Correct.
 * class SomeClass {}
 * </code>
 * </pre>
 *
 * @author <a href="mailto:andrew.uljanenko@gmail.com">Andrew Uljanenko</a>
 * @since 1.13.0
 */

public class RequiredParameterForAnnotationCheck extends AbstractCheck {

    /**
     * Key for error message.
     */
    public static final String MSG_KEY = "annotation.missing.required.parameter";

    /**
     * Parameters that should be in annotation.
     */
    private final Set<String> requiredParameters = new TreeSet<>();

    /**
     * The annotation name we are interested in.
     */
    private String annotationName;

    /**
     * The annotation name we are interested in.
     *
     * @param annotationName set annotation name
     */
    public void setAnnotationName(String annotationName) {
        this.annotationName = annotationName;
    }

    /**
     * The required list of parameters we have to use.
     *
     * @param requiredPropertiesParameter set required list of parameters
     */
    public void setRequiredParameters(String... requiredPropertiesParameter) {
        for (String item : requiredPropertiesParameter) {
            requiredParameters.add(item);
        }
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.ANNOTATION,
        };
    }

    @Override
    public int[] getRequiredTokens() {
        return new int[] {
            TokenTypes.ANNOTATION,
        };
    }

    @Override
    public int[] getAcceptableTokens() {
        return getDefaultTokens();
    }

    @Override
    public void visitToken(DetailAST annotationNode) {
        final String annotationNameCheck = getAnnotationName(annotationNode);

        if (annotationNameCheck.equals(annotationName)) {
            final Set<String> missingParameters =
                    Sets.difference(requiredParameters, getAnnotationParameters(annotationNode));

            if (!missingParameters.isEmpty()) {
                final String missingParametersAsString = Joiner.on(", ").join(missingParameters);
                log(annotationNode, MSG_KEY, annotationName, missingParametersAsString);
            }
        }
    }

    /**
     * Returns full name of an annotation.
     *
     * @param annotationNode The node to examine.
     * @return name of an annotation.
     */
    private static String getAnnotationName(DetailAST annotationNode) {
        final DetailAST identNode = annotationNode.findFirstToken(TokenTypes.IDENT);
        final String result;

        if (identNode == null) {
            final StringBuilder builder = new StringBuilder();
            DetailAST separationDotNode = annotationNode.findFirstToken(TokenTypes.DOT);
            while (separationDotNode.getType() == TokenTypes.DOT) {
                builder.insert(0, '.').insert(1, separationDotNode.getLastChild().getText());
                separationDotNode = separationDotNode.getFirstChild();
            }
            builder.insert(0, separationDotNode.getText());
            result = builder.toString();
        }
        else {
            result = identNode.getText();
        }
        return result;
    }

    /**
     * Returns the name of annotations properties.
     *
     * @param annotationNode The node to examine.
     * @return name of annotation properties.
     */
    private static Set<String> getAnnotationParameters(DetailAST annotationNode) {
        final Set<String> annotationParameters = new TreeSet<>();
        DetailAST annotationChildNode = annotationNode.getFirstChild();

        while (annotationChildNode != null) {
            if (annotationChildNode.getType() == TokenTypes.ANNOTATION_MEMBER_VALUE_PAIR) {
                annotationParameters.add(annotationChildNode.getFirstChild().getText());
            }
            annotationChildNode = annotationChildNode.getNextSibling();
        }
        return annotationParameters;
    }

}
