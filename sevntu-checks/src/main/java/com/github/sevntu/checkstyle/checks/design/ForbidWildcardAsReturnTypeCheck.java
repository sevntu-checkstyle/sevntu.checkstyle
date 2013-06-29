package com.github.sevntu.checkstyle.checks.design;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import antlr.collections.AST;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

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
 * </p>
 * @author <a href='mailto:barataliba@gmail.com'>Baratali Izmailov</a>
 */
public class ForbidWildcardAsReturnTypeCheck extends Check
{
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
    /**
     * Empty array of DetailAST.
     */
    private static final DetailAST[] EMPTY_DETAILAST_ARRAY = new DetailAST[0];
    /**
     * Check methods with 'public' modifier.
     */
    private boolean mCheckPublicMethods = true;
    /**
     * Check methods with 'protected' modifier.
     */
    private boolean mCheckProtectedMethods = true;
    /**
     * Check methods with 'package' modifier.
     */
    private boolean mCheckPackageMethods = true;
    /**
     * Check methods with 'private' modifier.
     */
    private boolean mCheckPrivateMethods;
    /**
     * Check methods with @Override annotation.
     */
    private boolean mCheckOverrideMethods;
    /**
     * Check methods with @Deprecated annotation.
     */
    private boolean mCheckDeprecatedMethods;
    /**
     * Allow wildcard with 'super'. Example: "? super T"
     */
    private boolean mAllowReturnWildcardWithSuper;
    /**
     * Allow wildcard with 'extends'. Example: "? extends T"
     */
    private boolean mAllowReturnWildcardWithExtends;
    /**
     * Ignore regexp for return type class names.
     */
    private Pattern mReturnTypeClassNamesIgnoreRegex = Pattern.compile(
            "^(Comparator|Comparable)$");

    public void setCheckPublicMethods(boolean aCheckPublicMethods)
    {
        this.mCheckPublicMethods = aCheckPublicMethods;
    }

    public void setCheckProtectedMethods(boolean aCheckProtectedMethods)
    {
        this.mCheckProtectedMethods = aCheckProtectedMethods;
    }

    public void setCheckPackageMethods(boolean aCheckPackageMethods)
    {
        this.mCheckPackageMethods = aCheckPackageMethods;
    }

    public void setCheckPrivateMethods(boolean aCheckPrivateMethods)
    {
        this.mCheckPrivateMethods = aCheckPrivateMethods;
    }

    public void setCheckOverrideMethods(boolean aCheckOverrideMethods)
    {
        this.mCheckOverrideMethods = aCheckOverrideMethods;
    }
    
    public void setCheckDeprecatedMethods(boolean aCheckDeprecatedMethods)
    {
        this.mCheckDeprecatedMethods = aCheckDeprecatedMethods;
    }

    public void
    setAllowReturnWildcardWithSuper(boolean aAllowReturnWildcardWithSuper)
    {
        this.mAllowReturnWildcardWithSuper = aAllowReturnWildcardWithSuper;
    }

    public void
    setAllowReturnWildcardWithExtends(boolean aAllowReturnWildcardWithExtends)
    {
        this.mAllowReturnWildcardWithExtends = aAllowReturnWildcardWithExtends;
    }

    public void
    setReturnTypeClassNamesIgnoreRegex(String aReturnTypeClassNamesIgnoreRegex)
    {
        this.mReturnTypeClassNamesIgnoreRegex = Pattern.compile(
                aReturnTypeClassNamesIgnoreRegex);
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] { TokenTypes.METHOD_DEF };
    }

    @Override
    public void visitToken(DetailAST aMethodDefAst)
    {
        final String methodScope = getVisibilityScope(aMethodDefAst);
        if (((mCheckPublicMethods && "public".equals(methodScope))
                || (mCheckPrivateMethods && "private".equals(methodScope))
                || (mCheckProtectedMethods && "protected".equals(methodScope))
                || (mCheckPackageMethods && "package".equals(methodScope)))
                && (mCheckOverrideMethods 
                        || !hasAnnotation(aMethodDefAst, "Override"))
                && (mCheckDeprecatedMethods
                        || !hasAnnotation(aMethodDefAst, "Deprecated")))
        {
            final List<DetailAST> wildcardTypeArguments =
                    getWildcardArgumentsAsMethodReturnType(aMethodDefAst);
            if (!wildcardTypeArguments.isEmpty()
                    && !isIgnoreCase(aMethodDefAst, wildcardTypeArguments))
            {
                log(aMethodDefAst.getLineNo(), MSG_KEY);
            }
        }
    }

    /**
     * Returns the visibility scope of method.
     * @param aMethodDefAst DetailAST of method definition.
     * @return one of "public", "private", "protected", "package"
     */
    private static String getVisibilityScope(DetailAST aMethodDefAst)
    {
        String result = "package";
        if (isInsideInterfaceDefinition(aMethodDefAst)) {
            result = "public";
        }
        else {
            final String[] visibilityScopeModifiers = {"public", "private",
                "protected", };
            final Set<String> methodModifiers = getModifiers(aMethodDefAst);
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
     * @param aMethodDefAst DetailAST of method definition.
     * @return true if method definition is inside interface definition.
     */
    private static boolean isInsideInterfaceDefinition(DetailAST aMethodDefAst)
    {
        boolean result = false;
        final DetailAST objBlock = aMethodDefAst.getParent();
        final DetailAST interfaceDef = objBlock.getParent();
        if (interfaceDef.getType() == TokenTypes.INTERFACE_DEF) {
            result = true;
        }
        return result;
    }

    /**
     * Returns the set of modifier Strings for a METHOD_DEF AST.
     * @param aMethodDefAst AST for a method definition
     * @return the set of modifier Strings for aMethodDefAST
     */
    private static Set<String> getModifiers(DetailAST aMethodDefAst)
    {
        final AST modifiersAst = aMethodDefAst.getFirstChild();
        final Set<String> modifiersSet = new HashSet<String>();
        AST modifierAst = modifiersAst.getFirstChild();
        while (modifierAst != null) {
            modifiersSet.add(modifierAst.getText());
            modifierAst = modifierAst.getNextSibling();
        }
        return modifiersSet;
    }

    /**
     * Get identifier of aAST.
     * @param aAst
     *        DetailAST instance
     * @return identifier of aAST, null if AST does not have identifier.
     */
    private static String getIdentifier(final DetailAST aAst)
    {
        String result = null;
        final DetailAST identifier = aAst.findFirstToken(TokenTypes.IDENT);
        if (identifier != null) {
            result = identifier.getText();
        }
        return result;
    }

    /**
     * Verify that method definition contains specified annotation.
     * @param aMethodDefAst DetailAST of method definition.
     * @param aAnnotationTitle Annotation title
     * @return true if method definition contains specified annotation.
     */
    private static boolean hasAnnotation(DetailAST aMethodDefAst,
            String aAnnotationTitle)
    {
        boolean result = false;
        final DetailAST modifiersAst = aMethodDefAst.getFirstChild();
        if (hasChildToken(modifiersAst, TokenTypes.ANNOTATION)) {
            DetailAST modifierAst  = modifiersAst.getFirstChild();
            while (modifierAst != null) {
                if (modifierAst.getType() == TokenTypes.ANNOTATION
                        && aAnnotationTitle.equals(getIdentifier(modifierAst))) {
                    result = true;
                    break;
                }
                modifierAst = modifierAst.getNextSibling();
            }
        }
        return result;
    }

    /**
     * Receive list of arguments(AST nodes) which have wildcard.
     * @param aMethodDefAst
     *        DetailAST of method definition.
     * @return list of arguments which have wildcard.
     */
    private static List<DetailAST>
    getWildcardArgumentsAsMethodReturnType(DetailAST aMethodDefAst)
    {
        final List<DetailAST> result = new LinkedList<DetailAST>();
        final DetailAST methodTypeAst =
                aMethodDefAst.findFirstToken(TokenTypes.TYPE);
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
     * @param aTypeAst
     *        DetailAST of type definition.
     * @return array of type arguments.
     */
    private static DetailAST[] getGenericTypeArguments(DetailAST aTypeAst)
    {
        DetailAST[] result = EMPTY_DETAILAST_ARRAY;
        if (hasChildToken(aTypeAst, TokenTypes.TYPE_ARGUMENTS)) {
            final DetailAST typeArguments = aTypeAst
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
     * @param aAst
     *        DetailAST instance.
     * @param aTokenType
     *        one of TokenTypes
     * @return true if aAST has token of given type, or false otherwise.
     */
    private static boolean hasChildToken(DetailAST aAst, int aTokenType)
    {
        return aAst.findFirstToken(aTokenType) != null;
    }

    /**
     * Verify that method with wildcards as return type is allowed by current
     * check configuration.
     * @param aMethodDefAst DetailAST of method definition.
     * @param aWildcardTypeArguments list of wildcard type arguments.
     * @return true if method is allowed by current check configuration.
     */
    private boolean isIgnoreCase(DetailAST aMethodDefAst,
            List<DetailAST> aWildcardTypeArguments)
    {
        boolean result = false;
        if (matchesIgnoreClassNames(aMethodDefAst)) {
            result = true;
        }
        else {
            final boolean hasExtendsWildcardAsReturnType =
                    hasBoundedWildcardAsReturnType(aWildcardTypeArguments,
                            WILDCARD_EXTENDS_IDENT);
            final boolean hasSuperWildcardAsReturnType =
                    hasBoundedWildcardAsReturnType(aWildcardTypeArguments,
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
                    mAllowReturnWildcardWithExtends
                    && mAllowReturnWildcardWithSuper;
            result = (isAllowedBoundedWildcards
                            && hasBoundedWildcardAsReturnType)
                    || (mAllowReturnWildcardWithExtends
                            && hasOnlyExtendsWildcardAsReturnType)
                    || (mAllowReturnWildcardWithSuper
                            && hasOnlySuperWildcardAsReturnType);
        }
        return result;
    }

    /**
     * Verify that method's return type name matches ignore regexp.
     * @param aMethodDefAst DetailAST of method.
     * @return true if aMethodDefAST's name matches ignore regexp.
     *      false otherwise.
     */
    private boolean matchesIgnoreClassNames(DetailAST aMethodDefAst)
    {
        final DetailAST methodTypeAst =
                aMethodDefAst.findFirstToken(TokenTypes.TYPE);
        final String typeIdentifier = getIdentifier(methodTypeAst);
        return mReturnTypeClassNamesIgnoreRegex
                .matcher(typeIdentifier).matches();
    }

    /**
     * Verify that method has bounded wildcard in type arguments list.
     * @param aTypeArgumentsList list of type arguments.
     * @param aBoundedWildcardType type of bounded wildcard.
     * @return true if aTypeArgumentsList contains bounded wildcard.
     */
    private static boolean hasBoundedWildcardAsReturnType(
            final List<DetailAST> aTypeArgumentsList, int aBoundedWildcardType)
    {
        boolean result = false;
        for (DetailAST typeArgumentAst: aTypeArgumentsList) {
            if (hasChildToken(typeArgumentAst, aBoundedWildcardType)) {
                result = true;
                break;
            }
        }
        return result;
    }
}
