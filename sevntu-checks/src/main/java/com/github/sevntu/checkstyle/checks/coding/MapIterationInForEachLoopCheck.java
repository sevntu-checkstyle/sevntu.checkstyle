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
 * <p>
 * This check can help you to write the whole for-each map iteration more
 * correctly:
 * </p>
 * <p>
 * 1. If you iterate over a map using map.keySet() or map.entrySet(), but your
 * code uses only map values, Check will propose you to use map.values() instead
 * of map.keySet() or map.entrySet(). Replacing map.keySet() or map.entrySet()
 * with map.values() for such cases can a bit improve an iteration performance.
 * <p>
 * Bad:
 * </p>
 *
 * <pre>
 * for (Map.Entry&lt;String, String&gt; entry : map.entrySet())
 * {
 *     System.out.println(entry.getValue());
 * }
 * </pre>
 *
 * <pre>
 * for (String key : map.keySet())
 * {
 *     System.out.println(map.get(key));
 * }
 * </pre>
 * <p>
 * Good:
 * </p>
 *
 * <pre>
 * for (String value : map.values())
 * {
 *     System.out.println(value);
 * }
 * </pre>
 *
 * 2. If you iterate over a map using map.entrySet(), but never call
 * entry.getValue(), Check will propose you to use map.keySet() instead of
 * map.entrySet(). to iterate over map keys only.
 * <p>
 * Bad:
 * </p>
 *
 * <pre>
 * for (Map.Entry&lt;String, String&gt; entry : map.entrySet())
 * {
 *     System.out.println(entry.getKey());
 * }
 * </pre>
 * <p>
 * Good:
 * </p>
 *
 * <pre>
 * for (String key : map.keySet())
 * {
 *     System.out.println(key);
 * }
 * </pre>
 *
 * 3. If you iterate over a map with map.keySet() and use both keys and values,
 * check will propose you to use map.entrySet() to improve an iteration
 * performance by avoiding search operations inside a map. For this case,
 * iteration can significantly grow up a performance.
 * <p>
 * Bad:
 * </p>
 *
 * <pre>
 * for (String key : map.keySet())
 * {
 *     System.out.println(key + &quot; &quot; + map.get(key));
 * }
 * </pre>
 * <p>
 * Good:
 * </p>
 *
 * <pre>
 * for (Map.Entry&lt;String, String&gt; entry : map.entrySet())
 * {
 *     System.out.println(entry.getValue() + &quot; &quot; + entry.getKey());
 * }
 * </pre>
 * @author <a href="mailto:maxvetrenko2241@gmail.com">Max Vetrenko</a>
 */

public class MapIterationInForEachLoopCheck extends Check
{
    /**
     * If this value is true, Checkstyle will process value() iterations.
     */
    private boolean mProposeValuesUsage = true;

    /**
     * If this value is true, Checkstyle will process keySet() iterations.
     */
    private boolean mProposeKeySetUsage = false;

    /**
     * If this value is true, Checkstyle will process entrySet() iterations.
     */
    private boolean mProposeEntrySetUsage = false;

    /**
     * The key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY_KEYSET = "map.iteration.keySet";

    /**
     * The key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY_ENTRYSET = "map.iteration.entrySet";

    /**
     * The key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY_VALUES = "map.iteration.values";

    /**
     * The name of keySet() method.
     */
    private static final String KEY_SET_METHOD_NAME = "keySet";

    /**
     * The name of entrySet() method.
     */
    private static final String ENTRY_SET_METHOD_NAME = "entrySet";

    /**
     * The name of get() method.
     */
    private static final String GET_NODE_NAME = "get";

    /**
     * The name of getValue() method.
     */
    private static final String GET_VALUE_NODE_NAME = "getValue";

    /**
     * The name of getKey() method.
     */
    private static final String GET_KEY_NODE_NAME = "getKey";

    /**
     * This list contains Map object's names.
     */
    private List<String> mMapNamesList = new ArrayList<String>();

    /**
     * This list contains all qualified Map implementations paths.
     */
    private List<String> mQualifiedImportList = new ArrayList<String>();

    /**
     * Set of allowable map implementations. You can set your own map
     * implementations in Checkstyle configuration
     */
    private final Set<String> mSupportedMapImplQualifiedNames;

    /**
     * Creates default importList and mapImportClassesNamesList.
     */
    public MapIterationInForEachLoopCheck()
    {
        mSupportedMapImplQualifiedNames = new HashSet<String>();
        setSupportedMapImplQualifiedNames(new String[] {
            "java.util.Map", "java.util.TreeMap", "java.util.HashMap", });
    }

    /**
     * Set user's map implementations. It must state the full paths of imported
     * classes. Import paths must be separated by commas. For example:
     * java.util.Map, java.util.HashMap.
     * @param aSetSupportedMapImplQualifiedNames
     *        User's set of map implementations
     */
    public void setSupportedMapImplQualifiedNames(
            final String[] aSetSupportedMapImplQualifiedNames)
    {
        mSupportedMapImplQualifiedNames.clear();
        if (aSetSupportedMapImplQualifiedNames != null) {
            for (String name : aSetSupportedMapImplQualifiedNames) {
                mSupportedMapImplQualifiedNames.add(name);
                final String importPathWithoutClassName = name.substring(0,
                        name.lastIndexOf(".") + 1) + "*";
                mSupportedMapImplQualifiedNames.add(
                        importPathWithoutClassName);
            }
        }
    }

    /**
     * Set aProcessingValue. If value is true, Check will process cases, where
     * values() method will be suitable.
     * @param aProposeValuesUsage
     *        User's value of mProcessingValue
     */
    public void setProposeValuesUsage(
            final boolean aProposeValuesUsage)
    {
        this.mProposeValuesUsage = aProposeValuesUsage;
    }

    /**
     * Set aProcessingKeySet. If value is true, Check will process cases, where
     * keySet() method will be suitable.
     * @param aProposeKeySetUsage
     *        User's value of mIsCheckKeySetProcessingEnabled
     */
    public void setProposeKeySetUsage(
            final boolean aProposeKeySetUsage)
    {
        this.mProposeKeySetUsage = aProposeKeySetUsage;
    }

    /**
     * Set aProcessingEntrySet. If value is true, Check will process cases,
     * where entrySet() method will be suitable.
     * @param aProposeEntrySetUsage
     *        User's value of mIsCheckEntrySetProcessingEnabled
     */
    public void setProposeEntrySetUsage(
            final boolean aProposeEntrySetUsage)
    {
        this.mProposeEntrySetUsage = aProposeEntrySetUsage;
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
            String qualifiedMapImportText = getMapImportQualifiedName(aAst);
            if (qualifiedMapImportText != null) {
                mQualifiedImportList.add(qualifiedMapImportText);
            }
            break;

        case TokenTypes.VARIABLE_DEF:
            if (!mQualifiedImportList.isEmpty() && isMapVariable(aAst)) {
                DetailAST mapIdentNode = aAst.getLastChild()
                        .getPreviousSibling();
                mMapNamesList.add(mapIdentNode.getText());
            }
            break;

        case TokenTypes.LITERAL_FOR:
            if (!mQualifiedImportList.isEmpty()) {
                final String messageKey = processForEachLoop(aAst);
                if (messageKey != null) {
                    log(aAst, messageKey);
                }
            }
            break;

        default:
            throw new IllegalArgumentException("Argument of type "
                    + aAst.getType() + "is not supported");
        }
    }

    /**
     * This method processes "for-each" loop.
     * It search keySet() or entrySet() nodes, iterated maps, keys or entries.
     * @param aForLiteralNode
     *        DetailAST of literal for
     */
    private String processForEachLoop(DetailAST aForLiteralNode)
    {
        String warningMessageKey = null;
        final DetailAST forEachNode = aForLiteralNode.getFirstChild()
                .getNextSibling();

        final DetailAST keySetOrEntrySetNode =
                getKeySetOrEntrySetNode(aForLiteralNode);
        // Search for keySet or entrySet
        if (keySetOrEntrySetNode != null) {
            final DetailAST variableDefNode = forEachNode.getFirstChild();
            final String keyOrEntryVariableName =
                    variableDefNode.getLastChild().getText();
            final String currentMapVariableName =
                    keySetOrEntrySetNode.getPreviousSibling()
                            .getText();
            final DetailAST forEachOpeningBrace = aForLiteralNode
                    .getLastChild();

            if (!isMapPassedIntoAnyMethod(forEachOpeningBrace)) {

                if (mProposeKeySetUsage
                        && KEY_SET_METHOD_NAME.equals(
                                keySetOrEntrySetNode.getText()))
                {
                    warningMessageKey =
                            checkForWrongKeySetUsage(forEachOpeningBrace,
                            keyOrEntryVariableName, currentMapVariableName);
                }
                else if (mProposeEntrySetUsage) {
                    warningMessageKey =
                            checkForWrongEntrySetUsage(forEachOpeningBrace,
                            keyOrEntryVariableName);
                }
            }
        }
        return warningMessageKey;
    }

    /**
     * This method searches keySet() or entrySet() node.
     * @param aCurrentLiteralForNode
     *        Contains current for node
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
        for (DetailAST ident : identList) {
            if (KEY_SET_METHOD_NAME.equals(ident.getText())
                    || ENTRY_SET_METHOD_NAME.equals(ident.getText()))
            {
                String mapClassName = ident.getPreviousSibling().getText();
                if (mMapNamesList.contains(mapClassName)) {
                    keySetOrEntrySetNode = ident;
                    break;
                }
            }
        }
        return keySetOrEntrySetNode;
    }

    /**
     * Method returns true, if any method call inside for loop contains map
     * object as parameter.
     * @param aForEachOpeningBrace
     *        List with subtree IDENT nodes
     * @return true, if any Method Call contains Map Parameter.
     */
    private boolean isMapPassedIntoAnyMethod(DetailAST aForEachOpeningBrace)
    {
        final List<DetailAST> methodCallNodeList = filterTokens(
                aForEachOpeningBrace, TokenTypes.METHOD_CALL);
        for (DetailAST methodCallNode : methodCallNodeList) {
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
        for (String mapName : mMapNamesList) {
            for (DetailAST ident : identList) {
                if (mapName.equals(ident.getText())
                        && ident.getParent().getType() == TokenTypes.EXPR)
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method searches wrong ketSet() usage into for cycles.
     * @param aForEachOpeningBrace
     *        For-each opening brace
     * @param aKeyName
     *        Map's key name
     * @param aMapName
     *        Current map name
     */
    private String checkForWrongKeySetUsage(DetailAST aForEachOpeningBrace,
            String aKeyName, String aMapName)
    {
        String result = null;

        final List<DetailAST> identAndLiteralIfList = filterTokens(
                aForEachOpeningBrace, TokenTypes.IDENT, TokenTypes.LITERAL_IF);
        int methodGetCallCount = 0;
        int keyIdentCount = 0;
        for (DetailAST identOrLiteralIf : identAndLiteralIfList) {
            final DetailAST mapIdentNode =
                    identOrLiteralIf.getPreviousSibling();

            if (GET_NODE_NAME.equals(identOrLiteralIf.getText())
                    && aMapName.equals(mapIdentNode.getText()))
            {
                methodGetCallCount++;
            }

            if (aKeyName.equals(identOrLiteralIf.getText())) {
                keyIdentCount++;
            }
        }

        final DetailAST literalIfToken =
                getFirstSpecificToken(identAndLiteralIfList,
                        TokenTypes.LITERAL_IF);
        int methodGetCallInsideIfCount = 0;
        if (literalIfToken != null) {
            for (DetailAST token : filterTokens(literalIfToken,
                    TokenTypes.IDENT))
            {
                final DetailAST mapIdentNode = token.getPreviousSibling();

                if (GET_NODE_NAME.equals(token.getText())
                        && aMapName.equals(mapIdentNode.getText()))
                {
                    methodGetCallInsideIfCount++;
                }
            }
        }

        if (methodGetCallCount != 0 && keyIdentCount != 0) {

            if (mProposeValuesUsage && methodGetCallCount == keyIdentCount) {
                result = MSG_KEY_VALUES;
            }

            else if (methodGetCallCount < keyIdentCount
                    && methodGetCallCount > 0
                    && methodGetCallInsideIfCount != methodGetCallCount)
            {
                result = MSG_KEY_ENTRYSET;
            }
        }
        return result;
    }

    /**
     * This method searches wrong entrySet() usage into for cycles.
     * @param aForEachOpeningBrace
     *        For-each opening brace
     * @param aEntryName
     *        This variable contains Map.Entry name
     */
    private String checkForWrongEntrySetUsage(DetailAST aForEachOpeningBrace,
            String aEntryName)
    {
        String result = null;

        final List<DetailAST> identList = filterTokens(
                aForEachOpeningBrace, TokenTypes.IDENT);
        int methodGetKeyCallCount = 0;
        int methodGetValueCallCount = 0;
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

        if (mProposeValuesUsage
                && methodGetKeyCallCount == 0 && methodGetValueCallCount > 0)
        {
            result = MSG_KEY_VALUES;
        }

        else if (methodGetKeyCallCount > 0 && methodGetValueCallCount == 0) {
            result = MSG_KEY_KEYSET;

        }
        return result;
    }

    /**
     * This method checks if the new variable is Map object, or not.
     * @param aVariableDefNode
     *        DetailAST node of Variable Definition
     * @return true, if the new variable is Map object
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
            result = isMapImplementation(className);
        }
        return result;
    }

    /**
     * This method checks, is current class a Map implementation or not.
     * @param aClassName
     *        Current class's name
     * @return true, if current class is contained inside mQualifiedImportList
     */
    private boolean isMapImplementation(String aClassName)
    {
        return containsInQualified(aClassName)
                || containsInSupportedMapImplQualifiedNames(aClassName);
    }

    /**
     * This method checks, is mSupportedMapImplQualifiedNames List contains
     * current class.
     * @param aClassName
     *        current class name
     * @return true, if List contains current class
     */
    private boolean containsInSupportedMapImplQualifiedNames(String aClassName)
    {
        boolean result = false;
        for (String supportedMapName : mSupportedMapImplQualifiedNames) {
            if (supportedMapName.endsWith(aClassName)) {
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
        return result;
    }

    /**
     * This method checks, is mQualifiedImportList contains
     * current class.
     * @param aClassName
     *        current class name
     * @return true, if List contains current class
     */
    private boolean containsInQualified(String aClassName)
    {
        boolean result = false;
        for (String mapImplementationQualifiedName : mQualifiedImportList) {
            if (mapImplementationQualifiedName.endsWith(aClassName)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * This method returns the instance's class name.
     * @param aLiteraNewlList
     *        This list contains "new" literals
     * @return object's class name,
     *        if class name is missed, returns null
     */
    private static String getClassName(final List<DetailAST> aLiteraNewlList)
    {
        for (DetailAST literalNewNode : aLiteraNewlList) {
            DetailAST exprNode = literalNewNode.getParent();
            if (exprNode.getParent().getType() == TokenTypes.ASSIGN) {
                return literalNewNode.getFirstChild().getText();
            }
        }
        return null;
    }

    /**
     * This method searches the first specific
     * DetailAST token inside List.
     * @param aTokenList
     *        DetailAST List witch maybe contains specific token
     * @param aSpecificType
     *        A specific type of token
     * @return specific token or null.
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
     * This method returns full path of map implementation. If path doesn't
     * contain full map implementation path, null will be returned.
     * @param aImportNode
     *        Import node
     * @return full path of map implementation or null.
     */
    private String getMapImportQualifiedName(DetailAST aImportNode)
    {
        final String mapClassQualifiedName = FullIdent.createFullIdent(
                aImportNode.getFirstChild()).getText();
        for (String qualifiedName : mSupportedMapImplQualifiedNames) {
            if (mapClassQualifiedName.equals(qualifiedName)) {
                return mapClassQualifiedName;
            }
        }
        return null;
    }

    /**
     * This method searches over subtree for all tokens of necessary types.
     * @param aRootNode
     *        The root of subtree
     * @param aTokenTypes
     *        Token's necessary types into If condition
     * @return DetailAST List with necessary tokens.
     */
    private static List<DetailAST> filterTokens(DetailAST aRootNode,
            int... aTokenTypes)
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
