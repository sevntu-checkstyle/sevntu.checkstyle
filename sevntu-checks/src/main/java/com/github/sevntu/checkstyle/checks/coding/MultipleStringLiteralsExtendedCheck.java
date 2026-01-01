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
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.TokenUtil;

/**
 * Checks for multiple occurrences of the same string literal within a single file.
 *
 * @author Daniel Grenner
 * @since 1.5.3
 */
public class MultipleStringLiteralsExtendedCheck extends AbstractCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "multiple.string.literal";

    /**
     * The found strings and their positions. &lt;String, ArrayList&gt;, with
     * the ArrayList containing StringInfo objects.
     */
    private final Map<String, List<DetailAST>> stringMap = new HashMap<>();

    /**
     * Marks the TokenTypes where duplicate strings should be ignored.
     */
    private final BitSet ignoreOccurrenceContext = new BitSet();

    /**
     * The allowed number of string duplicates in a file before an error is generated.
     */
    private int allowedDuplicates = 1;

    /**
     * Highlight all duplicates in a file if set true.
     */
    private boolean highlightAllDuplicates;

    /**
     * Pattern for matching ignored strings.
     */
    private Pattern pattern;

    /**
     * Construct an instance with default values.
     */
    public MultipleStringLiteralsExtendedCheck() {
        setIgnoreStringsRegexp("^\"\"$");
        ignoreOccurrenceContext.set(TokenTypes.ANNOTATION);
    }

    /**
     * Sets the maximum allowed duplicates of a string.
     *
     * @param allowedDuplicates
     *            The maximum number of duplicates.
     */
    public void setAllowedDuplicates(int allowedDuplicates) {
        this.allowedDuplicates = allowedDuplicates;
    }

    /**
     * Sets regexp pattern for ignored strings.
     *
     * @param ignoreStringsRegexp
     *            regexp pattern for ignored strings
     */
    public final void setIgnoreStringsRegexp(String ignoreStringsRegexp) {
        if (ignoreStringsRegexp != null
                && ignoreStringsRegexp.length() > 0) {
            pattern = Pattern.compile(ignoreStringsRegexp);
        }
        else {
            pattern = null;
        }
    }

    /**
     * Sets highlight for all duplicates or only first.
     *
     * @param highlightAllDuplicates
     *            if true show all duplicates
     */
    public final void setHighlightAllDuplicates(
            final boolean highlightAllDuplicates) {
        this.highlightAllDuplicates = highlightAllDuplicates;
    }

    /**
     * Adds a set of tokens the check is interested in.
     *
     * @param strRep
     *            the string representation of the tokens interested in
     */
    public final void setIgnoreOccurrenceContext(String... strRep) {
        ignoreOccurrenceContext.clear();
        for (final String s : strRep) {
            final int type = TokenUtil.getTokenId(s);
            ignoreOccurrenceContext.set(type);
        }
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.STRING_LITERAL,
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
        if (!isInIgnoreOccurrenceContext(ast)) {
            final String currentString = ast.getText();
            if (pattern == null || !pattern.matcher(currentString).find()) {
                stringMap
                    .computeIfAbsent(currentString, key -> new ArrayList<>())
                    .add(ast);
            }
        }
    }

    /**
     * Analyses the path from the AST root to a given AST for occurrences of the token types in
     * {@link #ignoreOccurrenceContext}.
     *
     * @param ast
     *            the node from where to start searching towards the root node
     * @return whether the path from the root node to aAST contains one of the token type in
     *         {@link #ignoreOccurrenceContext}.
     */
    private boolean isInIgnoreOccurrenceContext(DetailAST ast) {
        boolean result = false;
        DetailAST token = ast.getParent();
        while (token != null) {
            final int type = token.getType();
            if (ignoreOccurrenceContext.get(type)) {
                result = true;
                break;
            }
            token = token.getParent();
        }
        return result;
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        super.beginTree(rootAST);
        stringMap.clear();
    }

    @Override
    public void finishTree(DetailAST rootAST) {
        final Set<String> keys = stringMap.keySet();
        for (String key : keys) {
            final List<DetailAST> hits = stringMap.get(key);
            if (hits.size() > allowedDuplicates) {
                int hitsSize = 1;
                if (highlightAllDuplicates) {
                    hitsSize = hits.size();
                }
                for (int index = 0; index < hitsSize; index++) {
                    final DetailAST firstFinding = hits.get(index);
                    log(firstFinding,
                            MSG_KEY, key, hits.size());
                }
            }
        }
    }

}
