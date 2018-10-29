

package com.yy.logx;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

    private static SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static boolean loadLibrary(String loadName, Class className) {
        boolean isLoad = false;
        try {
            ClassLoader classLoader = className.getClassLoader();
            Class runtime = Runtime.getRuntime().getClass();
            Class[] args = new Class[2];
            int version = android.os.Build.VERSION.SDK_INT;
            String functionName = "loadLibrary";
            if (version > 24) {
                args[0] = ClassLoader.class;
                args[1] = String.class;
                functionName = "loadLibrary0";
                Method loadMethod = runtime.getDeclaredMethod(functionName, args);
                loadMethod.setAccessible(true);
                loadMethod.invoke(Runtime.getRuntime(), classLoader, loadName);
            } else {
                args[0] = String.class;
                args[1] = ClassLoader.class;
                Method loadMethod = runtime.getDeclaredMethod(functionName, args);
                loadMethod.setAccessible(true);
                loadMethod.invoke(Runtime.getRuntime(), loadName, classLoader);
            }
            isLoad = true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return isLoad;
    }

    public static long getCurrentTime() {
        long currentTime = System.currentTimeMillis();
        long tempTime = 0;
        try {
            String dataStr = sDateFormat.format(new Date(currentTime));
            tempTime = sDateFormat.parse(dataStr).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempTime;
    }

    public static String getDateStr(long time) {
        return sDateFormat.format(new Date(time));
    }
}
