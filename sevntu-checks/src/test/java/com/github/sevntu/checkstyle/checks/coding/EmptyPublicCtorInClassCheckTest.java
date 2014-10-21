package com.github.sevntu.checkstyle.checks.coding;

import static com.github.sevntu.checkstyle.checks.coding.EmptyPublicCtorInClassCheck.*;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class EmptyPublicCtorInClassCheckTest extends BaseCheckTestSupport
{
    private final String message = getCheckMessage(MSG_KEY);

    private DefaultConfiguration checkConfig = createCheckConfig(EmptyPublicCtorInClassCheck.class);

    @Test
    public void testEmptyPublicCtor()
            throws Exception
    {
        String expected[] = {
                "5:5: " + message,
        };

        verify(checkConfig, getPath("InputEmptyPublicCtorInClass1.java"), expected);
    }

    @Test
    public void testEmptyPrivateCtor()
            throws Exception
    {
        String expected[] = {};

        verify(checkConfig, getPath("InputEmptyPublicCtorInClass2.java"), expected);
    }
    
    @Test
    public void testEmptyProtectedCtor()
            throws Exception
    {
        String expected[] = {};

        verify(checkConfig, getPath("InputEmptyPublicCtorInClass6.java"), expected);
    }

    @Test
    public void testClassWithMultiplePublicCtors()
            throws Exception
    {
        String expected[] = {};

        verify(checkConfig, getPath("InputEmptyPublicCtorInClass3.java"), expected);
    }

    @Test
    public void testPublicNotEmptyCtor()
            throws Exception
    {
        String expected[] = {};

        verify(checkConfig, getPath("InputEmptyPublicCtorInClass4.java"), expected);
    }

    @Test
    public void testClassWithInnerClasses()
            throws Exception
    {
        String expected[] = {
                "5:5: " + message,
                "14:9: " + message,
        };

        verify(checkConfig, getPath("InputEmptyPublicCtorInClass5.java"), expected);
    }

    @Test
    public void testCtorAnnotatedWithAnnotation() throws Exception
    {
        DefaultConfiguration config = createCheckConfig(EmptyPublicCtorInClassCheck.class);

        config.addAttribute("ctorAnnotationNames", "com\\.github\\.sevntu\\.checkstyle\\.checks\\.coding\\.AnnotationName");

        String expected[] = {};

        verify(config, getPath("InputEmptyPublicCtorInClass7.java"), expected);
    }

    @Test
    public void testClassAnnotatedWithAnnotation1() throws Exception
    {
        DefaultConfiguration config = createCheckConfig(EmptyPublicCtorInClassCheck.class);

        config.addAttribute("classAnnotationNames",
                "com\\.github\\.sevntu\\.checkstyle\\.checks\\.coding\\.AnnotationName|" +
                "org\\.junit\\.runner\\.RunWith|" +
                "org\\.junit\\.Ignore|" +
                "com\\.github\\.sevntu\\.checkstyle\\.checks\\.coding\\.InputEmptyPublicCtorInClass9\\.InnerAnnotation");

        String expected[] = {};

        verify(config, getPath("InputEmptyPublicCtorInClass8.java"), expected);
    }

    @Test
    public void testClassAnnotatedWithAnnotation2() throws Exception
    {
        DefaultConfiguration config = createCheckConfig(EmptyPublicCtorInClassCheck.class);

        config.addAttribute("classAnnotationNames",
                "org\\.junit\\.runner\\.RunWith|" +
                "org\\.junit\\.Ignore|");

        String expected[] = {};

        verify(config, getPath("InputEmptyPublicCtorInClass10.java"), expected);
    }
}
