package org.javimmutable.collections.util;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.PersistentIndexedList;
import org.javimmutable.collections.PersistentStack;
import org.javimmutable.collections.PersistentRandomAccessList;
import org.javimmutable.collections.list.PersistentArrayList;
import org.javimmutable.collections.list.PersistentLinkedStack;
import org.javimmutable.collections.tree_list.PersistentTreeList;

import java.util.Iterator;

public final class Immutables
{
    public static <T> PersistentStack<T> stack()
    {
        return PersistentLinkedStack.of();
    }

    public static <T> PersistentStack<T> stack(Cursor<T> cursor)
    {
        return PersistentLinkedStack.of(cursor);
    }

    public static <T> PersistentIndexedList<T> list()
    {
        return PersistentArrayList.of();
    }

    public static <T> PersistentIndexedList<T> list(Cursor<T> cursor)
    {
        return Functions.addAll(PersistentArrayList.<T>of(), cursor);
    }

    public static <T> PersistentIndexedList<T> list(Iterator<T> iterator)
    {
        return Functions.addAll(PersistentArrayList.<T>of(), iterator);
    }

    public static <T> PersistentIndexedList<T> list(Iterable<T> iterable)
    {
        return Functions.addAll(PersistentArrayList.<T>of(), iterable.iterator());
    }

    public static <T> PersistentRandomAccessList<T> raList()
    {
        return PersistentTreeList.of();
    }

    public static <T> PersistentRandomAccessList raList(Cursor<T> cursor)
    {
        return Functions.addAll(PersistentTreeList.<T>of(), cursor);
    }

    public static <T> PersistentRandomAccessList<T> raList(Iterator<T> iterator)
    {
        return Functions.addAll(PersistentTreeList.<T>of(), iterator);
    }

    public static <T> PersistentRandomAccessList<T> raList(Iterable<T> iterable)
    {
        return Functions.addAll(PersistentTreeList.<T>of(), iterable.iterator());
    }
}
