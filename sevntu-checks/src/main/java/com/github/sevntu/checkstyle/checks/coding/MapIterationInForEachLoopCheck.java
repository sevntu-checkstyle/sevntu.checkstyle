////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2012 Oliver Burn
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.checks.coding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>This check can help you to write the whole for-each
 * map iteration more correctly:</p>
 * <p>1. If you iterate over a map using map.keySet() or map.entrySet(),
 * but your code uses only map values, Check will propose you to replace
 * map.keySet() or map.entrySet() with map.values() call.
 * Replacing map.keySet() or map.entrySet() with map.values()
 * for such cases can improve an iteration performance
 * a bit.
 *   <p>Bad:</p>
 *   <pre>for (Map.Entry<String, String> entry : map.entrySet())
 *        {
 *           System.out.println(entry.getValue());
 *        }</pre>
 *   <pre>for (String key : map.keySet())
 *        {
 *           System.out.println(map.get(key));
 *        }</pre>
 *   <p>Good:</p>
 *   <pre>for (String value : map.values())
 *        {
 *           System.out.println(value);
 *        }</pre>
 *
 * 2. If you iterate over a map using map.entrySet(),
 * but never call entry.getValue(), Check will propose
 * you to replace map.entrySet() with map.keySet()
 * to iterate over map keys only.
 *   <p>Bad:</p>
 *   <pre>for (Map.Entry<String, String> entry : map.entrySet())
 *   {
 *           System.out.println(entry.getKey());
 *   }</pre>
 *   <p>Good:</p>
 *   <pre>for (String key : map.keySet())
 *   {
 *           System.out.println(key);
 *   }</pre>
 * 3. If you iterate over a map with map.keySet() and use both keys and
 * values, check will propose you to replace
 * this pair with map.entrySet() call
 * to improve an iteration performance by avoiding search operations
 * inside a map. For this case, iteration can significantly grow up
 * a performance.
 * <p>Bad:</p>
 *     <pre>for (String key : map.keySet())
 *      {
 *          System.out.println(key + " " + map.get(key));
 *      }</pre>
 * <p>Good:</p>
 *     <pre>for (Map.Entry<String, String> entry : map.entrySet())
 *      {
 *          System.out.println(entry.getValue() + " " + entry.getKey());
 *      }</pre>
 * @author <a href="mailto:maxvetrenko2241@gmail.com">Max Vetrenko</a>
 */

public class MapIterationInForEachLoopCheck extends Check
{
    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY_KEYSET = "map.iteration.keySet";

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY_ENTRYSET = "map.iteration.entrySet";

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY_VALUES = "map.iteration.values";

    /**
     * Set of allowable map implementations. You can set your own map
     * implementations in checkstyle configuration
     */
    private final Set<String> mSupportedMapImplQualifiedNames;

    /**
     * This constant contains the name of keySet() method.
     */
    private static final String KEY_SET_METHOD_NAME = "keySet";

    /**
     * This constant contains the name of entrySet() method.
     */
    private static final String ENTRY_SET_METHOD_NAME = "entrySet";

    /**
     * This constant contains the name of get() method.
     */
    private static final String GET_NODE_NAME = "get";

    /**
     * This constant contains the name of getValue() method.
     */
    private static final String GET_VALUE_NODE_NAME = "getValue";

    /**
     * This constant contains the name of getKey() method.
     */
    private static final String GET_KEY_NODE_NAME = "getKey";

    /**
     * This list contains maps names, which was found during the class's
     * checking.
     */
    private List<String> mMapList = new ArrayList<String>();

    /**
     * This list contains Map imports paths, which was found during the class's
     * checking.
     */
    private List<String> mQualifiedImportList = new ArrayList<String>();

    /**
     * If this value is true, checkstyle will process value() iterations.
     */
    private boolean mProcessingValue = true;

    /**
     * If this value is true, checkstyle will process keySet() iterations.
     */
    private boolean mProcessingKeySet;

    /**
     * If this value is true, checkstyle will process entrySet() iterations.
     */
    private boolean mProcessingEntrySet;

    /**
     * Creates default importList and mapImportClassesNamesList.
     */
    public MapIterationInForEachLoopCheck()
    {
        mSupportedMapImplQualifiedNames = new HashSet<String>();
        setSupportedMapImplementationQualifiedNames(new String []
        {"java.util.Map", "java.util.TreeMap", "java.util.HashMap", });
    }

    /**
     * Set user's map implementations. It must state the full paths of imported
     * classes. Import paths must be separated by commas. For example:
     * java.util.Map, java.util.HashMap.
     * @param mSetSupportedMapImplementationQualifiedNames
     *        User's set of map implementations
     */
    public void setSupportedMapImplementationQualifiedNames(
            final String[] mSetSupportedMapImplementationQualifiedNames)
    {
        mSupportedMapImplQualifiedNames.clear();
        if (mSetSupportedMapImplementationQualifiedNames != null) {
            for (String name : mSetSupportedMapImplementationQualifiedNames) {
                mSupportedMapImplQualifiedNames.add(name);
                final String importPathWithoutClassName = name.substring(0,
                        name.lastIndexOf(".") + 1) + "*";
                mSupportedMapImplQualifiedNames.add(
                        importPathWithoutClassName);
            }
        }
    }

    /**
     * Set mIsCheckValueProcessingEnabled variable.
     * @param aProcessingValue
     *        User's value of mIsCheckValueProcessingEnabled
     */
    public void setProcessingValue(
            final boolean aProcessingValue)
    {
        mProcessingValue = aProcessingValue;
    }

    /**
     * Set mIsCheckKeySetProcessingEnabled variable.
     * @param aProcessingKeySet
     *        User's value of mIsCheckKeySetProcessingEnabled
     */
    public void setProcessingKeySet(
            final boolean aProcessingKeySet)
    {
        mProcessingKeySet = aProcessingKeySet;
    }

    /**
     * Set mIsCheckEntrySetProcessingEnabled variable.
     * @param aProcessingEntrySet
     *        User's value of mIsCheckEntrySetProcessingEnabled
     */
    public void setProcessingEntrySet(
            final boolean aProcessingEntrySet)
    {
        mProcessingEntrySet = aProcessingEntrySet;
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {TokenTypes.LITERAL_FOR, TokenTypes.IMPORT,
            TokenTypes.VARIABLE_DEF, };
    }

    @Override
    public void visitToken(DetailAST aAst)
    {
        switch (aAst.getType()) {

        case TokenTypes.IMPORT:
            isMapImport(aAst);
            break;

        case TokenTypes.VARIABLE_DEF:
            if (!mQualifiedImportList.isEmpty() && isMapVariable(aAst)) {
                DetailAST mapIdentNode = aAst.getLastChild()
                        .getPreviousSibling();
                mMapList.add(mapIdentNode.getText());
            }
            break;

        case TokenTypes.LITERAL_FOR:
            if (!mQualifiedImportList.isEmpty()) {
                DetailAST currentLiteralForNode = aAst;
                processForEachLoop(currentLiteralForNode);
            }
            break;
        default:
            throw new IllegalArgumentException("Argument of type "
                    + aAst.getType() + "is not supported");
        }
    }

    /**
     * This method process for cycle. It search keySet() or entrySet() nodes,
     * iterated maps, keys or entries.
     * @param aForLiteral
     *        DetailAST of literal for
     */
    private void processForEachLoop(DetailAST aForLiteral)
    {
        final DetailAST forEachNode = aForLiteral.getFirstChild()
                .getNextSibling();

        final DetailAST keySetOrEntrySetNode = getKeySetOrEntrySetNode(aForLiteral);
        // Search keySet or entrySet
        if (keySetOrEntrySetNode != null) {
            final DetailAST variableDefNode = forEachNode.getFirstChild();
            final String keyOrEntryVariableName =
                    variableDefNode.getLastChild().getText();
            final String currentMapVariableName =
                    keySetOrEntrySetNode.getPreviousSibling()
                    .getText();
            final DetailAST forEachOpeningBrace = aForLiteral
                    .getLastChild();

            if (!isMapPassedIntoAnyMethod(forEachOpeningBrace)) {

                if (mProcessingKeySet
                        && KEY_SET_METHOD_NAME.equals(
                            keySetOrEntrySetNode.getText()))
                {
                    checkForWrongKeySetUsage(forEachOpeningBrace,
                            keyOrEntryVariableName, currentMapVariableName);
                }
                if (mProcessingEntrySet) {
                    checkForWrongEntrySetUsage(forEachOpeningBrace,
                            keyOrEntryVariableName);
                }
            }
        }
    }

    /**
     * This method searches keySet() or entrySet() node.
     * @return keySet() or entrySet() node. If such node didn't found, method
     *         return null.
     */
    private DetailAST getKeySetOrEntrySetNode(DetailAST aCurrentLiteralForNode)
    {
        final DetailAST forEachNode =
                aCurrentLiteralForNode.getFirstChild().getNextSibling();
        final List<DetailAST> identList = filterTokens(forEachNode,
                TokenTypes.IDENT);
        DetailAST keySetOrEntrySetNode = null;
        for (DetailAST token : identList) {
            if (KEY_SET_METHOD_NAME.equals(token.getText())
                    || ENTRY_SET_METHOD_NAME.equals(token.getText()))
            {
                if (mMapList.contains(token.getPreviousSibling().getText())) {
                    keySetOrEntrySetNode = token;
                    break;
                }
            }
        }
        return keySetOrEntrySetNode;
    }

    /**
     * Method returns true, if any method call contains map object as parameter.
     * @param aForEachOpeningBrace
     *        List with subtree IDENT nodes
     * @return true, if any Method Call contains Map Parameter.
     */
    private boolean isMapPassedIntoAnyMethod(DetailAST aForEachOpeningBrace)
    {
        final List<DetailAST> methodCallList = filterTokens(
                aForEachOpeningBrace, TokenTypes.METHOD_CALL);
        for (DetailAST methodCallNode : methodCallList) {
            if (hasMapAsParameter(methodCallNode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method checks is map instance passed into method call, or not.
     * @param aMethodCallNode
     *        DetailAST node of Method Call
     * @return return true, if method call contain map as parameter.
     */
    private boolean hasMapAsParameter(DetailAST aMethodCallNode)
    {

        final List<DetailAST> identList = filterTokens(aMethodCallNode,
                TokenTypes.IDENT);
        for (String mapName : mMapList) {
            for (DetailAST token : identList) {
                if (mapName.equals(token.getText())
                        && token.getParent().getType() == TokenTypes.EXPR)
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method search wrong ketSet() usage into for cycles.
     * @param aForEachOpeningBrace
     *        For-each opening brace
     * @param aKeyName
     *        Map's key name
     * @param aIteratedMapName
     *        Current map name
     */
    private void checkForWrongKeySetUsage(DetailAST aForEachOpeningBrace,
            String aKeyName, String aIteratedMapName)
    {
        int methodGetCallCount = 0;
        int methodGetCallInsideIfCount = 0;
        int keyIdentCounter = 0;
        DetailAST currentLiteralForNode = aForEachOpeningBrace.getParent();
        final List<DetailAST> identAndLiteralIfList = filterTokens(
                aForEachOpeningBrace, TokenTypes.IDENT, TokenTypes.LITERAL_IF);
        for (DetailAST token : identAndLiteralIfList) {
            final DetailAST mapNode = token.getPreviousSibling();
            if (GET_NODE_NAME.equals(token.getText())
                    && aIteratedMapName.equals(mapNode.getText()))
            {
                methodGetCallCount++;
            }
            if (aKeyName.equals(token.getText())) {
                keyIdentCounter++;
            }
        }
        final DetailAST literalIfToken =
               getFirstSpecificToken(identAndLiteralIfList,
                       TokenTypes.LITERAL_IF);
        if (literalIfToken != null) {
            for (DetailAST token
                    : filterTokens(literalIfToken, TokenTypes.IDENT))
            {
                final DetailAST mapNode = token.getPreviousSibling();
                if (GET_NODE_NAME.equals(token.getText())
                        && aIteratedMapName.equals(mapNode.getText()))
                {
                    methodGetCallInsideIfCount++;
                }
            }
        }
        if (methodGetCallCount != 0 && keyIdentCounter != 0) {
            if (methodGetCallCount == keyIdentCounter
                    && mProcessingValue)
            {
                log(currentLiteralForNode, MSG_KEY_VALUES);
            }
            if (methodGetCallCount < keyIdentCounter && methodGetCallCount > 0
                    && methodGetCallInsideIfCount != methodGetCallCount)
            {
                log(currentLiteralForNode, MSG_KEY_ENTRYSET);
            }
        }
    }

    /**
     * This method search wrong entrySet() usage into for cycles.
     * @param aForEachOpeningBrace
     *        For-each opening brace
     * @param aEntryName
     *        This variable contains Map.Entry name
     */
    private void checkForWrongEntrySetUsage(DetailAST aForEachOpeningBrace,
            String aEntryName)
    {
        int methodGetKeyCallCount = 0;
        int methodGetValueCallCount = 0;
        DetailAST currentLiteralForNode = aForEachOpeningBrace.getParent();
        final List<DetailAST> identList = filterTokens(
                aForEachOpeningBrace, TokenTypes.IDENT);
        for (DetailAST token : identList) {
            final DetailAST entryNode = token.getPreviousSibling();
            if (GET_KEY_NODE_NAME.equals(token.getText())
                    && aEntryName.equals(entryNode.getText()))
            {
                methodGetKeyCallCount++;
            }
            if (GET_VALUE_NODE_NAME.equals(token.getText())
                    && aEntryName.equals(entryNode.getText()))
            {
                methodGetValueCallCount++;
            }
        }
        if (methodGetKeyCallCount == 0 && methodGetValueCallCount > 0
                && mProcessingValue)
        {
            log(currentLiteralForNode, MSG_KEY_VALUES);
        }
        if (methodGetKeyCallCount > 0 && methodGetValueCallCount == 0) {
            log(currentLiteralForNode, MSG_KEY_KEYSET);

        }
    }

    /**
     * This method checks if the new variable is Map object, or not.
     * @param aVariableDefNode
     *        DetailAST node of Variable Definition
     * @return return true, if the new variable is Map object
     */
    private boolean isMapVariable(DetailAST aVariableDefNode)
    {
        boolean result = false;
        final List<DetailAST> literaNewlList =
                    filterTokens(aVariableDefNode,
                            TokenTypes.LITERAL_NEW, TokenTypes.ASSIGN);
        if (getFirstSpecificToken(literaNewlList, TokenTypes.ASSIGN) == null) {
            return result;
        }
        String className = getClassName(literaNewlList);
        if (className != null) {
            for (String mapImplementationQualifiedName : mQualifiedImportList) {
                if (mapImplementationQualifiedName.endsWith(className)) {
                    result = true;
                    break;
                }
            }
            if (!result) {
                for (String supportedMapName
                        : mSupportedMapImplQualifiedNames)
                {
                    if (supportedMapName.endsWith(className)) {
                        final int lastDotIndex =
                                supportedMapName.lastIndexOf(".") + 1;
                        final String packageName =
                              supportedMapName.substring(0, lastDotIndex) + "*";
                        if (mQualifiedImportList.contains(packageName)) {
                            result = true;
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * This method returns the object's class name.
     * @param literaNewlList
     *        This list contains "new" literals
     * @return
     */
    private static String getClassName(final List<DetailAST> literaNewlList)
    {
        DetailAST exprNode;
        for (DetailAST literalNew : literaNewlList) {
            exprNode = literalNew.getParent();
            if (exprNode.getParent().getType() == TokenTypes.ASSIGN) {
                return literalNew.getFirstChild().getText();
            }
        }
        return null;
    }
    /**
     * This method checks is DetailAST List contains specific token, or not.
     * @param aTokenList
     *        DetailAST List witch maybe contains specific token
     * @param aSpecificType
     *        A specific type of token
     * @return true, if DetailAST List contains specific token
     */
    private static DetailAST getFirstSpecificToken(List<DetailAST> aTokenList,
            int aSpecificType)
    {
        for (DetailAST token : aTokenList) {
            if (token.getType() == aSpecificType) {
                return token;
            }
        }
        return null;
    }

    /**
     * This method add to mQualifiedNamesList full path of map implementation.
     * @param aImportNode
     *        Import node
     */
    private void isMapImport(DetailAST aImportNode)
    {
        final String mapClassQualifiedName = FullIdent.createFullIdent(
                aImportNode.getFirstChild()).getText();
        for (String qualifiedName
                : mSupportedMapImplQualifiedNames)
        {
            if (mapClassQualifiedName.equals(qualifiedName)) {
                mQualifiedImportList.add(mapClassQualifiedName);
                break;
            }
        }
    }

    /**
     * This method search over subtree all tokens of necessary types.
     * @param aRootNode
     *        The root of subtree
     * @param aTokenTypes
     *        Token's necessary types
     *        into If condition
     * @return DetailAST List with necessary tokens.
     */
    private static List<DetailAST> filterTokens(DetailAST aRootNode,
            int...aTokenTypes)
    {
        final List<DetailAST> result = new ArrayList<DetailAST>();
        final DetailAST finishNode;
        if (aRootNode.getNextSibling() == null) {
            finishNode = aRootNode.getLastChild();
        }
        else {
            finishNode = aRootNode.getNextSibling();
        }
        DetailAST curNode = aRootNode;
        while (curNode != null && curNode != finishNode) {
            for (int tokenType : aTokenTypes) {
                if (curNode.getType() == tokenType) {
                    result.add(curNode);
                }
            }
            DetailAST toVisit = curNode.getFirstChild();
            while ((curNode != null) && (toVisit == null)) {
                toVisit = curNode.getNextSibling();
                if (toVisit == null) {
                    curNode = curNode.getParent();
                }
            }
            curNode = toVisit;
        }
        return result;
    }
}
