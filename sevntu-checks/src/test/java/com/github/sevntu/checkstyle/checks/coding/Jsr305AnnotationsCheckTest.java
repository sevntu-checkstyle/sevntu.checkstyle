///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2024 the original author or authors.
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.DetailAstImpl;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.internal.utils.TestUtil;

public class Jsr305AnnotationsCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testParameters() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(Jsr305AnnotationsCheck.class);
        checkConfig.addProperty("packages", "com.github.sevntu.checkstyle.checks.coding");

        final String[] expected = {
            "36:44: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION, "e"),
            "40:45: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION, "e"),
            "40:64: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION, "e"),
            "68:41: "
                    + getCheckMessage(Jsr305AnnotationsCheck.MSG_PARAM_DEFINITIONS_WITH_CHECK, "e"),
            "73:27: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_OVERRIDDEN_WITH_INCREASED_CONSTRAINT, "e"),
            "88:35: " + getCheckMessage(Jsr305AnnotationsCheck.MSG_PARAM_NONNULL_AND_NULLABLE, "e"),
        };

        verify(checkConfig, getPath("InputJsr305AnnotationsCheckWithParameter.java"), expected);
    }

    @Test
    public void testPrimitives() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(Jsr305AnnotationsCheck.class);
        checkConfig.addProperty("packages", "com.github.sevntu.checkstyle.checks.coding");

        final String[] expected = {
            "28:37: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PRIMITIVES_WITH_NULLNESS_ANNOTATION, "e"),
            "32:38: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PRIMITIVES_WITH_NULLNESS_ANNOTATION, "e"),
            "36:42: "
                    + getCheckMessage(Jsr305AnnotationsCheck.MSG_PARAM_DEFINITIONS_WITH_CHECK, "e"),
            "40:5: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PRIMITIVES_WITH_NULLNESS_ANNOTATION, "e"),
            "45:5: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PRIMITIVES_WITH_NULLNESS_ANNOTATION, "e"),
            "50:5: " + getCheckMessage(Jsr305AnnotationsCheck.MSG_RETURN_VALUE_WITH_NULLABLE, "e"),
            "55:5: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PRIMITIVES_WITH_NULLNESS_ANNOTATION, "e"),
            "61:5: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PRIMITIVES_WITH_NULLNESS_ANNOTATION, "e"),
            "99:34: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PRIMITIVES_WITH_NULLNESS_ANNOTATION, "e"),
        };

        verify(checkConfig, getPath("InputJsr305AnnotationsCheckWithPrimitives.java"), expected);
    }

    @Test
    public void testReturnValues() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(Jsr305AnnotationsCheck.class);
        checkConfig.addProperty("packages", "com.github.sevntu.checkstyle.checks.coding");

        final String[] expected = {
            "46:5: " + getCheckMessage(Jsr305AnnotationsCheck.MSG_RETURN_VALUE_WITH_NULLABLE, "e"),
            "51:5: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_CONTRADICTING_RETURN_VALUE_ANNOTATIONS, "e"),
            "69:5: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PRIMITIVES_WITH_NULLNESS_ANNOTATION, "e"),
            "75:5: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PRIMITIVES_WITH_NULLNESS_ANNOTATION, "e"),
            "92:5: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_CONTRADICTING_CLASS_LEVEL_ANNOTATIONS, "e"),
            "95:26: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION, "e"),
            "95:48: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION, "e"),
            "99:5: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_OVERRIDDEN_METHOD_WITH_CHECK_RETURN_VALUE, "e"),
            "105:5: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_VOID_WITH_CHECK_RETURN_VALUE_ANNOTATION, "e"),
            "110:5: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PRIMITIVES_WITH_NULLNESS_ANNOTATION, "e"),
        };

        verify(checkConfig, getPath("InputJsr305AnnotationsCheckWithReturnValue.java"), expected);
    }

    @Test
    public void testConstructors() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(Jsr305AnnotationsCheck.class);
        checkConfig.addProperty("packages", "com.github.sevntu.checkstyle.checks.coding");

        final String[] expected = {
            "37:55: " + getCheckMessage(Jsr305AnnotationsCheck.MSG_PARAM_NONNULL_AND_NULLABLE, "e"),
            "42:55: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION, "e"),
            "54:55: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_REDUNDANT_NONNULL_PARAM_ANNOTATION, "e"),
            "66:75: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_REDUNDANT_NULLABLE_PARAM_ANNOTATION, "e"),
        };

        verify(checkConfig, getPath("InputJsr305AnnotationsCheckWithConstructor.java"), expected);
    }

    @Test
    public void testArrays() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(Jsr305AnnotationsCheck.class);
        checkConfig.addProperty("packages", "com.github.sevntu.checkstyle.checks.coding");

        final String[] expected = {
            "40:43: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION, "e"),
            "52:43: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION, "e"),
            "64:43: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION, "e"),
            "76:43: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION, "e"),
            "90:5: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_RETURN_WITHOUT_NULLNESS_ANNOTATION, "e"),
            "102:32: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION, "e"),
            "114:34: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION, "e"),
        };

        verify(checkConfig, getPath("InputJsr305AnnotationsCheckWithArrays.java"), expected);
    }

    @Test
    public void testClasses() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(Jsr305AnnotationsCheck.class);
        checkConfig.addProperty("packages", "com.github.sevntu.checkstyle.checks.coding");

        final String[] expected = {
            "32:5: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_CONTRADICTING_CLASS_LEVEL_ANNOTATIONS, "e"),
            "43:26: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_REDUNDANT_NULLABLE_PARAM_ANNOTATION, "e"),
            "51:29: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION, "e"),
            "68:32: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_REDUNDANT_NONNULL_PARAM_ANNOTATION, "e"),
            "75:36: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION, "e"),
            "119:43: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION, "e"),
            "125:9: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_REDUNDANT_NONNULL_RETURN_ANNOTATION, "e"),
            "128:9: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_RETURN_VALUE_WITH_NONNULL_BY_DEFAULT, "e"),
            "137:9: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_REDUNDANT_NONNULL_RETURN_ANNOTATION, "e"),
            "142:9: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_RETURN_VALUE_WITH_NONNULL_BY_DEFAULT, "e"),
            "157:9: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_RETURN_VALUE_WITH_NONNULL_BY_DEFAULT, "e"),
        };

        verify(checkConfig, getPath("InputJsr305AnnotationsCheckWithClass.java"), expected);
    }

    @Test
    public void testEnums() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(Jsr305AnnotationsCheck.class);
        checkConfig.addProperty("packages", "com.github.sevntu.checkstyle.checks.coding");

        final String[] expected = {
            "42:32: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_REDUNDANT_NONNULL_PARAM_ANNOTATION, "e"),
            "50:36: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION, "e"),
            "64:9: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_REDUNDANT_NONNULL_RETURN_ANNOTATION, "e"),
            "69:9: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_RETURN_VALUE_WITH_NONNULL_BY_DEFAULT, "e"),
            "80:5: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_CONTRADICTING_CLASS_LEVEL_ANNOTATIONS, "e"),
        };

        verify(checkConfig, getPath("InputJsr305AnnotationsCheckWithEnum.java"), expected);
    }

    @Test
    public void testInheritance() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(Jsr305AnnotationsCheck.class);
        checkConfig.addProperty("packages", "com.github.sevntu.checkstyle.checks.coding");

        final String[] expected = {
            "43:47: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION, "e"),
            "75:43: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION, "e"),
        };

        verify(checkConfig, getPath("InputJsr305AnnotationsCheckWithInheritance.java"), expected);
    }

    @Test
    public void testReturnValueDefaults() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(Jsr305AnnotationsCheck.class);
        checkConfig.addProperty("packages", "com.github.sevntu.checkstyle.checks.coding");

        final String[] expected = {
            "35:5: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_REDUNDANT_NONNULL_RETURN_ANNOTATION, "e"),
        };

        verify(checkConfig, getPath("InputJsr305AnnotationsCheckWithDefaultReturnValues.java"),
                expected);
    }

    @Test
    public void testRedudantClassLevelAnnotations() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(Jsr305AnnotationsCheck.class);
        checkConfig.addProperty("packages", "com.github.sevntu.checkstyle.checks.coding");

        final String[] expected = {
            "28:5: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_REDUNDANT_NULLABLE_BY_DEFAULT_ANNOTATION, "e"),
        };

        verify(checkConfig, getPath("InputJsr305AnnotationsCheckWithRedundantClassLevel.java"),
                expected);
    }

    @Test
    public void testLambdas() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(Jsr305AnnotationsCheck.class);
        checkConfig.addProperty("packages", "com.github.sevntu.checkstyle.checks.coding");

        final String[] expected = {
            "34:10: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_OVERRIDDEN_WITH_INCREASED_CONSTRAINT, "e"),
            "43:5: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_RETURN_WITHOUT_NULLNESS_ANNOTATION, "e"),
            "47:28: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION, "e"),
        };

        verify(checkConfig, getPath("InputJsr305AnnotationsCheckWithLambda.java"), expected);
    }

    @Test
    public void testAllowOverridingParameters() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(Jsr305AnnotationsCheck.class);
        checkConfig.addProperty("packages", "com.github.sevntu.checkstyle.checks.coding");
        checkConfig.addProperty("allowOverridingReturnValue", "true");
        checkConfig.addProperty("allowOverridingParameter", "true");

        final String[] expected = {
            "36:44: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION, "e"),
            "40:45: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION, "e"),
            "40:64: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION, "e"),
            "68:41: "
                    + getCheckMessage(Jsr305AnnotationsCheck.MSG_PARAM_DEFINITIONS_WITH_CHECK, "e"),
            "88:35: " + getCheckMessage(Jsr305AnnotationsCheck.MSG_PARAM_NONNULL_AND_NULLABLE, "e"),
        };

        verify(checkConfig, getPath("InputJsr305AnnotationsCheckWithParameter.java"), expected);
    }

    @Test
    public void testAllowOverridingLambdas() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(Jsr305AnnotationsCheck.class);
        checkConfig.addProperty("packages", "com.github.sevntu.checkstyle.checks.coding");
        checkConfig.addProperty("allowOverridingReturnValue", "true");
        checkConfig.addProperty("allowOverridingParameter", "true");

        final String[] expected = {
            "43:5: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_RETURN_WITHOUT_NULLNESS_ANNOTATION, "e"),
            "47:28: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION, "e"),
        };

        verify(checkConfig, getPath("InputJsr305AnnotationsCheckWithLambda.java"), expected);
    }

    @Test
    public void testAllowOverridingInheritance() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(Jsr305AnnotationsCheck.class);
        checkConfig.addProperty("packages", "com.github.sevntu.checkstyle.checks.coding");
        checkConfig.addProperty("allowOverridingReturnValue", "true");
        checkConfig.addProperty("allowOverridingParameter", "true");

        final String[] expected = {
            "43:47: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION, "e"),
            "75:43: " + getCheckMessage(
                    Jsr305AnnotationsCheck.MSG_PARAMETER_WITHOUT_NULLNESS_ANNOTATION, "e"),
        };

        verify(checkConfig, getPath("InputJsr305AnnotationsCheckWithInheritance.java"), expected);
    }

    @Test
    public void testCatch() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(Jsr305AnnotationsCheck.class);
        checkConfig.addProperty("packages", "com.github.sevntu.checkstyle.checks.coding");
        verify(checkConfig, getPath("InputJsr305AnnotationsCheckWithCatch.java"),
                new String[0]);
    }

    @Test
    public void testInclude1Package() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(Jsr305AnnotationsCheck.class);
        checkConfig.addProperty("packages",
                "com.github.sevntu.checkstyle.checks.naming");

        verify(checkConfig, getPath("InputJsr305AnnotationsCheckWithCatch.java"), new String[0]);
    }

    @Test
    public void testInclude2Packages() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(Jsr305AnnotationsCheck.class);
        checkConfig.addProperty("packages",
                "com.github.sevntu.checkstyle.internal,com.github.sevntu.checkstyle.checks.coding");

        verify(checkConfig, getPath("InputJsr305AnnotationsCheckWithCatch.java"), new String[0]);
    }

    @Test
    public void testExcludePackage() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(Jsr305AnnotationsCheck.class);
        checkConfig.addProperty("packages", "com.github.sevntu.checkstyle.checks.coding");
        checkConfig.addProperty("excludePackages",
                "com.github.sevntu.checkstyle.internal,com.github.sevntu.checkstyle.checks.coding");

        final String[] expected = {};

        verify(checkConfig, getPath("InputJsr305AnnotationsCheckWithParameter.java"), expected);
    }

    /**
     * This must be a reflection test as it is too difficult to hit normally and the responsible
     * code can't be removed without hitting checkstyle violations. This test targets the handling
     * of token types not in 'acceptable tokens' (which should normally not occur).
     *
     * @throws Exception
     *         if there is an unexpected error.
     */
    @Test
    public void testHandleDefinition() throws Exception {
        final DetailAstImpl ast = new DetailAstImpl();
        ast.setType(TokenTypes.WILDCARD_TYPE);

        final Method handleDefinition =
                TestUtil.getClassDeclaredMethod(
                        Jsr305AnnotationsCheck.class, "handleDefinition", 1);

        try {
            handleDefinition.invoke(new Jsr305AnnotationsCheck(), ast);
            Assertions.fail("Exception expected.");
        }
        catch (final InvocationTargetException exc) {
            Assertions.assertTrue(
                    exc.getCause().getClass().equals(IllegalArgumentException.class),
                    "IllegalArgumentException expected from 'handleDefinition'");
        }
    }

    @Test
    public void testNestedAnnotations() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(Jsr305AnnotationsCheck.class);
        checkConfig.addProperty("packages", "com.github.sevntu.checkstyle.checks.coding");

        final String[] expected = {};

        verify(checkConfig, getPath("InputJsr305AnnotationsCheckWithNestedAnnotation.java"),
                expected);
    }

}
