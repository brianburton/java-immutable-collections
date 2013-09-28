package org.javimmutable.collections.common;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.PersistentMap;

import java.util.Map;
import java.util.Set;

public abstract class AbstractPersistentMap<K, V>
        implements PersistentMap<K, V>
{
    @Override
    public int hashCode()
    {
        return asMap().hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this) {
            return true;
        } else if (o instanceof PersistentMap) {
            return asMap().equals(((PersistentMap)o).asMap());
        } else {
            return (o instanceof Map) && asMap().equals(o);
        }
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Cursor<Entry<K, V>> cursor = cursor().next(); cursor.hasValue(); cursor = cursor.next()) {
            if (sb.length() > 1) {
                sb.append(",");
            }
            PersistentMap.Entry<K, V> entry = cursor.getValue();
            sb.append("(");
            sb.append(entry.getKey());
            sb.append(" -> ");
            sb.append(entry.getValue());
            sb.append(")");
        }
        sb.append("]");
        return sb.toString();
    }

    public static <T> boolean areEqual(Set<T> a,
                                       Set<T> b)
    {
        if (a == null) {
            return b == null;
        } else if (b == null) {
            return false;
        }
        if (a.size() != b.size()) {
            return false;
        }
        for (T value : a) {
            if (!b.contains(value)) {
                return false;
            }
        }
        return true;
    }
}
