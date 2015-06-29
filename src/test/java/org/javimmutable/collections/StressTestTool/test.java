package org.javimmutable.collections.StressTestTool;


import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.list.JImmutableArrayList;

import java.util.ArrayList;
import java.util.List;

public class test
{
    public static void main(String[] args)
            throws IllegalAccessException, InstantiationException
    {
        Class<? extends List> klass;
        JImmutableList<String> list = JImmutableArrayList.<String>of();

        List t = new ArrayList();
        klass = t.getClass();

        List<String> expected = klass.newInstance();
        System.out.println(expected.getClass().toString());
        System.out.printf("Testing JImmutableList of type %s%n", list.getClass().toString().substring(list.getClass().toString().lastIndexOf(".") + 1));
    }
}
