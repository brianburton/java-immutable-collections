package org.javimmutable.collections.array.int_trie;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.MultiTransformCursor;
import org.javimmutable.collections.cursors.StandardCursor;

public class MultiBranchTrieNode<T>
        extends TrieNode<T>
{
    private final int shift;
    private final int bitmask;
    private final TrieNode<T>[] entries;

    public MultiBranchTrieNode(int shift)
    {
        this(shift, 0, MultiBranchTrieNode.<T>allocate(0));
    }

    public MultiBranchTrieNode(int shift,
                               int bitmask,
                               TrieNode<T>[] entries)
    {
        assert shift >= 0;
        this.shift = shift;
        this.bitmask = bitmask;
        this.entries = entries;
    }

    public static <T> MultiBranchTrieNode<T> forIndex(int shift,
                                                      int index,
                                                      TrieNode<T> child)
    {
        int branchIndex = ((index >>> shift) & 0x1f);
        return forBranchIndex(shift, branchIndex, child);
    }

    public static <T> MultiBranchTrieNode<T> forBranchIndex(int shift,
                                                            int branchIndex,
                                                            TrieNode<T> child)
    {
        assert branchIndex >= 0 && branchIndex < 32;
        TrieNode<T>[] entries = allocate(1);
        entries[0] = child;
        return new MultiBranchTrieNode<T>(shift, 1 << branchIndex, entries);
    }

    static <T> MultiBranchTrieNode<T> fullWithout(int shift,
                                                  TrieNode<T>[] entries,
                                                  int withoutIndex)
    {
        assert entries.length == 32;
        final TrieNode<T>[] newEntries = allocate(31);
        System.arraycopy(entries, 0, newEntries, 0, withoutIndex);
        System.arraycopy(entries, withoutIndex + 1, newEntries, withoutIndex, 31 - withoutIndex);
        final int newMask = ~(1 << withoutIndex);
        return new MultiBranchTrieNode<T>(shift, newMask, newEntries);
    }

    @Override
    public boolean isEmpty()
    {
        return entries.length == 0;
    }

    @Override
    public T getValueOr(int shift,
                        int index,
                        T defaultValue)
    {
        assert this.shift == shift;
        final int bit = 1 << ((index >>> shift) & 0x1f);
        final int bitmask = this.bitmask;
        if ((bitmask & bit) == 0) {
            return defaultValue;
        } else {
            final int childIndex = realIndex(bitmask, bit);
            return entries[childIndex].getValueOr(shift - 5, index, defaultValue);
        }
    }

    @Override
    public Holder<T> find(int shift,
                          int index)
    {
        assert this.shift == shift;
        final int bit = 1 << ((index >>> shift) & 0x1f);
        final int bitmask = this.bitmask;
        if ((bitmask & bit) == 0) {
            return Holders.of();
        } else {
            final int childIndex = realIndex(bitmask, bit);
            return entries[childIndex].find(shift - 5, index);
        }
    }

    @Override
    public TrieNode<T> assign(int shift,
                              int index,
                              T value,
                              MutableDelta sizeDelta)
    {
        assert this.shift == shift;
        final int bit = 1 << ((index >>> shift) & 0x1f);
        final int bitmask = this.bitmask;
        final int childIndex = realIndex(bitmask, bit);
        final TrieNode<T>[] entries = this.entries;
        if ((bitmask & bit) == 0) {
            final int oldLength = entries.length;
            final TrieNode<T>[] newEntries = allocate(oldLength + 1);
            sizeDelta.add(1);
            if (bitmask != 0) {
                System.arraycopy(entries, 0, newEntries, 0, childIndex);
                System.arraycopy(entries, childIndex, newEntries, childIndex + 1, oldLength - childIndex);
            }
            newEntries[childIndex] = new LeafTrieNode<T>(shift - 5, index, value);
            if (newEntries.length == 32) {
                return new FullBranchTrieNode<T>(shift, newEntries);
            } else {
                return new MultiBranchTrieNode<T>(shift, bitmask | bit, newEntries);
            }
        } else {
            final TrieNode<T> child = entries[childIndex];
            final TrieNode<T> newChild = child.assign(shift - 5, index, value, sizeDelta);
            if (newChild == child) {
                return this;
            } else {
                final TrieNode<T>[] newEntries = entries.clone();
                newEntries[childIndex] = newChild;
                return new MultiBranchTrieNode<T>(shift, bitmask, newEntries);
            }
        }
    }

    @Override
    public TrieNode<T> delete(int shift,
                              int index,
                              MutableDelta sizeDelta)
    {
        assert this.shift == shift;
        final int bit = 1 << ((index >>> shift) & 0x1f);
        final int bitmask = this.bitmask;
        final TrieNode<T>[] entries = this.entries;
        if ((bitmask & bit) == 0) {
            return this;
        } else {
            final int childIndex = realIndex(bitmask, bit);
            TrieNode<T> newChild = entries[childIndex].delete(shift - 5, index, sizeDelta);
            if (newChild.isEmpty()) {
                switch (entries.length) {
                case 1:
                    return new EmptyTrieNode<T>(shift);
                case 2: {
                    final int newBitmask = bitmask & ~bit;
                    final int remainingIndex = Integer.numberOfTrailingZeros(newBitmask);
                    return SingleBranchTrieNode.forIndex(shift, remainingIndex, entries[realIndex(bitmask, 1 << remainingIndex)]);
                }
                default: {
                    final int newLength = entries.length - 1;
                    final TrieNode<T>[] newArray = allocate(newLength);
                    System.arraycopy(entries, 0, newArray, 0, childIndex);
                    System.arraycopy(entries, childIndex + 1, newArray, childIndex, newLength - childIndex);
                    return new MultiBranchTrieNode<T>(shift, bitmask & ~bit, newArray);
                }
                }
            } else {
                final TrieNode<T>[] newEntries = entries.clone();
                newEntries[childIndex] = newChild;
                return new MultiBranchTrieNode<T>(shift, bitmask, newEntries);
            }
        }
    }

    @Override
    public Cursor<JImmutableMap.Entry<Integer, T>> anyOrderEntryCursor()
    {
        return MultiTransformCursor.of(StandardCursor.of(new AnyOrderCursorSource(bitmask)), new Func1<TrieNode<T>, Cursor<JImmutableMap.Entry<Integer, T>>>()
        {
            @Override
            public Cursor<JImmutableMap.Entry<Integer, T>> apply(TrieNode<T> node)
            {
                return node.anyOrderEntryCursor();
            }
        });
    }

    @Override
    public Cursor<T> anyOrderValueCursor()
    {
        return MultiTransformCursor.of(StandardCursor.of(new AnyOrderCursorSource(bitmask)), new Func1<TrieNode<T>, Cursor<T>>()
        {
            @Override
            public Cursor<T> apply(TrieNode<T> node)
            {
                return node.anyOrderValueCursor();
            }
        });
    }

    private class AnyOrderCursorSource
            implements StandardCursor.Source<TrieNode<T>>
    {
        private final int remainingMask;
        private final int index;

        private AnyOrderCursorSource(int remainingMask)
        {
            this.remainingMask = remainingMask;
            this.index = Integer.numberOfTrailingZeros(remainingMask);
        }

        @Override
        public boolean atEnd()
        {
            return remainingMask == 0;
        }

        @Override
        public TrieNode<T> currentValue()
        {
            return entries[index];
        }

        @Override
        public StandardCursor.Source<TrieNode<T>> advance()
        {
            if (remainingMask == 0) {
                return this;
            } else {
                final int bit = 1 << index;
                return new AnyOrderCursorSource(remainingMask & ~bit);
            }
        }
    }

    private static int realIndex(int bitmask,
                                 int bit)
    {
        return Integer.bitCount(bitmask & (bit - 1));
    }

    @SuppressWarnings("unchecked")
    private static <T> TrieNode<T>[] allocate(int size)
    {
        return (TrieNode<T>[])new TrieNode[size];
    }
}
