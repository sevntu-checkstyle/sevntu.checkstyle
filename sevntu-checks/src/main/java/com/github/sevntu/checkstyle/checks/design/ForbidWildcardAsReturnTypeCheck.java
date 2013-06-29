package com.github.sevntu.checkstyle.checks.design;

import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Forbids wildcard as return type of methods.
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
     * Check public methods option.
     */
    private boolean mCheckPublicMethods = true;
    /**
     * Check protected methods option.
     */
    private boolean mCheckProtectedMethods = true;
    /**
     * Check package methods option.
     */
    private boolean mCheckPackageMethods = true;
    /**
     * Check private methods option.
     */
    private boolean mCheckPrivateMethods;
    /**
     * Allow wildcard with 'super'. Example: "? super T"
     */
    private boolean mAllowReturnWildcardWithSuper;
    /**
     * Allow wildcard with 'extends'. Example: "? extends T"
     */
    private boolean mAllowReturnWildcardWithExtends;
    /**
     * Ignore list for class names(regexp).
     */
    private String mClassNamesIgnoreList = "^(Comparator|Comparable)$";

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
    setClassNamesIgnoreList(String aClassNamesIgnoreList)
    {
        this.mClassNamesIgnoreList = aClassNamesIgnoreList;
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] { TokenTypes.METHOD_DEF };
    }

    @Override
    public void visitToken(DetailAST aAST)
    {
        if ((mCheckPublicMethods
                && hasModifier(aAST, TokenTypes.LITERAL_PUBLIC))
                || (mCheckPrivateMethods
                        && hasModifier(aAST, TokenTypes.LITERAL_PRIVATE))
                || (mCheckProtectedMethods
                        && hasModifier(aAST, TokenTypes.LITERAL_PROTECTED))
                || (mCheckPackageMethods
                        && isPackageMethod(aAST)))
        {
            if (hasWildcardInMethodReturnType(aAST)) {
                final boolean containsExtends =
                        hasBoundedWildcardInReturnType(aAST,
                                WILDCARD_EXTENDS_IDENT);
                final boolean containsSuper =
                        hasBoundedWildcardInReturnType(aAST,
                                WILDCARD_SUPER_IDENT);
                if ((mAllowReturnWildcardWithExtends
                        && mAllowReturnWildcardWithSuper
                        && (containsExtends || containsSuper))
                    || (mAllowReturnWildcardWithExtends
                        && containsExtends
                        && !containsSuper)
                    || (mAllowReturnWildcardWithSuper
                        && containsSuper
                        && !containsExtends)
                    || matchesIgroneClassNames(aAST, mClassNamesIgnoreList))
                {
                    return;
                }
                log(aAST.getLineNo(), MSG_KEY);
            }
        }
    }

    /**
     * Verify that method return wildcard type.
     * @param aMethodDefAST
     *        DetailAST of method definition.
     * @return true if method return wildcard type, false otherwise.
     */
    private static boolean
    hasWildcardInMethodReturnType(DetailAST aMethodDefAST)
    {
        boolean result = false;
        final DetailAST methodTypeAST =
                aMethodDefAST.findFirstToken(TokenTypes.TYPE);
        final DetailAST[] methodTypeArgumentTokens =
                getGenericTypeArguments(methodTypeAST);
        for (DetailAST argument: methodTypeArgumentTokens) {
            if (hasChildToken(argument, TokenTypes.WILDCARD_TYPE)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Get all type arguments of TypeAST.
     * @param aTypeAST
     *        DetailAST of type definition.
     * @return array of type arguments.
     */
    private static DetailAST[] getGenericTypeArguments(DetailAST aTypeAST)
    {
        DetailAST[] result = null;
        if (hasChildToken(aTypeAST, TokenTypes.TYPE_ARGUMENTS)) {
            final DetailAST typeArguments = aTypeAST
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
        else {
            result = EMPTY_DETAILAST_ARRAY;
        }
        return result;
    }

    /**
     * Check that given method is protected-package.
     * @param aMethodDefAST
     *        DetailAST of method definition.
     * @return true if method is public.
     */
    private static boolean isPackageMethod(DetailAST aMethodDefAST)
    {
        boolean result = false;
        if (hasChildToken(aMethodDefAST, TokenTypes.MODIFIERS)) {
            final DetailAST modifiers =
                    aMethodDefAST.findFirstToken(TokenTypes.MODIFIERS);
            result = !hasChildToken(modifiers, TokenTypes.LITERAL_PRIVATE)
                    && !hasChildToken(modifiers, TokenTypes.LITERAL_PROTECTED)
                    && !hasChildToken(modifiers, TokenTypes.LITERAL_PUBLIC);
        }
        return result;
    }

    /**
     * Verify that method has bounded wildcard in return type.
     * @param aMethodDefAST DetailAST of method definition.
     * @param aBoundedWildcardType type of bounded wildcard.
     * @return true if method has bounded wildcard of defined type.
     */
    private static boolean hasBoundedWildcardInReturnType(
            DetailAST aMethodDefAST, int aBoundedWildcardType)
    {
        boolean result = false;
        final DetailAST methodTypeAST = aMethodDefAST
                .findFirstToken(TokenTypes.TYPE);
        final DetailAST[] typeArguments =
                getGenericTypeArguments(methodTypeAST);
        for (DetailAST current: typeArguments) {
            if (hasChildToken(current, aBoundedWildcardType)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Verify that method's return type name matches aTypeNamePattern.
     * @param aMethodDefAST DetailAST of method.
     * @param aTypeNamePattern pattern.
     * @return true if aMethodDefAST's name matches aTypeNamePattern.
     *      false otherwise.
     */
    private static boolean
    matchesIgroneClassNames(DetailAST aMethodDefAST, String aTypeNamePattern)
    {
        final DetailAST methodTypeAST =
                aMethodDefAST.findFirstToken(TokenTypes.TYPE);
        final String typeIdentifier = getIdentifier(methodTypeAST);
        return Pattern.matches(aTypeNamePattern, typeIdentifier);
    }

    /**
     * Get identifier of AST.
     * @param aAST
     *        DetailAST instance
     * @return identifier of AST, null if AST does not have identifier.
     */
    private static String getIdentifier(final DetailAST aAST)
    {
        final DetailAST identifier = aAST.findFirstToken(TokenTypes.IDENT);
        if (identifier != null) {
            return identifier.getText();
        }
        return null;
    }

    /**
     * Return true if aAST has token of aTokenType type.
     * @param aAST
     *        DetailAST instance.
     * @param aTokenType
     *        one of TokenTypes
     * @return true if aAST has token of given type, or false otherwise.
     */
    private static boolean hasChildToken(DetailAST aAST, int aTokenType)
    {
        return aAST.findFirstToken(aTokenType) != null;
    }

    /**
     * Checks that method is related to the current METHOD_DEF DetailAST node
     * has a specified modifier (private, final etc).
     *
     * @param aMethodDefAST
     *            A METHOD_DEF DetailAST node is currently being
     *            processed.
     * @param aModifierType
     *            desired modifier type.
     * @return true if method is related to current aMethodDefAST METHOD_DEF
     *         node has modifier aModifierType.
     */
    private static boolean hasModifier(final DetailAST aMethodDefAST,
        int aModifierType)
    {
        boolean result = false;
        if (hasChildToken(aMethodDefAST, TokenTypes.MODIFIERS)) {
            final DetailAST modifiers =
                    aMethodDefAST.findFirstToken(TokenTypes.MODIFIERS);
            result = hasChildToken(modifiers, aModifierType);
        }
        return result;
    }
}
