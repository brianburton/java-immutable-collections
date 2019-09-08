package org.javimmutable.collections.hash.hamt;

import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.ArrayHelper;
import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.hash.JImmutableHashMap;
import org.javimmutable.collections.list.ListCollisionMap;
import org.javimmutable.collections.tree.ComparableComparator;
import org.javimmutable.collections.tree.TreeCollisionMap;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.List;

@ThreadSafe
class HamtBuilder<K, V>
    implements ArrayHelper.Allocator<HamtNode<K, V>>,
               JImmutableMap.Builder<K, V>
{
    private final List<Value<K, V>> values = new ArrayList<>();

    @Nonnull
    @Override
    public synchronized JImmutableMap<K, V> build()
    {
        if (values.isEmpty()) {
            return JImmutableHashMap.of();
        } else {
            values.sort(ComparableComparator.of());
            final CollisionMap<K, V> collisionMap = getCollisionMap();
            HamtNode<K, V> root = combine(collisionMap, 0, 0, values.size());
            return JImmutableHashMap.forBuilder(root, collisionMap);
        }
    }

    public CollisionMap<K, V> getCollisionMap()
    {
        if (values.get(0).key instanceof Comparable) {
            return TreeCollisionMap.instance();
        } else {
            return ListCollisionMap.instance();
        }
    }

    @Nonnull
    @Override
    public synchronized JImmutableMap.Builder<K, V> add(@Nonnull K key,
                                                        V value)
    {
        values.add(new Value<>(key, value));
        return this;
    }

    private HamtNode<K, V> combine(CollisionMap<K, V> collisionMap,
                                   int shift,
                                   int offset,
                                   int limit)
    {
        assert limit > offset;
        CollisionMap.Node myValues = collisionMap.emptyNode();
        final HamtNode<K, V>[] children = allocate(32);
        final int childShift = Math.min(32, shift + HamtBranchNode.SHIFT);
        int childIndex = -1;
        int childOffset = limit;
        for (int i = offset; i < limit; ++i) {
            final Value<K, V> v = values.get(i);
            final int hash = (shift != 32) ? v.hash >>> shift : 0;
            if (hash == 0) {
                myValues = collisionMap.update(myValues, v.key, v.value);
            } else {
                final int index = hash & HamtBranchNode.MASK;
                if (index != childIndex) {
                    assert index > childIndex;
                    if (childOffset < i) {
                        children[childIndex] = combine(collisionMap, childShift, childOffset, i);
                    }
                    childOffset = i;
                    childIndex = index;
                }
            }
        }
        if (childOffset < limit) {
            children[childIndex] = combine(collisionMap, childShift, childOffset, limit);
        }
        return HamtBranchNode.forBuilder(collisionMap, myValues, children);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public HamtNode<K, V>[] allocate(int size)
    {
        return new HamtNode[size];
    }

    private static class Value<K, V>
        implements Comparable<Value<K, V>>
    {
        private final int hash;
        private final K key;
        private final V value;
        private final long sortHash;

        private Value(K key,
                      V value)
        {
            this.key = key;
            this.value = value;
            this.hash = key.hashCode();
            sortHash = computeSortCode(hash);
        }

        @Override
        public int compareTo(@Nonnull Value<K, V> other)
        {
            return Long.compare(sortHash, other.sortHash);
        }

        @Override
        public String toString()
        {
            return String.format("[%s,%s,%s]", binary(sortHash), binary(hash), hash);
        }

        private String binary(long value)
        {
            return formatBinary(Long.toString(value, 2));
        }

        private String binary(int value)
        {
            return formatBinary(Integer.toString(value, 2));
        }

        private String formatBinary(String s)
        {
            if (s.startsWith("-")) {
                s = s.substring(1);
            }
            while (s.length() < 35) {
                s = "0" + s;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 30; i >= 0; i -= 5) {
                if (sb.length() > 0) {
                    sb.insert(0, "_");
                }
                sb.insert(0, s.substring(i, i + 5));
            }
            return sb.toString();
        }
    }

    static long computeSortCode(int hashCode)
    {
        long answer = 0;
        for (int shift = 0; shift < 32; shift += HamtBranchNode.SHIFT) {
            answer = (answer << HamtBranchNode.SHIFT) | (hashCode & HamtBranchNode.MASK);
            hashCode = hashCode >>> HamtBranchNode.SHIFT;
        }
        return answer;
    }
}
