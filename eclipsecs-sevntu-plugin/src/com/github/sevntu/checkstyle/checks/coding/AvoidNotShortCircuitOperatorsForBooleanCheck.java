package com.github.sevntu.checkstyle.checks.coding;

import java.util.LinkedList;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.checks.CheckUtils;

/**
 * <p>
 * This check limits using of not short-circuit operators
 * ("|", "&", "|=", "&=") in boolean expressions.<br>
 * <br>
 * Reason: <br>
 * &nbsp&nbsp&nbsp&nbsp&nbsp Short-circuit operators ("||", "&&") are more
 * safer and can accelerate the evaluation of complex boolean expressions.
 * Check identifies an expression as a boolean if it contains at least one
 * boolean operand or if result of expression evaluation sets the value of a
 * boolean variable.
 * <br><br>&nbsp&nbsp&nbsp&nbsp&nbsp Using boolean variables that do not belong
 * to the current class and all calls to boolean methods are not handled by
 * this check. <br><br> Examples: <br>
 * <br>
 * <ol>
 * <li>Using of not short-circuit operators while determining a Boolean variable
 * </li> <samp>
 * <pre>
 * boolean x = true;
 * boolean result = true | x || false; // a warning here
 * </pre>
 * </samp>
 * <li>Using of not short-circuit operators while overriding a Boolean variable.
 * </li> <samp>
 * <pre>
 * boolean x = true;
 * boolean result = false;
 * // any code
 * result &amp;= true | x || false; // a warning here
 * </pre>
 * </samp>
 * <li>Expression calculated with not short-circuit operators contains at least
 * one boolean operand.</li>
 * <samp>
 * <pre>
 * public boolean isTrue() {
 *     return this.z | MyObject.is() // no warnings here
 *             || isModifier() &amp;&amp; isNotTrue();
 * }
 * ...
 * boolean r=true;
 * public boolean isTrue() {
 *     return this.z | true && r // a warning here
 *             || isModifier() &amp;&amp; isNotTrue();
 * }
 * </pre>
 * </samp>
 * </ol>
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class AvoidNotShortCircuitOperatorsForBooleanCheck extends Check
{

    /**
     * A "boolean" String.
     * */
    private static final String mBOOLEAN = "boolean";

    /**
     * A key to search the warning message text in "messages.properties" file.
     * */
    private static final String mKey = "avoid.not.short.circuit.operators.for.boolean";

    /**
     * A list contains all names of operands, which are used in the current
     * expression, which calculates with using "|", "&", "|=", "&=" operators.
     * */
    private final LinkedList<String> mSupportedOperands =
        new LinkedList<String>();

    /**
     * Variable, that indicates keywords "true" or "false" in current
     * expression.
     * */
    private boolean mHasTrueOrFalseLiteral;

    @Override
    public final int[] getDefaultTokens()
    {
        return new int[] {TokenTypes.BOR, TokenTypes.BAND,
            TokenTypes.BOR_ASSIGN, TokenTypes.BAND_ASSIGN, };
    }

    @Override
    public final void visitToken(final DetailAST aDetailAST)
    {

        DetailAST currentNode = aDetailAST;
        while (currentNode != null
                && currentNode.getType() != TokenTypes.EXPR
                && currentNode.getType() != TokenTypes.METHOD_DEF
                && currentNode.getType() != TokenTypes.CTOR_DEF
                && currentNode.getType() != TokenTypes.CLASS_DEF)
        {
            currentNode = currentNode.getParent();
        }

        final int type = currentNode.getType();

        if (type == TokenTypes.EXPR) {

            if (isBooleanExpression(currentNode)) {
                log(aDetailAST, mKey, aDetailAST.getText());
            }

            mSupportedOperands.clear();
            mHasTrueOrFalseLiteral = false;
        }

    }

    /**
     * Checks whether the current method/variable definition type
     * is "Boolean".
     * @param aNode - current method or variable definition node.
     * @return "true" if current method or variable has a Boolean type.
     */
    public final boolean isBooleanType(final DetailAST aNode)
    {
        return mBOOLEAN.equals(CheckUtils.createFullType(
                aNode.findFirstToken(TokenTypes.TYPE)).getText());
    }

    /**
     * Checks that current expression is calculated using "|", "&", "|=", "&="
     * operators contains at least one Boolean operand.
     * @param aNode - current TokenTypes.EXPR node to check.
     * @return "true" if current expression is calculated using "|", "&",
     * "|=". "&=" operators contains at least one Boolean operand or false
     * otherwise.
     */
    public final boolean isBooleanExpression(final DetailAST aNode)
    {

        DetailAST curNode = aNode;

        final LinkedList<String> childNames =
            getSupportedOperandsNames(curNode);
        final LinkedList<String> booleanVariablesNames =
            new LinkedList<String>();

        while (curNode != null
                && curNode.getType() != TokenTypes.CTOR_DEF
                && curNode.getType() != TokenTypes.METHOD_DEF
                && curNode.getType() != TokenTypes.CLASS_DEF)
        {
            curNode = curNode.getParent();
        }

        final int line = aNode.getLineNo();
        for (DetailAST currentNode : getChildren(curNode.getLastChild())) {
            if (currentNode.getLineNo() < line
                    && currentNode.getType() == TokenTypes.VARIABLE_DEF)
            {

                if (isBooleanType(currentNode)) {
                    booleanVariablesNames.add(currentNode.findFirstToken(
                            TokenTypes.IDENT).getText());
                }
            }
        }

        boolean result = false;

        for (String name : childNames) {
            if (booleanVariablesNames.contains(name)) {
                result = true;
                break;
            }
        }
        result = result || hasTrueOrFalseLiteral(aNode);
        return result;
    }

    /** Searches for all supported operands names in current expression.
     * When checking, treatments to external class variables, method calls,
     * etc are not considered as expression operands.
     * @param aEXPRParentAST - the current TokenTypes.EXPR parent node.
     * @return List of supported operands contained in current expression.
     */
    public final LinkedList<String> getSupportedOperandsNames(
            final DetailAST aEXPRParentAST)
    {

        for (DetailAST currentNode : getChildren(aEXPRParentAST)) {

            if (currentNode.getNumberOfChildren() > 0
                    && currentNode.getType() != TokenTypes.METHOD_CALL)
            {
                getSupportedOperandsNames(currentNode);
            }

            if (currentNode.getType() == TokenTypes.IDENT
                    && currentNode.getParent() != null
                    && currentNode.getParent().getType() != TokenTypes.DOT)
            {
                mSupportedOperands.add(currentNode.getText());
            }

            if (currentNode.getNextSibling() != null) {
                currentNode = currentNode.getNextSibling();
            }
        }
        return mSupportedOperands;
    }


    /**
     * Checks is the current expression has
     * keywords "true" or "false".
     * @param aParentAST - the current TokenTypes.EXPR parent node.
     * @return true if the current processed expression contains
     * "true" or "false" keywords and false otherwise.
     */
    public final boolean hasTrueOrFalseLiteral(final DetailAST aParentAST)
    {

        for (DetailAST currentNode : getChildren(aParentAST)) {

            if (currentNode.getNumberOfChildren() > 0) {
                hasTrueOrFalseLiteral(currentNode);
            }

            final int type = currentNode.getType();
            if (type == TokenTypes.LITERAL_TRUE
                    || type == TokenTypes.LITERAL_FALSE)
            {
                mHasTrueOrFalseLiteral = true;
            }

            if (currentNode.getNextSibling() != null) {
                currentNode = currentNode.getNextSibling();
            }

            if (mHasTrueOrFalseLiteral) {
                break;
            }
        }
        return mHasTrueOrFalseLiteral;
    }

    /**
     * Gets all the children one level below on the current top node.
     * @param aNode - current parent node.
     * @return an array of children one level below on the current parent node
     *         aNode.
     */
    public final LinkedList<DetailAST> getChildren(final DetailAST aNode)
    {
        final LinkedList<DetailAST> result = new LinkedList<DetailAST>();

        DetailAST currNode = aNode.getFirstChild();

        while (currNode != null) {
            result.add(currNode);
            currNode = currNode.getNextSibling();
        }

        return result;
    }

}
