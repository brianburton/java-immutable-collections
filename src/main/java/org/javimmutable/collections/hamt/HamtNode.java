package org.javimmutable.collections.hamt;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.IterableStreamable;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.array.trie32.Transforms;
import org.javimmutable.collections.common.ArrayHelper;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.cursors.LazyMultiCursor;
import org.javimmutable.collections.cursors.SingleValueCursor;
import org.javimmutable.collections.cursors.StandardCursor;
import org.javimmutable.collections.iterators.EmptyIterator;
import org.javimmutable.collections.iterators.LazyMultiIterator;
import org.javimmutable.collections.iterators.SingleValueIterator;
import org.javimmutable.collections.iterators.TransformStreamable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class HamtNode<T>
    implements ArrayHelper.Allocator<HamtNode<T>>,
               SplitableIterable<T>,
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

    public <K, V> Holder<V> find(@Nonnull Transforms<T, K, V> transforms,
                                 int hashCode,
                                 @Nonnull K hashKey)
    {
        if (hashCode == 0) {
            return filled ? transforms.findValue(value, hashKey) : Holders.of();
        }
        final int index = hashCode & MASK;
        final int remainder = hashCode >>> SHIFT;
        final int bit = 1 << index;
        if ((bitmask & bit) == 0) {
            return Holders.of();
        } else {
            final int childIndex = realIndex(bitmask, bit);
            return children[childIndex].find(transforms, remainder, hashKey);
        }
    }

    public <K, V> V getValueOr(@Nonnull Transforms<T, K, V> transforms,
                               int hashCode,
                               @Nonnull K hashKey,
                               V defaultValue)
    {
        if (hashCode == 0) {
            return filled ? transforms.findValue(value, hashKey).getValueOr(defaultValue) : defaultValue;
        }
        final int index = hashCode & MASK;
        final int remainder = hashCode >>> SHIFT;
        final int bit = 1 << index;
        if ((bitmask & bit) == 0) {
            return defaultValue;
        } else {
            final int childIndex = realIndex(bitmask, bit);
            return children[childIndex].getValueOr(transforms, remainder, hashKey, defaultValue);
        }
    }

    @Nonnull
    public <K, V> HamtNode<T> assign(@Nonnull Transforms<T, K, V> transforms,
                                     int hashCode,
                                     @Nonnull K hashKey,
                                     @Nullable V value,
                                     @Nonnull MutableDelta sizeDelta)
    {
        final HamtNode<T>[] children = this.children;
        final int bitmask = this.bitmask;
        if (hashCode == 0) {
            if (filled) {
                final T newValue = transforms.update(Holders.of(this.value), hashKey, value, sizeDelta);
                if (this.value == newValue) {
                    return this;
                } else {
                    return new HamtNode<>(bitmask, true, newValue, children);
                }
            } else {
                final T newValue = transforms.update(Holders.of(), hashKey, value, sizeDelta);
                return new HamtNode<>(bitmask, true, newValue, children);
            }
        }
        final int index = hashCode & MASK;
        final int remainder = hashCode >>> SHIFT;
        final int bit = 1 << index;
        final int childIndex = realIndex(bitmask, bit);
        if ((bitmask & bit) == 0) {
            final HamtNode<T> newChild = empty().assign(transforms, remainder, hashKey, value, sizeDelta);
            final HamtNode<T>[] newChildren = ArrayHelper.insert(this, children, childIndex, newChild);
            return new HamtNode<>(bitmask | bit, filled, this.value, newChildren);
        } else {
            final HamtNode<T> child = children[childIndex];
            final HamtNode<T> newChild = child.assign(transforms, remainder, hashKey, value, sizeDelta);
            if (newChild == child) {
                return this;
            } else {
                final HamtNode<T>[] newChildren = ArrayHelper.assign(children, childIndex, newChild);
                return new HamtNode<>(bitmask, filled, this.value, newChildren);
            }
        }
    }

    @Nonnull
    public <K, V> HamtNode<T> delete(@Nonnull Transforms<T, K, V> transforms,
                                     int hashCode,
                                     @Nonnull K hashKey,
                                     @Nonnull MutableDelta sizeDelta)
    {
        final int bitmask = this.bitmask;
        final HamtNode<T>[] children = this.children;
        final T value = this.value;
        if (hashCode == 0) {
            if (filled) {
                final Holder<T> newValue = transforms.delete(value, hashKey, sizeDelta);
                if (newValue == value) {
                    return this;
                } else if (newValue.isEmpty()) {
                    return (bitmask == 0) ? of() : new HamtNode<>(bitmask, false, null, children);
                } else {
                    return new HamtNode<>(bitmask, true, newValue.getValue(), children);
                }
            } else {
                return this;
            }
        }
        final int index = hashCode & MASK;
        final int remainder = hashCode >>> SHIFT;
        final int bit = 1 << index;
        final int childIndex = realIndex(bitmask, bit);
        if ((bitmask & bit) == 0) {
            return this;
        } else {
            final HamtNode<T> child = children[childIndex];
            final HamtNode<T> newChild = child.delete(transforms, remainder, hashKey, sizeDelta);
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
    public <K, V> IterableStreamable<JImmutableMap.Entry<K, V>> entries(@Nonnull Transforms<T, K, V> transforms)
    {
        return new IterableStreamable<JImmutableMap.Entry<K, V>>()
        {
            @Nonnull
            @Override
            public SplitableIterator<JImmutableMap.Entry<K, V>> iterator()
            {
                return HamtNode.this.iterator(transforms);
            }

            @Override
            public int getSpliteratorCharacteristics()
            {
                return StreamConstants.SPLITERATOR_UNORDERED;
            }
        };
    }

    @Nonnull
    public <K, V> IterableStreamable<K> keys(@Nonnull Transforms<T, K, V> transforms)
    {
        return TransformStreamable.ofKeys(entries(transforms));
    }

    @Nonnull
    public <K, V> IterableStreamable<V> values(@Nonnull Transforms<T, K, V> transforms)
    {
        return TransformStreamable.ofValues(entries(transforms));
    }

    @Nonnull
    public <K, V> SplitableIterator<JImmutableMap.Entry<K, V>> iterator(Transforms<T, K, V> transforms)
    {
        return LazyMultiIterator.transformed(indexedForIterator(), node -> () -> iteratorHelper(node.iterator(), transforms));
    }

    @Nonnull
    private <K, V> SplitableIterator<JImmutableMap.Entry<K, V>> iteratorHelper(SplitableIterator<T> value,
                                                                               Transforms<T, K, V> transforms)
    {
        return LazyMultiIterator.transformed(value, t -> () -> transforms.iterator(t));
    }

    @Nonnull
    @Override
    public SplitableIterator<T> iterator()
    {
        return LazyMultiIterator.iterator(indexedForIterator());
    }

    @Nonnull
    public <K, V> Cursor<JImmutableMap.Entry<K, V>> cursor(Transforms<T, K, V> transforms)
    {
        return LazyMultiCursor.transformed(indexedForCursor(), node -> () -> cursorHelper(node.cursor(), transforms));
    }

    @Nonnull
    private <K, V> Cursor<JImmutableMap.Entry<K, V>> cursorHelper(Cursor<T> value,
                                                                  Transforms<T, K, V> transforms)
    {
        return LazyMultiCursor.transformed(value, t -> () -> transforms.cursor(t));
    }

    @Nonnull
    @Override
    public Cursor<T> cursor()
    {
        return LazyMultiCursor.cursor(indexedForCursor());
    }

    @Override
    public String toString()
    {
        return "(" + filled + "," + value + ",0x" + Integer.toHexString(bitmask) + "," + children.length + ")";
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
