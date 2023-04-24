///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2023, Burton Computer Corporation
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
//     Redistributions of source code must retain the above copyright
//     notice, this list of conditions and the following disclaimer.
//
//     Redistributions in binary form must reproduce the above copyright
//     notice, this list of conditions and the following disclaimer in
//     the documentation and/or other materials provided with the
//     distribution.
//
//     Neither the name of the Burton Computer Corporation nor the names
//     of its contributors may be used to endorse or promote products
//     derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.javimmutable.collections.common;

import org.javimmutable.collections.IMap;
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.IMultiset;
import org.javimmutable.collections.ISet;
import org.javimmutable.collections.IStreamable;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.indexed.IndexedHelper;
import org.javimmutable.collections.iterators.IndexedIterator;
import org.javimmutable.collections.iterators.IteratorHelper;
import org.javimmutable.collections.iterators.LazyMultiIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

@Immutable
public abstract class AbstractMultiset<T>
    implements IMultiset<T>
{
    protected final IMap<T, Integer> map;
    protected final int occurrences;

    protected AbstractMultiset(IMap<T, Integer> map,
                               int occurrences)
    {
        this.map = map;
        this.occurrences = occurrences;
    }

    @Override
    @Nonnull
    public IMultiset<T> insert(@Nonnull T value)
    {
        return new Editor().delta(value, 1).build();
    }

    @Nonnull
    @Override
    public IMultiset<T> insert(@Nonnull T value,
                               int count)
    {
        if (count < 0) {
            throw new IllegalArgumentException();
        } else if (count == 0) {
            return this;
        } else {
            return new Editor().delta(value, count).build();
        }
    }

    @Override
    public boolean contains(@Nullable T value)
    {
        return (value != null) && (count(value) > 0);
    }

    @Override
    public boolean containsAtLeast(@Nullable T value,
                                   int count)
    {
        if (count < 0) {
            throw new IllegalArgumentException();
        } else {
            return (value != null) && (count(value) >= count);
        }
    }

    @Override
    public boolean containsAll(@Nonnull Iterable<? extends T> other)
    {
        return containsAll(other.iterator());
    }

    @Override
    public boolean containsAll(@Nonnull Iterator<? extends T> other)
    {
        while (other.hasNext()) {
            if (!contains(other.next())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAllOccurrences(@Nonnull Iterable<? extends T> other)
    {
        return containsAllOccurrences(other.iterator());
    }

    @Override
    public boolean containsAllOccurrences(@Nonnull Iterator<? extends T> other)
    {
        final Counter counter = new Counter();
        while (other.hasNext()) {
            final T value = other.next();
            if ((value == null) || (counter.add(value, 1) > count(value))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAllOccurrences(@Nonnull IMultiset<? extends T> values)
    {
        return containsAllOccurrencesMultisetHelper(values);
    }

    @Override
    public boolean containsAny(@Nonnull Iterable<? extends T> other)
    {
        return containsAny(other.iterator());
    }

    @Override
    public boolean containsAny(@Nonnull Iterator<? extends T> other)
    {
        while (other.hasNext()) {
            if (contains(other.next())) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Nonnull
    public IMultiset<T> delete(@Nonnull T value)
    {
        return new Editor().remove(value).build();
    }

    @Override
    @Nonnull
    public IMultiset<T> deleteOccurrence(@Nonnull T value)
    {
        return new Editor().delta(value, -1).build();
    }

    @Override
    @Nonnull
    public IMultiset<T> deleteOccurrence(@Nonnull T value,
                                         int subtractBy)
    {
        if (subtractBy < 0) {
            throw new IllegalArgumentException();
        } else {
            return new Editor().delta(value, -subtractBy).build();
        }
    }

    @Override
    @Nonnull
    public IMultiset<T> deleteAll(@Nonnull Iterable<? extends T> other)
    {
        return deleteAll(other.iterator());
    }

    @Override
    @Nonnull
    public IMultiset<T> deleteAll(@Nonnull Iterator<? extends T> other)
    {
        Editor editor = new Editor();
        while (other.hasNext()) {
            final T value = other.next();
            if (value != null) {
                editor.remove(value);
            }
        }
        return editor.build();
    }

    @Override
    @Nonnull
    public IMultiset<T> deleteAllOccurrences(@Nonnull Iterable<? extends T> other)
    {
        return deleteAllOccurrences(other.iterator());
    }

    @Override
    @Nonnull
    public IMultiset<T> deleteAllOccurrences(@Nonnull Iterator<? extends T> other)
    {
        Editor editor = new Editor();
        while (other.hasNext()) {
            final T value = other.next();
            if (value != null) {
                editor.delta(value, -1);
            }
        }
        return editor.build();
    }

    @Override
    @Nonnull
    public IMultiset<T> deleteAllOccurrences(@Nonnull IMultiset<? extends T> other)
    {
        return deleteAllOccurrencesMultisetHelper(other);
    }

    @Override
    @Nonnull
    public IMultiset<T> insertAll(@Nonnull Iterable<? extends T> values)
    {
        return insertAll(values.iterator());
    }

    @Override
    @Nonnull
    public IMultiset<T> insertAll(@Nonnull Iterator<? extends T> other)
    {
        Editor editor = new Editor();
        while (other.hasNext()) {
            final T value = other.next();
            if (value != null) {
                editor.delta(value, 1);
            }
        }
        return editor.build();
    }

    @Override
    @Nonnull
    public IMultiset<T> insertAll(@Nonnull IMultiset<? extends T> values)
    {
        return insertAllMultisetHelper(values);
    }

    @Override
    @Nonnull
    public IMultiset<T> union(@Nonnull Iterable<? extends T> other)
    {
        return union(other.iterator());
    }

    @Override
    @Nonnull
    public IMultiset<T> union(@Nonnull Iterator<? extends T> other)
    {
        final Counter counter = new Counter(other);
        return new Editor()
            .max(counter)
            .build();
    }

    @Override
    @Nonnull
    public IMultiset<T> union(@Nonnull IMultiset<? extends T> other)
    {
        return unionMultisetHelper(other);
    }

    @Override
    @Nonnull
    public IMultiset<T> intersection(@Nonnull Iterable<? extends T> other)
    {
        return intersection(other.iterator());
    }

    @Override
    @Nonnull
    public IMultiset<T> intersection(@Nonnull Iterator<? extends T> other)
    {
        if (isEmpty()) {
            return this;
        } else if (!other.hasNext()) {
            return deleteAll();
        } else {
            final Counter counter = new Counter(other);
            return new Editor()
                .min(counter)
                .removeValuesNotInCounter(counter)
                .build();
        }
    }

    @Override
    @Nonnull
    public IMultiset<T> intersection(@Nonnull IMultiset<? extends T> other)
    {
        if (isEmpty()) {
            return this;
        } else if (other.isEmpty()) {
            return deleteAll();
        } else {
            final Counter counter = new Counter(other.entries());
            return new Editor()
                .min(counter)
                .removeValuesNotInCounter(counter)
                .build();
        }
    }

    @Override
    @Nonnull
    public IMultiset<T> intersection(@Nonnull ISet<? extends T> other)
    {
        return intersection(other.getSet());
    }

    @Override
    @Nonnull
    public IMultiset<T> intersection(@Nonnull Set<? extends T> other)
    {
        if (isEmpty()) {
            return this;
        } else if (other.isEmpty()) {
            return deleteAll();
        } else {
            Editor editor = new Editor();
            for (IMapEntry<T, Integer> entry : map) {
                final T value = entry.getKey();
                final int oldCount = entry.getValue();
                if (other.contains(value)) {
                    editor.adjust(value, oldCount, 1);
                } else {
                    editor.adjust(value, oldCount, 0);
                }
            }
            return editor.build();
        }
    }

    @Override
    public int count(@Nonnull T value)
    {
        Conditions.stopNull(value);
        return map.getValueOr(value, 0);
    }

    @Nonnull
    @Override
    public IMultiset<T> setCount(@Nonnull T value,
                                 int count)
    {
        Conditions.stopNull(value, count);
        if (count < 0) {
            throw new IllegalArgumentException();
        } else {
            return new Editor().set(value, count).build();
        }
    }

    /**
     * Implemented by derived classes to create a new instance of the appropriate class.
     *
     * @param map         base map for new multiset
     * @param occurrences total occurrences in map
     * @return new multiset built from map
     */
    protected abstract IMultiset<T> create(IMap<T, Integer> map,
                                           int occurrences);

    /**
     * Implemented by derived classes to create a new empty mutable Map
     * that operates in the same way as this multiset's underlying immutable Map.
     *
     * @return new empty Counter
     */
    protected abstract Map<T, Integer> emptyMutableMap();

    @Override
    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    @Override
    public int size()
    {
        return map.size();
    }

    @Override
    public int occurrenceCount()
    {
        return occurrences;
    }

    @Nonnull
    public Set<T> getSet()
    {
        return SetAdaptor.of(this);
    }

    @Override
    @Nonnull
    public SplitableIterator<T> iterator()
    {
        return map.keys().iterator();
    }

    @Override
    public int getSpliteratorCharacteristics()
    {
        return map.getSpliteratorCharacteristics();
    }

    @Nonnull
    @Override
    public IStreamable<IMapEntry<T, Integer>> entries()
    {
        return map;
    }

    @Nonnull
    @Override
    public IStreamable<T> occurrences()
    {
        return new IStreamable<T>()
        {
            @Nonnull
            @Override
            public SplitableIterator<T> iterator()
            {
                return LazyMultiIterator.transformed(map.iterator(), e -> () -> IndexedIterator.iterator(IndexedHelper.repeating(e.getKey(), e.getValue())));
            }

            @Override
            public int getSpliteratorCharacteristics()
            {
                return map.getSpliteratorCharacteristics();
            }
        };
    }

    @Override
    public int hashCode()
    {
        return IteratorHelper.iteratorHashCode(iterator());
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this) {
            return true;
        } else if (o == null) {
            return false;
        } else if (o instanceof IMultiset) {
            final IMultiset that = (IMultiset)o;
            //noinspection unchecked
            return (occurrenceCount() == that.occurrenceCount()) && containsAllOccurrences(that);
        } else if (o instanceof ISet) {
            final ISet that = (ISet)o;
            return (size() == occurrences) && getSet().equals(that.getSet());
        } else {
            return (o instanceof Set) && (size() == occurrences) && getSet().equals(o);
        }
    }

    @Override
    public String toString()
    {
        return IteratorHelper.iteratorToString(occurrences().iterator());
    }

    public void checkInvariants()
    {
        map.checkInvariants();
        if (occurrences < map.size()) {
            throw new IllegalStateException();
        }
        int checkOccurrences = 0;
        for (IMapEntry<T, Integer> entry : entries()) {
            int entryCount = entry.getValue();
            if (entryCount <= 0) {
                throw new IllegalStateException(String.format("illegal count of %d for value %s%n", entryCount, entry.getKey()));
            }
            checkOccurrences += entryCount;
        }
        if (occurrences != checkOccurrences) {
            throw new RuntimeException(String.format("occurrence size mismatch - expected %d found %d%n", checkOccurrences, occurrences));
        }
    }

    private <T1 extends T> boolean containsAllOccurrencesMultisetHelper(@Nonnull IMultiset<T1> values)
    {
        for (IMapEntry<T1, Integer> entry : values.entries()) {
            final T value = entry.getKey();
            final int otherCount = entry.getValue();
            if (count(value) < otherCount) {
                return false;
            }
        }
        return true;
    }

    private <T1 extends T> IMultiset<T> deleteAllOccurrencesMultisetHelper(@Nonnull IMultiset<T1> values)
    {
        final Editor editor = new Editor();
        for (IMapEntry<T1, Integer> entry : values.entries()) {
            final T value = entry.getKey();
            final int otherCount = entry.getValue();
            editor.delta(value, -otherCount);
        }
        return editor.build();
    }

    private <T1 extends T> IMultiset<T> insertAllMultisetHelper(@Nonnull IMultiset<T1> values)
    {
        final Editor editor = new Editor();
        for (IMapEntry<T1, Integer> entry : values.entries()) {
            final T value = entry.getKey();
            final int otherCount = entry.getValue();
            editor.delta(value, otherCount);
        }
        return editor.build();
    }

    @Nonnull
    private <T1 extends T> IMultiset<T> unionMultisetHelper(@Nonnull IMultiset<T1> other)
    {
        final Editor editor = new Editor();
        for (IMapEntry<T1, Integer> entry : other.entries()) {
            final T value = entry.getKey();
            final int otherCount = entry.getValue();
            editor.set(value, Math.max(otherCount, count(value)));
        }
        return editor.build();
    }

    private class Editor
    {
        private IMap<T, Integer> newMap;
        private int newOccurrences;

        private Editor()
        {
            this.newMap = map;
            this.newOccurrences = occurrences;
        }

        private Editor remove(T value)
        {
            return set(value, 0);
        }

        private Editor delta(T value,
                             int delta)
        {
            final int oldCount = newMap.getValueOr(value, 0);
            adjust(value, oldCount, oldCount + delta);
            return this;
        }

        private Editor set(T value,
                           int newCount)
        {
            final int oldCount = newMap.getValueOr(value, 0);
            adjust(value, oldCount, newCount);
            return this;
        }

        private Editor max(Counter counter)
        {
            counter.forEach((value, count) -> {
                final int ourCount = newMap.getValueOr(value, 0);
                final int theirCount = count;
                adjust(value, ourCount, Math.max(ourCount, theirCount));
            });
            return this;
        }

        private Editor min(Counter counter)
        {
            counter.forEach((value, count) -> {
                final int ourCount = newMap.getValueOr(value, 0);
                final int theirCount = count;
                adjust(value, ourCount, Math.min(ourCount, theirCount));
            });
            return this;
        }

        private void adjust(T value,
                            int oldCount,
                            int newCount)
        {
            if (newCount != oldCount) {
                if (newCount <= 0) {
                    newMap = newMap.delete(value);
                    newOccurrences -= oldCount;
                } else {
                    newMap = newMap.assign(value, newCount);
                    newOccurrences = newOccurrences - oldCount + newCount;
                }
            }
        }

        private Editor removeValuesNotInCounter(Counter counter)
        {
            newMap.forEach((value, count) -> {
                if (counter.get(value) == 0) {
                    newMap = newMap.delete(value);
                    newOccurrences -= count;
                }
            });
            return this;
        }

        private IMultiset<T> build()
        {
            if (newMap == map) {
                return AbstractMultiset.this;
            } else if (newMap.isEmpty()) {
                return deleteAll();
            } else {
                return create(newMap, newOccurrences);
            }
        }
    }

    private class Counter
    {
        private final Map<T, Integer> counts;

        private Counter()
        {
            counts = emptyMutableMap();
            assert counts.isEmpty();
        }

        private Counter(@Nonnull Iterator<? extends T> values)
        {
            this();
            while (values.hasNext()) {
                final T value = values.next();
                if (value != null) {
                    add(value, 1);
                }
            }
        }

        private <T1 extends T> Counter(@Nonnull IStreamable<IMapEntry<T1, Integer>> values)
        {
            this();
            for (IMapEntry<? extends T, Integer> entry : values) {
                add(entry.getKey(), entry.getValue());
            }
        }

        private int add(T value,
                        int number)
        {
            assert value != null;
            assert number > 0;
            final int currentCount = get(value);
            final int newCount = currentCount + number;
            counts.put(value, newCount);
            return newCount;
        }

        private int get(T value)
        {
            assert value != null;
            final Integer count = counts.get(value);
            assert (count == null) || (count > 0);
            return (count == null) ? 0 : count;
        }

        void forEach(@Nonnull BiConsumer<T, Integer> proc)
        {
            counts.forEach(proc);
        }
    }
}
