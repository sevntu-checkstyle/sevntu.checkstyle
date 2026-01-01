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

package com.github.sevntu.checkstyle.checks.coding;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.github.sevntu.checkstyle.SevntuUtil;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * This check report you, when method, that must return array or collection,
 * return null value instead of empty collection or empty array.
 * </p>
 * <p>
 * Returning Null is error prone as developers forget to review method
 * implementation or Java doc for nuances of Null meaning.
 * </p>
 * <p>
 * List of collections set by default:
 * AbstractCollection, AbstractList, AbstractQueue, AbstractSequentialList, AbstractSet,
 * ArrayBlockingQueue, ArrayDeque, ArrayList, AttributeList, BeanContextServicesSupport,
 * BeanContextSupport, ConcurrentLinkedDeque, ConcurrentLinkedQueue, ConcurrentSkipListSet,
 * CopyOnWriteArrayList, CopyOnWriteArraySet, DelayQueue, EnumSet, HashSet, JobStateReasons,
 * LinkedBlockingDeque, LinkedBlockingQueue, LinkedHashSet, LinkedList, LinkedTransferQueue,
 * PriorityBlockingQueue, PriorityQueue, RoleList, RoleUnresolvedList, Stack, SynchronousQueue,
 * TreeSet, Vector, Collection, List, Map, Set.
 * </p>
 *
 * @author <a href="mailto:IliaDubinin91@gmail.com">Ilja Dubinin</a>
 * @since 1.9.0
 */
public class NoNullForCollectionReturnCheck extends AbstractCheck {

    /**
     * Warning message key.
     */
    public static final String MSG_KEY = "no.null.for.collections";

    /**
     * Default list of collection implementing classes.
     */
    private static final String DEFAULT_COLLECTIONS = "AbstractCollection AbstractList "
            + "AbstractQueue AbstractSequentialList AbstractSet ArrayBlockingQueue ArrayDeque "
            + "ArrayList AttributeList BeanContextServicesSupport BeanContextSupport "
            + "ConcurrentLinkedDeque ConcurrentLinkedQueue ConcurrentSkipListSet "
            + "CopyOnWriteArrayList CopyOnWriteArraySet DelayQueue EnumSet HashSet "
            + "JobStateReasons LinkedBlockingDeque LinkedBlockingQueue LinkedHashSet "
            + "LinkedList LinkedTransferQueue PriorityBlockingQueue PriorityQueue RoleList "
            + "RoleUnresolvedList Stack SynchronousQueue TreeSet Vector Collection List Map Set";
    /**
     * List of collection, that will be check.
     */
    private final Set<String> collectionList = new HashSet<>();

    /**
     * <p>
     * Search not only in return line.
     * </p>
     */
    private boolean searchThroughMethodBody;

    /**
     * <p>
     * List of the method definition tokens, that returns collection.
     * </p>
     */
    private final List<DetailAST> methodDefs = new LinkedList<>();

    /** Default constructor. */
    public NoNullForCollectionReturnCheck() {
        setCollectionList(DEFAULT_COLLECTIONS);
    }

    /**
     * <p>
     * Setter for list of known collections.
     * </p>
     *
     * @param collectionList
     *        - line contains all collection names.
     */
    public final void setCollectionList(String collectionList) {
        this.collectionList.clear();
        for (String currentCollection : collectionList.split("\\s+")) {
            this.collectionList.add(currentCollection);
        }
    }

    /**
     * <p>
     * Setter for searching through body of the method.
     * </p>
     *
     * @param searchThroughMethodBody
     *        - deep search value.
     */
    public void setSearchThroughMethodBody(boolean searchThroughMethodBody) {
        this.searchThroughMethodBody = searchThroughMethodBody;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.METHOD_DEF,
            TokenTypes.LITERAL_RETURN,
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
    public void beginTree(DetailAST rootAST) {
        methodDefs.clear();
    }

    @Override
    public void visitToken(DetailAST detailAST) {
        switch (detailAST.getType()) {
            case TokenTypes.METHOD_DEF:
                if (isReturnCollection(detailAST)) {
                    methodDefs.add(detailAST);
                }
                break;

            case TokenTypes.LITERAL_RETURN:
                if (!methodDefs.isEmpty()) {
                    final DetailAST currentMethodDef = getMethodDef(detailAST);
                    if (methodDefs.contains(currentMethodDef)
                        && (hasNullLiteralInReturn(detailAST)
                            || searchThroughMethodBody
                                && isReturnedValueBeNull(detailAST))) {
                        log(detailAST, MSG_KEY);
                    }
                }
                break;

            default:
                SevntuUtil.reportInvalidToken(detailAST.getType());
                break;
        }
    }

    /**
     * <p>
     * Returns true, when method type is a collection or an array.
     * </p>
     *
     * @param methodDef
     *        - DetailAST contains method definition node.
     * @return true, when method return collection.
     */
    private boolean isReturnCollection(DetailAST methodDef) {
        final DetailAST methodType = methodDef.findFirstToken(TokenTypes.TYPE);
        final boolean isArrayType =
                methodType.findFirstToken(TokenTypes.ARRAY_DECLARATOR) != null;
        return isArrayType
                || collectionList.contains(methodType.getFirstChild().getText());
    }

    /**
     * <p>
     * Returns true when null literal has in return expression.
     * </p>
     *
     * @param returnLit
     *        - DetailAST contains return literal
     * @return true when null literal has in return expression.
     */
    private static boolean hasNullLiteralInReturn(DetailAST returnLit) {
        DetailAST returnExpression = returnLit.findFirstToken(TokenTypes.EXPR);
        boolean result = false;
        if (returnExpression != null) {
            final DetailAST ternary = returnExpression.getFirstChild();
            if (TokenTypes.QUESTION == ternary.getType()) {
                returnExpression = ternary;
            }
            result = returnExpression.getChildCount(TokenTypes.LITERAL_NULL) > 0;
        }
        return result;
    }

    /**
     * <p>
     * Returns true, when variable in return may be null.
     * </p>
     *
     * @param returnLit
     *        - DetailAST contains LITERAL_RETURN
     * @return true, when variable may be null.
     */
    private static boolean isReturnedValueBeNull(DetailAST returnLit) {
        boolean result = false;
        final DetailAST returnedExpression = returnLit.getFirstChild();
        if (returnedExpression.getType() != TokenTypes.SEMI) {
            final DetailAST variable = returnedExpression.findFirstToken(TokenTypes.IDENT);
            if (variable != null) {
                final String variableName = variable.getText();
                final DetailAST methodDef = getMethodDef(returnLit);
                final List<DetailAST> subblocks = getAllSubblocks(methodDef);
                subblocks.add(0, methodDef);

                result = hasNullInDefinition(subblocks, variableName);

                // searching for not a null value into variable assignment
                if (result) {
                    for (DetailAST subblock : subblocks) {
                        final List<DetailAST> expressions =
                                getChildren(getBlockBody(subblock), TokenTypes.EXPR);
                        for (DetailAST expression : expressions) {
                            final DetailAST assign = expression.findFirstToken(TokenTypes.ASSIGN);
                            if (assign != null
                                    && variableName.equals(assign.findFirstToken(TokenTypes.IDENT)
                                            .getText())
                                    && !assign.branchContains(TokenTypes.LITERAL_NULL)) {
                                result = false;
                                break;
                            }
                        }
                        if (!result) {
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * <p>
     * Return all the nested subblocks in block.
     * </p>
     *
     * @param blockDef
     *        - node of the block.
     * @return all the nested subblocks in block.
     */
    private static List<DetailAST> getAllSubblocks(DetailAST blockDef) {
        final DetailAST blockBody = getBlockBody(blockDef);
        final LinkedList<DetailAST> subblocks = new LinkedList<>();
        subblocks.addAll(getChildren(blockBody, TokenTypes.LITERAL_IF));
        final List<DetailAST> elseBlocks = new LinkedList<>();
        for (DetailAST currentIf : subblocks) {
            if (currentIf.getChildCount(TokenTypes.LITERAL_ELSE) > 0) {
                elseBlocks.add(currentIf.findFirstToken(TokenTypes.LITERAL_ELSE));
            }
        }
        if (!elseBlocks.isEmpty()) {
            subblocks.addAll(elseBlocks);
        }
        subblocks.addAll(getChildren(blockBody, TokenTypes.LITERAL_WHILE));
        subblocks.addAll(getChildren(blockBody, TokenTypes.LITERAL_DO));
        subblocks.addAll(getChildren(blockBody, TokenTypes.LITERAL_FOR));
        subblocks.addAll(getChildren(blockBody, TokenTypes.LITERAL_TRY));
        final List<DetailAST> nestedSubblocks = new LinkedList<>();
        for (DetailAST currentSubblock : subblocks) {
            if (currentSubblock.findFirstToken(TokenTypes.SLIST) != null) {
                nestedSubblocks.addAll(getAllSubblocks(currentSubblock));
            }
        }
        subblocks.addAll(nestedSubblocks);
        return subblocks;
    }

    /**
     * <p>
     * Return true when variable is null into the variable definition.
     * </p>
     *
     * @param subBlocks
     *        - list contains subblocks.
     * @param variableName
     *        - name of returned variable.
     * @return true when variable is null into the variable definition.
     */
    private static boolean hasNullInDefinition(List<DetailAST> subBlocks, String variableName) {
        boolean result = false;
        for (DetailAST subblock : subBlocks) {
            final List<DetailAST> variableDefs =
                    getChildren(getBlockBody(subblock), TokenTypes.VARIABLE_DEF);
            boolean isFinded = false;
            for (DetailAST currentDef : variableDefs) {
                final DetailAST variable = currentDef.findFirstToken(TokenTypes.IDENT);

                if (variableName.equals(variable.getText())) {
                    final DetailAST variableDef = currentDef;
                    DetailAST variableValue = variableDef.findFirstToken(TokenTypes.ASSIGN);
                    if (variableValue == null) {
                        result = true;
                    }
                    else {
                        variableValue = variableValue.findFirstToken(TokenTypes.EXPR);
                        result = variableValue.getFirstChild().getType() == TokenTypes.LITERAL_NULL;
                    }
                    isFinded = true;
                    break;
                }
            }
            if (isFinded) {
                break;
            }
        }
        return result;
    }

    /**
     * <p>
     * Returns all children of that have the specified type.
     * </p>
     *
     * @param root
     *        - root token of a block
     * @param type
     *        - type of children
     * @return all children of that have the specified type.
     */
    private static List<DetailAST> getChildren(DetailAST root, int type) {
        final List<DetailAST> children = new LinkedList<>();
        DetailAST currentChild = root.findFirstToken(type);
        if (currentChild != null) {
            children.add(currentChild);
        }
        while (children.size() < root.getChildCount(type)) {
            currentChild = currentChild.getNextSibling();
            if (currentChild.getType() == type) {
                children.add(currentChild);
            }
        }
        return children;
    }

    /**
     * <p>
     * Return DetailAST that contained method definition.
     * </p>
     *
     * @param returnLit
     *        - DetailAST contains LITERAL_RETURN.
     * @return DetailAST contains METHOD_DEF
     */
    private static DetailAST getMethodDef(DetailAST returnLit) {
        DetailAST methodDef = returnLit;
        while (methodDef != null && methodDef.getType() != TokenTypes.METHOD_DEF) {
            methodDef = methodDef.getParent();
        }
        return methodDef;
    }

    /**
     * <p>
     * Return body of the block.
     * </p>
     *
     * @param blockDef
     *        - block definition node
     * @return body of the block.
     */
    private static DetailAST getBlockBody(DetailAST blockDef) {
        DetailAST blockBody = blockDef.findFirstToken(TokenTypes.SLIST);
        if (blockBody == null) {
            blockBody = blockDef;
        }
        return blockBody;
    }

}
