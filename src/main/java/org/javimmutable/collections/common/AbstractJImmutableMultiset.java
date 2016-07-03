///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2014, Burton Computer Corporation
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

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableMultiset;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.cursors.Cursors;
import org.javimmutable.collections.cursors.MultiTransformCursor;
import org.javimmutable.collections.cursors.StandardCursor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Immutable
public abstract class AbstractJImmutableMultiset<T>
        implements JImmutableMultiset<T>
{
    private final JImmutableMap<T, Integer> map;
    private final int occurrences;

    protected AbstractJImmutableMultiset(JImmutableMap<T, Integer> map,
                                         int occurrences)
    {
        this.map = map;
        this.occurrences = occurrences;
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> insert(@Nonnull T value)
    {
        JImmutableMap<T, Integer> newMap = increaseCount(map, value, 1);
        return (newMap != map) ? create(newMap, 1 + occurrences) : this;
    }

    @Nonnull
    @Override
    public JImmutableMultiset<T> insert(@Nonnull T value,
                                        int count)
    {
        if (count < 0) {
            throw new IllegalArgumentException();
        } else if (count == 0) {
            return this;
        } else {
            JImmutableMap<T, Integer> newMap = increaseCount(map, value, count);
            return (newMap != map) ? create(newMap, count + occurrences) : this;
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
    public boolean containsAll(@Nonnull Cursorable<? extends T> other)
    {
        return containsAll(other.cursor());
    }

    @Override
    public boolean containsAll(@Nonnull Collection<? extends T> other)
    {
        return containsAll(other.iterator());
    }

    @Override
    public boolean containsAll(@Nonnull Cursor<? extends T> other)
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
    public boolean containsAllOccurrences(@Nonnull Cursorable<? extends T> other)
    {
        return containsAllOccurrences(other.cursor().iterator());
    }

    @Override
    public boolean containsAllOccurrences(@Nonnull Collection<? extends T> other)
    {
        return containsAllOccurrences(other.iterator());
    }

    @Override
    public boolean containsAllOccurrences(@Nonnull Cursor<? extends T> other)
    {
        return containsAllOccurrences(other.iterator());
    }

    @Override
    public boolean containsAllOccurrences(@Nonnull Iterator<? extends T> other)
    {
        final Counter<T> counter = emptyCounter();
        while (other.hasNext()) {
            final T value = other.next();
            if (counter.delta(value, 1) > count(value)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAllOccurrences(@Nonnull JImmutableMultiset<? extends T> values)
    {
        return containsAllOccurrencesMultisetHelper(values);
    }

    @Override
    public boolean containsAny(@Nonnull Cursorable<? extends T> other)
    {
        return containsAny(other.cursor().iterator());
    }

    @Override
    public boolean containsAny(@Nonnull Collection<? extends T> other)
    {
        return containsAny(other.iterator());
    }

    @Override
    public boolean containsAny(@Nonnull Cursor<? extends T> other)
    {
        return containsAny(other.iterator());
    }

    @Override
    public boolean containsAny(@Nonnull Iterator<? extends T> other)
    {
        while (other.hasNext()) {
            if (this.contains(other.next())) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> delete(@Nonnull T value)
    {
        return editor().remove(value).build();
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteOccurrence(@Nonnull T value)
    {
        return editor().delta(value, -1).build();
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteOccurrence(@Nonnull T value,
                                                  int subtractBy)
    {
        if (subtractBy < 0) {
            throw new IllegalArgumentException();
        } else {
            return editor().delta(value, -subtractBy).build();
        }
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAll(@Nonnull Cursorable<? extends T> other)
    {
        return deleteAll(other.cursor());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAll(@Nonnull Collection<? extends T> other)
    {
        return deleteAll(other.iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAll(@Nonnull Cursor<? extends T> other)
    {
        return deleteAll(other.iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAll(@Nonnull Iterator<? extends T> other)
    {
        Editor editor = editor();
        while (other.hasNext()) {
            editor.remove(other.next());
        }
        return editor.build();
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAllOccurrences(@Nonnull Cursorable<? extends T> other)
    {
        return deleteAllOccurrences(other.cursor());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAllOccurrences(@Nonnull Collection<? extends T> other)
    {
        return deleteAllOccurrences(other.iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAllOccurrences(@Nonnull Cursor<? extends T> other)
    {
        return deleteAllOccurrences(other.iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAllOccurrences(@Nonnull Iterator<? extends T> other)
    {
        Editor editor = editor();
        while (other.hasNext()) {
            editor.delta(other.next(), -1);
        }
        return editor.build();
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAllOccurrences(@Nonnull JImmutableMultiset<? extends T> other)
    {
        return deleteAllOccurrencesHelper(other);
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> insertAll(@Nonnull Cursorable<? extends T> values)
    {
        return insertAll(values.cursor());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> insertAll(@Nonnull Collection<? extends T> values)
    {
        return insertAll(values.iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> insertAll(@Nonnull Cursor<? extends T> values)
    {
        return insertAll(values.iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> insertAll(@Nonnull Iterator<? extends T> other)
    {
        Editor editor = editor();
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
    public JImmutableMultiset<T> insertAll(@Nonnull JImmutableMultiset<? extends T> values)
    {
        return insertAllMultisetHelper(values);
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> union(@Nonnull Cursorable<? extends T> other)
    {
        return union(other.cursor().iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> union(@Nonnull Collection<? extends T> other)
    {
        return union(other.iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> union(@Nonnull Cursor<? extends T> other)
    {
        return union(other.iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> union(@Nonnull Iterator<? extends T> other)
    {
        final Counter<T> counter = emptyCounter();
        final Editor editor = editor();
        while (other.hasNext()) {
            final T value = other.next();
            if (value != null) {
                final int otherCount = counter.delta(value, 1);
                editor.set(value, Math.max(otherCount, count(value)));
            }
        }
        return editor.build();
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> union(@Nonnull JImmutableMultiset<? extends T> other)
    {
        return unionMultisetHelper(other);
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> intersection(@Nonnull Cursorable<? extends T> other)
    {
        return intersection(other.cursor().iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> intersection(@Nonnull Collection<? extends T> other)
    {
        return intersection(other.iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> intersection(@Nonnull Cursor<? extends T> other)
    {
        return intersection(other.iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> intersection(@Nonnull Iterator<? extends T> other)
    {
        final Counter<T> counter = emptyCounter();
        final Editor editor = editor();
        while (other.hasNext()) {
            final T value = other.next();
            if (value != null) {
                final int otherCount = counter.delta(value, 1);
                final int ourCount = this.count(value);
                editor.set(value, Math.min(otherCount, ourCount));
            }
        }
        return editor.removeValuesNotInCounter(counter).build();
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> intersection(@Nonnull JImmutableMultiset<? extends T> other)
    {
        if (isEmpty()) {
            return this;
        } else if (other.isEmpty()) {
            return deleteAll();
        } else {
            return intersectionMultisetHelper(other);
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
    public JImmutableMultiset<T> setCount(@Nonnull T value,
                                          int count)
    {
        Conditions.stopNull(value, count);
        if (count < 0) {
            throw new IllegalArgumentException();
        } else {
            return editor().set(value, count).build();
        }
    }

    /**
     * Implemented by derived classes to create a new empty Map instance for use by the Multiset.
     *
     * @return empty immutable map
     */
    protected abstract JImmutableMap<T, Integer> emptyMap();

    /**
     * Implemented by derived classes to create a new instance of the appropriate class.
     *
     * @param map         base map for new multiset
     * @param occurrences total occurrences in map
     * @return new multiset built from map
     */
    protected abstract JImmutableMultiset<T> create(JImmutableMap<T, Integer> map,
                                                    int occurrences);

    /**
     * Implemented by derived classes to create a new instance of Counter with a mutable Map
     * that operates in the same way as this multiset's underlying immutable Map.
     *
     * @return new empty Counter
     */
    protected abstract Counter<T> emptyCounter();


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

    @Override
    @Nonnull
    public Set<T> getSet()
    {
        return SetAdaptor.of(this);
    }

    @Override
    @Nonnull
    public Iterator<T> iterator()
    {
        return IteratorAdaptor.of(cursor());
    }

    @Override
    @Nonnull
    public Cursor<T> occurrenceCursor()
    {
        return MultiTransformCursor.of(entryCursor(), new Func1<JImmutableMap.Entry<T, Integer>, Cursor<T>>()
        {
            @Override
            public Cursor<T> apply(JImmutableMap.Entry<T, Integer> entry)
            {
                return StandardCursor.of(new StandardCursor.RepeatingValueCursorSource<T>(entry));
            }
        });
    }

    @Override
    @Nonnull
    public Cursor<T> cursor()
    {
        return map.keysCursor();
    }

    @Override
    @Nonnull
    public Cursor<JImmutableMap.Entry<T, Integer>> entryCursor()
    {
        return map.cursor();
    }

    @Override
    public int hashCode()
    {
        return Cursors.computeHashCode(occurrenceCursor());
    }

    // TODO verify this
    @Override
    public boolean equals(Object o)
    {
        if (o == this) {
            return true;
        } else if (o == null) {
            return false;
        } else if (o instanceof JImmutableMultiset) {
            //noinspection unchecked
            return (occurrenceCount() == ((JImmutableMultiset)o).occurrenceCount()) && ((JImmutableMultiset)o).containsAllOccurrences(this);
        } else if (o instanceof JImmutableSet) {
            return (size() == occurrences) && getSet().equals(((JImmutableSet)o).getSet());
        } else {
            //noinspection EqualsBetweenInconvertibleTypes
            return (o instanceof Set) && (size() == occurrences) && getSet().equals(o);
        }
    }

    @Override
    public String toString()
    {
        return Cursors.makeString(occurrenceCursor());
    }

    private JImmutableMap<T, Integer> increaseCount(JImmutableMap<T, Integer> map,
                                                    T value,
                                                    int addBy)
    {
        int newCount = getCount(map, value) + addBy;
        return map.assign(value, newCount);
    }

    public void checkInvariants()
    {
        map.checkInvariants();
        if (occurrences < map.size()) {
            throw new IllegalStateException();
        }
        int checkOccurrences = 0;
        for (JImmutableMap.Entry<T, Integer> entry : entryCursor()) {
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

    private Integer getCount(JImmutableMap<T, Integer> checkMap,
                             T value)
    {
        return checkMap.getValueOr(value, 0);
    }

    private <T1 extends T> boolean containsAllOccurrencesMultisetHelper(@Nonnull JImmutableMultiset<T1> values)
    {
        final Counter<T> counter = emptyCounter();
        for (Cursor<JImmutableMap.Entry<T1, Integer>> e = values.entryCursor().start(); e.hasValue(); e = e.next()) {
            final T1 entryValue = e.getValue().getKey();
            final int entryCount = e.getValue().getValue();
            if (count(entryValue) < counter.delta(entryValue, entryCount)) {
                return false;
            }
        }
        return true;
    }

    private <T1 extends T> JImmutableMultiset<T> deleteAllOccurrencesHelper(@Nonnull JImmutableMultiset<T1> values)
    {
        Editor editor = editor();
        for (Cursor<JImmutableMap.Entry<T1, Integer>> e = values.entryCursor().start(); e.hasValue(); e = e.next()) {
            final JImmutableMap.Entry<T1, Integer> entry = e.getValue();
            editor.delta(entry.getKey(), -entry.getValue());
        }
        return editor.build();
    }

    private <T1 extends T> JImmutableMultiset<T> insertAllMultisetHelper(@Nonnull JImmutableMultiset<T1> values)
    {
        Editor editor = editor();
        for (Cursor<JImmutableMap.Entry<T1, Integer>> e = values.entryCursor().start(); e.hasValue(); e = e.next()) {
            final JImmutableMap.Entry<T1, Integer> entry = e.getValue();
            editor.delta(entry.getKey(), entry.getValue());
        }
        return editor.build();
    }

    @Nonnull
    private <T1 extends T> JImmutableMultiset<T> unionMultisetHelper(@Nonnull JImmutableMultiset<T1> other)
    {
        final Counter<T> counter = emptyCounter();
        final Editor editor = editor();
        for (Cursor<JImmutableMap.Entry<T1, Integer>> e = other.entryCursor().start(); e.hasValue(); e = e.next()) {
            final JImmutableMap.Entry<T1, Integer> entry = e.getValue();
            final T value = entry.getKey();
            final int otherCount = counter.delta(value, entry.getValue());
            editor.set(value, Math.max(otherCount, count(value)));
        }
        return editor.build();
    }

    @Nonnull
    protected <T1 extends T> JImmutableMultiset<T> intersectionMultisetHelper(@Nonnull JImmutableMultiset<T1> other)
    {
        final Counter<T> counter = emptyCounter();
        final Editor editor = editor();
        for (Cursor<JImmutableMap.Entry<T1, Integer>> e = other.entryCursor().start(); e.hasValue(); e = e.next()) {
            final JImmutableMap.Entry<T1, Integer> entry = e.getValue();
            final T value = entry.getKey();
            final int otherCount = counter.delta(value, entry.getValue());
            editor.set(value, Math.min(otherCount, count(value)));
        }
        return editor.removeValuesNotInCounter(counter).build();
    }

    private Editor editor()
    {
        return new Editor(map, occurrences);
    }

    private class Editor
    {
        private JImmutableMap<T, Integer> newMap;
        private int newOccurrences;

        private Editor(JImmutableMap<T, Integer> newMap,
                       int newOccurrences)
        {
            this.newMap = newMap;
            this.newOccurrences = newOccurrences;
        }

        private Editor remove(T value)
        {
            final Integer oldCount = newMap.get(value);
            if (oldCount != null) {
                newMap = newMap.delete(value);
                newOccurrences -= oldCount;
            }
            return this;
        }

        private Editor delta(T value,
                             int delta)
        {
            if (delta != 0) {
                final int oldCount = newMap.find(value).getValueOr(0);
                adjust(value, oldCount, oldCount + delta);
            }
            return this;
        }

        private Editor set(T value,
                           int newCount)
        {
            final int oldCount = newMap.find(value).getValueOr(0);
            if (newCount != oldCount) {
                adjust(value, oldCount, newCount);
            }
            return this;
        }

        private void adjust(T value,
                            int oldCount,
                            int newCount)
        {
            if (newCount <= 0) {
                newMap = newMap.delete(value);
                newOccurrences -= oldCount;
            } else {
                newMap = newMap.assign(value, newCount);
                newOccurrences = newOccurrences - oldCount + newCount;
            }
        }

        private Editor removeValuesNotInCounter(Counter<T> counter)
        {
            for (JImmutableMap.Entry<T, Integer> entry : newMap) {
                if (counter.get(entry.getKey()) == 0) {
                    adjust(entry.getKey(), entry.getValue(), 0);
                }
            }
            return this;
        }

        private JImmutableMultiset<T> build()
        {
            return (map == newMap) ? AbstractJImmutableMultiset.this : create(newMap, newOccurrences);
        }
    }

    protected static class Counter<T>
    {
        private final Map<T, Integer> counts;
        private int totalCount;

        public Counter(Map<T, Integer> counts)
        {
            assert counts.isEmpty();
            this.counts = counts;
        }

        protected int delta(T value,
                            int number)
        {
            assert value != null;
            final int current = get(value);
            return adjust(value, current, current + number);
        }

        protected int set(T value,
                          int modified)
        {
            assert value != null;
            assert modified >= 0;
            final int current = get(value);
            return adjust(value, current, modified);
        }

        protected int get(T value)
        {
            assert value != null;
            final Integer count = counts.get(value);
            return (count == null) ? 0 : count;
        }

        private int adjust(T value,
                           int current,
                           int modified)
        {
            if (modified <= 0) {
                modified = 0;
                counts.remove(value);
            } else {
                counts.put(value, modified);
            }
            totalCount = totalCount + modified - current;
            return modified;
        }
    }
}