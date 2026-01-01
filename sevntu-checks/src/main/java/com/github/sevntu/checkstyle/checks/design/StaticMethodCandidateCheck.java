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

package com.github.sevntu.checkstyle.checks.design;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.ScopeUtil;
import com.puppycrawl.tools.checkstyle.utils.TokenUtil;

/**
 * Checks whether {@code private} methods can be declared as {@code static}.
 *
 * <p>The check has option {@code skippedMethods} which allows to specify the
 * list of comma separated names of methods to skip during the check. By default
 * the private methods which a class can have when it implements
 * <a href="https://docs.oracle.com/javase/7/docs/api/java/io/Serializable.html">
 * Serializable</a> are skipped: "readObject, writeObject, readObjectNoData, readResolve,
 * writeReplace".
 *
 * <p>The following configuration allows to skip method {@code foo} and {@code bar}:
 * <pre>
 *     &lt;module name=&quot;NestedSwitchCheck&quot;&gt;
 *         &lt;property name=&quot;skippedMethods&quot; value=&quot;foo, bar&quot;/&gt;
 *     &lt;/module&gt;
 * </pre>
 *
 * <p>Limitations:
 * <ul>
 * <li>
 * Due to limitation of Checkstyle, there is no ability to distinguish
 * overloaded methods, so we skip them from candidates.
 * </li>
 * <li>
 * Private methods called by reflection are not supported and have to be suppressed.
 * </li>
 * </ul>
 *
 * @author Vladislav Lisetskiy
 * @since 1.17.0
 */
public class StaticMethodCandidateCheck extends AbstractCheck {

    /** Warning message key. */
    public static final String MSG_KEY = "static.method.candidate";

    /** Comma literal. */
    private static final String COMMA_SEPARATOR = ",";

    /** Default method names to skip during the check. */
    private static final String[] DEFAULT_SKIPPED_METHODS = {
        "writeObject",
        "readObject",
        "readObjectNoData",
        "readResolve",
        "writeReplace",
    };

    /** Array of tokens which are frames. */
    private static final int[] FRAME_TOKENS = {
        TokenTypes.CLASS_DEF,
        TokenTypes.METHOD_DEF,
        TokenTypes.LITERAL_IF,
        TokenTypes.LITERAL_FOR,
        TokenTypes.LITERAL_WHILE,
        TokenTypes.LITERAL_DO,
        TokenTypes.LITERAL_CATCH,
        TokenTypes.LITERAL_TRY,
        TokenTypes.ENUM_DEF,
        TokenTypes.ENUM_CONSTANT_DEF,
        TokenTypes.STATIC_INIT,
        TokenTypes.INSTANCE_INIT,
        TokenTypes.CTOR_DEF,
        TokenTypes.INTERFACE_DEF,
    };

    /** Parent token types of children where we do not check idenifiers. */
    private static final int[] PARENT_TYPES_NOT_TO_BE_CHECKED = {
        TokenTypes.LITERAL_NEW,
        TokenTypes.TYPE,
        TokenTypes.METHOD_DEF,
        TokenTypes.TYPE_ARGUMENT,
    };

    /** Method names to skip during the check. */
    private List<String> skippedMethods = Arrays.asList(DEFAULT_SKIPPED_METHODS);

    /** Stack of sets of field names, one for each class of a set of nested classes. */
    private Frame currentFrame;

    /**
     * Sets custom skipped methods.
     *
     * @param skippedMethods user's skipped methods.
     */
    public void setSkippedMethods(String skippedMethods) {
        final List<String> customSkippedMethods = new ArrayList<>();
        final String[] splitSkippedMethods = skippedMethods.split(COMMA_SEPARATOR);
        for (String skippedMethod : splitSkippedMethods) {
            customSkippedMethods.add(skippedMethod.trim());
        }
        this.skippedMethods = customSkippedMethods;
    }

    @Override
    public int[] getAcceptableTokens() {
        return new int[] {
            TokenTypes.CLASS_DEF,
            TokenTypes.METHOD_DEF,
            TokenTypes.LITERAL_IF,
            TokenTypes.LITERAL_FOR,
            TokenTypes.LITERAL_WHILE,
            TokenTypes.LITERAL_DO,
            TokenTypes.LITERAL_CATCH,
            TokenTypes.LITERAL_TRY,
            TokenTypes.VARIABLE_DEF,
            TokenTypes.PARAMETER_DEF,
            TokenTypes.ENUM_DEF,
            TokenTypes.ENUM_CONSTANT_DEF,
            TokenTypes.EXPR,
            TokenTypes.STATIC_INIT,
            TokenTypes.INSTANCE_INIT,
            TokenTypes.LITERAL_NEW,
            TokenTypes.LITERAL_THIS,
            TokenTypes.CTOR_DEF,
            TokenTypes.TYPE,
            TokenTypes.TYPE_ARGUMENT,
            TokenTypes.TYPE_PARAMETER,
            TokenTypes.INTERFACE_DEF,
            TokenTypes.LITERAL_SUPER,
        };
    }

    @Override
    public int[] getDefaultTokens() {
        return getAcceptableTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return getAcceptableTokens();
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        currentFrame = new Frame(null);

        Arrays.sort(FRAME_TOKENS);
    }

    @Override
    public void visitToken(final DetailAST ast) {
        switch (ast.getType()) {
            case TokenTypes.VARIABLE_DEF:
            case TokenTypes.PARAMETER_DEF:
                currentFrame.addField(ast);
                break;
            case TokenTypes.EXPR:
                currentFrame.addExpr(ast);
                break;
            case TokenTypes.LITERAL_SUPER:
            case TokenTypes.LITERAL_THIS:
                currentFrame.hasLiteralThisOrSuper = true;
                break;
            case TokenTypes.TYPE:
            case TokenTypes.TYPE_ARGUMENT:
                final Optional<DetailAST> firstChild = Optional.ofNullable(ast.getFirstChild());
                if (firstChild.isPresent()
                        && firstChild.get().getType() == TokenTypes.IDENT) {
                    currentFrame.addType(firstChild.get().getText());
                }
                break;
            case TokenTypes.TYPE_PARAMETER:
                currentFrame.addTypeVariable(ast.getFirstChild().getText());
                break;
            case TokenTypes.METHOD_DEF:
                Frame frame = createMethodFrame(currentFrame, ast);

                currentFrame.addMethod(ast);
                currentFrame.addChild(frame);
                currentFrame = frame;
                break;
            case TokenTypes.LITERAL_NEW:
                if (isAnonymousClass(ast)) {
                    frame = new Frame(currentFrame);
                    // anonymous classes can't have static methods
                    frame.isShouldBeChecked = false;

                    currentFrame.addChild(frame);
                    currentFrame = frame;
                }
                break;
            case TokenTypes.ENUM_CONSTANT_DEF:
                frame = new Frame(currentFrame);
                // ENUM_CONSTANT_DEF can't have static methods
                frame.isShouldBeChecked = false;

                currentFrame.addEnumConst(ast);
                currentFrame.addChild(frame);
                currentFrame = frame;
                break;
            default:
                frame = createFrame(currentFrame, ast);

                currentFrame.addChild(frame);
                currentFrame = frame;
                break;
        }
    }

    @Override
    public void leaveToken(DetailAST ast) {
        if (isFrame(ast)
                || isAnonymousClass(ast)) {
            currentFrame = currentFrame.parent;
        }
    }

    @Override
    public void finishTree(DetailAST ast) {
        // result of checkFrame() is only used while checking methods and not needed here
        // as we start from the root of the Frame tree
        checkFrame(currentFrame);
    }

    /**
     * Create a new Frame from METHOD_DEF ast.
     *
     * @param parentFrame the parent frame for a new frame.
     * @param ast METHOD_DEF ast.
     * @return a new frame with the set fields.
     */
    private Frame createMethodFrame(Frame parentFrame, DetailAST ast) {
        final DetailAST modifiersAst = ast.findFirstToken(TokenTypes.MODIFIERS);
        final String methodName = ast.findFirstToken(TokenTypes.IDENT).getText();
        final Frame frame = new Frame(parentFrame);
        if (modifiersAst.findFirstToken(TokenTypes.LITERAL_PRIVATE) != null
                && modifiersAst.findFirstToken(TokenTypes.LITERAL_STATIC) == null
                && !skippedMethods.contains(methodName)) {
            frame.isPrivateMethod = true;
            frame.ast = ast;
            frame.frameName = getIdentText(ast);
        }
        else {
            // non-private or static methods are not checked and can't have static methods
            // because local classes cannot be declared as static
            frame.isShouldBeChecked = false;
        }
        return frame;
    }

    /**
     * Check whether the ast is an anonymous class.
     *
     * @param ast the ast to check.
     * @return if the checked ast is an anonymous class.
     */
    private static boolean isAnonymousClass(DetailAST ast) {
        final int astType = ast.getType();
        return astType == TokenTypes.LITERAL_NEW
                && ast.findFirstToken(TokenTypes.OBJBLOCK) != null;
    }

    /**
     * Create a new Frame from CLASS_DEF, LITERAL_IF, LITERAL_FOR, LITERAL_WHILE, LITERAL_DO,
     * LITERAL_CATCH, LITERAL_TRY, CTOR_DEF, ENUM_DEF.
     *
     * @param parentFrame the parent frame for a new frame.
     * @param ast the processed ast.
     * @return a new frame with the set fields.
     */
    private static Frame createFrame(Frame parentFrame, DetailAST ast) {
        final Frame frame = new Frame(parentFrame);
        final int astType = ast.getType();
        if (astType == TokenTypes.CLASS_DEF
                || astType == TokenTypes.ENUM_DEF) {
            if (astType == TokenTypes.CLASS_DEF
                    && !ScopeUtil.isOuterMostType(ast)
                    && !hasStaticModifier(ast)) {
                // local and inner classes can't have static methods
                frame.isShouldBeChecked = false;
            }
            frame.frameName = getIdentText(ast);
            frame.isClassOrEnum = true;
        }
        else if (astType == TokenTypes.STATIC_INIT
                || astType == TokenTypes.INSTANCE_INIT
                || astType == TokenTypes.CTOR_DEF
                || astType == TokenTypes.INTERFACE_DEF) {
            frame.isShouldBeChecked = false;
        }
        return frame;
    }

    /**
     * Check whether the ast is a Frame.
     *
     * @param ast the ast to check.
     * @return true if the checked ast is a Frame.
     */
    private static boolean isFrame(DetailAST ast) {
        final int astType = ast.getType();
        return Arrays.binarySearch(FRAME_TOKENS, astType) >= 0;
    }

    /**
     * Check whether the frame or its parent, which is a private method,
     * is a static method candidate.
     *
     * @param parentFrame the frame to check.
     * @return true if the frame or its parent, which is a private method,
     *     is a static method candidate.
     */
    private boolean checkFrame(Frame parentFrame) {
        boolean isStaticCandidate = true;
        for (Frame frame: parentFrame.children) {
            if (frame.isShouldBeChecked) {
                isStaticCandidate = checkFrame(frame);
                if (!frame.isClassOrEnum) {
                    isStaticCandidate = isStaticCandidate
                            && !frame.hasLiteralThisOrSuper
                            && isFrameExpressionsAcceptable(frame)
                            && isFrameTypesAcceptable(frame);
                    if (frame.isPrivateMethod) {
                        if (isStaticCandidate) {
                            log(frame.ast, MSG_KEY, frame.frameName);
                        }
                    }
                    else if (!isStaticCandidate) {
                        break;
                    }
                }
            }
        }
        return isStaticCandidate;
    }

    /**
     * Get the name of the field.
     *
     * @param field to get the name from.
     * @return name of the field.
     */
    private static String getIdentText(DetailAST field) {
        return field.findFirstToken(TokenTypes.IDENT).getText();
    }

    /**
     * Whether the ast has static modifier.
     *
     * @param ast the ast to check.
     * @return true if the ast has static modifier.
     */
    private static boolean hasStaticModifier(DetailAST ast) {
        return ast.findFirstToken(TokenTypes.MODIFIERS)
            .findFirstToken(TokenTypes.LITERAL_STATIC) != null;
    }

    /**
     * Check expressions in the given frame for being acceptable is static methods.
     *
     * @param frame the frame to check.
     * @return true if the currently checked method
     *     is still a static method candidate.
     */
    private static boolean isFrameExpressionsAcceptable(final Frame frame) {
        return frame.expressions.stream()
                .filter(expr -> !isExprAcceptable(frame, expr))
                .findFirst()
                .isEmpty();
    }

    /**
     * Check types in the given frame for being acceptable in static methods.
     *
     * @param frame the frame to check.
     * @return true if the currently checked method
     *     is still a static method candidate.
     */
    private static boolean isFrameTypesAcceptable(final Frame frame) {
        final Predicate<String> predicate = frameName -> {
            final Optional<Frame> typeFrame = findFrameByName(frame, frameName);
            return typeFrame.isPresent()
                    && !typeFrame.get().isShouldBeChecked || findTypeVariable(frame, frameName);
        };
        return frame.types.stream()
            .filter(predicate)
            .findFirst()
            .isEmpty();
    }

    /**
     * Check whether the expression only contains fields and method calls accepted
     * in static methods (which can be checked).
     *
     * @param frame the frame where the expression is located.
     * @param expr the expression to check.
     * @return true if the currently checked method
     *     is still a static method candidate.
     */
    private static boolean isExprAcceptable(Frame frame, DetailAST expr) {
        boolean isStaticCandidate = true;
        if (expr.branchContains(TokenTypes.IDENT)) {
            DetailAST childAst = expr.getFirstChild();
            while (childAst != null
                    && isStaticCandidate) {
                if (childAst.getType() == TokenTypes.METHOD_CALL) {
                    isStaticCandidate = isStaticMethod(frame, childAst)
                            && isExprAcceptable(frame, childAst);
                }
                else if (childAst.getType() == TokenTypes.IDENT
                        && isIdentShouldBeChecked(expr)) {
                    isStaticCandidate = isStaticFieldOrLocalVariable(frame, childAst);
                }
                else if (childAst.getType() == TokenTypes.LITERAL_NEW) {
                    final Optional<Frame> typeFrame = findFrameByName(
                            frame, childAst.getFirstChild().getText());
                    isStaticCandidate = isTypeFrameShouldBeChecked(typeFrame)
                            && isExprAcceptable(frame, childAst);
                }
                else {
                    isStaticCandidate = isExprAcceptable(frame, childAst);
                }
                childAst = childAst.getNextSibling();
            }
        }
        return isStaticCandidate;
    }

    /**
     * Find a frame with the specified name among the current frame and its parents.
     *
     * @param frame the frame to start searching from.
     * @param frameName the specified name.
     * @return search result.
     */
    private static Optional<Frame> findFrameByName(Frame frame, String frameName) {
        Optional<Frame> result = Optional.empty();
        Optional<Frame> parentFrame = Optional.of(frame.parent);
        while (parentFrame.isPresent() && result.isEmpty()) {
            for (Frame child: parentFrame.get().children) {
                if (child.isClassOrEnum
                        && frameName.equals(child.frameName)) {
                    result = Optional.of(child);
                    break;
                }
            }
            parentFrame = Optional.ofNullable(parentFrame.get().parent);
        }
        return result;
    }

    /**
     * Find a type variable with the specified name.
     *
     * @param frame the frame to start searching from.
     * @param type the name of the type variable to find.
     * @return true if a type variable with the specified name is found.
     */
    private static boolean findTypeVariable(Frame frame, String type) {
        boolean result = false;
        Optional<Frame> searchFrame = Optional.of(frame);
        while (!result && searchFrame.isPresent()) {
            result = searchFrame.get().typeVariables.contains(type);
            searchFrame = Optional.ofNullable(searchFrame.get().parent);
        }
        return result;
    }

    /**
     * Check whether a {@code static} method is called.
     *
     * @param frame the frame where the method call is located.
     * @param methodCallAst METHOD_CALL ast.
     * @return true if a {@code static} method is called.
     */
    private static boolean isStaticMethod(Frame frame, DetailAST methodCallAst) {
        boolean result = false;
        final DetailAST firstChild = methodCallAst.getFirstChild();
        if (firstChild.getType() == TokenTypes.DOT) {
            final DetailAST objCalledOn = getTheLeftmostIdent(methodCallAst);
            if (objCalledOn.getType() == TokenTypes.IDENT) {
                final Optional<DetailAST> field = findField(frame, objCalledOn);
                if (field.isPresent()) {
                    result = isAcceptableField(field.get());
                }
                else if (findFrameByName(frame, objCalledOn.getText()).isPresent()) {
                    result = true;
                }
            }
            else {
                result = true;
            }
        }
        else {
            result = findStaticMethod(frame, methodCallAst, firstChild.getText());
        }
        return result;
    }

    /**
     * Determine whether the method call should be checked.
     *
     * @param parentAst parent ast of the ident.
     * @return true, if LITERAL_THIS is used or the usage is too complex to check.
     */
    private static boolean isIdentShouldBeChecked(DetailAST parentAst) {
        return !TokenUtil.isOfType(parentAst, PARENT_TYPES_NOT_TO_BE_CHECKED);
    }

    /**
     * Check whether a {@code static} field or a local variable is used.
     *
     * @param frame the frame where the field is located.
     * @param identAst the identifier ast of the checked field.
     * @return true if the field is {@code static} or local.
     */
    private static boolean isStaticFieldOrLocalVariable(Frame frame, DetailAST identAst) {
        final boolean result;
        final int parentType = identAst.getParent().getType();
        if (parentType == TokenTypes.DOT) {
            if (identAst.getNextSibling() == null) {
                result = true;
            }
            else {
                final Optional<DetailAST> field = findField(frame, identAst);
                if (field.isPresent()) {
                    result = isAcceptableField(field.get());
                }
                else {
                    result = findFrameByName(frame, identAst.getText()).isPresent();
                }
            }
        }
        else if (parentType == TokenTypes.METHOD_CALL) {
            result = true;
        }
        else {
            final Optional<DetailAST> field = findField(frame, identAst);
            result = field.isPresent() && isAcceptableField(field.get());
        }
        return result;
    }

    /**
     * Whether the type frame should be checked.
     *
     * @param typeFrame the frame of the type to check.
     * @return true if the type frame should be checked.
     */
    private static boolean isTypeFrameShouldBeChecked(final Optional<Frame> typeFrame) {
        return typeFrame.isEmpty()
                    || typeFrame.get().isShouldBeChecked;
    }

    /**
     * Get the leftmost ident of the method call.
     *
     * @param mCall METHOD_CALL to get ident from.
     * @return the leftmost's ident DetailAST.
     */
    private static DetailAST getTheLeftmostIdent(DetailAST mCall) {
        DetailAST result = mCall.getFirstChild();
        while (result.getChildCount() != 0
                && result.getType() != TokenTypes.METHOD_CALL) {
            result = result.getFirstChild();
        }
        return result;
    }

    /**
     * Find a static field definition or local variable.
     *
     * @param startFrame the frame to start searching from.
     * @param identAst the IDENT ast to check.
     * @return search result.
     */
    private static Optional<DetailAST> findField(Frame startFrame, DetailAST identAst) {
        Optional<DetailAST> result = Optional.empty();
        Optional<Frame> frame = Optional.of(startFrame);
        final String fieldName = identAst.getText();
        while (frame.isPresent() && result.isEmpty()) {
            final Optional<DetailAST> field = frame.get().findFieldInFrame(fieldName);
            if (field.isPresent()) {
                if (!isLocalVariable(field.get())
                        || checkFieldLocation(field.get(), identAst)) {
                    result = field;
                }
            }
            else {
                result = frame.get().findEnumConstInFrame(fieldName);
            }
            frame = Optional.ofNullable(frame.get().parent);
        }
        return result;
    }

    /**
     * Check whether the field is acceptable is a {@code static} method.
     *
     * @param field the checked field.
     * @return true if the checked field is acceptable is a {@code static} method.
     */
    private static boolean isAcceptableField(DetailAST field) {
        boolean result = false;
        if (isLocalVariable(field)
                || field.getType() == TokenTypes.ENUM_CONSTANT_DEF
                || hasStaticModifier(field)) {
            result = true;
        }
        return result;
    }

    /**
     * Find a {@code static} method definition of the specified method call
     * and ensure that there are no non-{@code static} methods with the same name and
     * number of parameters in the current frame or its parents.
     *
     * @param startFrame the frame to start searching from.
     * @param methodCall METHOD_CALL ast.
     * @param checkedMethodName the name of the called method.
     * @return true if a {@code static} method definition of the specified method call
     *     is found and no non-{@code static} with the same name and number of parameters found.
     */
    private static boolean findStaticMethod(Frame startFrame, DetailAST methodCall,
                                            String checkedMethodName) {
        final int argsNumber = methodCall.findFirstToken(TokenTypes.ELIST).getChildCount();
        Optional<Frame> frame = Optional.of(startFrame);

        // if we do not find neither static nor non-static method, then we cannot claim
        // that the checked method can be static
        boolean hasNonStaticMethod = false;
        boolean hasStaticMethod = false;
        while (!hasNonStaticMethod
                && frame.isPresent()) {
            for (DetailAST method: frame.get().methods) {
                final DetailAST parametersAst = method.findFirstToken(TokenTypes.PARAMETERS);

                if (checkedMethodName.equals(getIdentText(method))
                        && (parametersAst.getChildCount() == argsNumber
                            || parametersAst.branchContains(TokenTypes.ELLIPSIS))) {
                    final DetailAST modifiersAst = method.findFirstToken(TokenTypes.MODIFIERS);

                    if (modifiersAst.findFirstToken(TokenTypes.LITERAL_STATIC) == null) {
                        // if a non-static method is found, then the checked method
                        // cannot be static
                        hasNonStaticMethod = true;
                        break;
                    }

                    // if a static method is found, we keep searching for a similar
                    // non-static one to the end of the frame and if a non-static
                    // method is not found, then the checked method is still
                    // a static method candidate
                    hasStaticMethod = true;
                }
            }
            frame = Optional.ofNullable(frame.get().parent);
        }
        return hasStaticMethod
                && !hasNonStaticMethod;
    }

    /**
     * Check whether the field is a local variable.
     *
     * @param ast VARIABLE_DEF ast.
     * @return true if the field is a local variable.
     */
    private static boolean isLocalVariable(DetailAST ast) {
        final int parentType = ast.getParent().getParent().getType();
        return parentType != TokenTypes.CLASS_DEF
                && parentType != TokenTypes.ENUM_DEF;
    }

    /**
     * Check whether the field is declared before its usage in case of methods.
     *
     * @param field field to check.
     * @param objCalledOn object equals method called on.
     * @return true if the field is declared before the method call.
     */
    private static boolean checkFieldLocation(DetailAST field, DetailAST objCalledOn) {
        boolean result = false;
        if (field.getLineNo() < objCalledOn.getLineNo()
                || field.getLineNo() == objCalledOn.getLineNo()
                    && field.getColumnNo() < objCalledOn.getColumnNo()) {
            result = true;
        }
        return result;
    }

    /**
     * Contains information about the frame.
     */
    private static class Frame {

        /** Name of the class, enum or method. */
        private String frameName;

        /** Parent frame. */
        private final Frame parent;

        /** List of frame's children. */
        private final List<Frame> children = new LinkedList<>();

        /** List of fields. */
        private final List<DetailAST> fields = new LinkedList<>();

        /** List of methods. */
        private final List<DetailAST> methods = new LinkedList<>();

        /** List of typeVariables. */
        private final List<String> typeVariables = new LinkedList<>();

        /** List of method calls. */
        private final List<DetailAST> expressions = new ArrayList<>();

        /** List of types. */
        private final Set<String> types = new HashSet<>();

        /** Set of enumConstants. */
        private final Set<DetailAST> enumConstants = new HashSet<>();

        /** Whether the frame is CLASS_DEF or ENUM_DEF. */
        private boolean isClassOrEnum;

        /** Whether the frame is {@code private} METHOD_DEF. */
        private boolean isPrivateMethod;

        /**
         * Whether the frame should be checked.
         * It is used in order not to check non-private methods, static methods
         * and frames, where static methods cannot be defined: local and inner classes,
         * constructors, anonymous classes, enum constant definitions, initializers.
         */
        private boolean isShouldBeChecked = true;

        /** Whether the frame has LITERAL_THIS or LITERAL_SUPER. */
        private boolean hasLiteralThisOrSuper;

        /** AST where the frame is declared. */
        private DetailAST ast;

        /**
         * Creates new frame.
         *
         * @param parent parent frame.
         */
        /* package */ Frame(Frame parent) {
            this.parent = parent;
        }

        /**
         * Add method call to this Frame.
         *
         * @param exprAst EXPR ast.
         */
        public void addExpr(DetailAST exprAst) {
            expressions.add(exprAst);
        }

        /**
         * Add child frame to this frame.
         *
         * @param child frame to add.
         */
        public void addChild(Frame child) {
            children.add(child);
        }

        /**
         * Add field to this Frame.
         *
         * @param field the ast of the field.
         */
        public void addField(DetailAST field) {
            fields.add(field);
        }

        /**
         * Add method definition to this frame.
         *
         * @param method METHOD_DEF ast.
         */
        public void addMethod(DetailAST method) {
            methods.add(method);
        }

        /**
         * Add method call to this frame.
         *
         * @param enumConst ENUM_CONST_DEF ast.
         */
        public void addEnumConst(DetailAST enumConst) {
            enumConstants.add(enumConst);
        }

        /**
         * Add type variable name to this frame.
         *
         * @param typeVariable the type variable name.
         */
        public void addTypeVariable(String typeVariable) {
            typeVariables.add(typeVariable);
        }

        /**
         * Add type to this frame.
         *
         * @param type the type name.
         */
        public void addType(String type) {
            types.add(type);
        }

        /**
         * Determine whether this Frame contains the field.
         *
         * @param name the name of the field to check.
         * @return search result.
         */
        public Optional<DetailAST> findFieldInFrame(final String name) {
            return fields.stream()
                .filter(field -> getIdentText(field).equals(name))
                .findFirst();
        }

        /**
         * Determine whether this Frame contains the enum constant.
         *
         * @param name the name of the enum constant to check.
         * @return search result.
         */
        public Optional<DetailAST> findEnumConstInFrame(final String name) {
            return enumConstants.stream()
                    .filter(enumConstant -> getIdentText(enumConstant).equals(name))
                    .findFirst();
        }

    }

}
