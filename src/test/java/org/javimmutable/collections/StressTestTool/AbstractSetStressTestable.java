package org.javimmutable.collections.StressTestTool;


import org.javimmutable.collections.JImmutableRandomAccessList;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public abstract class AbstractSetStressTestable
        extends AbstractStressTestable
{
    protected String valueInSet(JImmutableRandomAccessList<String> list,
                                Random random)
    {
        return list.get(random.nextInt(list.size()));
    }

    protected JImmutableRandomAccessList<String> deleteAllAt(Set<Integer> index,
                                                             JImmutableRandomAccessList<String> setList)
    {
        List<Integer> listIndex = new LinkedList<Integer>(index);
        for (int i = listIndex.size() - 1; i >= 0; --i) {
            setList = setList.delete(listIndex.get(i));
        }
        return setList;
    }
}
