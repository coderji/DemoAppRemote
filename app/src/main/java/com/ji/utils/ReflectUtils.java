package com.ji.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectUtils {
    private static final String TAG = "ReflectUtils";

    public static Method getMethod(Class cls, String methodName, Class<?>... parameterTypes) {
        try {
            @SuppressWarnings("unchecked")
            Method method = cls.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            LogUtils.e(TAG, "getMethod", e);
        }
        return null;
    }

    public static Method getMethod(String cls, String methodName, Class<?>... parameterTypes) {
        try {
            return getMethod(Class.forName(cls), methodName, parameterTypes);
        } catch (ClassNotFoundException e) {
            LogUtils.e(TAG, "getMethod", e);
        }
        return null;
    }

    public static Object invoke(Object receiver, Method method, Object... args) {
        if (method != null) {
            try {
                return method.invoke(receiver, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                LogUtils.e(TAG, "invoke", e);
            }
        }
        return null;
    }

    public static Object getField(Class cls, String fieldName, Object receiver) {
        try {
            Field field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(receiver);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LogUtils.e(TAG, "getField", e);
        }
        return null;
    }

    public static Object getField(String cls, String fieldName, Object receiver) {
        try {
            return getField(Class.forName(cls), fieldName, receiver);
        } catch (ClassNotFoundException e) {
            LogUtils.e(TAG, "getField", e);
        }
        return null;
    }

    public static void setField(Class cls, String fieldName, Object receiver, Object value) {
        try {
            Field field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(receiver, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LogUtils.e(TAG, "setField", e);
        }
    }

    public static void setField(String cls, String fieldName, Object receiver, Object value) {
        try {
            setField(Class.forName(cls), fieldName, receiver, value);
        } catch (ClassNotFoundException e) {
            LogUtils.e(TAG, "setField", e);
        }
    }
}
