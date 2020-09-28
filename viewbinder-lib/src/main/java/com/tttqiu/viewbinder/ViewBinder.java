package com.tttqiu.viewbinder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ViewBinder {
    public static void bind(Object target) {
        Class<?> cls = target.getClass();
        try {
            Class<?> bindingClass = cls.getClassLoader().loadClass(cls.getName() + "_ViewBinding");
            Constructor<?> constructor = bindingClass.getConstructor();
            Object bindingObject = constructor.newInstance();
            Method bindingMethod = bindingClass.getDeclaredMethod("bind", cls);
            bindingMethod.invoke(bindingObject, target);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
