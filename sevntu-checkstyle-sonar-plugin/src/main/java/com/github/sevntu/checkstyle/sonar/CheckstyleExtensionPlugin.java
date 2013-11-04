package com.github.sevntu.checkstyle.sonar;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.SonarPlugin;

public final class CheckstyleExtensionPlugin extends SonarPlugin {

	@Override
	public List<?> getExtensions() {
		return Arrays.asList(CheckstyleExtensionRepository.class);
	}
}
