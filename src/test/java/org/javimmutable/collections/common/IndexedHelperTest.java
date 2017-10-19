package org.javimmutable.collections.common;

import junit.framework.TestCase;
import org.javimmutable.collections.Indexed;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.*;

public class IndexedHelperTest
    extends TestCase
{
    public void test()
    {
        verifyIndexed(asList(1), IndexedHelper.indexed(1));
        verifyIndexed(asList(1, 2), IndexedHelper.indexed(1, 2));
        verifyIndexed(asList(1, 2, 3), IndexedHelper.indexed(1, 2, 3));
    }

    private void verifyIndexed(List<Integer> expected,
                               Indexed<Integer> actual)
    {
        assertThat(actual.size()).isEqualTo(expected.size());
        for (int i = 0; i < expected.size(); ++i) {
            assertThat(actual.get(i)).isEqualTo(expected.get(i));
        }
        assertThatExceptionOfType(ArrayIndexOutOfBoundsException.class)
            .isThrownBy(() -> expected.get(-1));
        assertThatExceptionOfType(ArrayIndexOutOfBoundsException.class)
            .isThrownBy(() -> expected.get(expected.size()));
    }
}
