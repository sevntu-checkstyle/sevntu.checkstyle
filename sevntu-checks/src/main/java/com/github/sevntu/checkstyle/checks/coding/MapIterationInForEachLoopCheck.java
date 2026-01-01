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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.sevntu.checkstyle.SevntuUtil;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * This check can help you to write the whole for-each map iteration more
 * correctly.
 * </p>
 * <p>
 * 1. If you iterate over a map using map.keySet() or map.entrySet(), but your
 * code uses only map values, Check will propose you to use map.values() instead
 * of map.keySet() or map.entrySet(). Replacing map.keySet() or map.entrySet()
 * with map.values() for such cases can a bit improve an iteration performance.
 * </p>
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
 * <p>
 * 2. If you iterate over a map using map.entrySet(), but never call
 * entry.getValue(), Check will propose you to use map.keySet() instead of
 * map.entrySet(). to iterate over map keys only.
 * </p>
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
 * <p>
 * 3. If you iterate over a map with map.keySet() and use both keys and values,
 * check will propose you to use map.entrySet() to improve an iteration
 * performance by avoiding search operations inside a map. For this case,
 * iteration can significantly grow up a performance.
 * </p>
 * <p>
 * Bad:
 * </p>
 *
 * <pre>
 * for (String key : map.keySet())
 * {
 *     System.out.println(key + "  " + map.get(key));
 * }
 * </pre>
 * <p>
 * Good:
 * </p>
 *
 * <pre>
 * for (Map.Entry&lt;String, String&gt;S entry : map.entrySet())
 * {
 *     System.out.println(entry.getValue() + "   " + entry.getKey());
 * }
 * </pre>
 *
 * @author <a href="mailto:maxvetrenko2241@gmail.com">Max Vetrenko</a>
 * @since 1.11.0
 */

public class MapIterationInForEachLoopCheck extends AbstractCheck {

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

    /** Path string to separate layers of packages. */
    private static final String PATH_SEPARATOR = ".";

    /** Path string to signify all classes of package. */
    private static final String PATH_WILDCARD = "*";

    /**
     * If this value is true, Checkstyle will process value() iterations.
     */
    private boolean proposeValuesUsage = true;

    /**
     * If this value is true, Checkstyle will process keySet() iterations.
     */
    private boolean proposeKeySetUsage;

    /**
     * If this value is true, Checkstyle will process entrySet() iterations.
     */
    private boolean proposeEntrySetUsage;

    /**
     * This list contains Map object's names.
     */
    private final List<String> mapNamesList = new ArrayList<>();

    /**
     * This list contains all qualified imports.
     */
    private final List<String> qualifiedImportList = new ArrayList<>();

    /**
     * Set of allowable map implementations. You can set your own map
     * implementations in Checkstyle configuration
     */
    private final Set<String> supportedMapImplQualifiedNames = new HashSet<>();

    /**
     * Creates default importList and mapImportClassesNamesList.
     */
    public MapIterationInForEachLoopCheck() {
        setSupportedMapImplQualifiedNames(new String[] {
            "java.util.Map",
            "java.util.TreeMap",
            "java.util.HashMap",
        });
    }

    /**
     * Set user's map implementations. It must state the full paths of imported
     * classes. Import paths must be separated by commas. For example:
     * java.util.Map, java.util.HashMap.
     *
     * @param setSupportedMapImplQualifiedNames
     *        User's set of map implementations.
     */
    public final void setSupportedMapImplQualifiedNames(
            final String... setSupportedMapImplQualifiedNames) {
        supportedMapImplQualifiedNames.clear();
        if (setSupportedMapImplQualifiedNames != null) {
            for (String name : setSupportedMapImplQualifiedNames) {
                supportedMapImplQualifiedNames.add(name);
                final String importPathWithoutClassName = name.substring(0,
                        name.lastIndexOf(PATH_SEPARATOR) + 1) + PATH_WILDCARD;
                supportedMapImplQualifiedNames.add(importPathWithoutClassName);
            }
        }
    }

    /**
     * Set aProcessingValue. If value is true, Check will process cases, where
     * values() method will be suitable.
     *
     * @param proposeValuesUsage
     *        User's value of mProcessingValue.
     */
    public void setProposeValuesUsage(
            final boolean proposeValuesUsage) {
        this.proposeValuesUsage = proposeValuesUsage;
    }

    /**
     * Set aProcessingKeySet. If value is true, Check will process cases, where
     * keySet() method will be suitable.
     *
     * @param proposeKeySetUsage
     *        User's value of mIsCheckKeySetProcessingEnabled.
     */
    public void setProposeKeySetUsage(
            final boolean proposeKeySetUsage) {
        this.proposeKeySetUsage = proposeKeySetUsage;
    }

    /**
     * Set aProcessingEntrySet. If value is true, Check will process cases,
     * where entrySet() method will be suitable.
     *
     * @param proposeEntrySetUsage
     *        User's value of mIsCheckEntrySetProcessingEnabled.
     */
    public void setProposeEntrySetUsage(
            final boolean proposeEntrySetUsage) {
        this.proposeEntrySetUsage = proposeEntrySetUsage;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {TokenTypes.LITERAL_FOR, TokenTypes.IMPORT, TokenTypes.VARIABLE_DEF, };
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
    public void beginTree(DetailAST ast) {
        qualifiedImportList.clear();
        mapNamesList.clear();
    }

    @Override
    public void visitToken(DetailAST ast) {
        switch (ast.getType()) {
            case TokenTypes.IMPORT:
                final String qualifiedMapImportText = getMapImportQualifiedName(ast);
                if (qualifiedMapImportText != null) {
                    qualifiedImportList.add(qualifiedMapImportText);
                }
                break;

            case TokenTypes.VARIABLE_DEF:
                if (!qualifiedImportList.isEmpty() && isMapVariable(ast)) {
                    final DetailAST mapIdentNode = ast.findFirstToken(TokenTypes.TYPE)
                            .getNextSibling();
                    final String mapName = mapIdentNode.getText();
                    // If Map name is contains into mMapNamesList, it doesn't need second inclusion
                    if (!mapNamesList.contains(mapName)) {
                        mapNamesList.add(mapIdentNode.getText());
                    }
                }
                break;

            case TokenTypes.LITERAL_FOR:
                if (!qualifiedImportList.isEmpty() && isForEach(ast)) {
                    final String warningMessageKey = validate(ast);
                    if (warningMessageKey != null) {
                        log(ast, warningMessageKey);
                    }
                }
                break;

            default:
                SevntuUtil.reportInvalidToken(ast.getType());
                break;
        }
    }

    /**
     * Processes "for-each" loop.
     * It searches for keySet() or entrySet() nodes,
     * iterated maps, keys or entries.
     *
     * @param forLiteralNode
     *        DetailAST of literal for.
     * @return warning message key.
     */
    private String validate(DetailAST forLiteralNode) {
        String warningMessageKey = null;
        final DetailAST forEachNode = forLiteralNode.findFirstToken(TokenTypes.FOR_EACH_CLAUSE);
        final DetailAST keySetOrEntrySetNode =
                getKeySetOrEntrySetNode(forEachNode);
        // Search for keySet or entrySet
        if (keySetOrEntrySetNode != null) {
            final boolean isMapClassField = keySetOrEntrySetNode.getPreviousSibling()
                    .getChildCount() != 0;

            final String currentMapVariableName;

            if (isMapClassField) {
                currentMapVariableName = keySetOrEntrySetNode.getPreviousSibling().getLastChild()
                        .getText();
            }
            else {
                currentMapVariableName = keySetOrEntrySetNode.getPreviousSibling().getText();
            }
            final DetailAST forEachOpeningBrace = forLiteralNode.getLastChild();

            if (!isMapPassedIntoAnyMethod(forEachOpeningBrace)) {
                final DetailAST variableDefNode = forEachNode.getFirstChild();
                final String keyOrEntryVariableName = variableDefNode.getLastChild().getText();

                if (proposeKeySetUsage
                        && KEY_SET_METHOD_NAME.equals(
                                keySetOrEntrySetNode.getText())) {
                    warningMessageKey =
                            checkForWrongKeySetUsage(forEachOpeningBrace,
                            keyOrEntryVariableName, currentMapVariableName, isMapClassField);
                }
                else if (proposeEntrySetUsage) {
                    warningMessageKey = checkForWrongEntrySetUsage(forEachOpeningBrace,
                            keyOrEntryVariableName);
                }
            }
        }
        return warningMessageKey;
    }

    /**
     * Checks if the not is a for each.
     *
     * @param forNode The token to examine.
     * @return true if is for each.
     */
    private static boolean isForEach(DetailAST forNode) {
        return forNode.findFirstToken(TokenTypes.FOR_EACH_CLAUSE) != null;
    }

    /**
     * Searches for keySet() or entrySet() node.
     *
     * @param forEachNode
     *        Contains current for node.
     * @return keySet() or entrySet() node. If such node didn't found, method
     *         return null.
     */
    private DetailAST getKeySetOrEntrySetNode(DetailAST forEachNode) {
        final List<DetailAST> identAndThisNodesList = getSubTreeNodesOfType(forEachNode,
                TokenTypes.IDENT, TokenTypes.LITERAL_THIS);
        boolean isMapClassField = false;
        for (DetailAST thisNode : identAndThisNodesList) {
            if (thisNode.getType() == TokenTypes.LITERAL_THIS) {
                isMapClassField = true;
                break;
            }
        }
        DetailAST keySetOrEntrySetNode = null;
        for (DetailAST identNode : identAndThisNodesList) {
            if (KEY_SET_METHOD_NAME.equals(identNode.getText())
                    || ENTRY_SET_METHOD_NAME.equals(identNode.getText())) {
                final String mapClassName;
                if (isMapClassField) {
                    final DetailAST lastChild = identNode.getPreviousSibling().getLastChild();
                    if (lastChild == null) {
                        mapClassName = null;
                    }
                    else {
                        mapClassName = lastChild.getText();
                    }
                }
                else {
                    final DetailAST previousSibling = identNode.getPreviousSibling();
                    if (previousSibling == null) {
                        mapClassName = null;
                    }
                    else {
                        mapClassName = previousSibling.getText();
                    }
                }
                if (mapNamesList.contains(mapClassName)) {
                    keySetOrEntrySetNode = identNode;
                    break;
                }
            }
        }
        return keySetOrEntrySetNode;
    }

    /**
     * Returns true, if any method call inside for loop contains map
     * object as parameter.
     *
     * @param forEachOpeningBraceNode
     *        List with subtree IDENT nodes.
     * @return true, if any Method Call contains Map Parameter.
     */
    private boolean isMapPassedIntoAnyMethod(DetailAST forEachOpeningBraceNode) {
        boolean result = false;
        final List<DetailAST> methodCallNodeList = getSubTreeNodesOfType(
                forEachOpeningBraceNode, TokenTypes.METHOD_CALL);
        for (DetailAST methodCallNode : methodCallNodeList) {
            if (hasMapAsParameter(methodCallNode)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Checks is map instance passed into method call, or not.
     *
     * @param methodCallNode
     *        DetailAST node of Method Call.
     * @return return true, if method call contain map as parameter.
     */
    private boolean hasMapAsParameter(DetailAST methodCallNode) {
        boolean result = false;
        final List<DetailAST> identNodesList = getSubTreeNodesOfType(methodCallNode,
                TokenTypes.IDENT);
        for (String mapName : mapNamesList) {
            for (DetailAST identNode : identNodesList) {
                if (mapName.equals(identNode.getText())
                        && identNode.getParent().getType() == TokenTypes.EXPR) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * Searches for wrong ketSet() usage into for cycles.
     *
     * @param forEachOpeningBraceNode
     *        For-each opening brace.
     * @param keyName
     *        Map's key name.
     * @param mapName
     *        Current map name.
     * @param isMapClassField if the map is a class field.
     * @return keySet warning message key.
     */
    private String checkForWrongKeySetUsage(DetailAST forEachOpeningBraceNode, String keyName,
            String mapName, boolean isMapClassField) {
        String result = null;

        final List<DetailAST> identAndLiteralIfNodesList =
                getSubTreeNodesOfType(forEachOpeningBraceNode,
                        TokenTypes.IDENT, TokenTypes.LITERAL_IF);
        int methodGetCallCount = 0;
        int keyIdentCount = 0;
        for (DetailAST identOrLiteralIfNode : identAndLiteralIfNodesList) {
            DetailAST mapIdentNode = identOrLiteralIfNode.getPreviousSibling();
            if (isMapClassField && mapIdentNode != null) {
                mapIdentNode = mapIdentNode.getLastChild();
            }
            if (mapIdentNode != null && GET_NODE_NAME.equals(identOrLiteralIfNode.getText())
                    && mapName.equals(mapIdentNode.getText())) {
                methodGetCallCount++;
            }

            if (keyName.equals(identOrLiteralIfNode.getText())) {
                keyIdentCount++;
            }
        }

        if (methodGetCallCount != 0 && keyIdentCount != 0) {
            if (proposeValuesUsage && methodGetCallCount == keyIdentCount) {
                result = MSG_KEY_VALUES;
            }
            else if (methodGetCallCount < keyIdentCount
                    && methodGetCallCount > 0
                    && getMethodGetCallInsideIfCount(identAndLiteralIfNodesList, mapName,
                            isMapClassField) != methodGetCallCount) {
                result = MSG_KEY_ENTRYSET;
            }
        }
        return result;
    }

    /**
     * Counts the getter methods called inside the if statement.
     *
     * @param identAndLiteralIfNodesList the nodes to examine.
     * @param mapName Current map name.
     * @param isMapClassField if the map is a class field.
     * @return The number of methods.
     */
    private static int getMethodGetCallInsideIfCount(List<DetailAST> identAndLiteralIfNodesList,
            String mapName, boolean isMapClassField) {
        final DetailAST literalIfNode =
                getFirstNodeOfType(identAndLiteralIfNodesList,
                        TokenTypes.LITERAL_IF);
        int result = 0;
        if (literalIfNode != null) {
            for (DetailAST node : getSubTreeNodesOfType(literalIfNode, TokenTypes.IDENT)) {
                DetailAST mapIdentNode = node.getPreviousSibling();
                if (isMapClassField && mapIdentNode != null) {
                    mapIdentNode = mapIdentNode.getLastChild();
                }

                if (mapIdentNode != null && GET_NODE_NAME.equals(node.getText())
                        && mapName.equals(mapIdentNode.getText())) {
                    result++;
                }
            }
        }
        return result;
    }

    /**
     * Searches for wrong entrySet() usage inside for cycles.
     *
     * @param forEachOpeningBraceNode For-each opening brace.
     * @param entryName This variable contains Map.Entry name.
     * @return entrySet warning message key.
     */
    private String checkForWrongEntrySetUsage(DetailAST forEachOpeningBraceNode,
            String entryName) {
        String result = null;

        final List<DetailAST> identNodesList = getSubTreeNodesOfType(
                forEachOpeningBraceNode, TokenTypes.IDENT);
        int methodGetKeyCallCount = 0;
        int methodGetValueCallCount = 0;
        for (DetailAST identNode : identNodesList) {
            final DetailAST entryNode = identNode.getPreviousSibling();

            if (entryNode != null && GET_KEY_NODE_NAME.equals(identNode.getText())
                    && entryName.equals(entryNode.getText())) {
                methodGetKeyCallCount++;
            }

            if (entryNode != null && GET_VALUE_NODE_NAME.equals(identNode.getText())
                    && entryName.equals(entryNode.getText())) {
                methodGetValueCallCount++;
            }
        }

        if (proposeValuesUsage
                && methodGetKeyCallCount == 0 && methodGetValueCallCount > 0) {
            result = MSG_KEY_VALUES;
        }
        else if (methodGetKeyCallCount > 0 && methodGetValueCallCount == 0) {
            result = MSG_KEY_KEYSET;
        }
        return result;
    }

    /**
     * Checks if the new variable is Map object, or not.
     *
     * @param variableDefNode
     *        DetailAST node of Variable Definition.
     * @return true, if the new variable is Map object.
     */
    private boolean isMapVariable(DetailAST variableDefNode) {
        boolean result = false;
        final List<DetailAST> literalNewNodeslList =
                getSubTreeNodesOfType(variableDefNode,
                        TokenTypes.LITERAL_NEW, TokenTypes.ASSIGN);
        final String className = getClassName(literalNewNodeslList);
        if (className != null
                && getFirstNodeOfType(literalNewNodeslList, TokenTypes.ASSIGN) != null) {
            result = isMapImplementation(className);
        }
        return result;
    }

    /**
     * Checks, is current class a Map implementation or not.
     *
     * @param className
     *        Current class's name.
     * @return true, if current class is contained inside mQualifiedImportList.
     */
    private boolean isMapImplementation(String className) {
        return isClassContainsInsideQualifiedImportList(className)
                || containsInSupportedMapImplQualifiedNames(className);
    }

    /**
     * Checks, is mSupportedMapImplQualifiedNames List contains
     * current class.
     *
     * @param className
     *        current class name.
     * @return true, if List contains current class.
     */
    private boolean containsInSupportedMapImplQualifiedNames(String className) {
        boolean result = false;
        for (String supportedMapName : supportedMapImplQualifiedNames) {
            if (supportedMapName.endsWith(className)) {
                final int lastDotIndex = supportedMapName.lastIndexOf(PATH_SEPARATOR) + 1;
                final String packageName = supportedMapName.substring(0, lastDotIndex)
                        + PATH_WILDCARD;
                if (qualifiedImportList.contains(packageName)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Checks, is mQualifiedImportList contains
     * current class.
     *
     * @param className
     *        current class name.
     * @return true, if List contains current class.
     */
    private boolean isClassContainsInsideQualifiedImportList(String className) {
        boolean result = false;
        for (String mapImplementationQualifiedName : qualifiedImportList) {
            if (mapImplementationQualifiedName.endsWith(className)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Returns the instance's class name.
     *
     * @param literalNewNodesList
     *        This list contains "new" literals.
     * @return object's class name,
     *        if class name is missed, returns null.
     */
    private static String getClassName(final List<DetailAST> literalNewNodesList) {
        String result = null;
        for (DetailAST literalNewNode : literalNewNodesList) {
            final DetailAST exprNode = literalNewNode.getParent();
            if (exprNode.getParent().getType() == TokenTypes.ASSIGN) {
                result = literalNewNode.getFirstChild().getText();
                break;
            }
        }
        return result;
    }

    /**
     * Searches the first specific
     * DetailAST node inside List.
     *
     * @param nodesList
     *        DetailAST List witch maybe contains specific token.
     * @param aSpecificType
     *        A specific type of token.
     * @return specific token or null.
     */
    private static DetailAST getFirstNodeOfType(List<DetailAST> nodesList,
            int aSpecificType) {
        DetailAST result = null;
        for (DetailAST node : nodesList) {
            if (node.getType() == aSpecificType) {
                result = node;
                break;
            }
        }
        return result;
    }

    /**
     * Returns full path of map implementation. If path doesn't
     * contain full map implementation path, null will be returned.
     *
     * @param importNode
     *        Import node.
     * @return full path of map implementation or null.
     */
    private String getMapImportQualifiedName(DetailAST importNode) {
        String result = null;
        final String mapClassQualifiedName = FullIdent.createFullIdent(
                importNode.getFirstChild()).getText();
        for (String qualifiedName : supportedMapImplQualifiedNames) {
            if (mapClassQualifiedName.equals(qualifiedName)) {
                result = mapClassQualifiedName;
                break;
            }
        }
        return result;
    }

    /**
     * Searches over subtree for all tokens of necessary types.
     *
     * @param rootNode
     *        The root of subtree.
     * @param tokenTypes
     *        Token's necessary types into If condition.
     * @return DetailAST List with necessary tokens.
     */
    private static List<DetailAST> getSubTreeNodesOfType(DetailAST rootNode,
            int... tokenTypes) {
        final List<DetailAST> result = new ArrayList<>();
        final DetailAST finishNode;
        if (rootNode.getNextSibling() == null) {
            finishNode = rootNode.getLastChild();
        }
        else {
            finishNode = rootNode.getNextSibling();
        }
        DetailAST curNode = rootNode;
        while (curNode != null && curNode != finishNode) {
            for (int tokenType : tokenTypes) {
                if (curNode.getType() == tokenType) {
                    result.add(curNode);
                }
            }
            DetailAST toVisit = curNode.getFirstChild();
            while (curNode != null && toVisit == null) {
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
