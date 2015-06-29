package org.javimmutable.collections.StressTestTool;


import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.cursors.StandardCursorTest;

import java.util.List;

public abstract class AbstractListStressTestable
    extends AbstractStressTestable
{
    protected void verifyContents(List<String> expected,
                                  JImmutableList<String> list)
    {
        System.out.printf("checking contents with size %d%n", list.size());
        if (list.isEmpty() != expected.isEmpty()) {
            throw new RuntimeException(String.format("isEmpty mismatch - expected %b found %b", expected.isEmpty(), list.isEmpty()));
        }
        if (list.size() != expected.size()) {
            throw new RuntimeException(String.format("size mismatch - expected %d found %d", expected.size(), list.size()));
        }

        int index = 0;
        for (String expectedValue : expected) {
            String listValue = list.get(index);
            if (!expectedValue.equals(listValue)) {
                throw new RuntimeException(String.format("value mismatch - expected %s found %s%n", expectedValue, listValue));
            }
            index += 1;
        }
        index = 0;
        for (String listValue : list) {
            String expectedValue = expected.get(index);
            if (!expectedValue.equals(listValue)) {
                throw new RuntimeException(String.format("value mismatch - expected %s found %s%n", expectedValue, listValue));
            }
            index += 1;
        }
        if(!expected.equals(list.getList())) {
            throw new RuntimeException("getList() call failed");
        }
        StandardCursorTest.listCursorTest(expected, list.cursor());
        StandardCursorTest.listIteratorTest(expected, list.iterator());
        list.checkInvariants();
    }
}
