package org.javimmutable.collections.array.bit32;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.cursors.StandardCursor;

public class FullBit32Array<T>
        extends Bit32Array<T>
{
    private final Holder<T>[] entries;

    FullBit32Array(Holder<T>[] entries)
    {
        assert entries.length == 32;
        this.entries = entries;
    }

    @Override
    public Bit32Array<T> assign(int key,
                                T value)
    {
        Holder<T> current = entries[key];
        if (current.getValue() == value) {
            return this;
        } else {
            Holder<T>[] newEntries = entries.clone();
            newEntries[key] = Holders.of(value);
            return new FullBit32Array<T>(newEntries);
        }
    }

    @Override
    public Bit32Array<T> delete(int key)
    {
        return StandardBit32Array.fullWithout(entries, key);
    }

    @Override
    public int firstIndex()
    {
        return 0;
    }

    @Override
    public Holder<T> find(int index)
    {
        return entries[index];
    }

    @Override
    public int size()
    {
        return 32;
    }

    @Override
    public Cursor<JImmutableMap.Entry<Integer, T>> cursor()
    {
        return StandardCursor.of(new CursorSource(0));
    }

    private class CursorSource
            implements StandardCursor.Source<JImmutableMap.Entry<Integer, T>>
    {
        private int index;

        private CursorSource(int index)
        {
            this.index = index;
        }

        @Override
        public boolean atEnd()
        {
            return index >= 32;
        }

        @Override
        public JImmutableMap.Entry<Integer, T> currentValue()
        {
            return MapEntry.of(index, entries[index].getValue());
        }

        @Override
        public StandardCursor.Source<JImmutableMap.Entry<Integer, T>> advance()
        {
            return new CursorSource(index + 1);
        }
    }
}
