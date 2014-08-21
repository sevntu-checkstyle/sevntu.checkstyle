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

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.LinkedList;

/**
 * @author Clebert Suconic
 */

public abstract class AnnotationAbstract extends Check
{

    /**
     * Helper method for subclasses to return the name of an annotation
     * @param aAnnotation
     * @return
     */
    protected String getAnnotationName(DetailAST aAnnotation)
    {
        DetailAST directname = aAnnotation.findFirstToken(TokenTypes.IDENT);

        if (directname != null) {
            return directname.getText();
        }
        else {
            //This means that annotation is specified with the full package name
            return aAnnotation.findFirstToken(TokenTypes.DOT).getLastChild()
                    .getText();
        }
    }

    /**
     * Helper method for subclasses to return the name of their properties
     * @param aAnnotation
     * @return
     */
    protected String[] getAnnotationParameters(DetailAST aAnnotation)
    {

        // The parameters that are existent on the annotation
        LinkedList<DetailAST> parameterstList = new LinkedList<DetailAST>();

        for (DetailAST i = aAnnotation.getFirstChild(); i != null; i = i
                .getNextSibling()) {
            if (i.getType() == TokenTypes.ANNOTATION_MEMBER_VALUE_PAIR) {
                parameterstList.add(i);
            }
        }

        String[] names = new String[parameterstList.size()];

        int count = 0;
        for (DetailAST itemIter : parameterstList) {
            names[count++] = itemIter.getFirstChild().getText();
        }
        return names;

    }
}
