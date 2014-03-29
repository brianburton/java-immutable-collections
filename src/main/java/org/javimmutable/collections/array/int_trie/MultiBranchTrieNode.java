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
    // used by SignedOrderCursorSource to determine which index to use next
    private static final IndexList SIGNED_INDEX_LIST = new IndexList(2, new IndexList(3, new IndexList(0, new IndexList(1, null))));

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
    public <K, V> V getValueOr(int shift,
                               int index,
                               K key,
                               Transforms<T, K, V> transforms,
                               V defaultValue)
    {
        assert this.shift == shift;
        final int bit = 1 << ((index >>> shift) & 0x1f);
        final int bitmask = this.bitmask;
        if ((bitmask & bit) == 0) {
            return defaultValue;
        } else {
            final int childIndex = realIndex(bitmask, bit);
            return entries[childIndex].getValueOr(shift - 5, index, key, transforms, defaultValue);
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
    public <K, V> Holder<V> find(int shift,
                                 int index,
                                 K key,
                                 Transforms<T, K, V> transforms)
    {
        assert this.shift == shift;
        final int bit = 1 << ((index >>> shift) & 0x1f);
        final int bitmask = this.bitmask;
        if ((bitmask & bit) == 0) {
            return Holders.of();
        } else {
            final int childIndex = realIndex(bitmask, bit);
            return entries[childIndex].find(shift - 5, index, key, transforms);
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
            if (bitmask != 0) {
                System.arraycopy(entries, 0, newEntries, 0, childIndex);
                System.arraycopy(entries, childIndex, newEntries, childIndex + 1, oldLength - childIndex);
            }
            newEntries[childIndex] = new LeafTrieNode<T>(index, value);
            sizeDelta.add(1);
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
    public <K, V> TrieNode<T> assign(int shift,
                                     int index,
                                     K key,
                                     V value,
                                     Transforms<T, K, V> transforms,
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
            if (bitmask != 0) {
                System.arraycopy(entries, 0, newEntries, 0, childIndex);
                System.arraycopy(entries, childIndex, newEntries, childIndex + 1, oldLength - childIndex);
            }
            newEntries[childIndex] = new LeafTrieNode<T>(index, transforms.update(Holders.<T>of(), key, value, sizeDelta));
            if (newEntries.length == 32) {
                return new FullBranchTrieNode<T>(shift, newEntries);
            } else {
                return new MultiBranchTrieNode<T>(shift, bitmask | bit, newEntries);
            }
        } else {
            final TrieNode<T> child = entries[childIndex];
            final TrieNode<T> newChild = child.assign(shift - 5, index, key, value, transforms, sizeDelta);
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
            final TrieNode<T> child = entries[childIndex];
            final TrieNode<T> newChild = child.delete(shift - 5, index, sizeDelta);
            if (newChild.isEmpty()) {
                switch (entries.length) {
                case 1:
                    return EmptyTrieNode.of();
                case 2: {
                    final int newBitmask = bitmask & ~bit;
                    final int remainingIndex = Integer.numberOfTrailingZeros(newBitmask);
                    return SingleBranchTrieNode.forBranchIndex(shift, remainingIndex, entries[realIndex(bitmask, 1 << remainingIndex)]);
                }
                default: {
                    final int newLength = entries.length - 1;
                    final TrieNode<T>[] newArray = allocate(newLength);
                    System.arraycopy(entries, 0, newArray, 0, childIndex);
                    System.arraycopy(entries, childIndex + 1, newArray, childIndex, newLength - childIndex);
                    return new MultiBranchTrieNode<T>(shift, bitmask & ~bit, newArray);
                }
                }
            } else if (newChild == child) {
                return this;
            } else {
                final TrieNode<T>[] newEntries = entries.clone();
                newEntries[childIndex] = newChild;
                return new MultiBranchTrieNode<T>(shift, bitmask, newEntries);
            }
        }
    }

    @Override
    public <K, V> TrieNode<T> delete(int shift,
                                     int index,
                                     K key,
                                     Transforms<T, K, V> transforms,
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
            final TrieNode<T> child = entries[childIndex];
            TrieNode<T> newChild = child.delete(shift - 5, index, key, transforms, sizeDelta);
            if (newChild.isEmpty()) {
                switch (entries.length) {
                case 1:
                    return EmptyTrieNode.of();
                case 2: {
                    final int newBitmask = bitmask & ~bit;
                    final int remainingIndex = Integer.numberOfTrailingZeros(newBitmask);
                    return SingleBranchTrieNode.forBranchIndex(shift, remainingIndex, entries[realIndex(bitmask, 1 << remainingIndex)]);
                }
                default: {
                    final int newLength = entries.length - 1;
                    final TrieNode<T>[] newArray = allocate(newLength);
                    System.arraycopy(entries, 0, newArray, 0, childIndex);
                    System.arraycopy(entries, childIndex + 1, newArray, childIndex, newLength - childIndex);
                    return new MultiBranchTrieNode<T>(shift, bitmask & ~bit, newArray);
                }
                }
            } else if (newChild == child) {
                return this;
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
        return entryCursor(new AnyOrderCursorSource());
    }

    @Override
    public Cursor<JImmutableMap.Entry<Integer, T>> signedOrderEntryCursor()
    {
        if (shift != ROOT_SHIFT) {
            return anyOrderEntryCursor();
        } else {
            return entryCursor(new SignedOrderCursorSource());
        }
    }

    @Override
    public <K, V> Cursor<JImmutableMap.Entry<K, V>> anyOrderEntryCursor(final Transforms<T, K, V> transforms)
    {
        return entryCursor(new AnyOrderCursorSource(), transforms);
    }

    @Override
    public <K, V> Cursor<JImmutableMap.Entry<K, V>> signedOrderEntryCursor(Transforms<T, K, V> transforms)
    {
        if (shift != ROOT_SHIFT) {
            return anyOrderEntryCursor(transforms);
        } else {
            return entryCursor(new SignedOrderCursorSource(), transforms);
        }
    }

    @Override
    public Cursor<T> anyOrderValueCursor()
    {
        return valueCursor(new AnyOrderCursorSource());
    }

    @Override
    public Cursor<T> signedOrderValueCursor()
    {
        if (shift != ROOT_SHIFT) {
            return anyOrderValueCursor();
        } else {
            return valueCursor(new SignedOrderCursorSource());
        }
    }

    private Cursor<JImmutableMap.Entry<Integer, T>> entryCursor(StandardCursor.Source<TrieNode<T>> source)
    {
        return MultiTransformCursor.of(StandardCursor.of(source), new Func1<TrieNode<T>, Cursor<JImmutableMap.Entry<Integer, T>>>()
        {
            @Override
            public Cursor<JImmutableMap.Entry<Integer, T>> apply(TrieNode<T> node)
            {
                return node.anyOrderEntryCursor();
            }
        });
    }

    private <K, V> Cursor<JImmutableMap.Entry<K, V>> entryCursor(StandardCursor.Source<TrieNode<T>> source,
                                                                 final Transforms<T, K, V> transforms)
    {
        return MultiTransformCursor.of(StandardCursor.of(source), new Func1<TrieNode<T>, Cursor<JImmutableMap.Entry<K, V>>>()
        {
            @Override
            public Cursor<JImmutableMap.Entry<K, V>> apply(TrieNode<T> node)
            {
                return node.anyOrderEntryCursor(transforms);
            }
        });
    }

    private Cursor<T> valueCursor(StandardCursor.Source<TrieNode<T>> source)
    {
        return MultiTransformCursor.of(StandardCursor.of(source), new Func1<TrieNode<T>, Cursor<T>>()
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
        private final int index;

        private AnyOrderCursorSource()
        {
            this(0);
        }

        private AnyOrderCursorSource(int index)
        {
            this.index = index;
        }

        @Override
        public boolean atEnd()
        {
            return index >= entries.length;
        }

        @Override
        public TrieNode<T> currentValue()
        {
            return entries[index];
        }

        @Override
        public StandardCursor.Source<TrieNode<T>> advance()
        {
            return new AnyOrderCursorSource(index + 1);
        }
    }

    private class SignedOrderCursorSource
            implements StandardCursor.Source<TrieNode<T>>
    {
        private final IndexList indexList;

        private SignedOrderCursorSource()
        {
            this(SIGNED_INDEX_LIST);
        }

        private SignedOrderCursorSource(IndexList indexList)
        {
            assert shift == TrieNode.ROOT_SHIFT;
            this.indexList = findFirstIndex(indexList);
        }

        @Override
        public boolean atEnd()
        {
            return indexList == null;
        }

        @Override
        public TrieNode<T> currentValue()
        {
            return entries[realIndex(bitmask, indexList.bit)];
        }

        @Override
        public StandardCursor.Source<TrieNode<T>> advance()
        {
            if (indexList == null) {
                return this;
            } else {
                return new SignedOrderCursorSource(findFirstIndex(indexList.next));
            }
        }
    }

    private IndexList findFirstIndex(IndexList next)
    {
        while (next != null && (bitmask & next.bit) == 0) {
            next = next.next;
        }
        return next;
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

    private static class IndexList
    {
        private final int bit;
        private final IndexList next;

        private IndexList(int index,
                          IndexList next)
        {
            this.bit = 1 << index;
            this.next = next;
        }
    }
}
