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

package org.javimmutable.collections.array.trie32;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.array.bit32.Bit32Array;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.MultiTransformCursor;

/**
 * Similar to Trie32Array but uses caller provided transformation objects to manage the leaf values.
 * Abstracting the leaf operations into the Transforms object allows different hash implementations
 * to use different types of leaf classes.  Also it moves the added complexity of hash collision
 * detection and management out of this class and into the class that defines the transforms.
 * <p/>
 * The root is a Bit32Array 6 levels deep.  At all levels except the bottom level the values
 * stored in the arrays are other Bit32Arrays.  At the bottom level (leaf level) the values
 * are objects managed by the transforms object.  Its methods provide the ability to
 * create and replace these objects, pull cursors and values from them, etc.
 */
public class Trie32HashTable<K, V>
        implements Cursorable<JImmutableMap.Entry<K, V>>
{
    private static final Bit32Array<Object> EMPTY_ARRAY = Bit32Array.of();

    private final Transforms<K, V> transforms;
    private final Bit32Array<Object> root;
    private final int size;

    /**
     * Interface for transformation objects that manage the leaf nodes in the hash table.
     * Implementations are free to use any class for their leaf nodes and manage them as needed.
     * NOTE: The transforms object is shared across all versions of a hash table so it MUST BE IMMUTABLE.
     * If the transforms object is not immutable it can cause the table to become
     * corrupted over time and/or make older versions of the table invalid.
     *
     * @param <K>
     * @param <V>
     */
    public static interface Transforms<K, V>
    {
        /**
         * Take the current leaf object (if there is one) and produce a new one
         * (possibly the same) with the specified key and value.  If there is not currently
         * a leaf for this key in the array the Holder will be empty.  The result must be
         * a non-null leaf object with the specified value associated with the specified key.
         * If this key was not previously present the method must add 1 to the delta
         * so that the size of the array can be properly maintained.
         */
        Object update(Holder<Object> leaf,
                      K key,
                      V value,
                      MutableDelta delta);

        /**
         * Take the current leaf object and produce a new one (possibly the same)
         * with the specified key removed.  If the key was previously present in the leaf
         * the method must subtract 1 from the delta so that the size of the array can be
         * properly maintained.
         */
        Holder<Object> delete(Object leaf,
                              K key,
                              MutableDelta delta);

        /**
         * Look for the specified key in the leaf object and return a Holder
         * that is empty if the key is not in the leaf or else contains the value associated
         * with the key.
         */
        Holder<V> findValue(Object leaf,
                            K key);

        /**
         * Look for the specified key in the leaf object and return a Holder
         * that is empty if the key is not in the leaf or else contains a JImmutableMap.Entry
         * associated with the key and value.
         */
        Holder<JImmutableMap.Entry<K, V>> findEntry(Object leaf,
                                                    K key);

        /**
         * Return a (possibly empty) Cursor over all of the JImmutableMap.Entries
         * in the specified leaf object.
         */
        Cursor<JImmutableMap.Entry<K, V>> cursor(Object leaf);
    }

    private Trie32HashTable(Transforms<K, V> transforms,
                            Bit32Array<Object> root,
                            int size)
    {
        this.transforms = transforms;
        this.root = root;
        this.size = size;
    }

    public static <K, V> Trie32HashTable<K, V> of(Transforms<K, V> transforms)
    {
        return new Trie32HashTable<K, V>(transforms, Bit32Array.of(), 0);
    }

    public Trie32HashTable<K, V> assign(int index,
                                        K key,
                                        V value)
    {
        final MutableDelta delta = new MutableDelta();
        final Bit32Array<Object> newRoot = assign(root, index, 30, key, value, delta);
        return (newRoot == root) ? this : new Trie32HashTable<K, V>(transforms, newRoot, size + delta.getValue());
    }

    public Trie32HashTable<K, V> delete(int index,
                                        K key)
    {
        final MutableDelta delta = new MutableDelta();
        final Bit32Array<Object> newRoot = delete(root, index, 30, key, delta);
        return (newRoot == root) ? this : new Trie32HashTable<K, V>(transforms, newRoot, size + delta.getValue());
    }

    public V getValueOr(int index,
                        K key,
                        V defaultValue)
    {
        final Holder<Object> entry = find(root, index, 30);
        if (entry.isEmpty()) {
            return defaultValue;
        } else {
            return transforms.findValue(entry.getValue(), key).getValueOr(defaultValue);
        }
    }

    public Holder<V> findValue(int index,
                               K key)
    {
        final Holder<Object> entry = find(root, index, 30);
        if (entry.isEmpty()) {
            return Holders.of();
        } else {
            return transforms.findValue(entry.getValue(), key);
        }
    }

    public Holder<JImmutableMap.Entry<K, V>> findEntry(int index,
                                                       K key)
    {
        final Holder<Object> entry = find(root, index, 30);
        if (entry.isEmpty()) {
            return Holders.of();
        } else {
            return transforms.findEntry(entry.getValue(), key);
        }
    }

    public int size()
    {
        return this.size;
    }

    public Cursor<JImmutableMap.Entry<K, V>> cursor()
    {
        return MultiTransformCursor.of(root.valuesCursor(), new CursorTransforminator(30));
    }

    public Transforms<K, V> getTransforms()
    {
        return transforms;
    }

    @SuppressWarnings("unchecked")
    private Holder<Object> find(Bit32Array<Object> array,
                                int index,
                                int shift)
    {
        final int childIndex = (index >>> shift) & 0x1f;
        if (shift == 0) {
            // child contains key/value pairs
            return array.find(childIndex);
        } else {
            // child contains next level of arrays
            final Bit32Array<Object> childArray = (Bit32Array<Object>)array.getValueOr(childIndex, EMPTY_ARRAY);
            return find(childArray, index, shift - 5);
        }
    }

    @SuppressWarnings("unchecked")
    private Bit32Array<Object> assign(Bit32Array<Object> array,
                                      int index,
                                      int shift,
                                      K key,
                                      V value,
                                      MutableDelta delta)
    {
        final int childIndex = (index >>> shift) & 0x1f;
        if (shift == 0) {
            // child contains key/value pairs
            return array.assign(childIndex, transforms.update(array.find(childIndex), key, value, delta));
        } else {
            // child contains next level of arrays
            final Bit32Array<Object> oldChildArray = (Bit32Array<Object>)array.getValueOr(childIndex, EMPTY_ARRAY);
            final Bit32Array<Object> newChildArray = assign(oldChildArray, index, shift - 5, key, value, delta);
            return (oldChildArray == newChildArray) ? array : array.assign(childIndex, newChildArray);
        }
    }

    @SuppressWarnings("unchecked")
    private Bit32Array<Object> delete(Bit32Array<Object> array,
                                      int index,
                                      int shift,
                                      K key,
                                      MutableDelta delta)
    {
        final int childIndex = (index >>> shift) & 0x1f;
        if (shift == 0) {
            // child contains key/value pairs
            final Holder<Object> oldLeaf = array.find(childIndex);
            if (oldLeaf.isEmpty()) {
                return array;
            } else {
                final Holder<Object> newLeaf = transforms.delete(oldLeaf.getValue(), key, delta);
                return newLeaf.isEmpty() ? array.delete(childIndex) : array.assign(childIndex, newLeaf.getValue());
            }
        } else {
            // child contains next level of arrays
            final Bit32Array<Object> oldChildArray = (Bit32Array<Object>)array.getValueOr(childIndex, EMPTY_ARRAY);
            final Bit32Array<Object> newChildArray = delete(oldChildArray, index, shift - 5, key, delta);
            if (oldChildArray == newChildArray) {
                return array;
            } else if (newChildArray.size() == 0) {
                return array.delete(childIndex);
            } else {
                return array.assign(childIndex, newChildArray);
            }
        }
    }

    /**
     * Transforminator (BEHOLD!!) that takes a Cursor of array (if shift > 0) or leaf (if shift == 0)
     * objects and returns a Cursor of the JImmutableMap.Entries stored in the children (if shift > 0)
     * or in the leaves (if shift == 0).
     */
    private class CursorTransforminator
            implements Func1<Object, Cursor<JImmutableMap.Entry<K, V>>>
    {
        private final int shift;

        private CursorTransforminator(int shift)
        {
            this.shift = shift;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Cursor<JImmutableMap.Entry<K, V>> apply(Object arrayValue)
        {
            if (shift == 0) {
                // the leaf arrays contain value objects as values
                return transforms.cursor(arrayValue);
            } else {
                // the internal arrays contain other arrays as values
                final Bit32Array<Object> array = (Bit32Array<Object>)arrayValue;
                return MultiTransformCursor.of(array.valuesCursor(), new CursorTransforminator(shift - 5));
            }
        }
    }
}
