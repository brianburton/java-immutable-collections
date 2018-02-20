package org.javimmutable.collections.list;

import java.util.ArrayList;
import java.util.List;

public class ListAppendTimingComparison
{
    public static void main(String[] argv)
    {
        Mode mode = (argv.length == 0) ? Mode.OLD_LAST : Mode.valueOf(argv[0].replace("-", "_").toUpperCase());
        final long startMillis = System.currentTimeMillis();
        runTest(mode);
        final long elapsedMillis = System.currentTimeMillis() - startMillis;
        System.out.printf("%s  %d%n", mode, elapsedMillis);
    }

    private enum Mode
    {
        OLD_FIRST,
        OLD_LAST,
        NEW_FIRST,
        NEW_LAST
    }

    private static void runTest(Mode mode)
    {
        for (int loop = 1; loop <= 10_000; ++loop) {
            JImmutableArrayList<Integer> list = JImmutableArrayList.of();
            List<Integer> extras = new ArrayList<>();
            for (int length = 1; length <= 250; ++length) {
                extras.add(length);
                switch (mode) {

                    case OLD_FIRST:
                        list = list.insertAllFirstOldWay(extras.iterator());
                        break;
                    case OLD_LAST:
                        list = list.insertAllLastOldWay(extras.iterator());
                        break;
                    case NEW_FIRST:
                        list = list.insertAllFirst(extras);
                        break;
                    case NEW_LAST:
                        list = list.insertAllLast(extras);
                        break;
                }
            }
        }
    }
}
