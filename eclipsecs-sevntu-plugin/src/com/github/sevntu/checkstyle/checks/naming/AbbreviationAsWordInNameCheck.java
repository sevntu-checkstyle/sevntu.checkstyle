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

package com.github.sevntu.checkstyle.checks.naming;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * Check name of the targeted item to validate abbreviations(capital letters) length in it.
 * Options:
 *         Allowed Abbreviation Length - allowed abbreviation length(length of capital characters).
 *         Allowed Abbreviations - list of abbreviations separated by comma, without spaces.
 * </p>
 * @author Roman Ivanov
 */
public class AbbreviationAsWordInNameCheck extends Check {

    /**
     * mAnnotationTargets is array of type forbidden annotation's target.
     */
    private int[] mTargets = new int[0];

    /** Variable indicates on the allowed amount of capital letters in abbreviations in the classes, interfaces, variables and methods names. */
    private int mAllowedAbbreviationLength = 3;

    /**
     * Set of allowed abbreviation to ignore in check
     */
    private Set<String> mAllowedAbbreviations = new HashSet<String>();

    /** Allows to ignore variables with 'final' modifier. */
    private boolean mIgnoreFinal = true;

    /** Allows to ignore variables with 'static' modifier. */
    private boolean mIgnoreStatic = true;

    /** Allows to ignore methods with '@Override' annotation. */
    private boolean mIgnoreOverriddenMethods = true;
    
    /**
     * Sets ignore option for variables with 'final' modifier.
     * @param aIgnoreFinal
     *        Defines if ignore variables with 'final' modifier or not.
     */
    public void setIgnoreFinal(boolean aIgnoreFinal)
    {
        this.mIgnoreFinal = aIgnoreFinal;
    }

    /**
     * Sets ignore option for variables with 'static' modifier.
     * @param aIgnoreFinal
     *        Defines if ignore variables with 'static' modifier or not.
     */
    public void setIgnoreStatic(boolean aIgnoreStatic)
    {
        this.mIgnoreStatic = aIgnoreStatic;
    }
    
    /**
     * Sets ignore option for methods with "@Override" annotation.
     * @param aIgnoreOverriddenMethods
     *        Defines if ignore methods with "@Override" annotation or not.
     */
    public void setIgnoreOverriddenMethods(boolean aIgnoreOverriddenMethods)
    {
        this.mIgnoreOverriddenMethods = aIgnoreOverriddenMethods;
    }
    
    /**
     * Targets for a Check.
     * 
     * @param aTargets
     *            - array of type's names
     */
    public void setTargets(String[] aTargets) {
        if (aTargets != null) {
            mTargets = new int[aTargets.length];
            for (int i = 0; i < aTargets.length; i++) {
                mTargets[i] = TokenTypes.getTokenId(aTargets[i]);
            }
        }
    }

    /**
     * Allowed abbreviation length in names.
     * 
     * @param aAllowedAbbreviationLength
     *            amount of allowed capital letters in abbreviation.
     */
    public void setAllowedAbbreviationLength(int aAllowedAbbreviationLength) {
        mAllowedAbbreviationLength = aAllowedAbbreviationLength;
    }

    /**
     * Set a list of abbreviations that must be skipped for checking.
     * Abbreviations should be separated by comma, no spaces is allowed.
     * 
     * @param aAllowedAbbr
     *            an string of abbreviations that must be skipped from checking, each
     *            abbreviation separated by comma.
     */
    public void setAllowedAbbreviations(String aAllowedAbbreviations) {
        if (aAllowedAbbreviations != null) {
            mAllowedAbbreviations = new HashSet<String>(Arrays.asList(aAllowedAbbreviations.split(",")));
        }
    }

    @Override
    public int[] getDefaultTokens() {
        return mTargets;
    }

    @Override
    public void visitToken(DetailAST aAst) {

        if (!isIgnoreSituation(aAst)) {
            
            final DetailAST nameAst = aAst.findFirstToken(TokenTypes.IDENT);

            final String typeName = nameAst.getText();

            String abbr = getDisallowedAbbreviation(typeName);
            if (abbr != null) {
                log(nameAst.getLineNo(),
                        "abbreviationAsWord",
                        mAllowedAbbreviationLength);
            }
        }        
    }

    private boolean isIgnoreSituation(DetailAST aAst)
    {
        final DetailAST modifiers = aAst.getFirstChild();

        return aAst.getType() == TokenTypes.VARIABLE_DEF
                && ((mIgnoreFinal && modifiers.branchContains(TokenTypes.FINAL))
                || (mIgnoreStatic && modifiers.branchContains(TokenTypes.LITERAL_STATIC)))
                || (mIgnoreOverriddenMethods && aAst.getType()
                            == TokenTypes.METHOD_DEF && hasOverrideAnnotation(modifiers));
    }

    /**
     * Checks that the method has "@Override" annotation.
     * @param aMethodModifiersAST
     *        A DetailAST nod is related to the given method modifiers
     *        (MODIFIERS type).
     * @return true if method has "@Override" annotation.
     */
    private static boolean hasOverrideAnnotation(DetailAST aMethodModifiersAST)
    {
        boolean result = false;
        for (DetailAST child : getChildren(aMethodModifiersAST)) {
            if (child.getType() == TokenTypes.ANNOTATION) {
                final String annotationText =
                        child.findFirstToken(TokenTypes.IDENT).getText();
                if ("Override".equals(annotationText)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    private String getDisallowedAbbreviation(String string) {
        int beginIndex = 0;
        boolean abbrStarted = false;
        String result = null;
        
        for(int index = 0 ; index < string.length(); index ++) {
            char symbol = string.charAt(index);
            
            if (Character.isUpperCase(symbol)) {
                if (!abbrStarted) {
                    abbrStarted = true;
                    beginIndex = index;
                }
            } else {
                if (abbrStarted) {
                    abbrStarted = false;
                    
                    // -1 as a first capital is usually beginning of next word 
                    int endIndex = index - 1;
                    int abbrLength = endIndex - beginIndex;
                    if (abbrLength > mAllowedAbbreviationLength) {
                        result = string.substring(beginIndex, endIndex);
                        if (!mAllowedAbbreviations.contains(result)) {
                            break;
                        } else {
                            result = null;
                        }
                    }
                    
                    beginIndex = -1;
                }
            }
        }
        if (abbrStarted) {
            int endIndex = string.length();
            int abbrLength = endIndex - beginIndex;
            if (abbrLength > mAllowedAbbreviationLength) {
                result = string.substring(beginIndex, endIndex);
                if (mAllowedAbbreviations.contains(result)) {
                    result = null;
                }
            }
        }
        return result;
    }
    
    /**
     * Gets all the children which are one level below on the current DetailAST
     * parent node.
     * @param aNode
     *        Current parent node.
     * @return The list of children one level below on the current parent node.
     */
    private static List<DetailAST> getChildren(final DetailAST aNode)
    {
        final List<DetailAST> result = new LinkedList<DetailAST>();
        DetailAST curNode = aNode.getFirstChild();
        while (curNode != null) {
            result.add(curNode);
            curNode = curNode.getNextSibling();
        }
        return result;
    }

}
