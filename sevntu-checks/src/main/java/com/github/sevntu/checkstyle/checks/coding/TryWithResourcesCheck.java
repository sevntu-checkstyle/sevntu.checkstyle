////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2012  Oliver Burn
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
package com.github.sevntu.checkstyle.checks.coding;

import java.util.HashSet;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Verifies that {@link AutoCloseable} resource is declared with try-with-resources statement to
 * make sure that is closed correctly.
 * @author Damian Szczepanik / damianszczepanik@github
 */
public class TryWithResourcesCheck extends Check
{

    public static final String MSG_KEY = "try.with.resources";

    /** Collects imports for parsed file. */
    private final Set<String> imports = new HashSet<String>();

    @Override
    public void beginTree(DetailAST ast)
    {
        // this one is located in java.lang package so will never be imported directly
        imports.add(AutoCloseable.class.getName());
    }

    @Override
    public void finishTree(DetailAST ast)
    {
        imports.clear();
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] { TokenTypes.IMPORT, TokenTypes.TYPE };
    }

    @Override
    public void visitToken(DetailAST ast)
    {
        switch (ast.getType()) {
        case TokenTypes.IMPORT:
            processImportTokens(ast);
            break;
        case TokenTypes.TYPE:
            if (matchedType(ast)) {
                reportProblem(ast.getLineNo());
            }
            break;
        default:
            throw reportUnsupportedToken(ast);
        }
    }

    private void processImportTokens(DetailAST aAST)
    {
        final FullIdent name = FullIdent.createFullIdentBelow(aAST);
        if (name != null) {
            imports.add(name.getText());
        }
    }

    private boolean matchedType(DetailAST typeAST)
    {
        DetailAST identFirstChild = typeAST.getFirstChild();

        switch (identFirstChild.getType()) {
        case TokenTypes.IDENT: // eg. FileInputStream
            if (isParentAcceptable(typeAST.getParent())) {
                // IDENT is fine but first check if the parent is OK
                return isClassAutoCloseable(identFirstChild.getText());
            }
            return false;
        case TokenTypes.DOT: // eg. java.io.FileInputStream
            if (isParentAcceptable(typeAST.getParent())) {
                // DOT is fine but first check if the parent is OK
                return isClassAutoCloseable(FullIdent.createFullIdent(identFirstChild).getText());
            }
            return false;
        case TokenTypes.LITERAL_VOID: // void next to definition
        case TokenTypes.LITERAL_BOOLEAN:
        case TokenTypes.LITERAL_BYTE:
        case TokenTypes.LITERAL_CHAR:
        case TokenTypes.LITERAL_SHORT:
        case TokenTypes.LITERAL_INT:
        case TokenTypes.LITERAL_FLOAT:
        case TokenTypes.LITERAL_LONG:
        case TokenTypes.LITERAL_DOUBLE:
            return false;
        case TokenTypes.ARRAY_DECLARATOR: // array declaration: []
            // supporting arrays is more complicated so don't complain about it
            return false;
        case TokenTypes.BOR: // pipeline, eg catch (FileNotFoundException | ZipException e
            return false;
        default:
            throw reportUnsupportedToken(identFirstChild);
        }
    }

    /**
     * Determines if parent for {@link TokenTypes#TYPE} token is acceptable to mark the problem.
     */
    private boolean isParentAcceptable(DetailAST parent)
    {
        switch (parent.getType()) {
        case TokenTypes.VARIABLE_DEF: // definition of the variable
            // this is exactly what we expect
            return matchVariableDef(parent.getParent());
        case TokenTypes.RESOURCE: // try with resources: try (
            // this AutoCloseable has been already defined via try (
            return false;
        case TokenTypes.METHOD_DEF: // introduces a method
            // if AutoCloseable is a parameter for method then this check does not make sense
            return false;
        case TokenTypes.PARAMETER_DEF: // definition of the parameter, eg catch (IOException e
        case TokenTypes.TYPE_ARGUMENT: // wildcards, eg <E extends Comparable<E>
        case TokenTypes.TYPECAST: // casting (type) value
        case TokenTypes.LITERAL_INSTANCEOF: // type after instanceof, eg s instanceof String
        case TokenTypes.ANNOTATION_FIELD_DEF: // definition of method in annotation. eg String name();
            return false;
        default:
            throw reportUnsupportedToken(parent);
        }
    }

    private boolean matchVariableDef(DetailAST variableDef)
    {
        switch (variableDef.getType()) {
        case TokenTypes.SLIST: // list of parameters
            // this is what we are looking for
            return true;
        case TokenTypes.OBJBLOCK: // definition of the class/enum/interface/... block
            // that would be hard to try-with-resources if it is class member
        case TokenTypes.FOR_EACH_CLAUSE:
        case TokenTypes.FOR_INIT:
            return false;
        default:
            throw reportUnsupportedToken(variableDef);
        }
    }

    /**
     * Checks if passed type implements {@link AutoCloseable}.
     * @param typeName
     *        short type such as <code>FileInputStream</code> or
     *        <code>java.io.FileInputStream</code>
     * @return true if given type implements {@link AutoCloseable}
     */
    private boolean isClassAutoCloseable(String typeName)
    {
        // check when this IDENT is not fully qualified type with package
        if (implementsAutoCloseable(buildClass(typeName))) {
            return true;
        }
        // try to merge with imports and check if type is valid
        for (String importName : imports) {
            boolean result = false;

            // in case of star imports (import java.io.*) drop last character and try to create class for this package
            if (importName.endsWith(".*")) {
                result = implementsAutoCloseable(buildClass(importName.substring(0,
                        importName.length() - 1) + typeName));
            }
            // otherwise import contains full class name
            else if (importName.endsWith(typeName)) {
                result = implementsAutoCloseable(buildClass(importName));
            }
            if (result) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if given type is a known class or interface type.
     * @param typeName
     *        literal that shall be checked
     * @return true if given type could be created
     */
    private Class<?> buildClass(String typeName)
    {
        try {
            return Class.forName(typeName);
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Check if given class implements {@link AutoCloseable} directly or indirectly.
     * @param instance
     *        class or interface that shall be validated
     * @return true if given type implements {@link AutoCloseable}
     */
    private boolean implementsAutoCloseable(Class<?> instance)
    {
        if (instance == null) {
            return false;
        }
        else {
            // if this implements interfaces check if any of them is not AutoCloseable
            // or it implements AutoCloseable
            for (Class<?> clazz : instance.getInterfaces()) {
                if (implementsAutoCloseable(clazz)) {
                    return true;
                }
            }
            // success if finally reached AutoCloseable
            return instance.equals(AutoCloseable.class)
                    // if not, repeat validation for class it extends
                    || implementsAutoCloseable(instance.getSuperclass());
        }
    }

    /**
     * Report the violation.
     * @param lineNumber
     *        line which is violated
     */
    private void reportProblem(int lineNumber)
    {
        log(lineNumber, MSG_KEY);
    }

    /**
     * Throwing when parsed token is not supported. It means that some cas it not supported by this
     * Check and it should be updated or new token appears with new JDK and shall be supported as
     * well.
     * @param ast
     *        AST which could not be parsed properly
     * @return exception that should be thrown/passed to inform about unexpected situation
     */
    private RuntimeException reportUnsupportedToken(DetailAST ast)
    {
        return new IllegalArgumentException(String.format("Unsupported type: %s with value %d.",
                ast,
                ast.getType()));
    }
}
