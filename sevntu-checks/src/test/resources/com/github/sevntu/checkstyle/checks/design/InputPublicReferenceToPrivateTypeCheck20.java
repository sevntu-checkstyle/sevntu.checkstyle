package com.github.sevntu.checkstyle.checks.design;

import java.lang.invoke.MethodType;
import java.lang.ref.WeakReference;

public class InputPublicReferenceToPrivateTypeCheck20 {
    static final ConcurrentWeakInternSet<MethodType> internTable = new ConcurrentWeakInternSet<>();

    private static class ConcurrentWeakInternSet<T> {
        private static class WeakEntry<T> extends WeakReference<T> {
            public WeakEntry(T referent) {
                super(referent);
            }
        }
    }
}