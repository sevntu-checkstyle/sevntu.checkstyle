#!/bin/bash
echo -n "Input path of project:"
read path
rm -r ./Out
mkdir Out
echo "Running AbstractClassNameCheck:"
java -classpath checkstyle-all-5.1.jar	com.puppycrawl.tools.checkstyle.Main	-c ./Configs/AbstractClassNameCheckConfig.xml -r $path > ./Out/AbstractClassNameCheck.txt
cat ./Out/AbstractClassNameCheck.txt
echo
echo "Running CustomDeclarationOrderCheck:"
java -classpath checkstyle-all-5.1.jar	com.puppycrawl.tools.checkstyle.Main	-c ./Configs/ConfigCustomDeclarationOrderCheck.xml -r $path > ./Out/CustomDeclarationOrderCheck.txt
cat ./Out/CustomDeclarationOrderCheck.txt
echo
echo "Running InnerClassCheck:"
java -classpath checkstyle-all-5.1.jar	com.puppycrawl.tools.checkstyle.Main	-c ./Configs/ConfigInnerClassCheck.xml -r $path > ./Out/InnerClassCheck.txt
cat ./Out/InnerClassCheck.txt
echo
echo "Running LineLengthCheck:"
java -classpath checkstyle-all-5.1.jar	com.puppycrawl.tools.checkstyle.Main	-c ./Configs/LineLengthCheckConfig.xml -r $path > ./Out/LineLengthCheck.txt
cat ./Out/LineLengthCheck.txt
echo
echo -n "Input any key for exit."
read



