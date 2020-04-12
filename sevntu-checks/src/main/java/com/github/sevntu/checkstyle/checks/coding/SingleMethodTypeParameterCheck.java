////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2020 the original author or authors.
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

import java.util.ArrayList;
import java.util.List;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * This check restricts the use of type parameters. If a type parameter appears only once in a
 * method declaration, it should be replaced with a wildcard. For more information read "Effective
 * Java (3nd edition)", "Item 31: use bounded wildcards to increase API flexibility".
 * </p>
 *
 * <p>
 * For example:
 * </p>
 *
 * <pre>{@code
 *  public static void swap(List<?> list, int i, int j); // OK, using wildcards
 *  public static <E> void swap(List<E> list, int i, int j); // Violation, single type parameter
 * }</pre>
 *
 * <p>
 * "Please note that there's one problem with the second declaration for swap. The straightforward
 * implementation won't compile:"
 * </p>
 *
 * <pre>{@code
 *  public static void swap(List<?> list, int i, int j) {
 *      list.set(i, list.set(j, list.get(i)));
 *  }
 * }</pre>
 *
 * <p>
 *  Swap.java:5: error: incompatible types: Object cannot be converted to CAP#1
 * </p>
 * <pre>{@code
 *  list.set(i, list.set(j, list.get(i)));
 * }</pre>
 *
 * <p>
 * "It doesn't seem right that we can't put an element back into the list that we just took it out
 * of. The problem is that the type of list is List{@code <?>}, and you can't put any value
 * except null into a List{@code <?>}. Fortunately, there is a way to implement this method
 * without resorting to an unsafe cast or a raw type. The idea is to write a private helper method
 * to capture the wildcard type. The helper method must be a generic method in order to capture the
 * type. Here's how it looks:"
 * </p>
 *
 * <pre>{@code
 *  public static void swap(List<?> list, int i, int j) {
 *      swapHelper(list, i, j);
 *  }
 *
 *  // Private helper method for wildcard capture
 *  private static <E> void swapHelper(List<E> list, int i, int j) {
 *      list.set(i, list.set(j, list.get(i)));
 *  }
 * }</pre>
 *
 * <p>
 * This check is only applicable to public methods.
 * </p>
 *
 * <p>
 * Limitation: this check does not identify methods which use both generics and wildcards in
 * tandem.
 * </p>
 *
 * <pre>{@code
 *  public <T, S extends T> void copy(List<T> dest, List<S> src) // OK, no violation
 *  public <T> void copy(List<T> dest, List<? extends T> src); // OK, with wildcards
 * }</pre>
 *
 * @author <a href="mailto:yasser.aziza@gmail.com">Yasser Aziza</a>
 * @since 1.38.0
 */
public class SingleMethodTypeParameterCheck extends AbstractCheck {

    /**
     * Warning message key.
     */
    public static final String MSG_KEY = "single.method.type.parameter";

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
    public void visitToken(DetailAST ast) {
        if (isPublicMethod(ast)) {
            final List<DetailAST> typeArguments = getTypeArguments(ast);

            if (isSingleTypeParameter(typeArguments)) {
                log(typeArguments.get(0), MSG_KEY);
            }
        }
    }

    /**
     * Checks whether this method is public or not.
     * @param ast the method node for the evaluation
     * @return {@code true} if this method is public, {@code false} otherwise.
     */
    private static boolean isPublicMethod(DetailAST ast) {
        final boolean isInterface =
            ast.getParent().getParent().getType() == TokenTypes.INTERFACE_DEF;

        final boolean hasPublicModifier = ast.findFirstToken(TokenTypes.MODIFIERS)
            .findFirstToken(TokenTypes.LITERAL_PUBLIC) != null;

        return isInterface || hasPublicModifier;
    }

    /**
     * Checks if the given {@link DetailAST} {@link List} contains a single type parameter.
     * @param astList {@link List} of the found {@link TokenTypes#TYPE_PARAMETER}
     * @return {@code true} if the {@link List} contains only one non
     * {@link TokenTypes#WILDCARD_TYPE} element, {@link false otherwise}.
     */
    private boolean isSingleTypeParameter(List<DetailAST> astList) {
        return astList.size() == 1
            && astList.get(0).getType() != TokenTypes.WILDCARD_TYPE;
    }

    /**
     * Returns all {@link TokenTypes#TYPE_ARGUMENTS} found in this method, expect
     * {@link TokenTypes#WILDCARD_TYPE}.
     * @param ast the method node for the evaluation
     * @return {@link List} containing all {@link TokenTypes#TYPE_ARGUMENTS} found,
     * {@code null} otherwise.
     */
    private static List<DetailAST> getTypeArguments(DetailAST ast) {
        final List<DetailAST> typeParameters = new ArrayList<>();
        DetailAST child = ast.findFirstToken(TokenTypes.PARAMETERS).getFirstChild();

        while (child != null) {
            if (child.getType() == TokenTypes.PARAMETER_DEF) {
                final DetailAST type = child.findFirstToken(TokenTypes.TYPE);
                if (type.findFirstToken(TokenTypes.TYPE_ARGUMENTS) != null) {
                    final DetailAST typeArgument = type.findFirstToken(TokenTypes.TYPE_ARGUMENTS)
                        .findFirstToken(TokenTypes.TYPE_ARGUMENT);
                    final DetailAST wildcard =
                            findFirstTokenByType(typeArgument, TokenTypes.WILDCARD_TYPE);

                    if (wildcard == null) {
                        typeParameters.add(type.findFirstToken(TokenTypes.TYPE_ARGUMENTS));
                    }
                    else {
                        typeParameters.add(wildcard);
                    }
                }
            }
            child = child.getNextSibling();
        }
        return typeParameters;
    }

    /**
     * Returns the first child token that has a specified type.
     * @param ast the root node for the search
     * @param type the token type to be find. Should be a constant from {@link TokenTypes}
     * @return either the first token found, {@code null} otherwise.
     */
    private static DetailAST findFirstTokenByType(DetailAST ast, int type) {
        DetailAST returnValue = null;

        for (DetailAST node = ast.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getType() == type) {
                returnValue = node;
                break;
            }
        }

        return returnValue;
    }

}
