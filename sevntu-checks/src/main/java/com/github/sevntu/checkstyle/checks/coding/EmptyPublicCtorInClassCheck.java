package com.github.sevntu.checkstyle.checks.coding;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.github.sevntu.checkstyle.Utils;
import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * This Check looks for useless empty public constructors. Class constructor is considered useless
 * by this Check if and only if class has exactly one ctor, which is public, empty(one that has no
 * statements) and <a href="http://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.8.9">
 * default</a>.
 * </p>
 * <p>
 * Example 1. Check will generate violation for this code:
 * 
 * <pre>
 * class Dummy {
 *     public Dummy() {
 *     }
 * }
 * </pre>
 *
 * <p>
 * Example 2. Check will not generate violation for this code:
 * 
 * <pre>
 * class Dummy {
 *     private Dummy() {
 *     }
 * }
 * </pre>
 * 
 * class Dummy has only one ctor, which is not public.
 * <p>
 * Example 3. Check will not generate violation for this code:
 * 
 * <pre>
 * class Dummy {
 *     public Dummy() {
 *     }
 *     public Dummy(int i) {
 *     }
 * }
 * </pre>
 * 
 * class Dummy has multiple ctors.
 * <p>
 * Check has two properties:
 * </p>
 * <p>
 * "classAnnotationNames" - This property contains regex for canonical names of class annotations,
 * which require class to have empty public ctor. Check will not log violations for classes marked
 * with annotations that match this regex. Default option value is "javax\.persistence\.Entity".
 * </p>
 * <p>
 * "ctorAnnotationNames" - This property contains regex for canonical names of ctor annotations,
 * which make empty public ctor essential. Check will not log violations for ctors marked with
 * annotations that match this regex. Default option value is "com\.google\.inject\.Inject".
 * </p>
 * Following configuration will adjust Check to skip classes which annotated with
 * "javax.persistence.Entity" and classes which has empty public ctor with
 * "com\.google\.inject\.Inject".
 * 
 * <pre>
 *   &lt;module name="EmptyPublicCtorInClassCheck"&gt;
 *     &lt;property name="classAnnotationNames" value="javax\.persistence\.Entity"/&gt;
 *     &lt;property name="ctorAnnotationNames" value="com\.google\.inject\.Inject"/&gt;
 *   &lt;/module&gt;
 * </pre>
 * 
 * @author <a href="mailto:zuy_alexey@mail.ru">Zuy Alexey</a>
 */
public class EmptyPublicCtorInClassCheck extends Check
{
    /**
     * Violation message key.
     */
    public static final String MSG_KEY = "empty.public.ctor";

    /**
     * List of single-type-imports for current AST.
     */
    private List<String> singleTypeImports = new ArrayList<String>();

    /**
     * List of on-demand-imports for current AST.
     */
    private List<String> onDemandImports = new ArrayList<String>();

    /**
     * Package name for current AST or empty string if AST does not contain package name.
     */
    private String filePackageName;

    /**
     * Regex which matches names of class annotations which require class to have public no-argument
     * ctor. Default value is "javax\.persistence\.Entity".
     */
    private Pattern classAnnotationNames = Pattern.compile("javax\\.persistence\\.Entity");

    /**
     * Regex which matches names of ctor annotations which make empty public ctor essential. Default
     * value is "com\.google\.inject\.Inject".
     */
    private Pattern ctorAnnotationNames = Pattern.compile("com\\.google\\.inject\\.Inject");

    /**
     * Sets regex which matches names of class annotations which require class to have public
     * no-argument ctor.
     * @param regex
     *        regex to match annotation names.
     */
    public void setClassAnnotationNames(String regex)
    {
        if (regex != null && !regex.isEmpty()) {
            classAnnotationNames = Pattern.compile(regex);
        }
        else {
            classAnnotationNames = null;
        }
    }

    /**
     * Sets regex which matches names of ctor annotations which make empty public ctor essential.
     * @param regex
     *        regex to match annotation names.
     */
    public void setCtorAnnotationNames(String regex)
    {
        if (regex != null && !regex.isEmpty()) {
            ctorAnnotationNames = Pattern.compile(regex);
        }
        else {
            ctorAnnotationNames = null;
        }
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] { TokenTypes.CLASS_DEF, TokenTypes.PACKAGE_DEF, TokenTypes.IMPORT };
    }

    @Override
    public void beginTree(DetailAST aRootNode)
    {
        singleTypeImports.clear();
        onDemandImports.clear();
        filePackageName = "";
    }

    @Override
    public void visitToken(DetailAST node)
    {
        switch (node.getType()) {

            case TokenTypes.IMPORT:
                {
                    String packageMemberName = getIdentifierName(node);
        
                    if (isOnDemandImport(packageMemberName)) {
                        onDemandImports.add(packageMemberName);
                    }
                    else {
                        singleTypeImports.add(packageMemberName);
                    }
                }
                break;

            case TokenTypes.CLASS_DEF:
                if (getClassCtorCount(node) == 1) {
                    DetailAST ctorDef = getFirstCtorDefinition(node);
    
                    if (isCtorPublic(ctorDef) &&
                            isCtorHasNoParameters(ctorDef) &&
                            isCtorHasNoStatements(ctorDef))
                    {
                        if (!isClassHasRegisteredAnnotation(node) &&
                                !isCtorHasRegisteredAnnotation(ctorDef))
                        {
                            log(ctorDef, MSG_KEY);
                        }
                    }
                }
                break;

            case TokenTypes.PACKAGE_DEF:
                filePackageName = getIdentifierName(node);
                break;
            default:
                Utils.reportInvalidToken(node.getType());
                break;
        }
    }

    /**
     * Calculates constructor count for class.
     * @param classDefNode
     *        a class definition node.
     * @return ctor count for given class definition.
     */
    private static int getClassCtorCount(DetailAST classDefNode)
    {
        return classDefNode.findFirstToken(TokenTypes.OBJBLOCK).getChildCount(TokenTypes.CTOR_DEF);
    }

    /**
     * Gets first constructor defininition for class.
     * @param classDefNode
     *        a class definition node.
     * @return first ctor definition node for class or null if class has no ctor.
     */
    private static DetailAST getFirstCtorDefinition(DetailAST classDefNode)
    {
        return classDefNode
                .findFirstToken(TokenTypes.OBJBLOCK)
                .findFirstToken(TokenTypes.CTOR_DEF);
    }

    /**
     * Checks whether constructor is public.
     * @param ctorDefNode
     *        a ctor definition node(TokenTypes.CTOR_DEF).
     * @return true, if given ctor is public.
     */
    private static boolean isCtorPublic(DetailAST ctorDefNode)
    {
        return ctorDefNode
                .findFirstToken(TokenTypes.MODIFIERS)
                .findFirstToken(TokenTypes.LITERAL_PUBLIC) != null;
    }

    /**
     * Checks whether ctor has no parameters.
     * @param ctorDefNode
     *        a ctor definition node(TokenTypes.CTOR_DEF).
     * @return true, if ctor has no parameters.
     */
    private static boolean isCtorHasNoParameters(DetailAST ctorDefNode)
    {
        return ctorDefNode.findFirstToken(TokenTypes.PARAMETERS).getChildCount() == 0;
    }

    /**
     * Checks whether ctor body has no statements.
     * @param ctorDefNode
     *        a ctor definition node(TokenTypes.CTOR_DEF).
     * @return
     */
    private static boolean isCtorHasNoStatements(DetailAST ctorDefNode)
    {
        return ctorDefNode.findFirstToken(TokenTypes.SLIST).getChildCount() == 1;
    }

    /**
     * Checks whether class definition has annotation with name specified in
     * {@link #classAnnotationNames} regexp.
     * @param classDefNode
     *        the node of type TokenTypes.CLASS_DEF.
     * @return true, if class definition has annotation with name specified in regexp.
     */
    private boolean isClassHasRegisteredAnnotation(DetailAST classDefNode)
    {
        List<String> annotationNames = getAnnotationCanonicalNames(classDefNode);
        return isAnyOfNamesMatches(annotationNames, classAnnotationNames);
    }

    /**
     * Checks whether ctor definition has annotation with name specified in
     * {@link #ctorAnnotationNames} regexp.
     * @param ctorDefNode
     *        the node of type TokenTypes.CTOR_DEF.
     * @return true, if ctor definition has annotation with name specified in regexp.
     */
    private boolean isCtorHasRegisteredAnnotation(DetailAST ctorDefNode)
    {
        List<String> annotationNames = getAnnotationCanonicalNames(ctorDefNode);
        return isAnyOfNamesMatches(annotationNames, ctorAnnotationNames);
    }

    /**
     * Checks whether any name from the list matches regex.
     * @param annotationNames
     *        annotation names to match against regex.
     * @param pattern
     *        regex to match names. may be null.
     * @return false, if pattern object is null, otherwise true, if any name from the list matches
     *         regex.
     */
    private static boolean isAnyOfNamesMatches(List<String> annotationNames, Pattern pattern)
    {
        if (pattern == null) {
            return false;
        }
        else {
            for (String annotationName : annotationNames) {
                if (pattern.matcher(annotationName).matches()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns canonical names of annotations for given node.
     * @param node
     *        annotated node.
     * @return list of canonical annotation names for given node.
     */
    private List<String> getAnnotationCanonicalNames(DetailAST node)
    {
        List<String> annotationNames = new ArrayList<String>();

        DetailAST modifierNode =
                node.findFirstToken(TokenTypes.MODIFIERS).getFirstChild();

        while (modifierNode != null) {
            if (modifierNode.getType() == TokenTypes.ANNOTATION) {
                String annotationName = getIdentifierName(modifierNode);

                List<String> annotationPossibleCanonicalNames =
                        generateAnnotationPossibleCanonicalNames(annotationName);

                annotationNames.add(annotationName);
                annotationNames.addAll(annotationPossibleCanonicalNames);
            }

            modifierNode = modifierNode.getNextSibling();
        }

        return annotationNames;
    }

    /**
     * Checks whether import is on demand import(one that imports entire package).
     * @param importTargetName
     *        target of import statement.
     * @return true, if import is on demand import import.
     */
    private static boolean isOnDemandImport(String importTargetName)
    {
        return importTargetName.endsWith(".*");
    }

    /**
     * <p>
     * Generates possible canonical annotation names.
     * </p>
     * @param annotationName
     *        simple annotation name.
     * @return list of possible canonical annotation names.
     */
    private List<String>
            generateAnnotationPossibleCanonicalNames(String annotationName)
    {
        List<String> annotationPossibleCanonicalNames = new ArrayList<String>();

        for (String importEntry : singleTypeImports) {
            String annotationCanonicalName =
                    joinSingleTypeImportWithIdentifier(importEntry, annotationName);

            if (annotationCanonicalName != null) {
                annotationPossibleCanonicalNames.add(annotationCanonicalName);
                break;
            }
        }

        for (String importEntry : onDemandImports)
        {
            String annotationCanonicalName =
                    joinOnDemandImportWithIdentifier(importEntry, annotationName);

            annotationPossibleCanonicalNames.add(annotationCanonicalName);
        }

        String annotationCanonicalName =
                joinFilePackageNameWithIdentifier(filePackageName, annotationName);

        annotationPossibleCanonicalNames.add(annotationCanonicalName);

        return annotationPossibleCanonicalNames;
    }

    /**
     * <p>
     * Joins single type import entry and identifier name into fully qualified name.
     * </p>
     * <p>
     * For example: joinMemberImportWithIdentifier("package.Person","Person") returns
     * "package.Person", joinMemberImportWithIdentifier("package.Person","Person.Name") returns
     * "package.Person.Name".
     * </p>
     * @param importEntry
     *        single type import entry for join.
     * @param identifierName
     *        identifier name to join to import.
     * @return fully qualified identifier name if given import corresponds to identifier, otherwise
     *         null.
     */
    private static String
            joinSingleTypeImportWithIdentifier(String importEntry, String identifierName)
    {
        String importEntryLastPart = getSimpleIdentifierNameFromQualifiedName(importEntry);
        String annotationNameFirstPart = getQualifiedNameFirstPart(identifierName);

        if (importEntryLastPart.equals(annotationNameFirstPart)) {
            return importEntry + identifierName.substring(annotationNameFirstPart.length());
        }
        else {
            return null;
        }
    }

    /**
     * <p>
     * Joins on demand import entry and identifier name into fully qualified name.
     * </p>
     * <p>
     * For example: joinWildcardImportWithIdentifier("package.*","Person") returns "package.Person",
     * joinWildcardImportWithIdentifier("package.*","Person.Name") returns "package.Person.Name".
     * </p>
     * @param importEntry
     *        on demand import entry for join.
     * @param identifierName
     *        identifier name to join to import.
     * @return fully qualified identifier name.
     */
    private static String
            joinOnDemandImportWithIdentifier(String importEntry, String identifierName)
    {
        return importEntry.substring(0, importEntry.length() - 1) + identifierName;
    }

    /**
     * <p>
     * Joins package name with identifier name into fully qualified name.
     * </p>
     * <p>
     * For example: joinFilePackageNameWithIdentifier("com.example","Person") returns
     * "com.example.Person".
     * </p>
     * @param packageName
     *        package name to use for join.
     * @param identifierName
     *        identifier name to join to package name.
     * @return fully qualified identifier name.
     */
    private static String
            joinFilePackageNameWithIdentifier(String packageName, String identifierName)
    {
        return packageName + "." + identifierName;
    }

    /**
     * Returns first part of identifier name.
     * @param canonicalName
     *        identifier name.
     * @return first part of identifier name if name is qualified, otherwise returns identifier name
     *         argument.
     */
    private static String getQualifiedNameFirstPart(String canonicalName)
    {
        int firstDotIndex = canonicalName.indexOf('.');

        if (firstDotIndex == -1) {
            return canonicalName;
        }
        else {
            return canonicalName.substring(0, firstDotIndex);
        }
    }

    /**
     * <p>
     * Returns simple identifier name from its qualified name.
     * </p>
     * <p>
     * For example: If method called for name "com.example.company.Person" it will return "Person".
     * </p>
     * @param qualifiedName
     *        qualified identifier name.
     * @return simple identifier name.
     */
    private static String getSimpleIdentifierNameFromQualifiedName(String qualifiedName)
    {
        return qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
    }

    /**
     * Returns name of identifier contained in specified node.
     * @param aNodeWithIdent
     *        a node containing identifier or qualified identifier.
     * @return identifier name for specified node. If node contains qualified name then method
     *         returns its text representation.
     */
    private static String getIdentifierName(DetailAST identifierNode)
    {
        DetailAST identNode = identifierNode.findFirstToken(TokenTypes.IDENT);
        String result;

        if (identNode != null) {
            result = identNode.getText();
        }
        else {
            StringBuilder builder = new StringBuilder(40);
            DetailAST node = identifierNode.findFirstToken(TokenTypes.DOT);

            while (node.getType() == TokenTypes.DOT) {
                builder.insert(0, '.').insert(1, node.getLastChild().getText());

                node = node.getFirstChild();
            }

            builder.insert(0, node.getText());

            result = builder.toString();
        }

        return result;
    }
}
