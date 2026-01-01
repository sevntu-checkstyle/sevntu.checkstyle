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

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;

/**
 * Sonar rules definition for Checkstyle Sevntu extensions.
 *
 * @author rdiachenko
 */
public final class CheckstyleExtensionRulesDefinition implements RulesDefinition {

    /**
     * Repository key.
     */
    private static final String REPOSITORY_KEY = "checkstyle";

    /**
     * Repository name.
     */
    private static final String REPOSITORY_NAME = "Checkstyle";

    /**
     * Repository langauge.
     */
    private static final String REPOSITORY_LANGUAGE = "java";

    /**
     * Relative path to XML file on classpath.
     */
    private static final String RULES_RELATIVE_FILE_PATH =
            "/com/github/sevntu/checkstyle/sonar/checkstyle-extensions.xml";

    /**
     * XML loader for rules definition.
     */
    private final RulesDefinitionXmlLoader xmlRuleLoader;

    /**
     * Useless JavaDoc for a Constructor.
     *
     * @param xmlRuleLoader rules definition xml loader.
     */
    public CheckstyleExtensionRulesDefinition(RulesDefinitionXmlLoader xmlRuleLoader) {
        this.xmlRuleLoader = xmlRuleLoader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void define(Context context) {
        final NewRepository repository = context
            .createRepository(REPOSITORY_KEY, REPOSITORY_LANGUAGE)
            .setName(REPOSITORY_NAME);

        xmlRuleLoader.load(repository, getClass().getResourceAsStream(RULES_RELATIVE_FILE_PATH),
            "UTF-8");

        repository.done();
    }
}
