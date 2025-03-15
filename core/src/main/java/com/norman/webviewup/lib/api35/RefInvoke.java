package com.norman.webviewup.lib.api35;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class RefInvoke {

    /** 根据全类名反射无参构造函数
     * 调用方式   Object obj = RefInvoke.createObject(className);
     * @param className 全类名
     * @return
     */
    public static Object createObject(String className) {
        Class[] pareTyples = new Class[]{};
        Object[] pareVaules = new Object[]{};

        try {
            Class r = Class.forName(className);
            return createObject(r, pareTyples, pareVaules);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /** 根据类反射无参构造函数
     * 调用方式   Object obj = RefInvoke.createObject(className);
     * @param clazz 类
     * @return
     */
    public static Object createObject(Class clazz) {
        Class[] pareTyple = new Class[]{};
        Object[] pareVaules = new Object[]{};

        return createObject(clazz, pareTyple, pareVaules);
    }

    /**根据全类名（String）反射只有一个参数的构造函数
     * @param className  类名
     * @param pareTyple  参数类型
     * @param pareVaule  参数值
     * @return
     */
    public static Object createObject(String className, Class pareTyple, Object pareVaule) {
        Class[] pareTyples = new Class[]{pareTyple};
        Object[] pareVaules = new Object[]{pareVaule};

        try {
            Class r = Class.forName(className);
            return createObject(r, pareTyples, pareVaules);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**根据类（Class）反射只有一个参数的构造函数
     * @param clazz      类
     * @param pareTyple 参数类型
     * @param pareVaule 参数值
     * @return
     */
    public static Object createObject(Class clazz, Class pareTyple, Object pareVaule) {
        Class[] pareTyples = new Class[]{pareTyple};
        Object[] pareVaules = new Object[]{pareVaule};

        return createObject(clazz, pareTyples, pareVaules);
    }

    /**根据全类名（String）反射有多个参数的构造函数
     * @param className   类名
     * @param pareTyples  参数类型数组
     * @param pareVaules  参数值数组
     * @return
     */
    public static Object createObject(String className, Class[] pareTyples, Object[] pareVaules) {
        try {
            Class r = Class.forName(className);
            return createObject(r, pareTyples, pareVaules);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**根据类（Class）反射有多个参数的构造函数
     * @param clazz         类
     * @param pareTyples   参数类型数组
     * @param pareVaules   参数值数组
     * @return
     */
    public static Object createObject(Class clazz, Class[] pareTyples, Object[] pareVaules) {
        try {
            Constructor ctor = clazz.getDeclaredConstructor(pareTyples);
            ctor.setAccessible(true);
            return ctor.newInstance(pareVaules);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 调用实例方法，多个参数
     * @param obj  根据类名获取的构造函数对象
     * @param methodName  方法名
     * @param pareTyples  参数类型
     * @param pareVaules  参数值
     * @return
     */
    public static Object invokeInstanceMethod(Object obj, String methodName, Class[] pareTyples, Object[] pareVaules) {
        if (obj == null) return null;

        try {
            //调用一个private方法
            Method method = obj.getClass().getDeclaredMethod(methodName, pareTyples); //在指定类中获取指定的方法
            method.setAccessible(true);
            return method.invoke(obj, pareVaules);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 调用实例方法，一个参数
     * @param obj  根据类名获取的构造函数对象
     * @param methodName  方法名
     * @param pareTyple   参数类型
     * @param pareVaule  参数值
     * @return
     */
    public static Object invokeInstanceMethod(Object obj, String methodName, Class pareTyple, Object pareVaule) {
        Class[] pareTyples = {pareTyple};
        Object[] pareVaules = {pareVaule};

        return invokeInstanceMethod(obj, methodName, pareTyples, pareVaules);
    }

    /**
     * 调用实例方法，无参
     * @param obj  根据类名获取的构造函数对象
     * @param methodName  方法名
     * @return
     */
    public static Object invokeInstanceMethod(Object obj, String methodName) {
        Class[] pareTyples = new Class[]{};
        Object[] pareVaules = new Object[]{};

        return invokeInstanceMethod(obj, methodName, pareTyples, pareVaules);
    }


    /**调用静态方法，无参
     * @param className  类名（String）
     * @param method_name 方法名
     * @return
     */
    public static Object invokeStaticMethod(String className, String method_name) {
        Class[] pareTyples = new Class[]{};
        Object[] pareVaules = new Object[]{};

        return invokeStaticMethod(className, method_name, pareTyples, pareVaules);
    }

    /**调用静态方法，一个参数
     * @param className  类名（String）
     * @param method_name 方法名
     * @param pareTyple  参数类型
     * @param pareVaule  参数值
     * @return
     */
    public static Object invokeStaticMethod(String className, String method_name, Class pareTyple, Object pareVaule) {
        Class[] pareTyples = new Class[]{pareTyple};
        Object[] pareVaules = new Object[]{pareVaule};

        return invokeStaticMethod(className, method_name, pareTyples, pareVaules);
    }

    /**用静态方法，多个参数
     * @param className     类名（String）
     * @param method_name  方法名
     * @param pareTyples  参数类型[]
     * @param pareVaules  参数值[]
     * @return
     */
    public static Object invokeStaticMethod(String className, String method_name, Class[] pareTyples, Object[] pareVaules) {
        try {
            Class obj_class = Class.forName(className);
            return invokeStaticMethod(obj_class, method_name, pareTyples, pareVaules);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**调用静态方法，无参(Class)
     * @param clazz 类
     * @param method_name 方法名
     * @return
     */
    public static Object invokeStaticMethod(Class clazz, String method_name) {
        Class[] pareTyples = new Class[]{};
        Object[] pareVaules = new Object[]{};

        return invokeStaticMethod(clazz, method_name, pareTyples, pareVaules);
    }

    /**调用静态方法，一个参数(Class)
     * @param clazz 类
     * @param method_name 方法名
     * @param classType 参数类型
     * @param pareVaule 参数值
     * @return
     */
    public static Object invokeStaticMethod(Class clazz, String method_name, Class classType, Object pareVaule) {
        Class[] classTypes = new Class[]{classType};
        Object[] pareVaules = new Object[]{pareVaule};

        return invokeStaticMethod(clazz, method_name, classTypes, pareVaules);
    }

    /**调用静态方法，多个参数(Class)
     * @param clazz 类
     * @param method_name 方法名
     * @param pareTyples  参数类型[]
     * @param pareVaules  参数值[]
     * @return
     */
    public static Object invokeStaticMethod(Class clazz, String method_name, Class[] pareTyples, Object[] pareVaules) {
        try {
            Method method = clazz.getDeclaredMethod(method_name, pareTyples);
            method.setAccessible(true);
            return method.invoke(null, pareVaules);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 获取实例字段，简写版本
     * @param obj 根据类名获取的构造函数对象
     * @param filedName 字段名
     * @return
     */
    public static Object getFieldObject(Object obj, String filedName) {
        return getFieldObject(obj.getClass(), obj, filedName);
    }

    /**获取实例字段
     * @param className 类名
     * @param obj  根据类名获取的构造函数对象
     * @param filedName 字段名
     * @return
     */
    public static Object getFieldObject(String className, Object obj, String filedName) {
        try {
            Class obj_class = Class.forName(className);
            return getFieldObject(obj_class, obj, filedName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**获取实例字段
     * @param clazz 类
     * @param obj   根据类名获取的构造函数对象
     * @param filedName  字段名
     * @return
     */
    public static Object getFieldObject(Class clazz, Object obj, String filedName) {
        try {
            Field field = clazz.getDeclaredField(filedName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**设置实例字段，简写版本
     * @param obj  根据类名获取的构造函数对象
     * @param filedName  字段名
     * @param filedVaule 字段值
     */
    public static void setFieldObject(Object obj, String filedName, Object filedVaule) {
        setFieldObject(obj.getClass(), obj, filedName, filedVaule);
    }

    /**设置实例字段
     * @param clazz  类
     * @param obj    根据类名获取的构造函数对象
     * @param filedName  字段名
     * @param filedVaule  字段值
     */
    public static void setFieldObject(Class clazz, Object obj, String filedName, Object filedVaule) {
        try {
            Field field = clazz.getDeclaredField(filedName);
            field.setAccessible(true);
            field.set(obj, filedVaule);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**设置实例字段
     * @param className 类名（String）
     * @param obj   根据类名获取的构造函数对象
     * @param filedName  字段名
     * @param filedVaule  字段值
     */
    public static void setFieldObject(String className, Object obj, String filedName, Object filedVaule) {
        try {
            Class obj_class = Class.forName(className);
            setFieldObject(obj_class, obj, filedName, filedVaule);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取静态实例字段
     * @param className 类名（String）
     * @param filedName 字段名
     * @return
     */
    public static Object getStaticFieldObject(String className, String filedName) {
        return getFieldObject(className, null, filedName);
    }

    /**获取静态实例字段
     * @param clazz 类（Class）
     * @param filedName 字段名
     * @return
     */
    public static Object getStaticFieldObject(Class clazz, String filedName) {
        return getFieldObject(clazz, null, filedName);
    }

    /**设置静态实例字段
     * @param classname 类名
     * @param filedName 字段名
     * @param filedVaule 字段值
     */
    public static void setStaticFieldObject(String classname, String filedName, Object filedVaule) {
        setFieldObject(classname, null, filedName, filedVaule);
    }

    /**设置静态实例字段
     * @param clazz 类
     * @param filedName 字段名
     * @param filedVaule 字段值
     */
    public static void setStaticFieldObject(Class clazz, String filedName, Object filedVaule) {
        setFieldObject(clazz, null, filedName, filedVaule);
    }
}