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

package com.github.sevntu.checkstyle.checks.design;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

/**
 * Prevents using wildcards as return type of methods.
 * <p>
 * <i>Joshua Bloch, "Effective Java (2nd edition)" Item 28: page 137 :</i>
 * </p>
 * <p>
 * "Do not use wildcard types as return types. Rather than providing
 * additional flexibility for your users,
 * it would force them to use wildcard types in client code. Properly used,
 * wildcard types are nearly invisible to users of a class. They cause methods
 * to accept the parameters they should accept and reject those they should
 * reject. If the user of a class has to think about wildcard types, there is
 * probably something wrong with the class’s API."
 * Attention: some JDK classes have public methods with "?"(wildcard) in return type
 * so it might not always possible to avoid wildcards in return type, as they do not demand user
 * to bother about it (invisible for user or method). So suppressions should be used.
 * </p>
 * <p>
 * Examples:
 * <a href="https://docs.oracle.com/javase/8/docs/api/java/util/stream/Collectors.html">
 * JDK Collectors</a>, so usage
 * of methods that return wildcard could force user customizations over Collectors use wildcard in
 * public methods
 * </p>
 * <pre>{@code
 * // custom util method, wildcard come from Collectors.toList()
 * public <T> Collector<T, ?, T> singleResult(Function<? super Iterable<T>, T> collector) {
 *   return Collectors.collectingAndThen(Collectors.toList(), collected -> collected.get(0));
 * }
 * }</pre>
 * <p>If suppressions become too wide spread and annoying it might be reasonable to update Check
 * with option to ignore wildcard if used with another type (not alone).
 * </p>
 *
 * @author <a href='mailto:barataliba@gmail.com'>Baratali Izmailov</a>
 * @since 1.9.0
 */
public class ForbidWildcardAsReturnTypeCheck extends AbstractCheck {

    /**
     * Key for error message.
     */
    public static final String MSG_KEY = "forbid.wildcard.as.return.type";
    /**
     * Token of 'extends' keyword in bounded wildcard.
     */
    private static final int WILDCARD_EXTENDS_IDENT =
            TokenTypes.TYPE_UPPER_BOUNDS;
    /**
     * Token of 'super' keyword in bounded wildcard.
     */
    private static final int WILDCARD_SUPER_IDENT =
            TokenTypes.TYPE_LOWER_BOUNDS;

    /** {@link Deprecated Deprecated} annotation name. */
    private static final String DEPRECATED = "Deprecated";

    /** Macro string for "java.lang.". */
    private static final String JAVA_LANG_MACRO = "java.lang.";

    /** Fully-qualified {@link Deprecated Deprecated} annotation name. */
    private static final String FQ_DEPRECATED = JAVA_LANG_MACRO + DEPRECATED;

    /** {@link Override Override} annotation name. */
    private static final String OVERRIDE = "Override";

    /** Fully-qualified {@link Override Override} annotation name. */
    private static final String FQ_OVERRIDE = JAVA_LANG_MACRO + OVERRIDE;

    /** Macro string for public. */
    private static final String PUBLIC_MACRO = "public";

    /** Macro string for private. */
    private static final String PRIVATE_MACRO = "private";

    /** Macro string for protected. */
    private static final String PROTECTED_MACRO = "protected";

    /** Macro string for package. */
    private static final String PACKAGE_MACRO = "package";

    /**
     * Empty array of DetailAST.
     */
    private static final DetailAST[] EMPTY_DETAILAST_ARRAY = new DetailAST[0];
    /**
     * Check methods with 'public' modifier.
     */
    private boolean checkPublicMethods = true;
    /**
     * Check methods with 'protected' modifier.
     */
    private boolean checkProtectedMethods = true;
    /**
     * Check methods with 'package' modifier.
     */
    private boolean checkPackageMethods = true;
    /**
     * Check methods with 'private' modifier.
     */
    private boolean checkPrivateMethods;
    /**
     * Check methods with @Override annotation.
     */
    private boolean checkOverrideMethods;
    /**
     * Check methods with @Deprecated annotation.
     */
    private boolean checkDeprecatedMethods;
    /**
     * Allow wildcard with 'super'. Example: "? super T"
     */
    private boolean allowReturnWildcardWithSuper;
    /**
     * Allow wildcard with 'extends'. Example: "? extends T"
     */
    private boolean allowReturnWildcardWithExtends;
    /**
     * Ignore regexp for return type class names.
     */
    private Pattern returnTypeClassNamesIgnoreRegex = Pattern.compile(
            "^(Comparator|Comparable)$");

    /**
     * Setter for checkPublicMethods.
     *
     * @param checkPublicMethods New value for the field.
     */
    public void setCheckPublicMethods(boolean checkPublicMethods) {
        this.checkPublicMethods = checkPublicMethods;
    }

    /**
     * Setter for checkProtectedMethods.
     *
     * @param checkProtectedMethods New value for the field.
     */
    public void setCheckProtectedMethods(boolean checkProtectedMethods) {
        this.checkProtectedMethods = checkProtectedMethods;
    }

    /**
     * Setter for checkPackageMethods.
     *
     * @param checkPackageMethods New value for the field.
     */
    public void setCheckPackageMethods(boolean checkPackageMethods) {
        this.checkPackageMethods = checkPackageMethods;
    }

    /**
     * Setter for checkPrivateMethods.
     *
     * @param checkPrivateMethods New value for the field.
     */
    public void setCheckPrivateMethods(boolean checkPrivateMethods) {
        this.checkPrivateMethods = checkPrivateMethods;
    }

    /**
     * Setter for checkOverrideMethods.
     *
     * @param checkOverrideMethods New value for the field.
     */
    public void setCheckOverrideMethods(boolean checkOverrideMethods) {
        this.checkOverrideMethods = checkOverrideMethods;
    }

    /**
     * Setter for checkDeprecatedMethods.
     *
     * @param checkDeprecatedMethods New value for the field.
     */
    public void setCheckDeprecatedMethods(boolean checkDeprecatedMethods) {
        this.checkDeprecatedMethods = checkDeprecatedMethods;
    }

    /**
     * Setter for allowReturnWildcardWithSuper.
     *
     * @param allowReturnWildcardWithSuper New value for the field.
     */
    public void setAllowReturnWildcardWithSuper(boolean allowReturnWildcardWithSuper) {
        this.allowReturnWildcardWithSuper = allowReturnWildcardWithSuper;
    }

    /**
     * Setter for allowReturnWildcardWithExtends.
     *
     * @param allowReturnWildcardWithExtends New value for the field.
     */
    public void setAllowReturnWildcardWithExtends(boolean allowReturnWildcardWithExtends) {
        this.allowReturnWildcardWithExtends = allowReturnWildcardWithExtends;
    }

    /**
     * Setter for returnTypeClassNamesIgnoreRegex.
     *
     * @param returnTypeClassNamesIgnoreRegex New value for the field.
     */
    public void setReturnTypeClassNamesIgnoreRegex(String returnTypeClassNamesIgnoreRegex) {
        this.returnTypeClassNamesIgnoreRegex = Pattern.compile(
                returnTypeClassNamesIgnoreRegex);
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.METHOD_DEF,
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
    public void visitToken(DetailAST methodDefAst) {
        final String methodScope = getVisibilityScope(methodDefAst);
        if (isCheckableMethodScope(methodScope)
                && (checkOverrideMethods
                        || !AnnotationUtil.containsAnnotation(methodDefAst, OVERRIDE)
                            && !AnnotationUtil.containsAnnotation(methodDefAst, FQ_OVERRIDE))
                && (checkDeprecatedMethods
                        || !AnnotationUtil.containsAnnotation(methodDefAst, DEPRECATED)
                            && !AnnotationUtil.containsAnnotation(methodDefAst,
                                FQ_DEPRECATED))) {
            final List<DetailAST> wildcardTypeArguments =
                    getWildcardArgumentsAsMethodReturnType(methodDefAst);
            if (!wildcardTypeArguments.isEmpty()
                    && !isIgnoreCase(methodDefAst, wildcardTypeArguments)) {
                log(methodDefAst, MSG_KEY);
            }
        }
    }

    /**
     * Checks if the method scope is defined as one of the types to check.
     *
     * @param methodScope The string version of the scope.
     * @return {@code true} if the method should be checked.
     */
    private boolean isCheckableMethodScope(String methodScope) {
        return checkPublicMethods && PUBLIC_MACRO.equals(methodScope)
                || checkPrivateMethods && PRIVATE_MACRO.equals(methodScope)
                || checkProtectedMethods && PROTECTED_MACRO.equals(methodScope)
                || checkPackageMethods && PACKAGE_MACRO.equals(methodScope);
    }

    /**
     * Returns the visibility scope of method.
     *
     * @param methodDefAst DetailAST of method definition.
     * @return one of "public", "private", "protected", "package"
     */
    private static String getVisibilityScope(DetailAST methodDefAst) {
        String result = PACKAGE_MACRO;
        if (isInsideInterfaceDefinition(methodDefAst)) {
            result = PUBLIC_MACRO;
        }
        else {
            final String[] visibilityScopeModifiers = {PUBLIC_MACRO, PRIVATE_MACRO,
                PROTECTED_MACRO, };
            final Set<String> methodModifiers = getModifiers(methodDefAst);
            for (final String modifier : visibilityScopeModifiers) {
                if (methodModifiers.contains(modifier)) {
                    result = modifier;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Verify that method definition is inside interface definition.
     *
     * @param methodDefAst DetailAST of method definition.
     * @return true if method definition is inside interface definition.
     */
    private static boolean isInsideInterfaceDefinition(DetailAST methodDefAst) {
        boolean result = false;
        final DetailAST objBlock = methodDefAst.getParent();
        final DetailAST interfaceDef = objBlock.getParent();
        if (interfaceDef.getType() == TokenTypes.INTERFACE_DEF) {
            result = true;
        }
        return result;
    }

    /**
     * Returns the set of modifier Strings for a METHOD_DEF AST.
     *
     * @param methodDefAst AST for a method definition
     * @return the set of modifier Strings for aMethodDefAST
     */
    private static Set<String> getModifiers(DetailAST methodDefAst) {
        final DetailAST modifiersAst = methodDefAst.getFirstChild();
        final Set<String> modifiersSet = new HashSet<>();
        DetailAST modifierAst = modifiersAst.getFirstChild();
        while (modifierAst != null) {
            modifiersSet.add(modifierAst.getText());
            modifierAst = modifierAst.getNextSibling();
        }
        return modifiersSet;
    }

    /**
     * Get identifier of aAST.
     *
     * @param ast
     *        DetailAST instance
     * @return identifier of aAST, null if AST does not have identifier.
     */
    private static String getIdentifier(final DetailAST ast) {
        final DetailAST identifier = ast.findFirstToken(TokenTypes.IDENT);
        return identifier.getText();
    }

    /**
     * Receive list of arguments(AST nodes) which have wildcard.
     *
     * @param methodDefAst
     *        DetailAST of method definition.
     * @return list of arguments which have wildcard.
     */
    private static List<DetailAST> getWildcardArgumentsAsMethodReturnType(DetailAST methodDefAst) {
        final List<DetailAST> result = new LinkedList<>();
        final DetailAST methodTypeAst =
                methodDefAst.findFirstToken(TokenTypes.TYPE);
        final DetailAST[] methodTypeArgumentTokens =
                getGenericTypeArguments(methodTypeAst);
        for (DetailAST typeArgumentAst: methodTypeArgumentTokens) {
            if (hasChildToken(typeArgumentAst, TokenTypes.WILDCARD_TYPE)) {
                result.add(typeArgumentAst);
            }
        }
        return result;
    }

    /**
     * Get all type arguments of TypeAST.
     *
     * @param typeAst
     *        DetailAST of type definition.
     * @return array of type arguments.
     */
    private static DetailAST[] getGenericTypeArguments(DetailAST typeAst) {
        DetailAST[] result = EMPTY_DETAILAST_ARRAY;
        if (hasChildToken(typeAst, TokenTypes.TYPE_ARGUMENTS)) {
            final DetailAST typeArguments = typeAst
                    .findFirstToken(TokenTypes.TYPE_ARGUMENTS);
            final int argumentsCount = typeArguments
                    .getChildCount(TokenTypes.TYPE_ARGUMENT);
            result = new DetailAST[argumentsCount];
            DetailAST firstTypeArgument = typeArguments
                    .findFirstToken(TokenTypes.TYPE_ARGUMENT);
            int counter = 0;
            while (firstTypeArgument != null) {
                if (firstTypeArgument.getType() == TokenTypes.TYPE_ARGUMENT) {
                    result[counter] = firstTypeArgument;
                    counter++;
                }
                firstTypeArgument = firstTypeArgument.getNextSibling();
            }
        }
        return result;
    }

    /**
     * Verify that aAST has token of aTokenType type.
     *
     * @param ast
     *        DetailAST instance.
     * @param tokenType
     *        one of TokenTypes
     * @return true if aAST has token of given type, or false otherwise.
     */
    private static boolean hasChildToken(DetailAST ast, int tokenType) {
        return ast.findFirstToken(tokenType) != null;
    }

    /**
     * Verify that method with wildcards as return type is allowed by current
     * check configuration.
     *
     * @param methodDefAst DetailAST of method definition.
     * @param wildcardTypeArguments list of wildcard type arguments.
     * @return true if method is allowed by current check configuration.
     */
    private boolean isIgnoreCase(DetailAST methodDefAst,
            List<DetailAST> wildcardTypeArguments) {
        final boolean result;
        if (matchesIgnoreClassNames(methodDefAst)) {
            result = true;
        }
        else {
            final boolean hasExtendsWildcardAsReturnType =
                    hasBoundedWildcardAsReturnType(wildcardTypeArguments,
                            WILDCARD_EXTENDS_IDENT);
            final boolean hasSuperWildcardAsReturnType =
                    hasBoundedWildcardAsReturnType(wildcardTypeArguments,
                            WILDCARD_SUPER_IDENT);
            final boolean hasOnlyExtendsWildcardAsReturnType =
                    hasExtendsWildcardAsReturnType
                    && !hasSuperWildcardAsReturnType;
            final boolean hasOnlySuperWildcardAsReturnType =
                    hasSuperWildcardAsReturnType
                    && !hasExtendsWildcardAsReturnType;
            final boolean hasBoundedWildcardAsReturnType =
                    hasExtendsWildcardAsReturnType
                    || hasSuperWildcardAsReturnType;
            final boolean isAllowedBoundedWildcards =
                    allowReturnWildcardWithExtends
                    && allowReturnWildcardWithSuper;
            result = isAllowedBoundedWildcards
                            && hasBoundedWildcardAsReturnType
                    || allowReturnWildcardWithExtends
                            && hasOnlyExtendsWildcardAsReturnType
                    || allowReturnWildcardWithSuper
                            && hasOnlySuperWildcardAsReturnType;
        }
        return result;
    }

    /**
     * Verify that method's return type name matches ignore regexp.
     *
     * @param methodDefAst DetailAST of method.
     * @return true if aMethodDefAST's name matches ignore regexp.
     *      false otherwise.
     */
    private boolean matchesIgnoreClassNames(DetailAST methodDefAst) {
        final DetailAST methodTypeAst =
                methodDefAst.findFirstToken(TokenTypes.TYPE);
        final String typeIdentifier = getIdentifier(methodTypeAst);
        return returnTypeClassNamesIgnoreRegex
                .matcher(typeIdentifier).matches();
    }

    /**
     * Verify that method has bounded wildcard in type arguments list.
     *
     * @param typeArgumentsList list of type arguments.
     * @param boundedWildcardType type of bounded wildcard.
     * @return true if aTypeArgumentsList contains bounded wildcard.
     */
    private static boolean hasBoundedWildcardAsReturnType(
            final List<DetailAST> typeArgumentsList, int boundedWildcardType) {
        boolean result = false;
        for (DetailAST typeArgumentAst: typeArgumentsList) {
            if (hasChildToken(typeArgumentAst, boundedWildcardType)) {
                result = true;
                break;
            }
        }
        return result;
    }

}
