////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2013  Oliver Burn
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
import java.util.LinkedList;
import java.util.List;

import com.puppycrawl.tools.checkstyle.api.Check;
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
 * @author <a href="mailto:IliaDubinin91@gmail.com">Ilja Dubinin</a>
 */
public class NoNullForCollectionReturnCheck extends Check
{
    /**
     * <p>
     * Default list of collection implementing classes.
     * <p>
     */
    private static final String DEFAULT_COLLECTIONS = "AbstractCollection AbstractList AbstractQueue " +
            "AbstractSequentialList AbstractSet ArrayBlockingQueue ArrayDeque ArrayList " +
            "AttributeList BeanContextServicesSupport BeanContextSupport ConcurrentLinkedDeque " +
            "ConcurrentLinkedQueue ConcurrentSkipListSet CopyOnWriteArrayList CopyOnWriteArraySet " +
            "DelayQueue EnumSet HashSet JobStateReasons LinkedBlockingDeque LinkedBlockingQueue " +
            "LinkedHashSet LinkedList LinkedTransferQueue PriorityBlockingQueue PriorityQueue " +
            "RoleList RoleUnresolvedList Stack SynchronousQueue TreeSet Vector Collection List Map Set";
    /**
     * <p>
     * List of collection, that will be check.
     * </p>
     */
    private HashSet<String> mCollectionList = new HashSet<String>();

    /**
     * <p>
     * Search not only in return line.
     * </p>
     */
    private boolean mSearchThroughMethodBody = false;

    /**
     * <p>
     * List of the method definition tokens, that returns collection.
     * </p>
     */
    private LinkedList<DetailAST> mMethodDefs = new LinkedList<DetailAST>();

    public NoNullForCollectionReturnCheck()
    {
        super();
        setCollectionList(DEFAULT_COLLECTIONS);
    }
    
    /**
     * <p>
     * Setter for list of known collections.
     * </p>
     * @param aCollectionList
     *        - line contains all collection names.
     */
    public void setCollectionList(String aCollectionList)
    {
        mCollectionList.clear();
        for (String currentCollection : aCollectionList.split("\\s+"))
        {
            mCollectionList.add(currentCollection);
        }
    }

    /**
     * <p>
     * Setter for searching through body of the method.
     * </p>
     * @param aSearchThroughMethodBody
     *        - deep search value.
     */
    public void setSearchThroughMethodBody(boolean aSearchThroughMethodBody)
    {
        this.mSearchThroughMethodBody = aSearchThroughMethodBody;
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] { TokenTypes.METHOD_DEF, TokenTypes.LITERAL_RETURN };
    }
    
    @Override
    public void beginTree(DetailAST aRootAST)
    {
        mMethodDefs.clear();
    }

    @Override
    public void visitToken(DetailAST aDetailAST)
    {
        switch (aDetailAST.getType())
        {
        case TokenTypes.METHOD_DEF:
        {
            if (isReturnCollection(aDetailAST))
            {
                mMethodDefs.push(aDetailAST);
            }
            break;
        }
        case TokenTypes.LITERAL_RETURN:
        {
            if (!mMethodDefs.isEmpty())
            {
                DetailAST currentMethodDef = getMethodDef(aDetailAST);
                if (mMethodDefs.contains(currentMethodDef)
                        && (hasNullLiteralInReturn(aDetailAST)
                                || (mSearchThroughMethodBody
                                        && isReturnedValueBeNull(aDetailAST))))
                {
                    log(aDetailAST.getLineNo(), "no.null.for.collections");
                }
            }
            break;
        }
        }
    }

    /**
     * <p>
     * Returns true, when method type is a collection or an array.
     * </p>
     * @param aMethodDef
     *        - DetailAST contains method definition node.
     * @return true, when method return collection.
     */
    private boolean isReturnCollection(DetailAST aMethodDef)
    {
        DetailAST methodType = aMethodDef.findFirstToken(TokenTypes.TYPE);
        methodType = methodType.getFirstChild();
        return methodType.getType() == TokenTypes.ARRAY_DECLARATOR
                || mCollectionList.contains(methodType.getText());
    }

    /**
     * <p>
     * Returns true when null literal has in return expression.
     * </p>
     * @param aReturnLit
     *        - DetailAST contains return literal
     * @return true when null literal has in return expression.
     */
    private static boolean hasNullLiteralInReturn(DetailAST aReturnLit)
    {
        DetailAST returnExpression = aReturnLit.findFirstToken(TokenTypes.EXPR);
        boolean result = false; 
        if (returnExpression != null)
        {
            DetailAST ternary = returnExpression.getFirstChild(); 
            if (TokenTypes.QUESTION == ternary.getType())
            {
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
     * @param aReturnLit
     *        - DetailAST contains LITERAL_RETURN
     * @return true, when variable may be null.
     */
    private static boolean isReturnedValueBeNull(DetailAST aReturnLit)
    {
        boolean result = false;
        DetailAST returnedExpression = aReturnLit.getFirstChild();
        if (returnedExpression.getType() != TokenTypes.SEMI)
        {
            DetailAST variable = returnedExpression.findFirstToken(TokenTypes.IDENT);
            if (variable != null)
            {
                String variableName = variable.getText();
                DetailAST methodDef = getMethodDef(aReturnLit);
                LinkedList<DetailAST> subblocks = getAllSubblocks(methodDef);
                subblocks.addFirst(methodDef);

                result = hasNullInDefinition(subblocks, variableName);

                //searching for not a null value into variable assignment
                if (result)
                {
                    for (DetailAST subblock : subblocks)
                    {
                        List<DetailAST> expressions =
                                getChildren(getBlockBody(subblock), TokenTypes.EXPR);
                        for (DetailAST expression : expressions)
                        {
                            DetailAST assign = expression.findFirstToken(TokenTypes.ASSIGN);
                            if (assign != null
                                    && variableName.equals(assign.findFirstToken(TokenTypes.IDENT).getText()) 
                                        && !assign.branchContains(TokenTypes.LITERAL_NULL))
                            {
                                result = false;
                                break;
                            }
                        }
                        if (!result)
                        {
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
     * @param aBlockDef
     *        - node of the block.
     * @return all the nested subblocks in block.
     */
    private static LinkedList<DetailAST> getAllSubblocks(DetailAST aBlockDef)
    {
        DetailAST blockBody = getBlockBody(aBlockDef);
        LinkedList<DetailAST> subblocks = new LinkedList<DetailAST>();
        subblocks.addAll(getChildren(blockBody, TokenTypes.LITERAL_IF));
        LinkedList<DetailAST> elseBlocks = new LinkedList<DetailAST>();
        for (DetailAST currentIf : subblocks)
        {
            if (currentIf.getChildCount(TokenTypes.LITERAL_ELSE) > 0)
            {
                elseBlocks.add(currentIf.findFirstToken(TokenTypes.LITERAL_ELSE));
            }
        }
        if (!elseBlocks.isEmpty())
        {
            subblocks.addAll(elseBlocks);
        }
        subblocks.addAll(getChildren(blockBody, TokenTypes.LITERAL_WHILE));
        subblocks.addAll(getChildren(blockBody, TokenTypes.LITERAL_DO));
        subblocks.addAll(getChildren(blockBody, TokenTypes.LITERAL_FOR));
        subblocks.addAll(getChildren(blockBody, TokenTypes.LITERAL_TRY));
        LinkedList<DetailAST> nestedSubblocks = new LinkedList<DetailAST>();
        for (DetailAST currentSubblock : subblocks)
        {
            if (currentSubblock.branchContains(TokenTypes.SLIST))
            {
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
     * @param aSubblocks
     *        - list contains subblocks.
     * @param aVariableName
     *        - name of returned variable.
     * @return true when variable is null into the variable definition.
     */
    private static boolean hasNullInDefinition(List<DetailAST> aSubblocks, String aVariableName)
    {
        boolean result = false;
        for (DetailAST subblock : aSubblocks)
        {
            List<DetailAST> variableDefs =
                    getChildren(getBlockBody(subblock), TokenTypes.VARIABLE_DEF);
            boolean isFinded = false;
            for (DetailAST currentDef : variableDefs)
            {
                DetailAST variable = currentDef.findFirstToken(TokenTypes.IDENT);

                if (aVariableName.equals(variable.getText()))
                {
                    DetailAST variableDef = currentDef;
                    DetailAST variableValue = variableDef.findFirstToken(TokenTypes.ASSIGN);
                    if (variableValue != null)
                    {
                        variableValue = variableValue.findFirstToken(TokenTypes.EXPR);
                        result = variableValue.getFirstChild().getType() == TokenTypes.LITERAL_NULL;
                    }
                    else
                    {
                        result = true;
                    }
                    isFinded = true;
                    break;
                }
            }
            if (isFinded)
            {
                break;
            }
        }
        return result;
    }

    /**
     * <p>
     * Returns all children of that have the specified type.
     * </p>
     * @param aRoot
     *        - root token of a block
     * @param aType
     *        - type of children
     * @return all children of that have the specified type.
     */
    private static List<DetailAST> getChildren(DetailAST aRoot, int aType)
    {
        List<DetailAST> children = new LinkedList<DetailAST>();
        DetailAST currentChild = aRoot.findFirstToken(aType);
        if (currentChild != null)
        {
            children.add(currentChild);
        }
        while (children.size() < aRoot.getChildCount(aType))
        {
            currentChild = currentChild.getNextSibling();
            if (currentChild.getType() == aType)
            {
                children.add(currentChild);
            }
        }
        return children;
    }

    /**
     * <p>
     * Return DetailAST that contained method definition.
     * </p>
     * @param aReturnLit
     *        - DetailAST contains LITERAL_RETURN.
     * @return DetailAST contains METHOD_DEF
     */
    private static DetailAST getMethodDef(DetailAST aReturnLit)
    {
        DetailAST methodDef = aReturnLit;
        while (methodDef.getType() != TokenTypes.METHOD_DEF)
        {
            methodDef = methodDef.getParent();
        }
        return methodDef;
    }

    /**
     * <p>
     * Return body of the block.
     * </p>
     * @param aBlockDef
     *        - block definition node
     * @return body of the block.
     */
    private static DetailAST getBlockBody(DetailAST aBlockDef)
    {
        DetailAST blockBody = aBlockDef.findFirstToken(TokenTypes.SLIST);
        if (blockBody == null)
        {
            blockBody = aBlockDef;
        }
        return blockBody;
    }
}
