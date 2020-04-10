package org.javimmutable.collections.common;

import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.SplitableIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractJImmutableSetUsingMap<T>
    extends AbstractJImmutableSet<T>
{
    protected final JImmutableMap<T, Boolean> map;

    public AbstractJImmutableSetUsingMap(@Nonnull JImmutableMap<T, Boolean> map)
    {
        this.map = map;
    }

    @Override
    @Nonnull
    public JImmutableSet<T> insert(@Nonnull T value)
    {
        final JImmutableMap<T, Boolean> newMap = map.assign(value, Boolean.TRUE);
        return (newMap != map) ? create(newMap) : this;
    }

    @Override
    public boolean contains(@Nullable T value)
    {
        return (value != null) && map.getValueOr(value, Boolean.FALSE);
    }

    @Nonnull
    @Override
    public JImmutableSet<T> delete(T value)
    {
        JImmutableMap<T, Boolean> newMap = map.delete(value);
        return (newMap != map) ? create(newMap) : this;
    }

    @Nonnull
    @Override
    public JImmutableSet<T> deleteAll(@Nonnull Iterator<? extends T> values)
    {
        JImmutableMap<T, Boolean> newMap = map;
        while (values.hasNext()) {
            final T value = values.next();
            if (value != null) {
                newMap = newMap.delete(value);
            }
        }
        return (newMap != map) ? create(newMap) : this;
    }

    @Nonnull
    @Override
    public JImmutableSet<T> union(@Nonnull Iterator<? extends T> values)
    {
        JImmutableMap<T, Boolean> newMap = map;
        while (values.hasNext()) {
            final T value = values.next();
            if (value != null) {
                newMap = newMap.assign(value, Boolean.TRUE);
            }
        }
        return (newMap != map) ? create(newMap) : this;
    }

    @Nonnull
    @Override
    public JImmutableSet<T> intersection(@Nonnull Iterator<? extends T> values)
    {
        if (isEmpty()) {
            return this;
        }

        if (!values.hasNext()) {
            return deleteAll();
        }

        Set<T> otherSet = emptyMutableSet();
        while (values.hasNext()) {
            final T value = values.next();
            if (value != null) {
                otherSet.add(value);
            }
        }

        JImmutableMap<T, Boolean> newMap = map;
        for (JImmutableMap.Entry<T, Boolean> entry : map) {
            if (!otherSet.contains(entry.getKey())) {
                newMap = newMap.delete(entry.getKey());
            }
        }

        return (newMap != map) ? create(newMap) : this;
    }

    @Nonnull
    @Override
    public JImmutableSet<T> intersection(@Nonnull Set<? extends T> other)
    {
        if (isEmpty()) {
            return this;
        } else if (other.isEmpty()) {
            return deleteAll();
        } else {
            JImmutableMap<T, Boolean> newMap = map;
            for (T value : map.keys()) {
                if (!other.contains(value)) {
                    newMap = newMap.delete(value);
                }
            }
            return (newMap != map) ? create(newMap) : this;
        }
    }

    @Override
    public int size()
    {
        return map.size();
    }

    @Override
    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    @Nonnull
    @Override
    public SplitableIterator<T> iterator()
    {
        return map.keys().iterator();
    }

    @Override
    public int getSpliteratorCharacteristics()
    {
        return map.keys().getSpliteratorCharacteristics();
    }

    @Override
    public void checkInvariants()
    {
        checkSetInvariants();
    }

    protected void checkSetInvariants()
    {
        map.checkInvariants();
        for (JImmutableMap.Entry<T, Boolean> entry : map) {
            if (!entry.getValue()) {
                throw new RuntimeException();
            }
        }
    }

    /**
     * Implemented by derived classes to create a new instance of the appropriate class.
     */
    protected abstract JImmutableSet<T> create(JImmutableMap<T, Boolean> map);
}
