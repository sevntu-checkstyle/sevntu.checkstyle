echo "Input path of project:"
read path
rmdir Out --ignore-fail-on-non-empty
sleep 1s
mkdir Out
echo "Running AbstractClassNameCheck:"
java -classpath checkstyle-all-5.1.jar	com.puppycrawl.tools.checkstyle.Main	-c AbstractClassNameCheckConfig.xml -r $path > ./Out/AbstractClassNameCheck.txt
cat -n ./Out/AbstractClassNameCheck.txt
echo "Running CustomDeclarationOrderCheck:"
java -classpath checkstyle-all-5.1.jar	com.puppycrawl.tools.checkstyle.Main	-c ConfigCustomDeclarationOrderCheck.xml -r $path > ./Out/CustomDeclarationOrderCheck.txt
cat -n ./Out/CustomDeclarationOrderCheck.txt
echo "Running InnerClassCheck:"
java -classpath checkstyle-all-5.1.jar	com.puppycrawl.tools.checkstyle.Main	-c ConfigInnerClassCheck.xml -r $path > ./Out/InnerClassCheck.txt
cat -n ./Out/InnerClassCheck.txt
echo "Running LineLengthCheck:"
java -classpath checkstyle-all-5.1.jar	com.puppycrawl.tools.checkstyle.Main	-c LineLengthCheckConfig.xml -r $path > ./Out/LineLengthCheck.txt
cat -n ./Out/LineLengthCheck.txt



