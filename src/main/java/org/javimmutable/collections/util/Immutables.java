package org.javimmutable.collections.util;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.PersistentList;
import org.javimmutable.collections.PersistentMap;
import org.javimmutable.collections.PersistentRandomAccessList;
import org.javimmutable.collections.PersistentStack;
import org.javimmutable.collections.hash.PersistentHashMap;
import org.javimmutable.collections.list.PersistentArrayList;
import org.javimmutable.collections.list.PersistentLinkedStack;
import org.javimmutable.collections.tree.PersistentTreeMap;
import org.javimmutable.collections.tree_list.PersistentTreeList;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

public final class Immutables
{
    public static <T> PersistentStack<T> stack()
    {
        return PersistentLinkedStack.of();
    }

    public static <T> PersistentStack<T> stack(Cursor<T> cursor)
    {
        return Functions.addAll(PersistentLinkedStack.<T>of(), cursor);
    }

    public static <T> PersistentStack<T> stack(Cursorable<T> cursorable)
    {
        return Functions.addAll(PersistentLinkedStack.<T>of(), cursorable.cursor());
    }

    public static <T> PersistentStack<T> stack(Iterator<T> iterator)
    {
        return Functions.addAll(PersistentLinkedStack.<T>of(), iterator);
    }

    public static <T> PersistentStack<T> stack(Iterable<T> iterable)
    {
        return Functions.addAll(PersistentLinkedStack.<T>of(), iterable.iterator());
    }

    public static <T> PersistentList<T> list()
    {
        return PersistentArrayList.of();
    }

    public static <T> PersistentList<T> list(Cursor<T> cursor)
    {
        return Functions.addAll(PersistentArrayList.<T>of(), cursor);
    }

    public static <T> PersistentList<T> list(Cursorable<T> cursorable)
    {
        return Functions.addAll(PersistentArrayList.<T>of(), cursorable.cursor());
    }

    public static <T> PersistentList<T> list(Iterator<T> iterator)
    {
        return Functions.addAll(PersistentArrayList.<T>of(), iterator);
    }

    public static <T> PersistentList<T> list(Iterable<T> iterable)
    {
        return Functions.addAll(PersistentArrayList.<T>of(), iterable.iterator());
    }

    public static <T> PersistentRandomAccessList<T> ralist()
    {
        return PersistentTreeList.of();
    }

    public static <T> PersistentRandomAccessList ralist(Cursor<T> cursor)
    {
        return Functions.addAll(PersistentTreeList.<T>of(), cursor);
    }

    public static <T> PersistentRandomAccessList ralist(Cursorable<T> cursorable)
    {
        return Functions.addAll(PersistentTreeList.<T>of(), cursorable.cursor());
    }

    public static <T> PersistentRandomAccessList<T> ralist(Iterator<T> iterator)
    {
        return Functions.addAll(PersistentTreeList.<T>of(), iterator);
    }

    public static <T> PersistentRandomAccessList<T> ralist(Iterable<T> iterable)
    {
        return Functions.addAll(PersistentTreeList.<T>of(), iterable.iterator());
    }

    public static <K, V> PersistentMap<K, V> hashMap()
    {
        return PersistentHashMap.of();
    }

    public static <K, V> PersistentMap<K, V> hashMap(Map<K, V> map)
    {
        return Functions.setAll(PersistentHashMap.<K, V>of(), map);
    }

    public static <K, V> PersistentMap<K, V> hashMap(PersistentMap<K, V> map)
    {
        return Functions.setAll(PersistentHashMap.<K, V>of(), map);
    }

    public static <K extends Comparable<K>, V> PersistentMap<K, V> treeMap()
    {
        return PersistentTreeMap.of();
    }

    public static <K extends Comparable<K>, V> PersistentMap<K, V> treeMap(Map<K, V> map)
    {
        return Functions.setAll(PersistentTreeMap.<K, V>of(), map);
    }

    public static <K extends Comparable<K>, V> PersistentMap<K, V> treeMap(PersistentMap<K, V> map)
    {
        return Functions.setAll(PersistentTreeMap.<K, V>of(), map);
    }

    public static <K, V> PersistentMap<K, V> treeMap(Comparator<K> comparator)
    {
        return PersistentTreeMap.of(comparator);
    }

    public static <K, V> PersistentMap<K, V> treeMap(Comparator<K> comparator,
                                                     Map<K, V> map)
    {
        return Functions.setAll(PersistentTreeMap.<K, V>of(comparator), map);
    }

    public static <K, V> PersistentMap<K, V> treeMap(Comparator<K> comparator,
                                                     PersistentMap<K, V> map)
    {
        return Functions.setAll(PersistentTreeMap.<K, V>of(comparator), map);
    }
}
