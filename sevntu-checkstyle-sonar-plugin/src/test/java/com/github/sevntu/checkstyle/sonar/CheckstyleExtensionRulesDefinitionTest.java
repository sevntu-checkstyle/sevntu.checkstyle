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

package com.github.sevntu.checkstyle.sonar;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;

public class CheckstyleExtensionRulesDefinitionTest {

    @Test
    public void testAllRulesValid() {
        final CheckstyleExtensionRulesDefinition definition =
            new CheckstyleExtensionRulesDefinition(new RulesDefinitionXmlLoader());
        final RulesDefinition.Context context = new RulesDefinition.Context();
        definition.define(context);
        final RulesDefinition.Repository repository = context.repository("checkstyle");

        Assert.assertEquals("Incorrect repository name", "Checkstyle", repository.name());
        Assert.assertEquals("Incorrect repository language", "java", repository.language());

        final List<RulesDefinition.Rule> rules = repository.rules();
        Assert.assertEquals("Incorrect number of loaded rules", 60, rules.size());

        for (RulesDefinition.Rule rule : rules) {
            Assert.assertNotNull("Rule key is not set", rule.key());
            Assert.assertNotNull("Config key is not set for rule " + rule.key(),
                rule.internalKey());
            Assert.assertNotNull("Name is not set for rule " + rule.key(), rule.name());
            Assert.assertNotNull("Description is not set for rule " + rule.key(),
                rule.htmlDescription());

            for (RulesDefinition.Param param : rule.params()) {
                Assert.assertNotNull("Key is not set for a parameter of rule " + rule.key(),
                    param.key());
                Assert.assertNotNull("Description is not set for a parameter '"
                    + param.name() + "' of rule " + rule.key(), param.description());
            }
        }
    }
}
