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
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMultiset;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractJImmutableMultiset<T>
        implements JImmutableMultiset<T>
{
    private final JImmutableMap<T, Integer> map;
    private final int occurences;

    protected AbstractJImmutableMultiset(JImmutableMap<T, Integer> map,
                                         int occurences)
    {
        this.map = map;
        this.occurences = occurences;
    }

//    @Override
//    @Nonnull
//    public JImmutableMultiset<T> insert(@Nonnull T value)
//    {
//        JImmutableMap<T, Integer> newMap = increaseCount(value, 1);
//        return (newMap != map) ? create(newMap, 1 + occurences) : this;
//    }
//
//    @Nonnull
//    @Override
//    public JImmutableMultiset<T> insert(@Nonnull T value,
//                                        int count)
//    {
//        if (count < 0) {
//            throw new IllegalArgumentException();
//        } else if (count == 0) {
//            return this;
//        } else {
//            JImmutableMap<T, Integer> newMap = increaseCount(value, count);
//            return (newMap != map) ? create(newMap, count + occurences) : this;
//        }
//    }

    @Override
    public boolean contains(@Nullable T value)
    {
        return (value != null) && (getCountOr(value, 0) > 0);
    }

    @Override
    public boolean contains(@Nullable T value,
                            int count)
    {
        if (count <= 0) {
            throw new IllegalArgumentException();
        } else {
            return (value != null) && (getCountOr(value, 0) >= count);
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
            if (!(contains(other.next()))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAllOccurences(@Nonnull Cursorable<? extends T> other)
    {
        return containsAllOccurences(other.cursor());
    }

    @Override
    public boolean containsAllOccurences(@Nonnull Collection<? extends T> other)
    {
        return containsAllOccurences(other.iterator());
    }

    @Override
    public boolean containsAllOccurences(@Nonnull Cursor<? extends T> other)
    {
        return containsAllOccurences(other.iterator());
    }

    @Override
    public boolean containsAllOccurences(@Nonnull Iterator<? extends T> other)
    {
        JImmutableMap<T, Integer> checkMap = map;
        while (other.hasNext()) {
            final T value = other.next();
            if((value != null) && (checkMap.getValueOr(value, 0) > 0)) {
                //value cannot be null
                //value exists in checkMap with a count of 1 or greater
                checkMap = checkMap.assign(value, checkMap.get(value) - 1);
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAllOccurences(@Nonnull JImmutableMultiset<? extends T> values)
    {
        return containsAllOccurencesHelper(values);
    }

    private <T1 extends T> boolean containsAllOccurencesHelper(@Nonnull JImmutableMultiset<T1> values)
    {
        Cursor<JImmutableMap.Entry<T1, Integer>> e = values.entryCursor();
        for (e = e.start(); e.hasValue(); e = e.next()) {
            final T1 entryValue = e.getValue().getKey();
            final int entryCount = e.getValue().getValue();
            if (!this.contains(entryValue) || this.count(entryValue) != entryCount) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAllOccurences(@Nonnull JImmutableSet<? extends T> values)
    {
        for (T value : values) {
            if (!this.contains(value)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAllOccurences(@Nonnull Set<? extends T> values)
    {
        for (T value : values) {
            if (!this.contains(value)) {
                return false;
            }
        }
        return true;
    }


    @Override
    public boolean containsAny(@Nonnull Cursorable<? extends T> other)
    {
        return containsAny(other.cursor());
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
        JImmutableMap<T, Integer> newMap = map.delete(value);
        int newOccurences = occurences - this.count(value);
        return (newMap != map) ? create(newMap, newOccurences) : this;
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteOccurence(@Nonnull T value)
    {
        if (this.count(value) == 0) {
            return this;
        } else {
            JImmutableMap<T, Integer> newMap = decreaseCount(value, 1);
            return (newMap != map) ? create(newMap, occurences - 1) : this;
            //logic: if value is in map, then newMap will not equal map. Therefore, the total number of occurences
            //should decrease by one. If newMap does equal map, then value was not in map. The number of occurences
            //did not change, and this should be returned with no changes.
        }
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteOccurrence(@Nonnull T value,
                                                  int count)
    {
        if (count < 0) {
            throw new IllegalArgumentException();
        } else {
            JImmutableMap<T, Integer> newMap;
            int newOccurrences;
            if (count >= this.count(value)) {
                newMap = map.delete(value);
                newOccurrences = occurences - this.count(value);
            } else {
                newMap = decreaseCount(value, count);
                newOccurrences = occurences - count;
            }
            return (newMap != map) ? create(newMap, newOccurrences) : this;
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
        JImmutableMap<T, Integer> newMap = map;
        int newOccurences = occurences;
        while (other.hasNext()) {
            final T value = other.next();
            if ((value != null) && (newMap.getValueOr(value, 0) > 0)) {
                newMap = newMap.delete(value);
                newOccurences = newOccurences - this.count(value);
            }
        }
        return (newMap != map) ? create(newMap, newOccurences) : this;
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAllOccurences(@Nonnull Cursorable<? extends T> other)
    {
        return deleteAllOccurences(other.cursor());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAllOccurences(@Nonnull Collection<? extends T> other)
    {
        return deleteAllOccurences(other.iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAllOccurences(@Nonnull Cursor<? extends T> other)
    {
        return deleteAllOccurences(other.iterator());
    }

    @Override
    @Nonnull
    public JImmutableMultiset<T> deleteAllOccurences(@Nonnull Iterator<? extends T> other)
    {
        JImmutableMap<T, Integer> newMap = map;
        int newOccurences = occurences;
        while (other.hasNext()) {
            final T value = other.next();
            if ((value != null) && (newMap.getValueOr(value, 0) > 0)) {
                newMap = decrementCount(newMap, value);
                newOccurences = newOccurences - 1;
            }
        }
        return (newMap != map) ? create(newMap, newOccurences) : this;
    }


    @Override
    public int count(T value)
    {
        Conditions.stopNull(value);
        Holder<Integer> current = map.find(value);
        return current.isFilled() ? current.getValue() : 0;
    }

//    @Nonnull
//    @Override
//    public JImmutableMultiset<T> setCount(@Nonnull T value,
//                                          int count)
//    {
//        Conditions.stopNull(value, count);
//        return (count > 0) ? create(map.assign(value, count)) : create(map.delete(value));
//    }
//
//
//    @Nonnull
//    @Override
//    public JImmutableMultiset<T> insertAll(@Nonnull Cursorable<? extends T> values)
//    {
//        return insertAll(values.cursor());
//    }
//
//    @Nonnull
//    @Override
//    public JImmutableMultiset<T> insertAll(@Nonnull Collection<? extends T> values)
//    {
//        return insertAll(values.iterator());
//    }
//
//    @Nonnull
//    @Override
//    public JImmutableMultiset<T> insertAll(@Nonnull Cursor<? extends T> values)
//    {
//        return insertAll(values.iterator());
//    }
//
//    @Nonnull
//    @Override
//    public JImmutableMultiset<T> insertAll(@Nonnull Iterator<? extends T> values)
//    {
//        JImmutableMap<T, Integer> newMap = map;
//        while (values.hasNext()) {
//            final T value = values.next();
//            if (value != null) {
//                newMap = increaseCount(newMap, value);
//            }
//        }
//    }

    /**
     * Implemented by derived classes to create a new instance of the appropriate class.
     *
     * @param map
     * @return
     */
    protected abstract JImmutableMultiset<T> create(JImmutableMap<T, Integer> map,
                                                    int occurences);

//    private JImmutableMap<T, Integer> increaseCount(T value,
//                                                    int addBy)
//    {
//        return map.assign(value, addBy + count(value));
//    }

    //removes subtractBy from map. Checks whether it needs to decrement or delete value.
    //preconditions: none. If value isn't in map, then this will be returned (see map.delete())
    //caution: calling method must know whether value is contained in this or not, so that it
    //can appropriately decrement occurrences.
    //logic: subtractBy = 0 or value not in this -- returns this.
    private JImmutableMap<T, Integer> decreaseCount(T value,
                                                    int subtractBy)
    {
        if(subtractBy == 0) {
            return map;
        } else {
            int newCount = this.count(value) - subtractBy;
            return (newCount > 0) ? map.assign(value, newCount) : map.delete(value);
        }
    }

    //decrements the count of value by 1 in newMap. For use in deleteAll type methods.
    //precondition: value exists at least once in newMap
    //caution: calling method must appropriately decrement occurences.
    private JImmutableMap<T, Integer> decrementCount(JImmutableMap<T, Integer> newMap,
                                                     T value)
    {
        int newCount = newMap.get(value) - 1;
        return (newCount > 0) ? newMap.assign(value, newCount) : newMap.delete(value);
    }

    private Integer getCountOr(T value,
                               Integer or)
    {
        return map.getValueOr(value, or);
    }


}
