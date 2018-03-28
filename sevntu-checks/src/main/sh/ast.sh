#!/usr/bin/env bash

#################################################################################
#  This is a useful script which builds Checkstyle AST (abstract syntax tree)   #
#  for custom Java file and opens it using simple built-in Checkstyle GUI.      #
#################################################################################

# Previously, to run Checkstyle AST Viewer you had to execute something like this:
#   $ java -classpath checkstyle-5.6-all.jar com.puppycrawl.tools.checkstyle.gui.Main

# I know for myself how it is uncomfortable when you should to execute 
# such commands in terminal every time you want to see an AST for Java file.
# This script should simplify you job.

# usage: ./ast.sh <path_to_Java_file>

# Advanced: you can bind this script as default shell command using shell alias:
# Add this line to the end of your ~/.bashrc file:
# "alias ast='< path_to_sh >'
# And reopen the Terminal window (or open new)

# experienced Checkstyle developers can install the 'Path tools' plugin into Eclipse 
# and configure it to execute this script on custom Java file by right-clicking 
# on it in Eclipse and choosing Path Tools --> Custom --> <Custom 'Show AST tree' command> ;)
# HOWTO: 
# 1. Install 'Path tools' plugin into Eclipse form update-site below:
# http://pathtools.googlecode.com/svn/trunk/PathToolsUpdateSite/site.xml
# 2. In Eclipse open: 'Preferences' --> 'Path Tools' and
# add the new custom FILE command with name=[Show AST Tree], pattern = [*.java] and command:
# [${workspace_loc:/sevntu-checks/src/main/sh/ast.sh} "{path}"] (all without [ ] braces)
# 3. Right-click on Java file anywhere in Eclipse and execute your command:
# 'Path Tools' --> 'Custom' --> 'Show AST tree'

# Note, that during the first launch this script downloads the custom 
# checkstyle 5.6 jar from remote server into your /tmp folder. 
# I do not know how to get rid of it or do it another way.

FILE_TO_CHECK=$1

CHECKSTYLE_VERSION=5.7
ARCHIVE_FILE_NAME=checkstyle-${CHECKSTYLE_VERSION}-bin.tar.gz
CHECKSTYLE_OUTPUT_FILE_NAME=checkstyle-${CHECKSTYLE_VERSION}-all.jar

# checking  /tmp for existence
TMPDIR=/tmp
if [ ! -d $TMPDIR ]
then
	echo "[ERROR] $TMPDIR folder couldn't be found"
	exit 1;
fi

CHECKSTYLE_JAR_DIR=${TMPDIR}/checkstyle-${CHECKSTYLE_VERSION}

if [ ! -f "${CHECKSTYLE_JAR_DIR}/checkstyle-${CHECKSTYLE_VERSION}-all.jar" ]
then
	echo 'Checkstyle Jar is not present in '$TMPDIR', trying to download it...'	
	wget http://sourceforge.net/projects/checkstyle/files/checkstyle/${CHECKSTYLE_VERSION}/${ARCHIVE_FILE_NAME}/download -O ${TMPDIR}/${ARCHIVE_FILE_NAME}
	RES=$?
	if [ ! "$RES" = "0" ]; then
		echo "[ERROR] Checkstyle download failed."
		exit 1
	fi
	cd ${TMPDIR}
	tar -zxvf ${ARCHIVE_FILE_NAME} -C . checkstyle-${CHECKSTYLE_VERSION}/${CHECKSTYLE_OUTPUT_FILE_NAME}
	rm -rf ${ARCHIVE_FILE_NAME}
fi

if [[ "$FILE_TO_CHECK" == file:///* ]]
then
	FILE_TO_CHECK=`echo $FILE_TO_CHECK | awk '{print substr($0,8)}'`
fi

if [ $FILE_TO_CHECK ]; then 
	echo "Building AST Tree for: '$FILE_TO_CHECK'"
fi

java -classpath ${CHECKSTYLE_JAR_DIR}/${CHECKSTYLE_OUTPUT_FILE_NAME} \
com.puppycrawl.tools.checkstyle.gui.Main ${FILE_TO_CHECK} &
