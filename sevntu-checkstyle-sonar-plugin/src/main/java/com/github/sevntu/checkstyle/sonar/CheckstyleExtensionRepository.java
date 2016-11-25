package com.github.sevntu.checkstyle.sonar;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleRepository;
import org.sonar.api.rules.XMLRuleParser;

public final class CheckstyleExtensionRepository extends RuleRepository {

    private static final String REPOSITORY_KEY = "checkstyle";

    private static final String REPOSITORY_NAME = "Checkstyle";

    private static final String REPOSITORY_LANGUAGE = "java";

    private static final String RULES_RELATIVE_FILE_PATH = "/com/github/sevntu/checkstyle/sonar/checkstyle-extensions.xml";

    private final XMLRuleParser xmlRuleParser;

    public CheckstyleExtensionRepository(XMLRuleParser xmlRuleParser) {
        super(REPOSITORY_KEY, REPOSITORY_LANGUAGE);
        setName(REPOSITORY_NAME);
        this.xmlRuleParser = xmlRuleParser;
    }

    @Override
    public List<Rule> createRules() {
        InputStream input = getClass().getResourceAsStream(RULES_RELATIVE_FILE_PATH);
        try {
            return xmlRuleParser.parse(input);

        } finally {
            IOUtils.closeQuietly(input);
        }
    }
}
