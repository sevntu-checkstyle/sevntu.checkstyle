package com.github.sevntu.checkstyle.checks.annotation;

import org.junit.Before;
import org.junit.Test;

public class InputForbidAnnotationElementValueCheck
{
    public static class AnnotationConfigUtils
    {
        public static final int SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME = 0;
    }

    public @interface Bean
    {
        int name();
    }

    public @interface Anno1
    {
        String str();
    }

    public @interface Anno2
    {
        int intVal();
    }

    public @interface Anno3
    {
        float floatVal();
    }

    public @interface Anno4
    {
        boolean boolVal();
    }

    public @interface Anno5
    {
        int intValue();

        float floatValue();

        String stringValue();
    }

    @Anno1(str = "someString123")
    public void method1()
    {
    }

    @Anno2(intVal = 1)
    public void method2()
    {
    }

    @Anno3(floatVal = 2.0f)
    public void method3()
    {
    }

    @Bean(name = AnnotationConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME)
    @Anno4(boolVal = true)
    public void method4()
    {
    }

    @SuppressWarnings("unchecked")
    @Anno5(intValue = 10, floatValue = 42.5f, stringValue = "some111String")
    public void method5()
    {
    }

    @SuppressWarnings(value = { "string1", "string2" })
    protected void method6()
    {
    }

    @SuppressWarnings({ "string1", "string2" })
    protected void method7()
    {
    }

    public @interface Name
    {
        String first();

        String last();
    }

    public @interface Author
    {
        Name value();
    }

    @Author(@Name(first = "Joe", last = "Hacker"))
    public void method8()
    {
    }

    @SuppressWarnings("")
    public void method9()
    {
    }

    @Test
    public void method10() {
    }

    @Test()
    public void method11() {
    }

    @Test(expected = Exception.class)
    public void method12() {
    }

    @Test(timeout = 0)
    public void method13() {
    }
}
