package JAVA00.week01;

import org.apache.jena.base.Sys;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TEST {

    public static void main(String[] args) {
        List list = new ArrayList();
        list.add("s");
        list.add("a");
        System.out.println(list.toString());
        String xClassPath = "C:\\Temp\\Hello.xlass";
        try {
            Class xClass = new MyClassLoader(xClassPath).findClass("Hello");
            Method method = xClass.getDeclaredMethod("hello");
            method.invoke(xClass.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}