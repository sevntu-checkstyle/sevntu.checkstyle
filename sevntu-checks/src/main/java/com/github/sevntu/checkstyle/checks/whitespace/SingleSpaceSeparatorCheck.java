package com.github.sevntu.checkstyle.checks.whitespace;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
* <p>
* Checks that if tokens are separated by whitespaces, it has to be a single space.
* Separating tokens by tabs or multiple spaces will be reported. Currently the
* check doesn't permit horizontal alignment. To inspect whitespaces before and after
* comments, set the property validateCommentNodes to true.
* </p>
* <pre>
* &lt;module name="SingleSpaceSeparator"&gt;
*   &lt;property name="validateCommentNodes" value="false"/&gt;
* &lt;/module&gt;
* </pre>
* <p>
* Setting validateCommentNodes to false will ignore cases like:
* </p>
* <pre>
* int i;  &#47;&#47; Multiple whitespaces before comment tokens will be ignored.
* private void foo(int  &#47;* whitespaces before and after block-comments will be ignored *&#47;  i) {
* </pre>
* <p>
* Since horizontal alignment isn't supported yet, following cases will be reported.
* </p>
* <pre>
* public long toNanos(long d)   { return d; }
* public long toMicros(long d)  { return d/(C1/C0); }
* </pre>
*/
public class SingleSpaceSeparatorCheck extends Check {

    public static final String MSG_KEY = "single.space.separator";

    private boolean validateCommentNodes = false;

    public void setValidateCommentNodes(boolean validateCommentNodes) {
        this.validateCommentNodes = validateCommentNodes;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[]{TokenTypes.EOF};
    }

    @Override
    public boolean isCommentNodesRequired() {
        return validateCommentNodes;
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        visitEachToken(rootAST);
    }

    private void visitEachToken(DetailAST ast) {
        DetailAST sibling = ast;
        while (sibling != null) {
            if (!isSeparatedCorrectlyFromPreviousToken(
                    sibling, getLine(sibling.getLineNo() - 1), validateCommentNodes)) {
                log(sibling.getLineNo(), sibling.getColumnNo() - 1, MSG_KEY);
            }
            if (sibling.getChildCount() > 0) {
                visitEachToken(sibling.getFirstChild());
            }
            sibling = sibling.getNextSibling();
        }
    }

    /**
     * Return true if
     * - the token is the first token in the line.
     * - the token is not separated by whitespaces from the previous token.
     * - if the token is separated by whitespaces from the previous token, it is separated by a single space.
     * - if validateCommentNodes is disabled and the previous token is a block comment end.
     *
     * @param ast, the token to check
     * @param line, the line the ast is on
     * @return true TODO.
     */
    private static boolean isSeparatedCorrectlyFromPreviousToken(DetailAST ast, String line, boolean validateCommentNodes) {
        return isPrecededBySingleSpace(ast, line)
                || !isPrecededByWhitespace(ast, line)
                || isFirstTokenInLine(ast, line)
                || (!validateCommentNodes && isPreviousTokenBlockCommentEnd(ast, line));
    }

    private static boolean isPrecededBySingleSpace(DetailAST ast, String line) {
        return !isPrecededByMultipleWhitespaces(ast, line) && isPrecededBySpace(ast, line);
    }

    private static boolean isPrecededBySpace(DetailAST ast, String line) {
        return ast.getColumnNo() > 0 && line.charAt(ast.getColumnNo() - 1) == ' ';
    }

    private static boolean isPrecededByMultipleWhitespaces(DetailAST ast, String line) {
        return ast.getColumnNo() > 1
                && Character.isWhitespace(line.charAt(ast.getColumnNo() - 1))
                && Character.isWhitespace(line.charAt(ast.getColumnNo() - 2));
    }

    private static boolean isPrecededByWhitespace(DetailAST ast, String line) {
        return ast.getColumnNo() > 0 && Character.isWhitespace(line.charAt(ast.getColumnNo() - 1));
    }

    private static boolean isFirstTokenInLine(DetailAST ast, String line) {
        return line.substring(0, ast.getColumnNo()).trim().length() == 0;
    }

    private static boolean isPreviousTokenBlockCommentEnd(DetailAST ast, String line) {
        return line.substring(0, ast.getColumnNo() - 1).trim().endsWith("*/");
    }
}
