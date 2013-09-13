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

import java.text.MessageFormat;

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

	private String msg = getCheckMessage(ForbidAnnotationCheck.MSG_KEY);

	@Test
	public void testPackageIsForbidden() throws Exception
	{
		DefaultConfiguration checkConfig = createCheckConfig(ForbidAnnotationCheck.class);

		checkConfig.addAttribute("annotationNames", "pack1,pack2,pack3");
		checkConfig.addAttribute("annotationTargets", "PACKAGE_DEF");

		final String[] expected1 = {
				buildMesssage(1, "package", "pack1"),
				buildMesssage(2, "package", "pack2"),
				buildMesssage(3, "package", "pack3"), };

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
				buildMesssage(12, "VARIABLE_DEF", "Edible"),
				buildMesssage(19, "VARIABLE_DEF", "Author"),
				buildMesssage(20, "VARIABLE_DEF", "Author2"),
				buildMesssage(58, "VARIABLE_DEF", "SuppressWarnings"), };

		verify(checkConfig, getPath("ForbiAnnotationInput.java"), expected2);
	}

	@Test
	public void testMethodIsForbidden() throws Exception
	{
		DefaultConfiguration checkConfig = createCheckConfig(ForbidAnnotationCheck.class);

		checkConfig.addAttribute("annotationNames", "Twizzle,One,Two,Three,B");
		checkConfig.addAttribute("annotationTargets", "METHOD_DEF");

		final String[] expected3 = {
				buildMesssage(27, "METHOD_DEF", "Twizzle"),
				buildMesssage(38, "METHOD_DEF", "One"),
				buildMesssage(39, "METHOD_DEF", "Two"),
				buildMesssage(40, "METHOD_DEF", "Three"),
				buildMesssage(46, "METHOD_DEF", "B"), };

		verify(checkConfig, getPath("ForbiAnnotationInput.java"), expected3);
	}

	@Test
	public void testClassAndConstuctorIsForbidden() throws Exception
	{
		DefaultConfiguration checkConfig = createCheckConfig(ForbidAnnotationCheck.class);

		checkConfig.addAttribute("annotationNames", "Test,ctor,ctor2");
		checkConfig.addAttribute("annotationTargets", "CLASS_DEF,CTOR_DEF");

		final String[] expected4 = {
				buildMesssage(5, "CLASS_DEF", "Test"),
				buildMesssage(7, "CTOR_DEF", "ctor"),
				buildMesssage(8, "CTOR_DEF", "ctor2"), };

		verify(checkConfig, getPath("ForbiAnnotationInput.java"), expected4);
	}

	@Test
	public void testAnnotationIsForbidden() throws Exception
	{
		DefaultConfiguration checkConfig = createCheckConfig(ForbidAnnotationCheck.class);

		checkConfig.addAttribute("annotationNames", "Retention,Target");
		checkConfig.addAttribute("annotationTargets", "ANNOTATION_DEF");

		final String[] expected5 = {
				buildMesssage(33, "ANNOTATION_DEF", "Retention"),
				buildMesssage(34, "ANNOTATION_DEF", "Target"), };

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
				buildMesssage(42, "PARAMETER_DEF", "MyAnnotation"),
				buildMesssage(44, "INTERFACE_DEF", "A"), };

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
				buildMesssage(49, "ENUM_DEF", "C"),
				buildMesssage(51, "ENUM_CONSTANT_DEF", "int1"),
				buildMesssage(53, "ENUM_CONSTANT_DEF", "int2"),
				buildMesssage(55, "ENUM_CONSTANT_DEF", "int3"),
		};

		verify(checkConfig, getPath("ForbiAnnotationInput.java"), expected7);
	}

	private String buildMesssage(int lineNumber, String target, String annotationName) {
		return lineNumber + ": " + MessageFormat.format(msg, target, annotationName);
	}
}
