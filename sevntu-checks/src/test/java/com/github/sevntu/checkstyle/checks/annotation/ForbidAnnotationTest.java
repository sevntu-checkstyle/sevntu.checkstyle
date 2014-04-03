////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2011  Oliver Burn
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
package com.github.sevntu.checkstyle.checks.annotation;

import static com.github.sevntu.checkstyle.checks.annotation.ForbidAnnotationCheck.*;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.annotation.ForbidAnnotationCheck;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * Test that annotation's target is correct.
 * 
 * @author <a href="mailto:hidoyatov.v.i@gmail.com">Hidoyatov Victor</a>
 * 
 */
public class ForbidAnnotationTest extends BaseCheckTestSupport
{

	@Test
	public void testPackageIsForbidden() throws Exception
	{
		DefaultConfiguration checkConfig = createCheckConfig(ForbidAnnotationCheck.class);

		checkConfig.addAttribute("annotationNames", "pack1,pack2,pack3");
		checkConfig.addAttribute("annotationTargets", "PACKAGE_DEF");

		final String[] expected1 = {
				"1: " + getCheckMessage(MSG_KEY, "package", "pack1"),
				"2: " + getCheckMessage(MSG_KEY, "package", "pack2"),
				"3: " + getCheckMessage(MSG_KEY, "package", "pack3"), };

		verify(checkConfig, getPath("ForbiAnnotationInput.java"), expected1);
	}

	@Test
	public void testFullAnnotationName() throws Exception
	{
		DefaultConfiguration checkConfig = createCheckConfig(ForbidAnnotationCheck.class);

		final String[] expected1 = {};

		verify(checkConfig, getPath("ForbiAnnotationInput2.java"), expected1);
	}

	@Test
	public void testVariableIsForbidden() throws Exception
	{
		DefaultConfiguration checkConfig = createCheckConfig(ForbidAnnotationCheck.class);

		checkConfig.addAttribute("annotationNames",
				"Edible,Author,Author2,SuppressWarnings");
		checkConfig.addAttribute("annotationTargets", "VARIABLE_DEF");

		final String[] expected2 = {
				"12: " + getCheckMessage(MSG_KEY, "VARIABLE_DEF", "Edible"),
				"19: " + getCheckMessage(MSG_KEY, "VARIABLE_DEF", "Author"),
				"20: " + getCheckMessage(MSG_KEY, "VARIABLE_DEF", "Author2"),
				"58: " + getCheckMessage(MSG_KEY, "VARIABLE_DEF", "SuppressWarnings"), };

		verify(checkConfig, getPath("ForbiAnnotationInput.java"), expected2);
	}

	@Test
	public void testMethodIsForbidden() throws Exception
	{
		DefaultConfiguration checkConfig = createCheckConfig(ForbidAnnotationCheck.class);

		checkConfig.addAttribute("annotationNames", "Twizzle,One,Two,Three,B");
		checkConfig.addAttribute("annotationTargets", "METHOD_DEF");

		final String[] expected3 = {
				"27: " + getCheckMessage(MSG_KEY, "METHOD_DEF", "Twizzle"),
				"38: " + getCheckMessage(MSG_KEY, "METHOD_DEF", "One"),
				"39: " + getCheckMessage(MSG_KEY, "METHOD_DEF", "Two"),
				"40: " + getCheckMessage(MSG_KEY, "METHOD_DEF", "Three"),
				"46: " + getCheckMessage(MSG_KEY, "METHOD_DEF", "B"), };

		verify(checkConfig, getPath("ForbiAnnotationInput.java"), expected3);
	}

	@Test
	public void testClassAndConstuctorIsForbidden() throws Exception
	{
		DefaultConfiguration checkConfig = createCheckConfig(ForbidAnnotationCheck.class);

		checkConfig.addAttribute("annotationNames", "Test,ctor,ctor2");
		checkConfig.addAttribute("annotationTargets", "CLASS_DEF,CTOR_DEF");

		final String[] expected4 = {
				"5: " + getCheckMessage(MSG_KEY, "CLASS_DEF", "Test"),
				"7: " + getCheckMessage(MSG_KEY, "CTOR_DEF", "ctor"),
				"8: " + getCheckMessage(MSG_KEY, "CTOR_DEF", "ctor2"), };

		verify(checkConfig, getPath("ForbiAnnotationInput.java"), expected4);
	}

	@Test
	public void testAnnotationIsForbidden() throws Exception
	{
		DefaultConfiguration checkConfig = createCheckConfig(ForbidAnnotationCheck.class);

		checkConfig.addAttribute("annotationNames", "Retention,Target");
		checkConfig.addAttribute("annotationTargets", "ANNOTATION_DEF");

		final String[] expected5 = {
				"33: " + getCheckMessage(MSG_KEY, "ANNOTATION_DEF", "Retention"),
				"34: " + getCheckMessage(MSG_KEY, "ANNOTATION_DEF", "Target"), };

		verify(checkConfig, getPath("ForbiAnnotationInput.java"), expected5);
	}

	@Test
	public void testParamerterAndInterfaceIsForbidden() throws Exception
	{
		DefaultConfiguration checkConfig = createCheckConfig(ForbidAnnotationCheck.class);

		checkConfig.addAttribute("annotationNames", "MyAnnotation,A");
		checkConfig.addAttribute("annotationTargets",
				"PARAMETER_DEF,INTERFACE_DEF");

		final String[] expected6 = {
				"42: " + getCheckMessage(MSG_KEY, "PARAMETER_DEF", "MyAnnotation"),
				"44: " + getCheckMessage(MSG_KEY, "INTERFACE_DEF", "A"), };

		verify(checkConfig, getPath("ForbiAnnotationInput.java"), expected6);
	}

	@Test
	public void testEnumIsForbidden() throws Exception
	{
		DefaultConfiguration checkConfig = createCheckConfig(ForbidAnnotationCheck.class);

		checkConfig.addAttribute("annotationNames", "C,int1,int2,int3");
		checkConfig.addAttribute("annotationTargets",
				"ENUM_DEF,ENUM_CONSTANT_DEF");

		final String[] expected7 = {
				"49: " + getCheckMessage(MSG_KEY, "ENUM_DEF", "C"),
				"51: " + getCheckMessage(MSG_KEY, "ENUM_CONSTANT_DEF", "int1"),
				"53: " + getCheckMessage(MSG_KEY, "ENUM_CONSTANT_DEF", "int2"),
				"55: " + getCheckMessage(MSG_KEY, "ENUM_CONSTANT_DEF", "int3"), };

		verify(checkConfig, getPath("ForbiAnnotationInput.java"), expected7);
	}
}
