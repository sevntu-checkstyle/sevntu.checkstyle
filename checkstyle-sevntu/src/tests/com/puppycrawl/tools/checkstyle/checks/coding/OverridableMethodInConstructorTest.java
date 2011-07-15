package com.puppycrawl.tools.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import java.io.File;
import org.junit.Test;

public class OverridableMethodInConstructorTest extends BaseCheckTestSupport {

    @Test
    public final void testNoWarnings() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(OverridableMethodInConstructorCheck.class);

        String[] expected = {
        };

      System.setProperty("testinputs.dir",
      "/media/data/Work/Git repository clone = Eclipse workspace/sevntu.checkstyle/checkstyle-sevntu/src/testinputs/com/puppycrawl/tools/checkstyle");

        verify(checkConfig, getPath("coding" + File.separator + "InputOverridableMethodInConstructor1.java"), expected);
    }
	
    @Test
    public final void testWarning() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(OverridableMethodInConstructorCheck.class);

        String[] expected = { "10:27: " + createMsg("overrideMe") };
        
        System.setProperty("testinputs.dir",
        "/media/data/Work/Git repository clone = Eclipse workspace/sevntu.checkstyle/checkstyle-sevntu/src/testinputs/com/puppycrawl/tools/checkstyle");
        
                verify(checkConfig, getPath("coding" + File.separator + "InputOverridableMethodInConstructor2.java"), expected);
    }
    
    @Test
    public final void test2Warnings() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(OverridableMethodInConstructorCheck.class);
        
        
        String[] expected = { 
                "10:27: " + createMsg("overrideMe"),
                "15:27: " + createMsg("overrideMe")
                };

        System.setProperty("testinputs.dir",
        "/media/data/Work/Git repository clone = Eclipse workspace/sevntu.checkstyle/checkstyle-sevntu/src/testinputs/com/puppycrawl/tools/checkstyle");

                verify(checkConfig, getPath("coding" + File.separator + "InputOverridableMethodInConstructor3.java"), expected);
    }
    
    @Test
    public final void testWarningInSecondDepth() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(OverridableMethodInConstructorCheck.class);

        String[] expected = { "10:32: " + createMsg("overrideMe") };

        System.setProperty("testinputs.dir",
        "/media/data/Work/Git repository clone = Eclipse workspace/sevntu.checkstyle/checkstyle-sevntu/src/testinputs/com/puppycrawl/tools/checkstyle");

        verify(checkConfig, getPath("coding" + File.separator + "InputOverridableMethodInConstructor4.java"), expected);
    }

    
    @Test
    public final void testWarningsInThirdDepth() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(OverridableMethodInConstructorCheck.class);

        String[] expected = { "10:32: " + createMsg("overrideMe"),
        		"11:27: " + createMsg("overrideMe"),};

        System.setProperty("testinputs.dir",
        "/media/data/Work/Git repository clone = Eclipse workspace/sevntu.checkstyle/checkstyle-sevntu/src/testinputs/com/puppycrawl/tools/checkstyle");
        
                verify(checkConfig, getPath("coding" + File.separator + "InputOverridableMethodInConstructor5.java"), expected);
    }


    @Test
    public final void testCloneNoWarningsSimple() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(OverridableMethodInConstructorCheck.class);

        String[] expected = {
                };

        System.setProperty("testinputs.dir",
        "/media/data/Work/Git repository clone = Eclipse workspace/sevntu.checkstyle/checkstyle-sevntu/src/testinputs/com/puppycrawl/tools/checkstyle");
        
                verify(checkConfig, getPath("coding" + File.separator + "InputOverridableMethodInConstructor6.java"), expected);
    }
    
    @Test
    public final void testCloneNoWarningsComplicated() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(OverridableMethodInConstructorCheck.class);

        String[] expected = {
                };

        System.setProperty("testinputs.dir",
        "/media/data/Work/Git repository clone = Eclipse workspace/sevntu.checkstyle/checkstyle-sevntu/src/testinputs/com/puppycrawl/tools/checkstyle");
        
                verify(checkConfig, getPath("coding" + File.separator + "InputOverridableMethodInConstructor7.java"), expected);
    }

    @Test
    public final void testCloneWarnings() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(OverridableMethodInConstructorCheck.class);

        String[] expected = {
        		"20:37: Overridable method 'doSmth' called in \"clone()\" method.",
        		"37:37: Overridable method 'doSmth' called in \"clone()\" method.",
                };

        System.setProperty("testinputs.dir",
        "/media/data/Work/Git repository clone = Eclipse workspace/sevntu.checkstyle/checkstyle-sevntu/src/testinputs/com/puppycrawl/tools/checkstyle");
        
                verify(checkConfig, getPath("coding" + File.separator + "InputOverridableMethodInConstructor8.java"), expected);
    }

    @Test
    public final void testSerializableNoWarnings() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(OverridableMethodInConstructorCheck.class);

        String[] expected = {
                };

        System.setProperty("testinputs.dir",
        "/media/data/Work/Git repository clone = Eclipse workspace/sevntu.checkstyle/checkstyle-sevntu/src/testinputs/com/puppycrawl/tools/checkstyle");
        
                verify(checkConfig, getPath("coding" + File.separator + "InputOverridableMethodInConstructor10.java"), expected);
    }
    
    @Test
    public final void testSerializableWarning() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(OverridableMethodInConstructorCheck.class);

        String[] expected = {
        		"31:20: Overridable method 'doSmth' called in \"readObject()\" method.",
                };

        System.setProperty("testinputs.dir",
        "/media/data/Work/Git repository clone = Eclipse workspace/sevntu.checkstyle/checkstyle-sevntu/src/testinputs/com/puppycrawl/tools/checkstyle");
        
                verify(checkConfig, getPath("coding" + File.separator + "InputOverridableMethodInConstructor11.java"), expected);
    }
    
    
    public String createMsg(String methodName) {
        return "Overridable method '" + methodName + "' called in constructor.";
    }

}