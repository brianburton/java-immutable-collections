package org.javimmutable.collections.hash.hamt;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.array.trie32.Transforms;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.SingleValueCursor;
import org.javimmutable.collections.iterators.SingleValueIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * HamtNode that stores only one value.  Any assign that would progress down the tree
 * below this node replaces it with a normal node instead.  These exist to shorten the
 * height of the overall tree structure when hashCodes are dispersed.
 */
public class HamtLeafNode<T, K, V>
    implements HamtNode<T, K, V>
{
    private final int hashCode;
    @Nonnull
    private final T value;

    HamtLeafNode(int hashCode,
                 @Nonnull T value)
    {
        this.hashCode = hashCode;
        this.value = value;
    }

    @Override
    public Holder<V> find(@Nonnull Transforms<T, K, V> transforms,
                          int hashCode,
                          @Nonnull K hashKey)
    {
        if (hashCode == this.hashCode) {
            return transforms.findValue(value, hashKey);
        } else {
            return Holders.of();
        }
    }

    @Override
    public V getValueOr(@Nonnull Transforms<T, K, V> transforms,
                        int hashCode,
                        @Nonnull K hashKey,
                        V defaultValue)
    {
        if (hashCode == this.hashCode) {
            return transforms.getValueOr(value, hashKey, defaultValue);
        } else {
            return defaultValue;
        }
    }

    @Nonnull
    @Override
    public HamtNode<T, K, V> assign(@Nonnull Transforms<T, K, V> transforms,
                                    int hashCode,
                                    @Nonnull K hashKey,
                                    @Nullable V value,
                                    @Nonnull MutableDelta sizeDelta)
    {
        final int thisHashCode = this.hashCode;
        final T thisValue = this.value;
        if (hashCode == thisHashCode) {
            final T newValue = transforms.update(thisValue, hashKey, value, sizeDelta);
            if (newValue == thisValue) {
                return this;
            } else {
                return new HamtLeafNode<>(hashCode, newValue);
            }
        } else {
            final HamtNode<T, K, V> expanded = HamtBranchNode.forLeafExpansion(thisHashCode, thisValue);
            return expanded.assign(transforms, hashCode, hashKey, value, sizeDelta);
        }
    }

    @Nonnull
    @Override
    public HamtNode<T, K, V> delete(@Nonnull Transforms<T, K, V> transforms,
                                    int hashCode,
                                    @Nonnull K hashKey,
                                    @Nonnull MutableDelta sizeDelta)
    {
        final int thisHashCode = this.hashCode;
        final T thisValue = this.value;
        if (hashCode == this.hashCode) {
            final T newValue = transforms.delete(thisValue, hashKey, sizeDelta);
            if (newValue == thisValue) {
                return this;
            } else if (newValue == null) {
                return HamtEmptyNode.of();
            } else {
                return new HamtLeafNode<>(hashCode, newValue);
            }
        } else {
            return this;
        }
    }

    public HamtNode<T, K, V> liftNode(int index)
    {
        return new HamtLeafNode<>(hashCode << HamtBranchNode.SHIFT | index, value);
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Nonnull
    @Override
    public SplitableIterator<JImmutableMap.Entry<K, V>> iterator(Transforms<T, K, V> transforms)
    {
        return transforms.iterator(value);
    }

    @Nonnull
    @Override
    public Cursor<JImmutableMap.Entry<K, V>> cursor(Transforms<T, K, V> transforms)
    {
        return transforms.cursor(value);
    }

    @Nonnull
    @Override
    public SplitableIterator<T> iterator()
    {
        return SingleValueIterator.of(value);
    }

    @Nonnull
    @Override
    public Cursor<T> cursor()
    {
        return SingleValueCursor.of(value);
    }
}
