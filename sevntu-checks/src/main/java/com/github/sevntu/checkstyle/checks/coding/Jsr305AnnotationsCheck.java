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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.sevntu.checkstyle.SevntuUtil;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.TokenUtil;

/**
 * <p>
 * The <a href="https://jcp.org/en/jsr/detail?id=305">Jsr305 annotations</a> (annotations for
 * software defect detection) contain a subset of "nullness" annotations that can be used to mark
 * parameters as possibly null ({@code @Nullable}) or always non-null ({@code @Nonnull}), function
 * return values as to be checked for null ({@code @CheckForNull}) or always non-null
 * ({@code @Nonnull}) including defaults on class level ({@code @ParametersAreNonnullByDefault},
 * {@code @ParametersAreNullableByDefault}, {@code @ReturnValuesAreNonnullByDefault}).
 * </p>
 * <p>
 * Using these annotations a programmer can declare how the code is meant to behave, and static code
 * analysis (like e.g. FindBugs) can be used to verify that this is actually true. Also these
 * annotations help others understanding code more easily, e.g. if confrontend with an annotated
 * interface the necessity for null checks can easily be deducted from the annotations.
 * </p>
 * <p>
 * The Jsr305AnnotationsCheck supports enforcing the following code style:
 * </p>
 * <ul>
 * <li>Every method declaration, implementation or lambda requires nullness annotations for all
 * parameters and return values except for primitive types (because a void or an int can never be
 * null anyway).</li>
 * <li>The annotation can be made directly or applied through either inheritance from an already
 * annotated class or a annotation for class-wide defaults.</li>
 * <li>Annotations need to make sense. For instance, a class-scope annotation cannot be used for a
 * method, and a method annotation cannot be used for a class.</li>
 * <li>In overridden methods, the following rule applies (regardless of what was annotated in the
 * parent method): For parameter definitions {@code @Nonnull} annotation is always illegal because
 * being less "nullable" cannot be assumed for a parameter in an inherited method. Conversely
 * {@code @Nullable} is always allowed. For return values it is the other way round:
 * {@code @CheckForNull} is always illegal while {@code @Nonnull} is always legal.</li>
 * </ul>
 * <p>
 * The following configuration properties are supported:
 * </p>
 * <dl>
 * <dt>{@code packages = com.github.sevntu.pkg1,com.github.sevntu.pkg2}</dt>
 * <dd>Activate this check for a list of parent packages and their children.</dd>
 * <dt>{@code excludePackages = com.github.sevntu.pkg1.sub1,com.github.sevntu.pkg1.sub2}</dt>
 * <dd>Set packages excluded from checking. This setting can be useful if under the parent package
 * set with "packages" there are subpackages which should not be checked.</dd>
 * <dt>{@code allowOverridingReturnValue = true}</dt>
 * <dd>Annotating return values "@CheckForNull" in overridden methods is flagged as a violation.
 * When setting the this property to true, this will be ignored (useful for upgrading).</dd>
 * <dt>{@code allowOverridingParameters = true}</dt>
 * <dd>Annotating parameters "@Nonnull" in overridden methods is flagged as a violation. When
 * setting this property to true, this will be ignored (useful for upgrading).</dd>
 * </dl>
 * <p>
 * Note that by default no files will be checked. To enable this check, you need to set the
 * {@code packages} property to one or more parent packages.
 * </p>
 * <p>
 * Example code:
 * </p>
 * <p>
 * Configure the check so that it scans the packages of the classes we want to run this on:
 * </p>
 *
 * <pre>
 * &lt;module name=&quot;Jsr305Annotations&quot;&gt;
 *   &lt;property name=&quot;packages&quot; value=&quot;org,com&quot;/&gt;
 *   &lt;/module&gt;
 * </pre>
 *
 *
 * <pre>
 * // Example 1: a class without any class-level annotations
 * class Class1 {
 *     &#64;CheckForNull // Violation: obj2 not annotated!
 *     String method1(@Nullable Object obj1, Object obj2) {
 *         return "";
 *     }
 *
 *     // Violation: return value not annotated
 *     String method2() {
 *         return "";
 *     }
 * }
 *
 * // Example 2: a class with class-level annotations for parameters
 * &#64;ParametersAreNonnullByDefault
 * class Class2 {
 *     &#64;CheckForNull // Legal
 *     String method1(Object obj1, Object obj2) {
 *         return "";
 *     }
 *
 *     &#64;Nonnull // Legal
 *     String method2(Object obj1, @Nullable Object obj2) {
 *         return "";
 *     }
 *
 *     &#64;Nonnull // Violation, redefinition of obj2's nullness
 *     String method3(Object obj1, @Nonnull Object obj2) {
 *         return "";
 *     }
 *
 *     // Violation: return value not annotated
 *     String method4() {
 *         return "";
 *     }
 * }
 *
 * // Example 3: a class overriding some methods
 * class Class3 implements Interface1 {
 *     &#64;Override // Legal
 *     public Object method1(Object obj1, Object obj2) {
 *         return "";
 *     }
 *
 *     &#64;Override
 *     &#64;Nonnull // Legal, return value becomes less "nullable"
 *     public Object method2(Object obj1, Object obj2) {
 *         return "";
 *     }
 *
 *     &#64;Override // Violation: Setting a parameter to non-null in an overridden method
 *     public Object method3(@Nonnull Object obj1, Object obj2) {
 *         return "";
 *     }
 *
 *     &#64;Override // Legal: parameter obj2 becomes more "nullable"
 *     public Object method4(Object obj1, @Nullable Object obj2) {
 *         return "";
 *     }
 *
 *     &#64;Override
 *     &#64;CheckForNull // Violation: return value becomes more "nullable"
 *     public Object method5() {
 *         return "";
 *     }
 * }
 * </pre>
 */
public class Jsr305AnnotationsCheck extends AbstractCheck {

    /** Key for violation message. */
    public static final String MSG_ILLEGAL_CLASS_LEVEL_ANNOTATION =
            "jsr305.illegal.class.level.annotation";

    /** Key for violation message. */
    public static final String MSG_CONTRADICTING_CLASS_LEVEL_ANNOTATIONS =
            "jsr305.contradicting.class.level.annotations";

    /** Key for violation message. */
    public static final String MSG_PARAM_DEFINITIONS_WITH_CHECK =
            "jsr305.param.definitions.with.check.annotation";

    /** Key for violation message. */
    public static final String MSG_PARAM_DEFINITION_WITH_OVERRIDE =
            "jsr305.param.definition.with.override.annotation";

    /** Key for violation message. */
    public static final String MSG_PARAM_DEFINITION_WITH_NONNULL_BY_DEFAULT =
            "jsr305.param.definition.with.nonnull.by.default.annotation";

    /** Key for violation message. */
    public static final String MSG_PARAM_DEFINITION_WITH_NULLABLE_BY_DEFAULT =
            "jsr305.param.definition.with.nullable.by.default.annotation";

    /** Key for violation message. */
    public static final String MSG_PARAM_DEFINITION_WITH_RETURN_DEFAULT =
            "jsr305.param.definition.with.return.values.default.annotation";

    /** Key for violation message. */
    public static final String MSG_PARAM_NONNULL_AND_NULLABLE =
            "jsr305.param.nonnull.and.nullable.annotation";

    /** Key for violation message. */
    public static final String MSG_PRIMITIVES_WITH_NULLNESS_ANNOTATION =
            "jsr305.primitives.with.nullness.annotation";

    /** Key for violation message. */
    public static final String MSG_OVERRIDDEN_WITH_INCREASED_CONSTRAINT =
            "jsr305.overridden.definitions.with.increased.param.constraint";

    /** Key for violation message. */
    public static final String MSG_REDUNDANT_NONNULL_PARAM_ANNOTATION =
            "jsr305.redundant.nonnull.param.annotation";

    /** Key for violation message. */
    public static final String MSG_REDUNDANT_NULLABLE_PARAM_ANNOTATION =
            "jsr305.redundant.nullable.param.annotation";

    /** Key for violation message. */
    public static final String MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION =
            "jsr305.parameter.without.nullness.annotation";

    /** Key for violation message. */
    public static final String MSG_RETURN_VALUE_WITH_NONNULL_BY_DEFAULT =
            "jsr305.return.value.with.nonnull.by.default.annotation";

    /** Key for violation message. */
    public static final String MSG_RETURN_VALUE_WITH_NULLABLE =
            "jsr305.return.value.with.nullable.annotation";

    /** Key for violation message. */
    public static final String MSG_CONTRADICTING_RETURN_VALUE_ANNOTATIONS =
            "jsr305.contradicting.return.value.annotations";

    /** Key for violation message. */
    public static final String MSG_OVERRIDDEN_METHOD_WITH_CHECK_RETURN_VALUE =
            "jsr305.overridden.method.with.check.return.value.annotation";

    /** Key for violation message. */
    public static final String MSG_REDUNDANT_NONNULL_BY_DEFAULT_ANNOTATION =
            "jsr305.redundant.nonnull.by.default.annotation";

    /** Key for violation message. */
    public static final String MSG_REDUNDANT_NULLABLE_BY_DEFAULT_ANNOTATION =
            "jsr305.redundant.nullable.by.default.annotation";

    /** Key for violation message. */
    public static final String MSG_VOID_WITH_CHECK_RETURN_VALUE_ANNOTATION =
            "jsr305.void.with.check.return.value.annotation";

    /** Key for violation message. */
    public static final String MSG_REDUNDANT_NONNULL_RETURN_ANNOTATION =
            "jsr305.redundant.nonnull.return.annotation";

    /** Key for violation message. */
    public static final String MSG_RETURN_WITHOUT_NULLNESS_ANNOTATION =
            "jsr305.return.without.nullness.annotation";

    /** Key for violation message. */
    public static final String MSG_OVERRIDDEN_METHODS_ALLOW_ONLY_NONNULL =
            "jsr305.overridden.methods.allow.only.nonnull";

    /** Key for violation message. */
    public static final String MSG_NEED_TO_INHERIT_PARAM_ANNOTATIONS =
            "jsr305.need.to.inherit.param.annotations";

    /** Key for violation message. */
    public static final String MSG_CONSTRUCTOR_WITH_RETURN_ANNOTATION =
            "jsr305.constructor.with.return.annotation";

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
        private final String annotationName;

        /** The annotation's fully qualified class name. */
        private final String fullyQualifiedClassName;

        /**
         * Constructor.
         *
         * @param annotationName
         *        the annotation's name
         * @param packageName
         *        the package name
         */
        NullnessAnnotation(final String annotationName, final String packageName) {
            this.annotationName = annotationName;
            fullyQualifiedClassName = packageName + "." + annotationName;
        }

    }

    /** The map of annotations against their respective names. */
    private static final Map<String, NullnessAnnotation> STRING2ANNOTATION =
            createString2AnnotationMap();

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

    @Override
    public final int[] getDefaultTokens() {
        return getAcceptableTokens();
    }

    @Override
    public final int[] getRequiredTokens() {
        return new int[0];
    }

    @Override
    public final int[] getAcceptableTokens() {
        return new int[] {
            TokenTypes.PARAMETER_DEF,
            TokenTypes.METHOD_DEF,
            TokenTypes.PACKAGE_DEF,
            TokenTypes.CTOR_DEF,
            TokenTypes.CLASS_DEF,
            TokenTypes.INTERFACE_DEF,
            TokenTypes.ENUM_DEF,
        };
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        packageExcluded = false;
    }

    @Override
    public final void visitToken(final DetailAST ast) {
        if (ast.getType() == TokenTypes.PACKAGE_DEF) {
            final DetailAST nameAST = ast.getLastChild().getPreviousSibling();
            packageExcluded = isPackageExcluded(FullIdent.createFullIdent(nameAST));
        }
        else if (!packageExcluded) {
            final AbstractJsr305Handler handler = handleDefinition(ast);
            if (handler != null) {
                handler.handle();
            }
        }
    }

    /**
     * Sets the included packages property.
     *
     * @param packageNames
     *        the package names, comma separated
     */
    public void setPackages(final String... packageNames) {
        packages = transformToUnique(packageNames);
    }

    /**
     * Sets the excluded packages property.
     *
     * @param packageNames
     *        the package names, comma separated
     */
    public void setExcludePackages(final String... packageNames) {
        excludePackages = transformToUnique(packageNames);
    }

    /**
     * Sets the property for allowing overriding return values.
     *
     * @param newAllowOverridingReturnValue
     *        true if yes
     */
    public void setAllowOverridingReturnValue(final boolean newAllowOverridingReturnValue) {
        allowOverridingReturnValue = newAllowOverridingReturnValue;
    }

    /**
     * Sets the property for allowing overriding parameters.
     *
     * @param newAllowOverridingParameter
     *        true if yes
     */
    public void setAllowOverridingParameter(final boolean newAllowOverridingParameter) {
        allowOverridingParameter = newAllowOverridingParameter;
    }

    /**
     * Maps annotations to their respective names.
     *
     * @return the map
     */
    private static Map<String, NullnessAnnotation> createString2AnnotationMap() {
        final Map<String, NullnessAnnotation> result = new HashMap<>();

        for (final NullnessAnnotation annotation : NullnessAnnotation.values()) {
            result.put(annotation.annotationName, annotation);
            result.put(annotation.fullyQualifiedClassName, annotation);
        }

        return Collections.unmodifiableMap(result);
    }

    /**
     * Removes duplicates from an array of strings.
     *
     * @param input
     *        the array
     * @return a new, duplicate-free array
     */
    private static String[] transformToUnique(final String... input) {
        final Set<String> inputSet = new HashSet<>(Arrays.asList(input));
        return inputSet.toArray(new String[0]);
    }

    /**
     * Checks whether a package is excluded.
     *
     * @param fullIdent
     *        the identifier
     * @return true if yes
     */
    private boolean isPackageExcluded(final FullIdent fullIdent) {
        Boolean result = null;
        final String packageName = fullIdent.getText();
        for (final String excludesPackageName : excludePackages) {
            if (packageName.startsWith(excludesPackageName)) {
                result = true;
                break;
            }
        }
        if (result == null) {
            for (final String includePackageName : packages) {
                if (packageName.startsWith(includePackageName)) {
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
     * Returns the handler to use for a given definition.
     *
     * @param ast
     *        the ast
     * @return the handler.
     */
    private AbstractJsr305Handler handleDefinition(final DetailAST ast) {

        // no definition in catch clause
        final DetailAST parent = ast.getParent();
        AbstractJsr305Handler result = null;
        if (parent == null || parent.getType() != TokenTypes.LITERAL_CATCH) {
            // search modifiers
            final int type = ast.getType();
            switch (type) {
                case TokenTypes.METHOD_DEF:
                    result = new MethodJsr305Handler(ast);
                    break;
                case TokenTypes.CTOR_DEF:
                    result = new ConstructorJsr305Handler(ast);
                    break;
                case TokenTypes.PARAMETER_DEF:
                    result = new ParameterJsr305Handler(ast);
                    break;
                case TokenTypes.CLASS_DEF:
                case TokenTypes.INTERFACE_DEF:
                case TokenTypes.ENUM_DEF:
                    result = new ClassJsr305Handler(ast);
                    break;
                default:
                    SevntuUtil.reportInvalidToken(ast.getType());
                    break;
            }
        }
        return result;
    }

    /**
     * Find the nullness annotations.
     *
     * @param ast
     *        the ast
     * @return the annotations.
     */
    private static Set<NullnessAnnotation> findAnnotations(final DetailAST ast) {
        final Set<NullnessAnnotation> result = new HashSet<>();

        final DetailAST modifiers = ast.findFirstToken(TokenTypes.MODIFIERS);
        for (DetailAST child = modifiers.getFirstChild(); child != null; child =
                child.getNextSibling()) {
            if (child.getType() == TokenTypes.ANNOTATION) {
                addNextNullnessAnnotation(result, child);
            }
        }

        return result;
    }

    /**
     * Adds the nullness annotation from the argument's first token if any.
     *
     * @param result
     *        the result
     * @param ast
     *        the ast
     */
    private static void addNextNullnessAnnotation(final Set<NullnessAnnotation> result,
            DetailAST ast) {
        final DetailAST identifier = ast.findFirstToken(TokenTypes.IDENT);
        if (identifier != null) {
            final String annotationName = identifier.getText();
            final NullnessAnnotation annotation = STRING2ANNOTATION.get(annotationName);
            if (annotation != null) {
                result.add(annotation);
            }
        }
    }

    /**
     * Is the current symbol a primitive type.
     *
     * @param ast
     *        the ast
     * @return true if yes
     */
    private static boolean isPrimitiveType(DetailAST ast) {
        boolean result = false;
        final DetailAST parameterType = ast.findFirstToken(TokenTypes.TYPE);
        final DetailAST identToken = parameterType.getFirstChild();

        if (identToken != null) {
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
            }
        }
        return result;
    }

    /**
     * Checks whether token is array or elipsis.
     *
     * @param typeToken
     *        the token
     * @return true if yes
     */
    private static boolean isArrayOrElipsis(final DetailAST typeToken) {
        final DetailAST next = typeToken.getNextSibling();
        final boolean isArrayDeclarator =
                typeToken.findFirstToken(TokenTypes.ARRAY_DECLARATOR) != null;
        final boolean hasArrayOrEllipses =
                TokenUtil.isOfType(next, TokenTypes.ARRAY_DECLARATOR, TokenTypes.ELLIPSIS);
        return hasArrayOrEllipses || isArrayDeclarator;
    }

    /**
     * Is the current symbol of void type.
     *
     * @param ast
     *        the ast
     * @return true if yes
     */
    protected static boolean isVoid(DetailAST ast) {
        final DetailAST parameterType = ast.findFirstToken(TokenTypes.TYPE);
        final boolean result;
        final DetailAST identToken = parameterType.getFirstChild();
        result = identToken.getType() == TokenTypes.LITERAL_VOID;
        return result;
    }

    /**
     * Class ClassJsr305Handler. Handles a class.
     */
    private final class ClassJsr305Handler extends AbstractJsr305Handler {

        /**
         * Constructor.
         *
         * @param ast
         *        the ast
         */
        protected ClassJsr305Handler(final DetailAST ast) {
            super(ast);
        }

        /**
         * Run the actual handler.
         */
        @Override
        protected void runHandler() {
            checkContainsAny(MSG_ILLEGAL_CLASS_LEVEL_ANNOTATION,
                    NullnessAnnotation.CHECK_FOR_NULL, NullnessAnnotation.CHECK_RETURN_VALUE,
                    NullnessAnnotation.NONNULL, NullnessAnnotation.NULLABLE);
            checkContainsAll(MSG_CONTRADICTING_CLASS_LEVEL_ANNOTATIONS,
                    NullnessAnnotation.PARAMETERS_ARE_NONNULL_BY_DEFAULT,
                    NullnessAnnotation.PARAMETERS_ARE_NULLABLE_BY_DEFAULT);
        }

    }

    /**
     * Class ParameterJsr305Handler. Handles a parameter.
     */
    private final class ParameterJsr305Handler extends AbstractJsr305Handler {

        /**
         * Constructor.
         *
         * @param ast
         *        the ast
         */
        protected ParameterJsr305Handler(final DetailAST ast) {
            super(ast);
        }

        /**
         * Run the actual handler.
         */
        @Override
        protected void runHandler() {
            checkContainsAny(MSG_PARAM_DEFINITIONS_WITH_CHECK,
                    NullnessAnnotation.CHECK_FOR_NULL, NullnessAnnotation.CHECK_RETURN_VALUE);
            checkContainsAny(MSG_PARAM_DEFINITION_WITH_OVERRIDE,
                    NullnessAnnotation.OVERRIDE);
            checkContainsAny(MSG_PARAM_DEFINITION_WITH_NONNULL_BY_DEFAULT,
                    NullnessAnnotation.PARAMETERS_ARE_NONNULL_BY_DEFAULT);
            checkContainsAny(MSG_PARAM_DEFINITION_WITH_NULLABLE_BY_DEFAULT,
                    NullnessAnnotation.PARAMETERS_ARE_NULLABLE_BY_DEFAULT);
            checkContainsAny(MSG_PARAM_DEFINITION_WITH_RETURN_DEFAULT,
                    NullnessAnnotation.RETURN_VALUES_ARE_NONNULL_BY_DEFAULT);
            checkContainsAll(MSG_PARAM_NONNULL_AND_NULLABLE,
                    NullnessAnnotation.NONNULL, NullnessAnnotation.NULLABLE);

            final DetailAST ast = getAst();
            if (isPrimitiveType(ast)) {
                checkContainsAny(MSG_PRIMITIVES_WITH_NULLNESS_ANNOTATION,
                        NullnessAnnotation.CHECK_FOR_NULL, NullnessAnnotation.NONNULL,
                        NullnessAnnotation.NULLABLE);
            }
            else {
                final NullnessAnnotation firstAncestorAnnotation =
                        getParentMethodOrClassAnnotation(
                                    NullnessAnnotation.PARAMETERS_ARE_NONNULL_BY_DEFAULT,
                                    NullnessAnnotation.PARAMETERS_ARE_NULLABLE_BY_DEFAULT);
                final boolean isMethodOverridden = isMethodOverridden();
                final boolean parametersAreNonnullByDefault = firstAncestorAnnotation
                        == NullnessAnnotation.PARAMETERS_ARE_NONNULL_BY_DEFAULT;
                final boolean parametersAreNullableByDefault = firstAncestorAnnotation
                        == NullnessAnnotation.PARAMETERS_ARE_NULLABLE_BY_DEFAULT;

                if (isMethodOverridden && !allowOverridingParameter) {
                    checkContainsAny(MSG_OVERRIDDEN_WITH_INCREASED_CONSTRAINT,
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
     * Class AbstractMethodJsr305Handler. A handler on a method or constructor (special case).
     */
    private abstract class AbstractMethodJsr305Handler extends AbstractJsr305Handler {

        /**
         * Constructor.
         *
         * @param ast
         *        the ast
         */
        private AbstractMethodJsr305Handler(final DetailAST ast) {
            super(ast);
        }

        /**
         * Run the actual handler.
         */
        @Override
        protected void runHandler() {
            checkContainsAll(MSG_CONTRADICTING_CLASS_LEVEL_ANNOTATIONS,
                    NullnessAnnotation.PARAMETERS_ARE_NONNULL_BY_DEFAULT,
                    NullnessAnnotation.PARAMETERS_ARE_NULLABLE_BY_DEFAULT);
            runReturnAnnotationHandler();
        }

        /**
         * Run annotation handler for return.
         */
        protected abstract void runReturnAnnotationHandler();

    }

    /**
     * Class MethodJsr305Handler. A handler for a method.
     */
    private final class MethodJsr305Handler extends AbstractMethodJsr305Handler {

        /**
         * Constructor.
         *
         * @param ast
         *        the ast
         */
        protected MethodJsr305Handler(final DetailAST ast) {
            super(ast);
        }

        /**
         * Run annotation handler for return.
         */
        @Override
        protected void runReturnAnnotationHandler() {
            checkContainsAny(MSG_RETURN_VALUE_WITH_NONNULL_BY_DEFAULT,
                    NullnessAnnotation.RETURN_VALUES_ARE_NONNULL_BY_DEFAULT);
            checkContainsAny(MSG_RETURN_VALUE_WITH_NULLABLE,
                    NullnessAnnotation.NULLABLE);
            checkContainsAll(MSG_CONTRADICTING_RETURN_VALUE_ANNOTATIONS, NullnessAnnotation.NONNULL,
                    NullnessAnnotation.CHECK_FOR_NULL);
            checkContainsAll(MSG_OVERRIDDEN_METHOD_WITH_CHECK_RETURN_VALUE,
                    NullnessAnnotation.CHECK_RETURN_VALUE, NullnessAnnotation.OVERRIDE);
            checkRedundancyDueToClassLevelAnnotation(MSG_REDUNDANT_NONNULL_BY_DEFAULT_ANNOTATION,
                    NullnessAnnotation.PARAMETERS_ARE_NONNULL_BY_DEFAULT);
            checkRedundancyDueToClassLevelAnnotation(MSG_REDUNDANT_NULLABLE_BY_DEFAULT_ANNOTATION,
                    NullnessAnnotation.PARAMETERS_ARE_NULLABLE_BY_DEFAULT);

            final DetailAST ast = getAst();
            if (isVoid(ast)) {
                checkContainsAny(MSG_VOID_WITH_CHECK_RETURN_VALUE_ANNOTATION,
                        NullnessAnnotation.CHECK_RETURN_VALUE);
            }
            if (isPrimitiveType(ast)) {
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
     * Class ConstructorJsr305Handler. Handles a constructor.
     */
    private final class ConstructorJsr305Handler extends AbstractMethodJsr305Handler {

        /**
         * Constructor.
         *
         * @param ast
         *        the ast
         */
        protected ConstructorJsr305Handler(final DetailAST ast) {
            super(ast);
        }

        /**
         * Look for return type nullness annotations (which are illegal).
         */
        @Override
        protected void runReturnAnnotationHandler() {
            checkContainsAny(MSG_CONSTRUCTOR_WITH_RETURN_ANNOTATION,
                    NullnessAnnotation.CHECK_FOR_NULL, NullnessAnnotation.CHECK_RETURN_VALUE,
                    NullnessAnnotation.NONNULL, NullnessAnnotation.NULLABLE,
                    NullnessAnnotation.OVERRIDE);
        }

    }

    /**
     * An abstract handler, the base for handling parameters, methods, classes.
     */
    public abstract class AbstractJsr305Handler {

        /**
         * Some violations could technically trigger two warnings, e.g. an illegal nullness
         * annotation for a parameter is usually technically also a missing legal nullness
         * annotation. In such cases we print only the relevant message and suppress the irrelevant
         * one. This is achieved by sticking to an order of relevance (relevant first) when
         * performing the check and using this field to suppress the (irrelevant) second one.
         */
        private boolean violationFound;
        /** The found annotations. */
        private final Set<NullnessAnnotation> annotations;
        /** The ast. */
        private final DetailAST ast;

        /**
         * Construtor.
         *
         * @param ast
         *        the ast
         */
        private AbstractJsr305Handler(final DetailAST ast) {
            this.ast = ast;
            violationFound = false;
            annotations = findAnnotations(ast);
        }

        /**
         * Run the actual handler.
         */
        public final void handle() {
            runHandler();
        }

        /**
         * Run the actual handler.
         */
        protected abstract void runHandler();

        /**
         * Returns the ast.
         *
         * @return the ast
         */
        protected DetailAST getAst() {
            return ast;
        }

        /**
         * Emits a violation if any of the given annotations are found.
         *
         * @param msg
         *        the violation message to emit
         * @param search
         *        the annotations to look for
         */
        protected void checkContainsAny(final String msg, final NullnessAnnotation... search) {
            if (!violationFound && containsAny(search)) {
                violation(msg);
            }
        }

        /**
         * Check whether any of the given annotations are found.
         *
         * @param search
         *        the annotations to look for
         * @return true if yes
         */
        private boolean containsAny(final NullnessAnnotation... search) {
            boolean result = false;
            if (!annotations.isEmpty()) {
                for (final NullnessAnnotation obj : search) {
                    if (annotations.contains(obj)) {
                        result = true;
                        break;
                    }
                }
            }
            return result;
        }

        /**
         * Emits a violation if all given annotations are found.
         *
         * @param msg
         *        the violation message to emit
         * @param search
         *        the annotations to look for
         */
        protected void checkContainsAll(final String msg, final NullnessAnnotation... search) {
            if (!violationFound && containsAll(search)) {
                violation(msg);
            }
        }

        /**
         * Emits a violation if both this and the parent class have redundant nullness annotations.
         *
         * @param msg
         *        the violation message to emit
         * @param search
         *        the annotations to look for
         */
        protected void checkRedundancyDueToClassLevelAnnotation(final String msg,
                final NullnessAnnotation... search) {
            if (!violationFound) {
                for (final NullnessAnnotation nullnessAnnotation : search) {
                    final boolean thisIsAnnotated = annotations.contains(nullnessAnnotation);
                    final boolean parentIsAnnotated =
                            getParentMethodOrClassAnnotation(nullnessAnnotation) != null;
                    if (thisIsAnnotated && parentIsAnnotated) {
                        violation(msg);
                        break;
                    }
                }
            }
        }

        /**
         * Check whether all the given annotations are present.
         *
         * @param search
         *        the annotations to look for
         * @return true if yes
         */
        private boolean containsAll(final NullnessAnnotation... search) {
            boolean result = true;
            if (annotations.isEmpty()) {
                // an empty list of annotations can never contain all
                result = false;
            }
            else {
                for (final NullnessAnnotation obj : search) {
                    if (!annotations.contains(obj)) {
                        result = false;
                        break;
                    }
                }
            }
            return result;
        }

        /**
         * Make sure that none of the given annotations are present.
         *
         * @param msg
         *        the violation message to emit if one of the given annotations was found
         * @param search
         *        the annotations to look for
         */
        protected void checkContainsNone(final String msg,
                final NullnessAnnotation... search) {
            if (!violationFound && !containsAny(search)) {
                violation(msg);
            }
        }

        /**
         * Handle a violation (log and set 'violationFound' to 'true').
         *
         * @param msg
         *        the violation message
         */
        private void violation(final String msg) {
            log(ast, msg);
            violationFound = true;
        }

        /**
         * Gets the nullness annotation for the parent method or class.
         *
         * @param annotationsToLookFor
         *        the annotations to look for.
         * @return the annotation or null if none was found
         */
        protected NullnessAnnotation
                getParentMethodOrClassAnnotation(final NullnessAnnotation... annotationsToLookFor) {

            boolean finished = false;
            NullnessAnnotation result = null;
            for (DetailAST current = ast.getParent(); current != null && !finished; current =
                    current.getParent()) {
                final int tokenType = current.getType();

                if (isPossibleTokenTypeForNullnessAnnotations(tokenType)) {
                    final Set<NullnessAnnotation> foundAndLookedFor =
                            collectLookedForAnnotations(current, annotationsToLookFor);
                    if (foundAndLookedFor.size() == 1) {
                        result = foundAndLookedFor.iterator().next();
                        finished = true;
                    }
                    else if (!foundAndLookedFor.isEmpty()) {
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
         *
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
         *
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
         *
         * @return true if yes
         */
        protected boolean isMethodOverridden() {
            DetailAST current = ast;
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
