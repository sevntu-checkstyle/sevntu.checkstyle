////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2016 the original author or authors.
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

package com.github.sevntu.checkstyle.filters;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.github.sevntu.checkstyle.checks.DetailAstRootHolder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AutomaticBean;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.Filter;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * A filter that uses defined annotations to suppress defined audit events.
 * </p>
 * <p>
 * Structurally based on SupressionCommentFilter.
 * </p>
 * <p>
 * Usage:
 * </p>
 *
 * <p>1. Make sure that AST is available to the filter by enabling DetailASTRootHolder:<br/>
 * <pre>
 * &lt;module name="TreeWalker"&gt;
 *     ...
 *     &lt;module name="DetailASTRootHolder"/&gt;
 *     ...
 * &lt;/module&gt;
 * </pre>
 * </p>
 *
 * <p>2. To configure a filter to suppress audit events for a certain annotated entities add:
 * <br/>
 * <pre>
 * &lt;module name="Checker"&gt;
 *     ...
 *     &lt;module name="SuppressionAnnotationFilter"&gt;
 *         &lt;property name="annotationNames" value="MyAnnotation, Generated"/&gt;
 *     &lt;/module&gt;
 *     ...
 * &lt;/module&gt;
 *      </pre>
 * "annotationNames" defines a list of suppressor annotations, they may start
 * from '@' symbol or not. Annotation names also may be given in a fully-qualified form,
 * but internally they will be reduced to a simple form, and any annotation with that simple
 * name will match. One may find particularly useful to suppress generated code marked with
 * {@code @Generated} annotation, see
 * <a href = "https://docs.oracle.com/javase/7/docs/api/javax/annotation/Generated.html">
 * https://docs.oracle.com/javase/7/docs/api/javax/annotation/Generated.html </a>
 * </p>
 *
 * <p>3.To configure a filter to suppress audit events for a certain annotated entities but
 * allow passing of some checks through filter add:<br/>
 * <pre>
 * &lt;module name="Checker&gt;
 *      ...
 *      &lt;module name="SuppressionAnnotationFilter"&gt;
 *          &lt;property name="annotationNames" value="MyAnnotation, Generated"/&gt;
 *          &lt;property name="checkNames" value=".*Name.*"/&gt;
 *      &lt;/module&gt;
 *      ...
 * &lt;/module&gt;
 * </pre>
 * "checkNames" defines a regular expression which must be found in name of the check
 * in order to pass through.
 * </p>
 *
 * <p>4.It is possible to avoid usage of regular expressions and simply list permitted
 * check's names:<br/>
 * <pre>
 * &lt;module name="Checker&gt;
 *      ...
 *      &lt;module name="SuppressionAnnotationFilter"&gt;
 *          &lt;property name="annotationNames" value="MyAnnotation, Generated"/&gt;
 *          &lt;property name="checkNames" value="ThrowsCountCheck, FinalClassCheck"/&gt;
 *      &lt;/module&gt;
 *      ...
 * &lt;/module&gt;
 * </pre>
 * </p>
 *
 * <p>5.In context of this filter annotations themselves, javadocs, access modifiers
 * and other modifiers such as 'synchronized', 'transient' are
 * called 'modifiers' and by default excluded from action of the filter.
 * To add them into suppressed ranges,
 * set "modifiersExcluded" to "false". Default value is "true":<br/>
 *  <pre>
 * &lt;module name="Checker&gt;
 *      ...
 *      &lt;module name="SuppressionAnnotationFilter"&gt;
 *          &lt;property name="annotationNames" value="MyAnnotation, Generated"/&gt;
 *          &lt;property name="modifiersExcluded" value="false"/&gt;
 *      &lt;/module&gt;
 *      ...
 * &lt;/module&gt;
 * </pre>
 * </p>
 * @author attatrol
 * @see com.puppycrawl.tools.checkstyle.filters.SupressionCommentFilter
 * @see com.github.sevntu.checkstyle.checks.DetailASTRootHolder
 */
public class SuppressionAnnotationFilter extends AutomaticBean implements Filter {

    /** Set containing names of suppressive annotations. */
    private Set<String> annotationNames = Sets.newHashSet();

    /** The check format to suppress. */
    private Set<String> checkNames = Sets.newHashSet();

    /** Excludes modifiers from check if set true. */
    private boolean modifiersExcluded = true;

    /** The parsed check regexp, expanded for the text of this tag. */
    private List<Pattern> checkRegexp = Lists.newArrayList();

    /** Ranges of tokens being suppressed. */
    private List<SuppressRange> ranges = Lists.newArrayList();

    /**
     * Reference to the root of the current AST. Since this is a weak reference to the AST, it can
     * be reclaimed as soon as the strong references in TreeWalker are reassigned to the next AST,
     * at which time filtering for the current AST is finished.
     */
    private WeakReference<DetailAST> currentASTRootRef = new WeakReference<>(null);

    /**
     * Sets the names of suppressive annotations.
     * @param annotations
     *        , the set of file extensions.
     */
    public void setAnnotationNames(String... annotations) {
        for (String str : annotations) {
            annotationNames.add(getSimpleName(str));
        }
    }

    /**
     * Getter for annotationsExcluded.
     * @return excludeAnnotations, excludes annotations from check if set true.
     */
    public boolean isModifiersExcluded() {
        return modifiersExcluded;
    }

    /**
     * Setter for annotationsExcluded.
     * @param excluded
     *        , set value for excludeAnnotations
     */
    public void setModifiersExcluded(boolean excluded) {
        modifiersExcluded = excluded;
    }

    /**
     * Setter for checkNames.
     * @param checks
     *        , contains patterns for non-suppressed checks.
     */
    public void setCheckNames(String... checks) {
        for (String string : checks) {
            checkNames.add(string);
        }
    }

    /**
     * {@inheritDoc} Creates checkRegexp from checkFormat.
     */
    @Override
    protected void finishLocalSetup()
            throws CheckstyleException {
        for (String string : checkNames) {
            try {
                checkRegexp.add(Pattern.compile(string));
            }
            catch (final PatternSyntaxException ex) {
                throw new CheckstyleException("unable to compile check names regex " + string, ex);
            }
        }
    }

    /**
     * {@inheritDoc} Filters event.
     */
    @Override
    public boolean accept(AuditEvent event) {
        final boolean accept;
        if (event.getLocalizedMessage() == null || matchesCheck(event)) {
            accept = true;
        }
        else {
            final DetailAST currentRoot = DetailAstRootHolder.getASTRootReference();
            if (currentRoot == null) {
                accept = true;
            }
            else {
                resetLocalData(currentRoot);
                accept = !isIncludedInSuppressedRanges(event);
            }
        }
        return accept;
    }

    /**
     * Produces simple form from the annotation name.
     * @param annotationName
     *        , incoming annotation name.
     * @return simple form of the annotation name.
     */
    private static String getSimpleName(String annotationName) {
        final String simpleName;
        if (annotationName.charAt(0) == '@') {
            simpleName = annotationName.substring(1);
        }
        else {
            simpleName = annotationName;
        }
        final int lastIndexOfPoint = simpleName.lastIndexOf('.');
        return simpleName.substring(lastIndexOfPoint + 1);
    }

    /**
     * If weak reference from DetailASTRootHolder gets update
     * then resets ranges of suppression according to new AST.
     * @param currentRoot
     *        , current AST root reference.
     */
    private void resetLocalData(DetailAST currentRoot) {
        final DetailAST localRoot = currentASTRootRef.get();
        if (currentRoot != localRoot) {
            currentASTRootRef =
                    new WeakReference<DetailAST>(
                        DetailAstRootHolder.getASTRootReference());
            //clearing ranges here is a preparation for a recursive reset.
            ranges.clear();
            traverseAst(currentRoot);
        }
    }

    /**
     * Checks if event coordinates belong to some range in the set.
     * @param event
     *        , audited event.
     * @return result of this check.
     */
    private boolean isIncludedInSuppressedRanges(AuditEvent event) {
        boolean isIncluded = false;
        final int eventColumn = event.getColumn();
        final int eventLine = event.getLine();
        for (SuppressRange range : ranges) {
            if (range.isInRange(eventLine, eventColumn)) {
                isIncluded = true;
                break;
            }
        }
        return isIncluded;
    }

    /**
     * Searches AST for new ranges, where audit events are suppressed.
     * @param currentRoot
     *        , AST tree node, should be the root node.
     */
    private void traverseAst(DetailAST currentRoot) {
        // it was already checked that node != null,
        // in accept method, in case of root AST.
        if (!findSuppressedRange(currentRoot)) {
            final DetailAST child = currentRoot.getFirstChild();
            if (child != null) {
                traverseAst(child);
            }
        }
        final DetailAST sibling = currentRoot.getNextSibling();
        if (sibling != null) {
            traverseAst(sibling);
        }
    }

    /**
     * Checks if this node has suppressive annotation, if it has, adds range occupied by it into
     * range list.
     * @param node
     *        , AST tree node.
     * @return if node was suppressed.
     */
    private boolean findSuppressedRange(DetailAST node) {
        boolean belongsToSuppressedRange = false;
        final DetailAST modifiers = node.findFirstToken(TokenTypes.MODIFIERS);
        if (modifiers != null) {
            DetailAST annotation = modifiers.findFirstToken(TokenTypes.ANNOTATION);
            while (annotation != null) {
                if (annotation.getType() == TokenTypes.ANNOTATION) {
                    final String name = getAnnotationName(annotation);
                    if (annotationNames.contains(name)) {
                        ranges.add(getSuppressRange(node));
                        belongsToSuppressedRange = true;
                        break;
                    }
                }
                annotation = annotation.getNextSibling();
            }
        }
        return belongsToSuppressedRange;
    }

    /**
     * Recovers string name of annotation from tree.
     * @param annotation
     *        , node which is annotation.
     * @return simple name of the annotation.
     */
    private static String getAnnotationName(DetailAST annotation) {
        DetailAST node = annotation.findFirstToken(TokenTypes.AT).getNextSibling();
        final DetailAST returnNode = node;
        while (node.getType() != TokenTypes.IDENT) {
            node = node.getFirstChild();
        }
        String simpleName = node.getText();
        if (node.getNextSibling() != null) {
            while (node != returnNode) {
                simpleName = node.getNextSibling().getText();
                node = node.getParent();
            }
        }
        return simpleName;
    }

    /**
     * Generates a range occupied by the node in the list of all ranges which are to suppress.
     * @param node
     *        , that is annotated with suppressive annotation.
     * @return new SuppressRange instance.
     */
    private SuppressRange getSuppressRange(DetailAST node) {
        final DetailAST startNode = getStartNode(node);
        final int startColumn;
        if (columnIndexIsNecessary(node)) {
            startColumn = startNode.getColumnNo();
        }
        else {
            startColumn = 0;
        }
        final int startLine = startNode.getLineNo();

        final DetailAST endNode = getEndNode(node);
        final int endLine = endNode.getLineNo();
        final int endColumn = endNode.getColumnNo() + 1;
        return new SuppressRange(startLine, startColumn, endLine, endColumn);
    }

    /**
     * Get node which starts suppressed range.
     * @param node
     *         , the node to be suppressed.
     * @return start border node.
     */
    private DetailAST getStartNode(DetailAST node) {
        final DetailAST result;
        if (isModifiersExcluded()) {
            // it was already checked that grand-child exists.
            result = node.getFirstChild().getNextSibling();
        }
        else {
            result = node;
        }
        return result;
    }

    /**
     * Get the rightmost lowest node, which is the end border element of the suppressed node.
     * @param node
     *         , the node to be suppressed.
     * @return end border node.
     */
    private static DetailAST getEndNode(DetailAST node) {
        DetailAST nextNode = node.getFirstChild();
        final DetailAST result;
        if (nextNode == null) {
            result = node;
        }
        else {
            DetailAST probe = nextNode;
            while (true) {
                probe = probe.getNextSibling();
                if (probe == null) {
                    break;
                }
                else {
                    nextNode = probe;
                }
            }
            result = getEndNode(nextNode);
        }
        return result;
    }

    /**
     * A lot of checks return only number of the line, and number of the column is zeroed. It is
     * supposed that code is already formatted and a chance that events which don't address to the
     * annotated element are generated on one string with the annotated element can be neglected.
     * The obvious exception is a parameter's definition.
     * @param node
     *         , the node to be suppressed.
     * @return true
     *          , if the start column index should not be set 0.
     */
    private static boolean columnIndexIsNecessary(DetailAST node) {
        return node.getType() == TokenTypes.PARAMETER_DEF;
    }

    /**
     * Checks if a check that generated the event, is not suppressed.
     * @param event
     *        , the event under audit.
     * @return result of check.
     */
    private boolean matchesCheck(AuditEvent event) {
        boolean result = false;
        for (Pattern pat : checkRegexp) {
            final Matcher matcher = pat.matcher(event.getSourceName());
            result |= matcher.matches();
        }
        return result;
    }

    /**
     * A SuppressRange is a POJO that holds start and end ranges of an element
     * annotated with a suppressive annotation.
     */
    public static class SuppressRange {
        /** Index of the first line of a suppressed range. */
        private final int startLine;

        /** Index of the first column of a suppressed range. */
        private final int startColumn;

        /** Index of the last line of a suppressed range. */
        private final int endLine;

        /** Index of the last column of a suppressed range. */
        private final int endColumn;

        /**
         * Default constructor.
         * @param startLine
         *        , index of the first line of a suppressed range.
         * @param startColumn
         *        , index of the first column of a suppressed range.
         * @param endLine
         *        , index of the last line of a suppressed range.
         * @param endColumn
         *        , index of the last column of a suppressed range.
         */
        public SuppressRange(int startLine, int startColumn, int endLine, int endColumn) {
            this.startColumn = startColumn;
            this.startLine = startLine;
            this.endColumn = endColumn;
            this.endLine = endLine;
        }

        /**
         * Checks if coordinates of an event fall in the range.
         * @param eventLine
         *        , index line of the event.
         * @param eventColumn
         *        , index of column of the event.
         * @return result of the check.
         */
        public boolean isInRange(int eventLine, int eventColumn) {
            return (eventLine > startLine || eventLine == startLine && eventColumn >= startColumn)
                    && (eventLine < endLine || eventLine == endLine && eventColumn <= endColumn);
        }
    }
}
