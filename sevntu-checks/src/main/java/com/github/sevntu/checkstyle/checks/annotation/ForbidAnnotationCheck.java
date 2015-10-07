////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2015 the original author or authors.
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.TokenUtils;

/**
 * Forbid specific annotation of variable,methods,class,package and other. If
 * you want to forbid use of '@XXX' annotation with methods and class, you must
 * write:
 * <pre>
 * &lt;module name="ForbidAnnotation"&gt; &lt;property name="annotationNames"
 * value="XXX"/&gt; &lt;property name="annotationTargets"
 * value="METHOD_DEF,CLASS_DEF"/&gt; &lt;/module&gt;
 * </pre>
 * @author <a href="mailto:hidoyatov.v.i@gmail.com">Hidoyatov Victor</a>
 */

public class ForbidAnnotationCheck extends Check
{

    /**
     * A key is used to retrieve check message from 'messages.properties' file
     */
    public static final String MSG_KEY = "annotation.incorrect.target";

    /**
     * annotationNames is set of annotation's names.
     */
    private Set<String> annotationNames = new HashSet<String>();
    /**
     * annotationTargets is array of type forbidden annotation's target.
     */
    private int[] annotationTargets = new int[0];
    /**
     * setAnnotationNames is a setter for mAnnotationNames.
     * @param names - array of annotation's names
     */
    public void setAnnotationNames(final String[] names)
    {
        if (names != null) {
            for (String name : names) {
                annotationNames.add(name);
            }
        }
    }

    /**
     * setAnnotationTargets is a getter for mAnnotationNames.
     * @param targets - array of type's names
     */
    public void setAnnotationTargets(String[] targets)
    {
        if (targets != null) {
            annotationTargets = new int[targets.length];
            for (int i = 0; i < targets.length; i++) {
                annotationTargets[i] = TokenUtils.getTokenId(targets[i]);
            }
            Arrays.sort(annotationTargets);
        }
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {TokenTypes.ANNOTATION };
    }

    @Override
    public void visitToken(DetailAST annotation)
    {

        final String annotationName = getAnnotationName(annotation);
        // first parent - 'MODIFIERS', second parent - annotation's target
        final DetailAST annotationTarget = annotation.getParent().getParent();

        final int targetType = annotationTarget.getType();

        if (isRequiredAnnotationName(annotationName)
                && isForbiddenAnnotationTarget(targetType))
        {

            final String currentTarget = annotationTarget.getText();

            log(annotation.getLineNo(), MSG_KEY,
                    currentTarget, annotationName);
        }
    }
    
    private static String getAnnotationName(DetailAST annotation){
        DetailAST directname = annotation.findFirstToken(TokenTypes.IDENT);

        if(directname != null){
            return directname.getText();
        }else{
            //This means that annotation is specified with the full package name
            return annotation.findFirstToken(TokenTypes.DOT).getLastChild().getText();
        }
    }
    

    /**
     * return true if mAnnotationNames contains aAnnotationName.
     * @param annotationName - name of current annotation
     * @return boolean
     */
    private boolean isRequiredAnnotationName(String annotationName)
    {
        return annotationName != null
                && annotationNames.contains(annotationName);
    }

    /**
     * return true if mAnnotationTargets contains aTargetType.
     * @param targetType - type of current annotation
     * @return boolean
     */
    private boolean isForbiddenAnnotationTarget(int targetType)
    {
        return Arrays.binarySearch(annotationTargets, targetType) > -1;
    }
}
