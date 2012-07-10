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
package com.github.sevntu.checkstyle.checks.coding;

import java.io.File;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.coding.AvoidDefaultSerializableInInnerClasses;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class AvoidDefaultSerializableInInnerClassesTest extends
    BaseCheckTestSupport
{

    @Test
    public void testWithAllowPartiaFalse()
        throws Exception
        {
        final DefaultConfiguration checkConfig = createCheckConfig(AvoidDefaultSerializableInInnerClasses.class);

        final String[] expected = {
            "33: Inner class should not implement default Serializable interface.",
            "45: Inner class should not implement default Serializable interface.",
            "52: Inner class should not implement default Serializable interface.",
            "59: Inner class should not implement default Serializable interface.",
            "67: Inner class should not implement default Serializable interface.",
            "74: Inner class should not implement default Serializable interface.",
            "97: Inner class should not implement default Serializable interface.",
            "104: Inner class should not implement default Serializable interface.",
            "121: Inner class should not implement default Serializable interface.",
            "134: Inner class should not implement default Serializable interface.",
            "145: Inner class should not implement default Serializable interface.",
            "159: Inner class should not implement default Serializable interface.",
            "170: Inner class should not implement default Serializable interface.",
            "177: Inner class should not implement default Serializable interface.", };
        verify(checkConfig, getPath("coding" + File.separator
                + "InputAvoidDefaultSerializableInInnerClasses1.java"),
                expected);
    }

    @Test
    public void testPrivateNotRealReadObject()
        throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(AvoidDefaultSerializableInInnerClasses.class);
        checkConfig.addAttribute("allowPartialImplementation", "true");

        final String[] expected = {
            "10: Inner class should not implement default Serializable interface.", };

        verify(checkConfig, getPath("coding" + File.separator
            + "InputAvoidDefaultSerializableInInnerClasses2.java"),
            expected);
    }

    @Test
    public void testRealReadObjectNotRealReadObjectRealPrivate()
        throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(AvoidDefaultSerializableInInnerClasses.class);

        final String[] expected = {};

        verify(checkConfig, getPath("coding" + File.separator
                + "InputAvoidDefaultSerializableInInnerClasses3.java"),
                expected);
    }

    @Test
    public void testWithAllowPartiaTrue()
        throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(AvoidDefaultSerializableInInnerClasses.class);
        checkConfig.addAttribute("allowPartialImplementation", "true");
        final String[] expected = {
            "33: Inner class should not implement default Serializable interface.",
            "59: Inner class should not implement default Serializable interface.",
            "67: Inner class should not implement default Serializable interface.",
            "74: Inner class should not implement default Serializable interface.",
            "97: Inner class should not implement default Serializable interface.",
            "104: Inner class should not implement default Serializable interface.",
            "121: Inner class should not implement default Serializable interface.",
            "134: Inner class should not implement default Serializable interface.",
            "145: Inner class should not implement default Serializable interface.",
            "170: Inner class should not implement default Serializable interface.", };
        verify(checkConfig, getPath("coding" + File.separator
                + "InputAvoidDefaultSerializableInInnerClasses1.java"),
                expected);
    }
}
