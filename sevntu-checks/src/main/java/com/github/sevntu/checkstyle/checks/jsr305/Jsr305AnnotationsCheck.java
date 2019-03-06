////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2019 the original author or authors.
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

package com.github.sevntu.checkstyle.checks.jsr305;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import antlr.collections.AST;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * JSR305CheckstylePlugin - a checkstyle plugin to enforce use of nullness anotations. The
 * standalone version of this plugin resides under
 * <a href="https://github.com/bjrke/JSR305CheckstylePlugin">
 * https://github.com/mbert/JSR305CheckstylePlugin</a>
 * <p>
 * Copyright (C) 2008 Marcus Thiesen (initial version) Copyright (C) 2008-2019 Jan Burkhardt
 * (maintainer)
 * <br>
 * Thanks to Mattias Nissler, Thorsten Ehlers, Fabian Loewner, Ole Langbehn for contributions.
 * </p>
 */
public class Jsr305AnnotationsCheck extends AbstractCheck {

    /** Key for error message. */
    public static final String MSG_ILLEGAL_CLASS_LEVEL_ANNOTATION =
            "illegal.class.level.annotation";

    /** Key for error message. */
    public static final String MSG_CONTRADICTING_CLASS_LEVEL_ANNOTATIONS =
            "contradicting.class.level.annotations";

    /** Key for error message. */
    public static final String MSG_PARAM_DEFINITIONS_WITH_CHECK_ANNOTATION =
            "param.definitions.with.check.annotation";

    /** Key for error message. */
    public static final String MSG_PARAM_DEFINITION_WITH_OVERRIDE_ANNOTATION =
            "param.definition.with.override.annotation";

    /** Key for error message. */
    public static final String MSG_PARAM_DEFINITION_WITH_NONNULL_BY_DEFAULT_ANNOTATION =
            "param.definition.with.nonnull.by.default.annotation";

    /** Key for error message. */
    public static final String MSG_PARAM_DEFINITION_WITH_NULLABLE_BY_DEFAULT_ANNOTATION =
            "param.definition.with.nullable.by.default.annotation";

    /** Key for error message. */
    public static final String MSG_PARAM_DEFINITION_WITH_RETURN_VALUES_DEFAULT_ANNOTATION =
            "param.definition.with.return.values.default.annotation";

    /** Key for error message. */
    public static final String MSG_PARAM_NONNULL_AND_NULLABLE_ANNOTATION =
            "param.nonnull.and.nullable.annotation";

    /** Key for error message. */
    public static final String MSG_PRIMITIVES_WITH_NULLNESS_ANNOTATION =
            "primitives.with.nullness.annotation";

    /** Key for error message. */
    public static final String MSG_OVERRIDDEN_DEFINITIONS_WITH_INCREASED_PARAM_CONSTRAINT =
            "overridden.definitions.with.increased.param.constraint";

    /** Key for error message. */
    public static final String MSG_REDUNDANT_NONNULL_PARAM_ANNOTATION =
            "redundant.nonnull.param.annotation";

    /** Key for error message. */
    public static final String MSG_REDUNDANT_NULLABLE_PARAM_ANNOTATION =
            "redundant.nullable.param.annotation";

    /** Key for error message. */
    public static final String MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION =
            "parameter.without.nullness.annotation";

    /** Key for error message. */
    public static final String MSG_RETURN_VALUE_WITH_NONNULL_BY_DEFAULT_ANNOTATION =
            "return.value.with.nonnull.by.default.annotation";

    /** Key for error message. */
    public static final String MSG_RETURN_VALUE_WITH_NULLABLE_ANNOTATION =
            "return.value.with.nullable.annotation";

    /** Key for error message. */
    public static final String MSG_CONTRADICTING_RETURN_VALUE_ANNOTATIONS =
            "contradicting.return.value.annotations";

    /** Key for error message. */
    public static final String MSG_OVERRIDDEN_METHOD_WITH_CHECK_RETURN_VALUE_ANNOTATION =
            "overridden.method.with.check.return.value.annotation";

    /** Key for error message. */
    public static final String MSG_REDUNDANT_NONNULL_BY_DEFAULT_ANNOTATION =
            "redundant.nonnull.by.default.annotation";

    /** Key for error message. */
    public static final String MSG_REDUNDANT_NULLABLE_BY_DEFAULT_ANNOTATION =
            "redundant.nullable.by.default.annotation";

    /** Key for error message. */
    public static final String MSG_VOID_WITH_CHECK_RETURN_VALUE_ANNOTATION =
            "void.with.check.return.value.annotation";

    /** Key for error message. */
    public static final String MSG_REDUNDANT_NONNULL_RETURN_ANNOTATION =
            "redundant.nonnull.return.annotation";

    /** Key for error message. */
    public static final String MSG_RETURN_WITHOUT_NULLNESS_ANNOTATION =
            "return.without.nullness.annotation";

    /** Key for error message. */
    public static final String MSG_OVERRIDDEN_METHODS_ALLOW_ONLY_NONNULL =
            "overridden.methods.allow.only.nonnull";

    /** Key for error message. */
    public static final String MSG_NEED_TO_INHERIT_PARAM_ANNOTATIONS =
            "need.to.inherit.param.annotations";

    /** Key for error message. */
    public static final String MSG_CONSTRUCTOR_WITH_RETURN_ANNOTATION =
            "constructor.with.return.annotation";

    /** Package name. */
    private static final String PKG_JAVAX_ANNOTATION = "javax.annotation";

    /**
     * Class NullnessAnnotation. The annotations we consider as "nullness-relevant".
     */
    private enum NullnessAnnotation {

        /** Override. */
        OVERRIDE("Override", "java.lang"),
        /** CheckForNull. */
        CHECK_FOR_NULL("CheckForNull", PKG_JAVAX_ANNOTATION),
        /** Nullable. */
        NULLABLE("Nullable", PKG_JAVAX_ANNOTATION),
        /** Nonnull. */
        NONNULL("Nonnull", PKG_JAVAX_ANNOTATION),
        /** CheckReturnValue. */
        CHECK_RETURN_VALUE("CheckReturnValue", PKG_JAVAX_ANNOTATION),
        /** ParametersAreNonnullByDefault. */
        PARAMETERS_ARE_NONNULL_BY_DEFAULT("ParametersAreNonnullByDefault", PKG_JAVAX_ANNOTATION),
        /** ParametersAreNullableByDefault. */
        PARAMETERS_ARE_NULLABLE_BY_DEFAULT("ParametersAreNullableByDefault", PKG_JAVAX_ANNOTATION),
        /** ReturnValuesAreNonnullByDefault. */
        RETURN_VALUES_ARE_NONNULL_BY_DEFAULT("ReturnValuesAreNonnullByDefault",
                "edu.umd.cs.findbugs.annotations");

        /** The annotation's name. */
        private final String theAnnotationName;

        /** The annotation's fully qualified class name. */
        private final String theFqcn;

        /**
         * Constructor.
         * @param annotationName
         *        the annotation's name
         * @param packageName
         *        the package name
         */
        NullnessAnnotation(final String annotationName, final String packageName) {
            this.theAnnotationName = annotationName;
            this.theFqcn = packageName + "." + annotationName;
        }

    }

    /** The map of annotations against their respective names. */
    private static final Map<String, NullnessAnnotation> STRING2ANNOTATION =
            createString2AnnotationMap();

    /** The modifiers of interest. */
    private static final int[] DEFAULT_MODIFIERS = {
        TokenTypes.PARAMETER_DEF,
        TokenTypes.METHOD_DEF,
        TokenTypes.PACKAGE_DEF,
        TokenTypes.CTOR_DEF,
        TokenTypes.CLASS_DEF,
        TokenTypes.INTERFACE_DEF,
        TokenTypes.ENUM_DEF,
    };

    /** Parameter: packages to check. */
    private String[] packages = new String[0];
    /** Parameter: packages to exclude from checking. */
    private String[] excludePackages = new String[0];
    /** Parameter: overriding return value annotations allowed. */
    private boolean allowOverridingReturnValue;
    /** Parameter: overriding parameter annotations allowed. */
    private boolean allowOverridingParameter;

    /** State, is a package excluded. */
    private boolean packageExcluded;

    /**
     * Sets the included packages parameter.
     * @param packageNames
     *        the package names, comma separated
     */
    public void setPackages(final String[] packageNames) {
        packages = transformToUnique(packageNames);
    }

    /**
     * Maps annotations to their respective names.
     * @return the map
     */
    private static Map<String, NullnessAnnotation> createString2AnnotationMap() {
        final Map<String, NullnessAnnotation> result = new HashMap<String, NullnessAnnotation>();

        for (final NullnessAnnotation annotation : NullnessAnnotation.values()) {
            result.put(annotation.theAnnotationName, annotation);
            result.put(annotation.theFqcn, annotation);
        }

        return Collections.unmodifiableMap(result);
    }

    /**
     * Sets the excluded packages parameter.
     * @param packageNames
     *        the package names, comma separated
     */
    public void setExcludePackages(final String[] packageNames) {
        excludePackages = transformToUnique(packageNames);
    }

    /**
     * Removes duplicates from an array of strings.
     * @param input
     *        the array
     * @return a new, duplicate-free array
     */
    private static String[] transformToUnique(final String[] input) {
        final Set<String> inputSet = new HashSet<String>(Arrays.asList(input));
        return inputSet.toArray(new String[inputSet.size()]);
    }

    @Override
    public final int[] getDefaultTokens() {
        return DEFAULT_MODIFIERS.clone();
    }

    @Override
    public final int[] getRequiredTokens() {
        return new int[0];
    }

    @Override
    public final int[] getAcceptableTokens() {
        return DEFAULT_MODIFIERS.clone();
    }

    @Override
    public final void visitToken(final DetailAST ast) {
        if (ast.getType() == TokenTypes.PACKAGE_DEF) {
            final DetailAST nameAST = ast.getLastChild().getPreviousSibling();
            packageExcluded = isPackageExcluded(FullIdent.createFullIdent(nameAST));
        }
        else if (!packageExcluded) {
            handleDefinition(ast);
        }
    }

    /**
     * Checks whether a package is excluded.
     * @param fullIdent
     *        the identifier
     * @return true if yes
     */
    private boolean isPackageExcluded(final FullIdent fullIdent) {
        Boolean result = null;
        if (fullIdent == null) {
            result = true;
        }
        else {
            final String packageName = fullIdent.getText();
            if (packageName == null) {
                result = true;
            }
            else {
                for (final String excludesPackageName : excludePackages) {
                    if (packageName.startsWith(excludesPackageName)) {
                        result = true;
                    }
                }
                if (result == null) {
                    for (final String includePackageName : packages) {
                        if (packageName.startsWith(includePackageName)) {
                            result = false;
                        }
                    }
                }
            }
        }
        if (result == null) {
            result = true;
        }
        return result;
    }

    /**
     * Returns the check to use for a given definition.
     * @param ast
     *        the ast
     * @return the check.
     */
    private AbstractJsr305Check handleDefinition(final DetailAST ast) {

        // no definition in catch clause
        final DetailAST parent = ast.getParent();
        final AbstractJsr305Check result;
        if (parent != null && parent.getType() == TokenTypes.LITERAL_CATCH) {
            result = null;
        }
        else {
            // search modifiers
            final int type = ast.getType();
            switch (type) {
                case TokenTypes.METHOD_DEF:
                    result = new MethodJsr305Check(ast);
                    break;
                case TokenTypes.CTOR_DEF:
                    result = new ConstructorJsr305Check(ast);
                    break;
                case TokenTypes.PARAMETER_DEF:
                    result = new ParameterJsr305Check(ast);
                    break;
                case TokenTypes.CLASS_DEF:
                case TokenTypes.INTERFACE_DEF:
                case TokenTypes.ENUM_DEF:
                    result = new ClassJsr305Check(ast);
                    break;
                default:
                    throw new UnsupportedOperationException("no implementation for " + type);
            }
        }
        return result;
    }

    /**
     * Sets the parameter for allowing overriding return values.
     * @param newAllowOverridingReturnValue
     *        true if yes
     */
    public void setAllowOverridingReturnValue(final boolean newAllowOverridingReturnValue) {
        this.allowOverridingReturnValue = newAllowOverridingReturnValue;
    }

    /**
     * Sets the parameter for allowing overriding parameters.
     * @param newAllowOverridingParameter
     *        true if yes
     */
    public void setAllowOverridingParameter(final boolean newAllowOverridingParameter) {
        this.allowOverridingParameter = newAllowOverridingParameter;
    }

    /**
     * Class ClassJsr305Check. Checks a class.
     */
    private final class ClassJsr305Check extends AbstractJsr305Check {

        /**
         * Constructor.
         * @param ast
         *        the ast
         */
        ClassJsr305Check(final DetailAST ast) {
            super(ast);
        }

        /**
         * Run the actual check.
         */
        @Override
        protected void runcheck() {
            checkContainsAny(MSG_ILLEGAL_CLASS_LEVEL_ANNOTATION,
                    NullnessAnnotation.CHECK_FOR_NULL, NullnessAnnotation.CHECK_RETURN_VALUE,
                    NullnessAnnotation.NONNULL, NullnessAnnotation.NULLABLE);
            checkContainsAll(MSG_CONTRADICTING_CLASS_LEVEL_ANNOTATIONS,
                    NullnessAnnotation.PARAMETERS_ARE_NONNULL_BY_DEFAULT,
                    NullnessAnnotation.PARAMETERS_ARE_NULLABLE_BY_DEFAULT);
        }

    }

    /**
     * Class ParameterJsr305Check. Checks a parameter.
     */
    private final class ParameterJsr305Check extends AbstractJsr305Check {

        /**
         * Constructor.
         * @param ast
         *        the ast
         */
        ParameterJsr305Check(final DetailAST ast) {
            super(ast);
        }

        /**
         * Run the actual check.
         */
        @Override
        protected void runcheck() {
            checkContainsAny(MSG_PARAM_DEFINITIONS_WITH_CHECK_ANNOTATION,
                    NullnessAnnotation.CHECK_FOR_NULL, NullnessAnnotation.CHECK_RETURN_VALUE);
            checkContainsAny(MSG_PARAM_DEFINITION_WITH_OVERRIDE_ANNOTATION,
                    NullnessAnnotation.OVERRIDE);
            checkContainsAny(MSG_PARAM_DEFINITION_WITH_NONNULL_BY_DEFAULT_ANNOTATION,
                    NullnessAnnotation.PARAMETERS_ARE_NONNULL_BY_DEFAULT);
            checkContainsAny(MSG_PARAM_DEFINITION_WITH_NULLABLE_BY_DEFAULT_ANNOTATION,
                    NullnessAnnotation.PARAMETERS_ARE_NULLABLE_BY_DEFAULT);
            checkContainsAny(MSG_PARAM_DEFINITION_WITH_RETURN_VALUES_DEFAULT_ANNOTATION,
                    NullnessAnnotation.RETURN_VALUES_ARE_NONNULL_BY_DEFAULT);
            checkContainsAll(MSG_PARAM_NONNULL_AND_NULLABLE_ANNOTATION,
                    NullnessAnnotation.NONNULL, NullnessAnnotation.NULLABLE);

            final NullnessAnnotation firstAncestorAnnotation = getParentMethodOrClassAnnotation(
                    NullnessAnnotation.PARAMETERS_ARE_NONNULL_BY_DEFAULT,
                    NullnessAnnotation.PARAMETERS_ARE_NULLABLE_BY_DEFAULT);
            final boolean parametersAreNonnullByDefault =
                    firstAncestorAnnotation == NullnessAnnotation.PARAMETERS_ARE_NONNULL_BY_DEFAULT;
            final boolean parametersAreNullableByDefault = firstAncestorAnnotation
                    == NullnessAnnotation.PARAMETERS_ARE_NULLABLE_BY_DEFAULT;
            final boolean isMethodOverridden = isMethodOverridden();

            if (isPrimitiveType()) {
                checkContainsAny(MSG_PRIMITIVES_WITH_NULLNESS_ANNOTATION,
                        NullnessAnnotation.CHECK_FOR_NULL, NullnessAnnotation.NONNULL,
                        NullnessAnnotation.NULLABLE);
            }
            else {
                if (isMethodOverridden && !allowOverridingParameter) {
                    checkContainsAny(MSG_OVERRIDDEN_DEFINITIONS_WITH_INCREASED_PARAM_CONSTRAINT,
                            NullnessAnnotation.NONNULL);
                }
                if (parametersAreNonnullByDefault) {
                    checkContainsAny(MSG_REDUNDANT_NONNULL_PARAM_ANNOTATION,
                            NullnessAnnotation.NONNULL);
                }
                if (parametersAreNullableByDefault) {
                    checkContainsAny(MSG_REDUNDANT_NULLABLE_PARAM_ANNOTATION,
                            NullnessAnnotation.NULLABLE);
                }

                if (!isMethodOverridden && !parametersAreNonnullByDefault
                        && !parametersAreNullableByDefault) {
                    checkContainsNone(MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION,
                            NullnessAnnotation.NONNULL, NullnessAnnotation.NULLABLE);
                }
            }
        }
    }

    /**
     * Class AbstractMethodJsr305Check. A check on a method or constructor (special case).
     */
    private abstract class AbstractMethodJsr305Check extends AbstractJsr305Check {

        /**
         * Constructor.
         * @param ast
         *        the ast
         */
        AbstractMethodJsr305Check(final DetailAST ast) {
            super(ast);
        }

        /**
         * Run the actual check.
         */
        @Override
        protected final void runcheck() {
            checkContainsAll(MSG_CONTRADICTING_CLASS_LEVEL_ANNOTATIONS,
                    NullnessAnnotation.PARAMETERS_ARE_NONNULL_BY_DEFAULT,
                    NullnessAnnotation.PARAMETERS_ARE_NULLABLE_BY_DEFAULT);
            runReturnAnnotationCheck();
        }

        /**
         * Run annotation check for return.
         */
        protected abstract void runReturnAnnotationCheck();

    }

    /**
     * Class MethodJsr305Check. A check for a method.
     */
    private final class MethodJsr305Check extends AbstractMethodJsr305Check {

        /**
         * Constructor.
         * @param ast
         *        the ast
         */
        MethodJsr305Check(final DetailAST ast) {
            super(ast);
        }

        /**
         * Run annotation check for return.
         */
        @Override
        protected void runReturnAnnotationCheck() {
            checkContainsAny(MSG_RETURN_VALUE_WITH_NONNULL_BY_DEFAULT_ANNOTATION,
                    NullnessAnnotation.RETURN_VALUES_ARE_NONNULL_BY_DEFAULT);
            checkContainsAny(MSG_RETURN_VALUE_WITH_NULLABLE_ANNOTATION,
                    NullnessAnnotation.NULLABLE);
            checkContainsAll(MSG_CONTRADICTING_RETURN_VALUE_ANNOTATIONS, NullnessAnnotation.NONNULL,
                    NullnessAnnotation.CHECK_FOR_NULL);
            checkContainsAll(MSG_OVERRIDDEN_METHOD_WITH_CHECK_RETURN_VALUE_ANNOTATION,
                    NullnessAnnotation.CHECK_RETURN_VALUE, NullnessAnnotation.OVERRIDE);
            checkRedundancyDueToClassLevelAnnotation(MSG_REDUNDANT_NONNULL_BY_DEFAULT_ANNOTATION,
                    NullnessAnnotation.PARAMETERS_ARE_NONNULL_BY_DEFAULT);
            checkRedundancyDueToClassLevelAnnotation(MSG_REDUNDANT_NULLABLE_BY_DEFAULT_ANNOTATION,
                    NullnessAnnotation.PARAMETERS_ARE_NULLABLE_BY_DEFAULT);

            if (isVoid()) {
                checkContainsAny(MSG_VOID_WITH_CHECK_RETURN_VALUE_ANNOTATION,
                        NullnessAnnotation.CHECK_RETURN_VALUE);
            }
            if (isPrimitiveType()) {
                checkContainsAny(MSG_PRIMITIVES_WITH_NULLNESS_ANNOTATION,
                        NullnessAnnotation.CHECK_FOR_NULL, NullnessAnnotation.NONNULL,
                        NullnessAnnotation.NULLABLE);
            }
            else {
                final NullnessAnnotation annotation = getParentMethodOrClassAnnotation(
                        NullnessAnnotation.RETURN_VALUES_ARE_NONNULL_BY_DEFAULT);
                final boolean returnValuesAreNonnullByDefault = annotation
                        == NullnessAnnotation.RETURN_VALUES_ARE_NONNULL_BY_DEFAULT;
                final boolean isMethodOverridden = isMethodOverridden();

                if (returnValuesAreNonnullByDefault) {
                    if (!isMethodOverridden) {
                        checkContainsAny(MSG_REDUNDANT_NONNULL_RETURN_ANNOTATION,
                                NullnessAnnotation.NONNULL);
                    }
                }
                else {
                    checkContainsNone(MSG_RETURN_WITHOUT_NULLNESS_ANNOTATION,
                            NullnessAnnotation.CHECK_FOR_NULL, NullnessAnnotation.NONNULL,
                            NullnessAnnotation.OVERRIDE);
                }

                if (isMethodOverridden && !allowOverridingReturnValue) {
                    checkContainsAny(MSG_OVERRIDDEN_METHODS_ALLOW_ONLY_NONNULL,
                            NullnessAnnotation.CHECK_FOR_NULL, NullnessAnnotation.NULLABLE);
                }

                if (isMethodOverridden) {
                    checkContainsAny(MSG_NEED_TO_INHERIT_PARAM_ANNOTATIONS,
                            NullnessAnnotation.PARAMETERS_ARE_NONNULL_BY_DEFAULT);
                }
            }
        }

    }

    /**
     * Class ConstructorJsr305Check. Check a constructor.
     */
    private final class ConstructorJsr305Check extends AbstractMethodJsr305Check {

        /**
         * Constructor.
         * @param ast
         *        the ast
         */
        ConstructorJsr305Check(final DetailAST ast) {
            super(ast);
        }

        /**
         * Check for return type nullness annotations (which are illegal).
         */
        @Override
        protected void runReturnAnnotationCheck() {
            checkContainsAny(MSG_CONSTRUCTOR_WITH_RETURN_ANNOTATION,
                    NullnessAnnotation.CHECK_FOR_NULL, NullnessAnnotation.CHECK_RETURN_VALUE,
                    NullnessAnnotation.NONNULL, NullnessAnnotation.NULLABLE,
                    NullnessAnnotation.OVERRIDE);
        }

    }

    /**
     * An abstract check, the base for checks on parameters, methods, classes. Class
     * AbstractJsr305Check.
     */
    public abstract class AbstractJsr305Check {

        /** Has an error been found. */
        private boolean errorFound;
        /** The found annotations. */
        private final Set<NullnessAnnotation> theAnnotations;
        /** The ast. */
        private final DetailAST theAst;

        /**
         * Construtor.
         * @param ast
         *        the ast
         */
        AbstractJsr305Check(final DetailAST ast) {
            errorFound = false;
            theAst = ast;
            if (ast == null) {
                theAnnotations = Collections.emptySet();
            }
            else {
                theAnnotations = findAnnotation();
                runcheck();
            }
        }

        /**
         * Run the actual check.
         */
        protected abstract void runcheck();

        /**
         * Emits an error if any of the given annotations are found.
         * @param msg
         *        the error message to emit
         * @param annotations
         *        the annotations to look for
         */
        protected void checkContainsAny(final String msg, final NullnessAnnotation... annotations) {
            if (!errorFound && containsAny(annotations)) {
                error(msg);
            }
        }

        /**
         * Check whether any of the given annotations are found.
         * @param annotations
         *        the annotations to look for
         * @return true if yes
         */
        protected boolean containsAny(final NullnessAnnotation... annotations) {
            Boolean result = null;
            if (theAnnotations.isEmpty()) {
                result = false;
            }
            else {
                for (final NullnessAnnotation obj : annotations) {
                    if (theAnnotations.contains(obj)) {
                        result = true;
                        break;
                    }
                }
            }
            if (result == null) {
                result = false;
            }
            return result;
        }

        /**
         * Emits an error if all given annotations are found.
         * @param msg
         *        the error message to emit
         * @param annotations
         *        the annotations to look for
         */
        protected void checkContainsAll(final String msg, final NullnessAnnotation... annotations) {
            if (!errorFound && containsAll(annotations)) {
                error(msg);
            }
        }

        /**
         * Emits an error if both this and the parent class have redundant nullness annotations.
         * @param msg
         *        the error message to emit
         * @param annotations
         *        the annotations to look for
         */
        protected void checkRedundancyDueToClassLevelAnnotation(final String msg,
                final NullnessAnnotation... annotations) {
            if (!errorFound) {
                for (final NullnessAnnotation nullnessAnnotation : annotations) {
                    final boolean thisIsAnnotated = theAnnotations.contains(nullnessAnnotation);
                    final boolean parentIsAnnotated =
                            getParentMethodOrClassAnnotation(nullnessAnnotation) != null;
                    if (thisIsAnnotated && parentIsAnnotated) {
                        error(msg);
                        break;
                    }
                }
            }
        }

        /**
         * Check whether all the given annotations are present.
         * @param annotations
         *        the annotations to look for
         * @return true if yes
         */
        protected boolean containsAll(final NullnessAnnotation... annotations) {
            Boolean result = null;
            if (theAnnotations.isEmpty()) {
                result = annotations.length == 0;
            }
            else {
                for (final NullnessAnnotation obj : annotations) {
                    if (!theAnnotations.contains(obj)) {
                        result = false;
                        break;
                    }
                }
            }
            if (result == null) {
                result = true;
            }
            return result;
        }

        /**
         * Make sure that none of the given annotations are present.
         * @param msg
         *        the error message to emit if one of the given annotations was found
         * @param annotations
         *        the annotations to look for
         */
        protected void checkContainsNone(final String msg,
                final NullnessAnnotation... annotations) {
            if (!errorFound && !containsAny(annotations)) {
                error(msg);
            }
        }

        /**
         * Handle an error (log and set 'errorFound' to 'true').
         * @param msg
         *        the error message
         */
        private void error(final String msg) {
            if (!errorFound) {
                log(theAst, msg);
            }
            errorFound = true;
        }

        /**
         * Is the current symbol a primitive type.
         * @return true if yes
         */
        protected boolean isPrimitiveType() {
            final DetailAST parameterType = theAst.findFirstToken(TokenTypes.TYPE);
            final boolean result;
            if (parameterType == null) {
                result = false;
            }
            else {
                final DetailAST identToken = parameterType.getFirstChild();

                if (identToken == null) {
                    result = false;
                }
                else {
                    switch (identToken.getType()) {
                        case TokenTypes.LITERAL_BOOLEAN:
                        case TokenTypes.LITERAL_INT:
                        case TokenTypes.LITERAL_LONG:
                        case TokenTypes.LITERAL_SHORT:
                        case TokenTypes.LITERAL_BYTE:
                        case TokenTypes.LITERAL_CHAR:
                        case TokenTypes.LITERAL_VOID:
                        case TokenTypes.LITERAL_DOUBLE:
                        case TokenTypes.LITERAL_FLOAT:
                            result = !isArrayOrElipsis(parameterType);
                            break;
                        default:
                            result = false;
                    }
                }
            }
            return result;
        }

        /**
         * Checks whether token is array or elipsis.
         * @param identToken
         *        the token
         * @return true if yes
         */
        private boolean isArrayOrElipsis(final DetailAST identToken) {
            final DetailAST next = identToken.getNextSibling();
            final boolean result;
            if (next == null) {
                result = false;
            }
            else {
                switch (next.getType()) {
                    case TokenTypes.ARRAY_DECLARATOR:
                    case TokenTypes.ELLIPSIS:
                        result = true;
                        break;
                    default:
                        result = false;
                }
            }
            return result;
        }

        /**
         * Is the current symbol of void type.
         * @return true if yes
         */
        protected boolean isVoid() {
            final DetailAST parameterType = theAst.findFirstToken(TokenTypes.TYPE);
            final boolean result;
            if (parameterType == null) {
                result = false;
            }
            else {
                final DetailAST identToken = parameterType.getFirstChild();
                result = identToken != null && identToken.getType() == TokenTypes.LITERAL_VOID;
            }
            return result;
        }

        /**
         * Find the nullness annotations.
         * @return the annotations.
         */
        private Set<NullnessAnnotation> findAnnotation() {
            return findAnnotations(theAst);
        }

        /**
         * Find the nullness annotations.
         * @param ast
         *        the ast
         * @return the annotations.
         */
        private Set<NullnessAnnotation> findAnnotations(final DetailAST ast) {
            final Set<NullnessAnnotation> result = new HashSet<NullnessAnnotation>();

            final DetailAST modifiers = ast.findFirstToken(TokenTypes.MODIFIERS);
            if (modifiers != null) {
                for (AST child = modifiers.getFirstChild(); child != null; child =
                        child.getNextSibling()) {
                    if (child.getType() != TokenTypes.ANNOTATION) {
                        continue;
                    }
                    final DetailAST identifier =
                            ((DetailAST) child).findFirstToken(TokenTypes.IDENT);
                    if (identifier != null) {
                        final String annotationName = identifier.getText();
                        if (annotationName != null) {
                            final NullnessAnnotation annotation =
                                    STRING2ANNOTATION.get(annotationName);
                            if (annotation != null && !result.add(annotation)) {
                                error("Double Annotation (" + annotation.theAnnotationName
                                        + ") found!");
                            }
                        }
                    }
                }
            }

            return result;
        }

        /**
         * Gets the nullness annotation for the parent method or class.
         * @param annotationsToLookFor
         *        the annotations to look for.
         * @return the annotation or null if none was found
         */
        protected NullnessAnnotation
                getParentMethodOrClassAnnotation(final NullnessAnnotation... annotationsToLookFor) {

            boolean finished = false;
            NullnessAnnotation result = null;
            for (DetailAST current = theAst.getParent(); current != null && !finished; current =
                    current.getParent()) {
                final int tokenType = current.getType();

                if (isPossibleTokenTypeForNullnessAnnotations(tokenType)) {
                    final Set<NullnessAnnotation> foundAndLookedFor =
                            collectLookedForAnnotations(current, annotationsToLookFor);
                    if (foundAndLookedFor.size() == 1) {
                        result = foundAndLookedFor.iterator().next();
                        finished = true;
                    }
                    else if (foundAndLookedFor.size() > 0) {
                        finished = true;
                    }
                }
                // break on inner and anonymous classes/interfaces, we can't
                // handle inheritance correctly
                if (tokenType == TokenTypes.LITERAL_NEW || tokenType == TokenTypes.CLASS_DEF
                        || tokenType == TokenTypes.INTERFACE_DEF
                        || tokenType == TokenTypes.ENUM_DEF) {
                    finished = true;
                }
            }
            return result;
        }

        /**
         * Is the current tokenType a possible type for nullness annotations.
         * @param tokenType the token type
         * @return true if yes
         */
        private boolean isPossibleTokenTypeForNullnessAnnotations(final int tokenType) {
            return tokenType == TokenTypes.CLASS_DEF || tokenType == TokenTypes.INTERFACE_DEF
                    || tokenType == TokenTypes.METHOD_DEF || tokenType == TokenTypes.CTOR_DEF
                    || tokenType == TokenTypes.ENUM_DEF;
        }

        /**
         * Extracts all given annotations from the current ast.
         * @param current the current ast
         * @param annotationsToLookFor the annotations we are looking for
         * @return the annotations
         */
        private Set<NullnessAnnotation> collectLookedForAnnotations(DetailAST current,
                final NullnessAnnotation... annotationsToLookFor) {
            final Set<NullnessAnnotation> foundAnnotations = findAnnotations(current);
            final Set<NullnessAnnotation> foundAndLookedFor = new HashSet<>();
            for (final NullnessAnnotation nullnessAnnotation : annotationsToLookFor) {
                if (foundAnnotations.contains(nullnessAnnotation)) {
                    foundAndLookedFor.add(nullnessAnnotation);
                }
            }
            return foundAndLookedFor;
        }

        /**
         * Is the current method overridden.
         * @return true if yes
         */
        protected boolean isMethodOverridden() {
            DetailAST current = theAst;
            Boolean result = null;
            for (; current != null && result == null; current = current.getParent()) {
                switch (current.getType()) {
                    case TokenTypes.METHOD_DEF:
                        result = findAnnotations(current).contains(NullnessAnnotation.OVERRIDE);
                        break;
                    case TokenTypes.LAMBDA:
                        result = true;
                        break;
                    default:
                }
            }
            if (result == null) {
                result = false;
            }
            return result;
        }

    }

}
