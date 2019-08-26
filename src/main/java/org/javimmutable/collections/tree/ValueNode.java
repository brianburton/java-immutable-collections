package org.javimmutable.collections.tree;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap.Entry;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.cursors.LazyMultiCursor;
import org.javimmutable.collections.cursors.SingleValueCursor;
import org.javimmutable.collections.indexed.IndexedHelper;
import org.javimmutable.collections.iterators.LazyMultiIterator;
import org.javimmutable.collections.iterators.SingleValueIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Comparator;

@Immutable
class ValueNode<K, V>
    extends Node<K, V>
{
    private final K key;
    private final V value;
    private final Node<K, V> left;
    private final Node<K, V> right;
    private final int depth;
    private final int size;

    ValueNode(K key,
              V value,
              Node<K, V> left,
              Node<K, V> right)
    {
        this.key = key;
        this.value = value;
        this.left = left;
        this.right = right;
        depth = 1 + Math.max(left.depth(), right.depth());
        size = 1 + left.size() + right.size();
    }

    static <K, V> Node<K, V> balance(@Nonnull K key,
                                     @Nullable V value,
                                     @Nonnull Node<K, V> left,
                                     @Nonnull Node<K, V> right)
    {
        final int diff = left.depth() - right.depth();
        if (diff < -1) {
            right = right.rightWeighted();
            final Node<K, V> newLeft = new ValueNode<>(key, value, left, right.left());
            return new ValueNode<>(right.key(), right.value(), newLeft, right.right());
        } else if (diff > 1) {
            left = left.leftWeighted();
            final Node<K, V> newRight = new ValueNode<>(key, value, left.right(), right);
            return new ValueNode<>(left.key(), left.value(), left.left(), newRight);
        } else {
            return new ValueNode<>(key, value, left, right);
        }
    }

    @Nonnull
    @Override
    public Node<K, V> assign(@Nonnull Comparator<K> comp,
                             @Nonnull K key,
                             @Nullable V value)
    {
        final K thisKey = this.key;
        final V thisValue = this.value;
        final Node<K, V> left = this.left;
        final Node<K, V> right = this.right;
        final int diff = comp.compare(key, thisKey);
        if (diff == 0) {
            if (value != thisValue) {
                return new ValueNode<>(key, value, left, right);
            }
        } else if (diff < 0) {
            final Node<K, V> newLeft = left.assign(comp, key, value);
            if (newLeft != left) {
                return balance(thisKey, thisValue, newLeft, right);
            }
        } else {
            final Node<K, V> newRight = right.assign(comp, key, value);
            if (newRight != right) {
                return balance(thisKey, thisValue, left, newRight);
            }
        }
        return this;
    }

    @Nonnull
    @Override
    public Node<K, V> update(@Nonnull Comparator<K> comp,
                             @Nonnull K key,
                             @Nonnull Func1<Holder<V>, V> generator)
    {
        final K thisKey = this.key;
        final V thisValue = this.value;
        final Node<K, V> left = this.left;
        final Node<K, V> right = this.right;
        final int diff = comp.compare(key, thisKey);
        if (diff == 0) {
            final V newValue = generator.apply(Holders.of(thisValue));
            if (newValue != thisValue) {
                return new ValueNode<>(key, newValue, left, right);
            }
        } else if (diff < 0) {
            final Node<K, V> newLeft = left.update(comp, key, generator);
            if (newLeft != left) {
                return balance(thisKey, thisValue, newLeft, right);
            }
        } else {
            final Node<K, V> newRight = right.update(comp, key, generator);
            if (newRight != right) {
                return balance(thisKey, thisValue, left, newRight);
            }
        }
        return this;
    }

    @Nonnull
    @Override
    public Node<K, V> delete(@Nonnull Comparator<K> comp,
                             @Nonnull K key)
    {
        final K thisKey = this.key;
        final V thisValue = this.value;
        final Node<K, V> left = this.left;
        final Node<K, V> right = this.right;
        final int diff = comp.compare(key, thisKey);
        if (diff == 0) {
            if (left.isEmpty()) {
                return right;
            } else if (right.isEmpty()) {
                return left;
            } else if (left.depth() > right.depth()) {
                final DeleteResult<K, V> result = left.deleteRightmost();
                return balance(result.key, result.value, result.remainder, right);
            } else {
                final DeleteResult<K, V> result = right.deleteLeftmost();
                return balance(result.key, result.value, left, result.remainder);
            }
        } else if (diff < 0) {
            final Node<K, V> newLeft = left.delete(comp, key);
            if (newLeft != left) {
                return balance(thisKey, thisValue, newLeft, right);
            }
        } else {
            final Node<K, V> newRight = right.delete(comp, key);
            if (newRight != right) {
                return balance(thisKey, thisValue, left, newRight);
            }
        }
        return this;
    }

    @Nonnull
    @Override
    DeleteResult<K, V> deleteLeftmost()
    {
        if (left.isEmpty()) {
            return new DeleteResult<>(key, value, right);
        } else {
            final DeleteResult<K, V> result = left.deleteLeftmost();
            return result.withRemainder(balance(key, value, result.remainder, right));
        }
    }

    @Nonnull
    @Override
    DeleteResult<K, V> deleteRightmost()
    {
        if (right.isEmpty()) {
            return new DeleteResult<>(key, value, left);
        } else {
            final DeleteResult<K, V> result = right.deleteRightmost();
            return result.withRemainder(balance(key, value, left, result.remainder));
        }
    }

    @Nullable
    @Override
    public V get(@Nonnull Comparator<K> comp,
                 @Nonnull K key,
                 V defaultValue)
    {
        final int diff = comp.compare(key, this.key);
        if (diff == 0) {
            return value;
        } else if (diff < 0) {
            return left.get(comp, key, defaultValue);
        } else {
            return right.get(comp, key, defaultValue);
        }
    }

    @Nonnull
    @Override
    public Holder<V> find(@Nonnull Comparator<K> comp,
                          @Nonnull K key)
    {
        final int diff = comp.compare(key, this.key);
        if (diff == 0) {
            return Holders.of(value);
        } else if (diff < 0) {
            return left.find(comp, key);
        } else {
            return right.find(comp, key);
        }
    }

    @Nonnull
    @Override
    public Holder<Entry<K, V>> findEntry(@Nonnull Comparator<K> comp,
                                         @Nonnull K key)
    {
        final int diff = comp.compare(key, this.key);
        if (diff == 0) {
            return Holders.of(entry());
        } else if (diff < 0) {
            return left.findEntry(comp, key);
        } else {
            return right.findEntry(comp, key);
        }
    }

    private Entry<K, V> entry()
    {
        return MapEntry.of(key, value);
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    int depth()
    {
        return depth;
    }

    @Override
    public int size()
    {
        return size;
    }

    @Nonnull
    @Override
    K key()
    {
        return key;
    }

    @Nullable
    @Override
    V value()
    {
        return value;
    }

    @Nonnull
    @Override
    Node<K, V> left()
    {
        return left;
    }

    @Nonnull
    @Override
    Node<K, V> right()
    {
        return right;
    }

    @Override
    void checkInvariants(@Nonnull Comparator<K> comp)
    {
        if (key == null) {
            throw new IllegalStateException();
        }
        if (left.size() > 0 && comp.compare(left.key(), key) >= 0) {
            throw new IllegalStateException();
        }
        if (right.size() > 0 && comp.compare(right.key(), key) <= 0) {
            throw new IllegalStateException();
        }
        if (Math.abs(left.depth() - right.depth()) > 1) {
            throw new IllegalStateException();
        }
        if (depth != 1 + Math.max(left.depth(), right.depth())) {
            throw new IllegalStateException();
        }
        if (size != 1 + left.size() + right.size()) {
            throw new IllegalStateException();
        }
    }

    @Override
    Node<K, V> leftWeighted()
    {
        if (right.depth() > left.depth()) {
            final Node<K, V> newLeft = new ValueNode<>(key, value, left, right.left());
            return new ValueNode<>(right.key(), right.value(), newLeft, right.right());
        }
        return this;
    }

    @Override
    Node<K, V> rightWeighted()
    {
        if (left.depth() > right.depth()) {
            final Node<K, V> newRight = new ValueNode<>(key, value, left.right(), right);
            return new ValueNode<>(left.key(), left.value(), left.left(), newRight);
        }
        return this;
    }

    @Nonnull
    @Override
    public SplitableIterator<Entry<K, V>> iterator()
    {
        final SplitableIterable<Entry<K, V>> valueIterator = () -> SingleValueIterator.of(entry());
        return LazyMultiIterator.iterator(IndexedHelper.indexed(left, valueIterator, right));
    }

    @Nonnull
    @Override
    public Cursor<Entry<K, V>> cursor()
    {
        final Cursorable<Entry<K, V>> valueCursor = () -> SingleValueCursor.of(entry());
        return LazyMultiCursor.cursor(IndexedHelper.indexed(left, valueCursor, right));
    }
}
