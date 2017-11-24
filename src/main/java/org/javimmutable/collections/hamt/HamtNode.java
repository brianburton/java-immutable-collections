package org.javimmutable.collections.hamt;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.IterableStreamable;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.ArrayHelper;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.cursors.LazyMultiCursor;
import org.javimmutable.collections.cursors.SingleValueCursor;
import org.javimmutable.collections.cursors.StandardCursor;
import org.javimmutable.collections.iterators.EmptyIterator;
import org.javimmutable.collections.iterators.IndexedIterator;
import org.javimmutable.collections.iterators.LazyMultiIterator;
import org.javimmutable.collections.iterators.SingleValueIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class HamtNode<T>
    implements ArrayHelper.Allocator<HamtNode<T>>,
               Holder<T>,
               IterableStreamable<T>,
               Cursorable<T>
{
    private static final HamtNode[] EMPTY_NODES = new HamtNode[0];
    @SuppressWarnings("unchecked")
    private static final HamtNode EMPTY = new HamtNode(0, false, null, EMPTY_NODES);

    private static final int SHIFT = 5;
    private static final int MASK = 0x1f;

    private final int bitmask;
    private final boolean filled;
    private final T value;
    private final HamtNode<T>[] children;

    private HamtNode(int bitmask,
                     boolean filled,
                     T value,
                     HamtNode<T>[] children)
    {
        this.bitmask = bitmask;
        this.filled = filled;
        this.value = value;
        this.children = children;
    }

    @SuppressWarnings("unchecked")
    public static <T> HamtNode<T> of()
    {
        return EMPTY;
    }

    @Nonnull
    public Holder<T> find(int key)
    {
        if (key == 0) {
            return this;
        }
        final int index = key & MASK;
        final int remainder = key >>> SHIFT;
        final int bit = 1 << index;
        if ((bitmask & bit) == 0) {
            return Holders.of();
        } else {
            final int childIndex = realIndex(bitmask, bit);
            return children[childIndex].find(remainder);
        }
    }

    public T getValueOr(int key,
                        T defaultValue)
    {
        if (key == 0) {
            return filled ? value : defaultValue;
        }
        final int index = key & MASK;
        final int remainder = key >>> SHIFT;
        final int bit = 1 << index;
        if ((bitmask & bit) == 0) {
            return defaultValue;
        } else {
            final int childIndex = realIndex(bitmask, bit);
            return children[childIndex].getValueOr(remainder, defaultValue);
        }
    }

    @Nonnull
    public HamtNode<T> assign(int key,
                              @Nullable T value,
                              @Nonnull MutableDelta sizeDelta)
    {
        final HamtNode<T>[] children = this.children;
        final int bitmask = this.bitmask;
        if (key == 0) {
            if (filled) {
                if (this.value == value) {
                    return this;
                } else {
                    return new HamtNode<>(bitmask, true, value, children);
                }
            } else {
                sizeDelta.add(1);
                return new HamtNode<>(bitmask, true, value, children);
            }
        }
        final int index = key & MASK;
        final int remainder = key >>> SHIFT;
        final int bit = 1 << index;
        final int childIndex = realIndex(bitmask, bit);
        if ((bitmask & bit) == 0) {
            final HamtNode<T> newChild = empty().assign(remainder, value, sizeDelta);
            final HamtNode<T>[] newChildren = ArrayHelper.insert(this, children, childIndex, newChild);
            return new HamtNode<>(bitmask | bit, filled, this.value, newChildren);
        } else {
            final HamtNode<T> child = children[childIndex];
            final HamtNode<T> newChild = child.assign(remainder, value, sizeDelta);
            if (newChild == child) {
                return this;
            } else {
                final HamtNode<T>[] newChildren = ArrayHelper.assign(children, childIndex, newChild);
                return new HamtNode<>(bitmask, filled, this.value, newChildren);
            }
        }
    }

    @Nonnull
    public HamtNode<T> delete(int key,
                              @Nonnull MutableDelta sizeDelta)
    {
        final int bitmask = this.bitmask;
        final HamtNode<T>[] children = this.children;
        if (key == 0) {
            if (filled) {
                sizeDelta.subtract(1);
                return (bitmask == 0) ? of() : new HamtNode<>(bitmask, false, null, children);
            } else {
                return this;
            }
        }
        final int index = key & MASK;
        final int remainder = key >>> SHIFT;
        final int bit = 1 << index;
        final int childIndex = realIndex(bitmask, bit);
        if ((bitmask & bit) == 0) {
            return this;
        } else {
            final HamtNode<T> child = children[childIndex];
            final HamtNode<T> newChild = child.delete(remainder, sizeDelta);
            if (newChild == child) {
                return this;
            } else if (newChild.isEmpty()) {
                if ((children.length == 1) && !filled) {
                    return of();
                } else {
                    final HamtNode<T>[] newChildren = ArrayHelper.delete(this, children, childIndex);
                    return new HamtNode<>(bitmask & ~bit, filled, value, newChildren);
                }
            } else {
                final HamtNode<T>[] newChildren = ArrayHelper.assign(children, childIndex, newChild);
                return new HamtNode<>(bitmask, filled, value, newChildren);
            }
        }
    }

    public boolean isEmpty()
    {
        return bitmask == 0 && !filled;
    }

    @Override
    public boolean isFilled()
    {
        return filled;
    }

    @Override
    public T getValue()
    {
        if (filled) {
            return value;
        }
        throw new UnsupportedOperationException("cannot get empty value");
    }

    @Override
    public T getValueOrNull()
    {
        return filled ? value : null;
    }

    @Override
    public T getValueOr(T defaultValue)
    {
        return filled ? value : defaultValue;
    }

    @SuppressWarnings("unchecked")
    private HamtNode<T> empty()
    {
        return EMPTY;
    }

    private static int realIndex(int bitmask,
                                 int bit)
    {
        return Integer.bitCount(bitmask & (bit - 1));
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public HamtNode<T>[] allocate(int size)
    {
        return (size == 0) ? EMPTY_NODES : new HamtNode[size];
    }

    @Nonnull
    @Override
    public SplitableIterator<T> iterator()
    {
        return LazyMultiIterator.iterator(IndexedIterator.iterator(indexedForIterator()));
    }

    @Override
    public int getSpliteratorCharacteristics()
    {
        return StreamConstants.SPLITERATOR_UNORDERED;
    }

    @Nonnull
    @Override
    public Cursor<T> cursor()
    {
        return LazyMultiCursor.cursor(StandardCursor.of(indexedForCursor()));
    }

    @Override
    public String toString()
    {
        return "(" + filled + "," + value + "," + bitmask + "," + children.length + ")";
    }

    private Indexed<SplitableIterable<T>> indexedForIterator()
    {
        return new Indexed<SplitableIterable<T>>()
        {
            @Override
            public SplitableIterable<T> get(int index)
            {
                if (index == 0) {
                    if (filled) {
                        return () -> SingleValueIterator.of(value);
                    } else {
                        return () -> EmptyIterator.of();
                    }
                } else {
                    return children[index - 1];
                }
            }

            @Override
            public int size()
            {
                return children.length + 1;
            }
        };
    }

    private Indexed<Cursorable<T>> indexedForCursor()
    {
        return new Indexed<Cursorable<T>>()
        {
            @Override
            public Cursorable<T> get(int index)
            {
                if (index == 0) {
                    if (filled) {
                        return () -> SingleValueCursor.of(value);
                    } else {
                        return () -> StandardCursor.of();
                    }
                } else {
                    return children[index - 1];
                }
            }

            @Override
            public int size()
            {
                return children.length + 1;
            }
        };
    }
}
