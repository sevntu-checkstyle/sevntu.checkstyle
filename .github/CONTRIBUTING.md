Please read http://checkstyle.sourceforge.net/report_issue.html

**Note:** Sevntu checks are in a different JAR from the main Checkstyle JAR and cannot be run without the main JAR.
The command to run sevntu in the Command Line Interface (CLI) is slightly different as noted below:
```
java -classpath sevntu-checks-X.XX.X.jar;checkstyle-X.XX-all.jar com.puppycrawl.tools.checkstyle.Main -c config.xml YOUR_FILE.java
```

Please provide issue report in format that we request, EACH DETAIL MAKE A HUGE HELP.

Issues that are not following the guidelines, will be processed with last priority.