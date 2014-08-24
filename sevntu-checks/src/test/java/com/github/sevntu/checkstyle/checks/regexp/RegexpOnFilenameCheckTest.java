////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2014  Oliver Burn
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
package com.github.sevntu.checkstyle.checks.regexp;

import java.io.File;

import com.github.sevntu.checkstyle.BaseFileSetCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Unit test of {@link RegexpOnFilenameCheck}.
 *
 * @author Thomas Jensen
 */
public class RegexpOnFilenameCheckTest
    extends BaseFileSetCheckTestSupport
{
    private static final String REAL_EXT = "txt";

    private static final String SIMPLE_FILENAME = "InputRegexpOnFilename." + REAL_EXT;

    private DefaultConfiguration mCheckConfig;



    @Before
    public void setUp()
    {
        mCheckConfig = createCheckConfig(RegexpOnFilenameCheck.class);
    }



    @Test
    public void testSelectByExtension_Include1()
        throws Exception
    {
        final String filepath = getPath(SIMPLE_FILENAME);
        final String regexp = "no_match";
        mCheckConfig.addAttribute("fileExtensions", "java, " + REAL_EXT);
        mCheckConfig.addAttribute("regexp", regexp);
        mCheckConfig.addAttribute("mode", "required");
        final String[] expected = {"0: Filename '" + SIMPLE_FILENAME
            + "' does not contain required pattern '" + regexp + "'.", };
        verify(mCheckConfig, filepath, expected);
    }



    @Test
    public void testSelectByExtension_Include2()
        throws Exception
    {
        final String filepath = getPath(SIMPLE_FILENAME);
        final String regexp = "no_match";
        mCheckConfig.addAttribute("fileExtensions", REAL_EXT);
        mCheckConfig.addAttribute("regexp", regexp);
        mCheckConfig.addAttribute("mode", "required");
        final String[] expected = {"0: Filename '" + SIMPLE_FILENAME
            + "' does not contain required pattern '" + regexp + "'.", };
        verify(mCheckConfig, filepath, expected);
    }



    @Test
    public void testSelectByExtension_Exclude()
        throws Exception
    {
        final String filepath = getPath(SIMPLE_FILENAME);
        final String regexp = "no_match";
        mCheckConfig.addAttribute("fileExtensions", "noMatch");
        mCheckConfig.addAttribute("regexp", regexp);
        mCheckConfig.addAttribute("simple", "false");
        final String[] expected = {};
        verify(mCheckConfig, filepath, expected);
    }



    @Test
    public void testSelectByRegexp_Include1()
        throws Exception
    {
        final String filepath = getPath(SIMPLE_FILENAME);
        final String regexp = "no_match";
        mCheckConfig.addAttribute("fileExtensions", REAL_EXT);
        mCheckConfig.addAttribute("selection", SIMPLE_FILENAME + "$");
        mCheckConfig.addAttribute("regexp", regexp);
        mCheckConfig.addAttribute("mode", "required");
        final String[] expected = {"0: Filename '" + SIMPLE_FILENAME
            + "' does not contain required pattern '" + regexp + "'.", };
        verify(mCheckConfig, filepath, expected);
    }



    @Test
    public void testSelectByRegexp_Include2()
        throws Exception
    {
        final String filepath = getPath(SIMPLE_FILENAME);
        final String regexp = "no_match";
        mCheckConfig.addAttribute("regexp", regexp);
        mCheckConfig.addAttribute("selection", SIMPLE_FILENAME + "$");
        mCheckConfig.addAttribute("mode", "required");
        final String[] expected = {"0: Filename '" + SIMPLE_FILENAME
            + "' does not contain required pattern '" + regexp + "'.", };
        verify(mCheckConfig, filepath, expected);
    }



    @Test
    public void testSelectByRegexp_Exclude()
        throws Exception
    {
        final String filepath = getPath(SIMPLE_FILENAME);
        final String regexp = "no_match";
        mCheckConfig.addAttribute("regexp", regexp);
        mCheckConfig.addAttribute("selection", "^no_match");
        mCheckConfig.addAttribute("mode", "required");
        final String[] expected = {};
        verify(mCheckConfig, filepath, expected);
    }



    @Test
    public void testNoRegexpsGiven_Ok()
        throws Exception
    {
        final String filepath = getPath(SIMPLE_FILENAME);
        final String[] expected = {};
        verify(mCheckConfig, filepath, expected);
    }



    @Test
    public void testIllegal()
        throws Exception
    {
        final String filepath = getPath(SIMPLE_FILENAME);
        final String slash = "[\\\\/]";
        final String regexp = slash + getClass().getPackage().getName().replace(".", slash) + slash;
        mCheckConfig.addAttribute("regexp", regexp);
        mCheckConfig.addAttribute("simple", "false");
        mCheckConfig.addAttribute("mode", "illegal");
        final String[] expected = {"0: Filename '" + filepath + "' contains illegal pattern '"
            + regexp + "'.", };
        verify(mCheckConfig, filepath, expected);
    }



    @Test
    public void testRequired()
        throws Exception
    {
        final String filepath = getPath(SIMPLE_FILENAME);
        final String slash = "[\\\\/]";
        final String regexp = slash + getClass().getPackage().getName().replace(".", slash) + slash;
        mCheckConfig.addAttribute("regexp", regexp);
        mCheckConfig.addAttribute("simple", "false");
        mCheckConfig.addAttribute("mode", "required");
        final String[] expected = {};
        verify(mCheckConfig, filepath, expected);
    }



    @Test
    public void testIllegal_Not()
        throws Exception
    {
        final String filepath = getPath(SIMPLE_FILENAME);
        final String regexp = "no_match";
        mCheckConfig.addAttribute("regexp", regexp);
        mCheckConfig.addAttribute("simple", "false");
        mCheckConfig.addAttribute("mode", "illegal");
        final String[] expected = {};
        verify(mCheckConfig, filepath, expected);
    }



    @Test
    public void testRequired_Not()
        throws Exception
    {
        final String filepath = getPath(SIMPLE_FILENAME);
        final String regexp = "no_match";
        mCheckConfig.addAttribute("regexp", regexp);
        mCheckConfig.addAttribute("simple", "false");
        mCheckConfig.addAttribute("mode", "required");
        final String[] expected = {"0: Filename '" + filepath
            + "' does not contain required pattern '" + regexp + "'.", };
        verify(mCheckConfig, filepath, expected);
    }



    @Test
    public void testIllegalSimple()
        throws Exception
    {
        final String filepath = getPath(SIMPLE_FILENAME);
        final String regexp = "^" + SIMPLE_FILENAME + "$";
        mCheckConfig.addAttribute("regexp", regexp);
        final String[] expected = {"0: Filename '" + SIMPLE_FILENAME
            + "' contains illegal pattern '" + regexp + "'.", };
        verify(mCheckConfig, filepath, expected);
    }



    @Test
    public void testRequiredSimple()
        throws Exception
    {
        final String filepath = getPath(SIMPLE_FILENAME);
        final String regexp = "^" + SIMPLE_FILENAME + "$";
        mCheckConfig.addAttribute("regexp", regexp);
        mCheckConfig.addAttribute("mode", "required");
        final String[] expected = {};
        verify(mCheckConfig, filepath, expected);
    }



    @Test
    public void testBrokenRegexp()
        throws Exception
    {
        final String filepath = getPath(SIMPLE_FILENAME);
        final String illegal = "*$"; // incorrect syntax
        mCheckConfig.addAttribute("regexp", illegal);
        mCheckConfig.addAttribute("simple", "false");
        try {
            verify(mCheckConfig, filepath, new String[]{});
            Assert.fail("CheckstyleException was not thrown");
        }
        catch (CheckstyleException expected) {
            // expected
        }
    }



    @Test
    public void testBrokenModeParam()
        throws Exception
    {
        final String filepath = getPath(SIMPLE_FILENAME);
        mCheckConfig.addAttribute("mode", "unknownMode");
        try {
            verify(mCheckConfig, filepath, new String[]{});
            Assert.fail("CheckstyleException was not thrown");
        }
        catch (CheckstyleException expected) {
            // expected
        }
    }



    @Test
    public void testTrailingSpace()
        throws Exception
    {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("checkstyle-test-", "txt ");
            final String[] expected = {"0: Filename '" + tempFile.getName()
                + "' contains illegal pattern '" + "^(?:\\s+.*|.*?\\s+)$" + "'.", };
            verify(mCheckConfig, tempFile.getAbsolutePath(), expected);
        }
        finally {
            if (tempFile != null) {
                Assert.assertTrue("Could not delete temp file: " + tempFile.getAbsolutePath(),
                    tempFile.delete());
            }
        }
    }



    @Test
    public void testLeadingSpace()
        throws Exception
    {
        File tempFile = null;
        try {
            tempFile = File.createTempFile(" checkstyle-test-", "txt");
            final String[] expected = {"0: Filename '" + tempFile.getName()
                + "' contains illegal pattern '" + "^(?:\\s+.*|.*?\\s+)$" + "'.", };
            verify(mCheckConfig, tempFile.getAbsolutePath(), expected);
        }
        finally {
            if (tempFile != null) {
                Assert.assertTrue("Could not delete temp file: " + tempFile.getAbsolutePath(),
                    tempFile.delete());
            }
        }
    }



    @Test
    public void testNoSpaces()
        throws Exception
    {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("checkstyle-test-", "txt");
            final String[] expected = {};
            verify(mCheckConfig, tempFile.getAbsolutePath(), expected);
        }
        finally {
            if (tempFile != null) {
                Assert.assertTrue("Could not delete temp file: " + tempFile.getAbsolutePath(),
                    tempFile.delete());
            }
        }
    }



    @Test
    public void testNullEmptyRegexParams_Ok()
        throws Exception
    {
        RegexpOnFilenameCheck check = new RegexpOnFilenameCheck();
        check.setSelection(null);
        check.setSelection("");
        check.setRegexp(null);
        check.setRegexp("");
    }
}
