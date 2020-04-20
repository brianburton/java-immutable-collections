package org.javimmutable.collections.array;

import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.IterableStreamable;
import org.javimmutable.collections.JImmutableArray;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.array.nodes.ArrayLeafNode;
import org.javimmutable.collections.array.nodes.ArrayNode;
import org.javimmutable.collections.common.ArrayHelper;
import org.javimmutable.collections.common.ArrayToMapAdaptor;
import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.iterators.GenericIterator;
import org.javimmutable.collections.iterators.IteratorHelper;
import org.javimmutable.collections.iterators.TransformStreamable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collector;

import static org.javimmutable.collections.MapEntry.entry;
import static org.javimmutable.collections.common.HamtIntMath.*;

public class JImmutableNodeArray<T>
    implements Serializable,
               JImmutableArray<T>,
               ArrayHelper.Allocator<ArrayNode<T>>
{
    @SuppressWarnings("rawtypes")
    private static final JImmutableNodeArray EMPTY = new JImmutableNodeArray();

    private static final int DIVISOR = 1 << 30;

    private final int bitmask;
    private final ArrayNode<T>[] children;
    private final int size;

    private JImmutableNodeArray()
    {
        this.bitmask = 0;
        this.children = allocate(0);
        this.size = 0;
    }

    private JImmutableNodeArray(int bitmask,
                                ArrayNode<T>[] children,
                                int size)
    {
        this.bitmask = bitmask;
        this.children = children;
        this.size = size;
    }

    @SuppressWarnings("unchecked")
    public static <T> JImmutableArray<T> of()
    {
        return (JImmutableArray<T>)EMPTY;
    }

    public static <T> JImmutableArray.Builder<T> builder()
    {
        return new Builder<>();
    }

    @Nonnull
    public static <T> Collector<T, ?, JImmutableArray<T>> collector()
    {
        return Collector.<T, JImmutableNodeArray.Builder<T>, JImmutableArray<T>>of(() -> new Builder<>(),
                                                                                   (b, v) -> b.add(v),
                                                                                   (b1, b2) -> (Builder<T>)b1.add(b2.iterator()),
                                                                                   b -> b.build());
    }

    private static int childIndex(int userIndex)
    {
        return 2 + Math.floorDiv(userIndex, DIVISOR);
    }

    private static int entryBaseIndex(int userIndex)
    {
        return DIVISOR * Math.floorDiv(userIndex, DIVISOR);
    }

    private static int nodeIndex(int userIndex)
    {
        return Math.floorMod(userIndex, DIVISOR);
    }

    @Nullable
    @Override
    public T get(int index)
    {
        return getValueOr(index, null);
    }

    @Override
    public T getValueOr(int index,
                        @Nullable T defaultValue)
    {
        final int childIndex = childIndex(index);
        final int bit = bitFromIndex(childIndex);
        if (bitIsPresent(bitmask, bit)) {
            final int nodeIndex = nodeIndex(index);
            final int arrayIndex = arrayIndexForBit(bitmask, bit);
            return children[arrayIndex].getValueOr(ArrayNode.ROOT_SHIFTS, nodeIndex, defaultValue);
        }
        return defaultValue;
    }

    @Nonnull
    @Override
    public Holder<T> find(int index)
    {
        final int childIndex = childIndex(index);
        final int bit = bitFromIndex(childIndex);
        if (bitIsPresent(bitmask, bit)) {
            final int nodeIndex = nodeIndex(index);
            final int arrayIndex = arrayIndexForBit(bitmask, bit);
            return children[arrayIndex].find(ArrayNode.ROOT_SHIFTS, nodeIndex);
        }
        return Holders.of();
    }

    @Nonnull
    @Override
    public Holder<JImmutableMap.Entry<Integer, T>> findEntry(int index)
    {
        return find(index).map(v -> entry(index, v));
    }

    @Nonnull
    @Override
    public JImmutableArray<T> assign(int index,
                                     @Nullable T value)
    {
        final int entryBaseIndex = entryBaseIndex(index);
        final int childIndex = childIndex(index);
        final int bit = bitFromIndex(childIndex);
        final int nodeIndex = nodeIndex(index);
        final int arrayIndex = arrayIndexForBit(bitmask, bit);
        if (bitIsPresent(bitmask, bit)) {
            final ArrayNode<T> child = children[arrayIndex];
            final ArrayNode<T> newChild = child.assign(entryBaseIndex, ArrayNode.ROOT_SHIFTS, nodeIndex, value);
            final int newSize = size - child.iterableSize() + newChild.iterableSize();
            final ArrayNode<T>[] newChildren = ArrayHelper.assign(children, arrayIndex, newChild);
            return new JImmutableNodeArray<>(bitmask, newChildren, newSize);
        } else {
            final ArrayNode<T> newChild = ArrayLeafNode.forValue(entryBaseIndex, nodeIndex, value);
            final ArrayNode<T>[] newChildren = ArrayHelper.insert(this, children, arrayIndex, newChild);
            return new JImmutableNodeArray<>(addBit(bitmask, bit), newChildren, size + 1);
        }
    }

    @Nonnull
    @Override
    public JImmutableArray<T> delete(int index)
    {
        final int childIndex = childIndex(index);
        final int bit = bitFromIndex(childIndex);
        final int nodeIndex = nodeIndex(index);
        final int arrayIndex = arrayIndexForBit(bitmask, bit);
        if (bitIsPresent(bitmask, bit)) {
            final ArrayNode<T> child = children[arrayIndex];
            final ArrayNode<T> newChild = child.delete(ArrayNode.ROOT_SHIFTS, nodeIndex);
            if (newChild != child) {
                final int newSize = size - child.iterableSize() + newChild.iterableSize();
                if (newSize == 0) {
                    return of();
                } else if (newChild.isEmpty()) {
                    final ArrayNode<T>[] newChildren = ArrayHelper.delete(this, children, arrayIndex);
                    return new JImmutableNodeArray<>(removeBit(bitmask, bit), newChildren, newSize);
                } else {
                    final ArrayNode<T>[] newChildren = ArrayHelper.assign(children, arrayIndex, newChild);
                    return new JImmutableNodeArray<>(bitmask, newChildren, newSize);
                }
            }
        }
        return this;
    }

    @Override
    public int size()
    {
        return size;
    }

    @Override
    public boolean isEmpty()
    {
        return size == 0;
    }

    @Override
    public boolean isNonEmpty()
    {
        return size != 0;
    }

    @Nonnull
    @Override
    public JImmutableArray<T> deleteAll()
    {
        return of();
    }

    @Nonnull
    @Override
    public Map<Integer, T> getMap()
    {
        return ArrayToMapAdaptor.of(this);
    }

    @Nonnull
    @Override
    public IterableStreamable<Integer> keys()
    {
        return TransformStreamable.ofKeys(this);
    }

    @Nonnull
    @Override
    public IterableStreamable<T> values()
    {
        return TransformStreamable.ofValues(this);
    }

    @Nonnull
    @Override
    public JImmutableArray<T> insert(JImmutableMap.Entry<Integer, T> e)
    {
        return (e == null) ? this : assign(e.getKey(), e.getValue());
    }

    @Nonnull
    @Override
    public JImmutableArray<T> getInsertableSelf()
    {
        return this;
    }

    @Override
    public void checkInvariants()
    {

    }

    @Nonnull
    @Override
    public SplitableIterator<JImmutableMap.Entry<Integer, T>> iterator()
    {
        return new GenericIterator<>(new IterableChildren(), 0, size);
    }

    @Override
    public int getSpliteratorCharacteristics()
    {
        return StreamConstants.SPLITERATOR_ORDERED;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public ArrayNode<T>[] allocate(int size)
    {
        return (ArrayNode<T>[])new ArrayNode[size];
    }

    @Override
    public boolean equals(Object o)
    {
        return (o == this) || ((o instanceof JImmutableArray) && IteratorHelper.iteratorEquals(iterator(), ((JImmutableArray)o).iterator()));
    }

    @Override
    public int hashCode()
    {
        return IteratorHelper.iteratorHashCode(iterator());
    }

    @Override
    public String toString()
    {
        return IteratorHelper.iteratorToString(iterator());
    }

    private class IterableChildren
        implements GenericIterator.Iterable<JImmutableMap.Entry<Integer, T>>
    {
        @Nullable
        @Override
        public GenericIterator.State<JImmutableMap.Entry<Integer, T>> iterateOverRange(@Nullable GenericIterator.State<JImmutableMap.Entry<Integer, T>> parent,
                                                                                       int offset,
                                                                                       int limit)
        {
            final Indexed<ArrayNode<T>> source = IndexedArray.retained(children);
            return GenericIterator.indexedState(parent, source, offset, limit);
        }

        @Override
        public int iterableSize()
        {
            return JImmutableNodeArray.this.size;
        }
    }

    public static class Builder<T>
        implements JImmutableArray.Builder<T>
    {
        private JImmutableArray<T> array;

        private Builder()
        {
            array = of();
        }

        @Override
        public int size()
        {
            return array.size();
        }

        @Nonnull
        @Override
        public JImmutableArray.Builder<T> clear()
        {
            array = of();
            return this;
        }

        @Nonnull
        @Override
        public JImmutableArray.Builder<T> add(T value)
        {
            array = array.assign(array.size(), value);
            return this;
        }

        @Nonnull
        @Override
        public JImmutableArray<T> build()
        {
            return array;
        }

        @Nonnull
        private Iterator<T> iterator()
        {
            return array.values().iterator();
        }
    }
}
