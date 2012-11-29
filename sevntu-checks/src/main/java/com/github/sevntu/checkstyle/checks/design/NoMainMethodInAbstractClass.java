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

package com.github.sevntu.checkstyle.checks.design;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Checks if an abstract class does not have "public static void main(String[] args)",
 * or "public static void main(String... args)" methods, because this can
 * mislead a developer to consider this class as a ready-to-use implementation.
 * @author Vadym Chekrii
 */
public class NoMainMethodInAbstractClass extends Check
{
    /**
     * Set to store abstract classes. If a parent AST of a method passed into
     * visitToken() is stored in this set, then the method is checked, otherwise
     * it is ignored.
     */
    private Set<DetailAST> mAbstractClassesSet = new HashSet<DetailAST>();
    
    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {TokenTypes.CLASS_DEF, TokenTypes.METHOD_DEF};
    }

    @Override
    public void visitToken(DetailAST aAST)
    {
        final int type = aAST.getType();
        if (type == TokenTypes.CLASS_DEF) {
            final DetailAST parentAst = aAST.getParent();
            //clear collection if we get outer class
            if (parentAst == null) { 
               mAbstractClassesSet.clear();
            }
            final DetailAST modifiersBlock =
                    aAST.findFirstToken(TokenTypes.MODIFIERS);
            final DetailAST abstractMod =
                    modifiersBlock.findFirstToken(TokenTypes.ABSTRACT);
            if (abstractMod != null) {
                mAbstractClassesSet.add(aAST);
            }
        } else if (type == TokenTypes.METHOD_DEF) {
            final DetailAST objBlock = aAST.getParent();
            final DetailAST astClass = objBlock.getParent();
            if (!(mAbstractClassesSet.contains(astClass))) {
                return;
            }
            final Boolean isAppropriate = isAppropriateToLog(aAST);
            if (isAppropriate) {
                log(aAST.getLineNo(), "avoid.main.method");
            }
        }
    }
    /**
     * Defines if the given DetailAST is appropriate for logging.
     * @param aAST DetailAST instance, which is analyzed
     * @return Boolean
     */
    Boolean isAppropriateToLog(DetailAST aAST)
    {
        final String methodName =
                aAST.findFirstToken(TokenTypes.IDENT).getText();
        if (!(("main").equals(methodName))) {
            return false;
        }
        final Boolean modifiers = isModifierAppropriateToLog(aAST);
        if (!modifiers) {
            return false;
        }
        final Boolean isVoid = isVoid(aAST);
        if (!isVoid) {
            return false;
        }
        final Boolean params = isParameterAppropriateToLog(aAST);
        if (!params) {
            return false;
        }
        return true;
    }
    /**
     *Returns a map of modifiers of given method.
     * @param aAST DetailAST instance, which is analyzed
     * @return map of modifiers
     */
    Boolean isModifierAppropriateToLog(DetailAST aAST)
    {
        final Map<String, DetailAST> modifiersMap =
                new HashMap<String, DetailAST>();
        final DetailAST modifiers =
                aAST.findFirstToken(TokenTypes.MODIFIERS);
        if (modifiers == null) {
            return false;
        }
        final DetailAST publicMod =
                modifiers.findFirstToken(TokenTypes.LITERAL_PUBLIC);
        modifiersMap.put("public", publicMod);
        final DetailAST staticMod =
                modifiers.findFirstToken(TokenTypes.LITERAL_STATIC);
        modifiersMap.put("static", staticMod);
        final DetailAST abstractMod =
                modifiers.findFirstToken(TokenTypes.ABSTRACT);
        modifiersMap.put("abstract", abstractMod);
        if (modifiersMap.get("public") == null
                || modifiersMap.get("static") == null
                || modifiersMap.get("abstract") != null)
        {
            return false;
        }
        return true;
    }
    /**
     * Defines if the method passed is of void return type.
     * @param aAST instance of a method
     * @return Boolean
     */
    Boolean isVoid(DetailAST aAST)
    {
        final DetailAST typeMod =
                aAST.findFirstToken(TokenTypes.TYPE);
        if (typeMod == null) {
            return false;
        }
        final DetailAST voidType =
                typeMod.findFirstToken(TokenTypes.LITERAL_VOID);
        if (voidType == null) {
            return false;
        }
        return true;
    }
    /**
     * Defines the parameters of a method.
     * @param aAST instance of a method
     * @return list of Strings
     */
    Boolean isParameterAppropriateToLog(DetailAST aAST)
    {
        final DetailAST params =
                aAST.findFirstToken(TokenTypes.PARAMETERS);
        if (params.getChildCount(TokenTypes.PARAMETER_DEF) > 1) {
            return false;
        }
        final DetailAST paramDef =
                params.findFirstToken(TokenTypes.PARAMETER_DEF);
        if (paramDef == null || paramDef.getChildCount() == 0) {
            return false;
        }
        final DetailAST paramDefType =
                paramDef.findFirstToken(TokenTypes.TYPE);
        //check if method parameter is String[]
        final DetailAST paramArray =
                paramDefType.findFirstToken(TokenTypes.ARRAY_DECLARATOR);
        //check if method parameter is String...
        final DetailAST paramEllipsis =
                paramDef.findFirstToken(TokenTypes.ELLIPSIS);
        if (paramArray != null) {
            final String paramName =
                    paramArray.findFirstToken(TokenTypes.IDENT).getText();
            if ("String".equals(paramName)) {
                return true;
            }
        }
        else if (paramEllipsis != null) {
            final DetailAST paramType =
                    paramDef.findFirstToken(TokenTypes.TYPE);
            final String paramName =
                    paramType.findFirstToken(TokenTypes.IDENT).getText();
            if ("String".equals(paramName)) {
                return true;
            }
        }
        return false;
    }
}
