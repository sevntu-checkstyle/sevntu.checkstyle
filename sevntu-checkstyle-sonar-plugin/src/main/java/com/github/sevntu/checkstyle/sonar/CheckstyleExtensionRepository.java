////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2018 the original author or authors.
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

package com.github.sevntu.checkstyle.sonar;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleRepository;
import org.sonar.api.rules.XMLRuleParser;

/**
 * Sonar RuleRepository with Checkstyle Sevntu extensions.
 * @author rdiachenko
 */
public final class CheckstyleExtensionRepository extends RuleRepository {

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
     * XML Parser.
     */
    private final XMLRuleParser xmlRuleParser;

    /**
     * Useless JavaDoc for a Constructor.
     * @param xmlRuleParser obviously the XML Parser as it already says, what else?!
     */
    public CheckstyleExtensionRepository(XMLRuleParser xmlRuleParser) {
        super(REPOSITORY_KEY, REPOSITORY_LANGUAGE);
        setName(REPOSITORY_NAME);
        this.xmlRuleParser = xmlRuleParser;
    }

    @Override
    public List<Rule> createRules() {
        final InputStream input = getClass().getResourceAsStream(RULES_RELATIVE_FILE_PATH);
        try {
            return xmlRuleParser.parse(input);
        }
        finally {
            IOUtils.closeQuietly(input);
        }
    }

}
