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

package com.github.sevntu.checkstyle.checks.annotation;

import com.google.common.base.Joiner;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * <p>
 * Marks a given parameter as required for a given annotation. This only certify
 * the minimal parameters that need to be used on the annotation. For example if
 * you enforce the parameter "description" on @EJB, you are still free to use
 * beanName if you like.
 * </p>
 * <p>
 * Say you want to ensure that the parameter name should be mandatory on a given
 * annotation.
 * </p>
 * <p/>
 * 
 * <pre>
 * &lt;module name="RequiredParameterForAnnotation"&gt; &lt;property name="annotationName"
 * value="TheAnnotation"/&gt; &lt;property name="requiredProperties"
 * value="ThePropertyName,ThePropertyName2,...ThePropertyNameN"/&gt; &lt;/module&gt;
 * </pre>
 * @author Clebert Suconic
 */

public class RequiredParameterForAnnotationCheck extends AnnotationAbstract
{

    public static final String MSG_KEY = "annotation.missing.required.parameter";

    public RequiredParameterForAnnotationCheck()
    {
    }

    private String annotationName;

    /**
     * This will be using a LinkedHashSet as it will preserve the order in which
     * it was added. Otherwise parameter names will be printed on a random order
     * when going to the log
     */
    private Set<String> requiredParameters = new LinkedHashSet<String>();

    /** The annotation name we are interested in */
    public void setAnnotationName(String annotationName)
    {
        this.annotationName = annotationName;
    }

    /** The required list of parameters we have to use. */
    public void setRequiredParameters(String[] requiredPropertiesParameter)
    {
        if (requiredPropertiesParameter != null) {
            for (String item : requiredPropertiesParameter) {
                this.requiredParameters.add(item);
            }
        }
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] { TokenTypes.ANNOTATION };
    }

    @Override
    public void visitToken(DetailAST aAST)
    {
        String annotationName = getAnnotationName(aAST);

        // we only check the annotations that we were asked to
        if (annotationName.equals(this.annotationName)) {

            // cloning the initial hashMap
            // it will use a LInkedHashSet as a HashSet has no guarantee of ordering
            // when printing the parameter names
            HashSet<String> missingParameters = new LinkedHashSet<String>(
                    requiredParameters);

            String names[] = getAnnotationParameters(aAST);

            for (String lookupParameter : names) {
                missingParameters.remove(lookupParameter);
            }

            if (missingParameters.size() != 0) {
                String requiredParametersAsString = Joiner.on(", ").join(
                        missingParameters);
                log(aAST, MSG_KEY, this.annotationName,
                        requiredParametersAsString);
            }

        }
    }

}
