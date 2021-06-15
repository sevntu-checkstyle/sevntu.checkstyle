package com.github.sevntu.checkstyle.checks.coding;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarEntry;

import org.assertj.core.internal.bytebuddy.implementation.bytecode.member.MethodInvocation;

/* Config: default
 *
 */
public class InputNoNullForCollectionReturnCheckObjectArray {
    public static File[] getFiles1(int x) {
        if (x == 9) {
            return null; // violation
        }
        else {
            return new File[9]; // ok
        }
    }

    public static File[] getFiles2(int x) {
        if (x == 9) {
            return new File[5]; // ok
        }
        else {
            return new File[9]; // ok
        }
    }

    String[] getStrings1(int x) {
        if (x == 9) {
            return null; // violation
        }
        else {
            return new String[9]; // ok
        }
    }

    String[] getStrings2(int x) {
        if (x == 9) {
            return new String[5]; // ok
        }
        else {
            return new String[9]; // ok
        }
    }

    public java.security.cert.Certificate[] getCertificates()
              throws IOException {
        JarEntry e = getJarEntry();
        return e != null ? e.getCertificates() : null; // violation
    }

    private static JarEntry getJarEntry() {
        return null;
    }

    protected Object[] getMethodArguments(MethodInvocation invocation) {
        if (invocation != null) {
              return new MethodInvocation[2];
        }
        else {
              return null;
        }
    }
}
