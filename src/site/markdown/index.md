Overview
---

The JImmutable Collections library provides a useful set of immutable/persistent collection classes designed with performance and ease of integration in mind. These collections are intended to replace the java.util collection classes when you require the thread safety and other benefits that immutability provides.

Immutability and persistence are terms which people tend to interpret in different ways. The JImmutable collection classes are immutable in the sense that once once a given collection has been created it cannot be modified. This means that it can be safely shared throughout a program without the need for synchronization or defensive copying.

However the collections are designed to allow themselves to be easily modified as well. Each collection provides methods for adding and removing elements. Each of these methods creates a new collection of the same type while leaving the original collection intact (i.e. the original persists). The data structures used to implement the collections (linked lists, 2-3 trees, and integer tries) allow for almost all of the structure of the original collection to be shared by the new collection. Since all objects within each collection are immutable this sharing is completely safe. The collections are persistent in the functional programming sense. The collections are not persistent in the database sense. All contents are stored in memory at all times.

Each collection class provides adapter methods to create java.util style unmodifiable collections backed by the immutable collection. Unlike the Guava immutable collection classes these adapters do not create defensive copies of all elements from the original collections. They simply access elements within the original collection. If you have code that needs a java.util.Map to do its work you can still use a PersisentHashMap and simply call it's asMap() method when you need to pass a java.util.Map to your older code.

The library uses a Cursor class to allow iteration over the collection elements. Cursor is similar to Iterator but is immutable and allows for lazy evaluation. An adapter is provided to easily turn a Cursor into an Iterator for easier integration with standard java classes. All collections implement the Iterable interface so you can use them in foreach loops.

The library is designed to have no dependencies on other libraries but it should interact well with others. Standard java interfaces are used where appropriate. Class names were chosen so as not to conflict with Guava's immutable container class names.

Resources
---

- [Project Wiki](https://github.com/brianburton/java-immutable-collections/wiki)
- [Project Website](https://github.com/brianburton/java-immutable-collections)
- [Javadocs](./apidocs/index.html)

Factory Methods
---

Internally JImmutable Collections uses integer tries and 2-3 trees for the collection implementations but it strives to hide that fact from its users. There is no reason to directly create objects of the implementation classes. Your code will be cleaner and more future proof if you always create new collections using the factory methods in the JImmutables class.

Static methods in JImmutables can be used to create new instances of each collection interface. For example:

````
	import org.javimmutable.collections.util.JImmutables;

	...

	// create a new empty list
	JImmutableList<String> es = JImmutables.list();

	// create a new list containing all the same values as a java List
	JImmutableList<String> copied = JImmutables.list(Arrays.asList("a", "b", "c"));
````

The second example shows how to create a new list with starting values copied from another source. Variations of the list() method are defined to copy values from a Java array, a Cursor, a Cursorable (any JImmutable collection), a Java Iterator, or a Java Collection. When copying values from another source the list() method preserves their original order.

Equivalent constructor methods exist for creating JImmutableStacks (stack()), JImmutableRandomAccessLists (ralist()), unsorted JImmutableMaps (map()), and sorted JImmutableMaps (sortedMap()). (see [Collections Overview])

Important note about stack() - when copying from another source the objects in the source will be added to the stack in the same order they appear in the source. Because of the nature of stacks that means that when you remove values from the stack or iterate over them you will encounter them in the opposite order.

The versions of map() and sortedMap() methods that accept a JImmutableMap to copy from will detect when you are copying from a compatible map and in that case will just return the source map itself. This might happen when you want to produce a sorted map from a map passed to you and don't know the type of the map you are copying.

The sortedMap() method can create maps that sort keys based on their natural order (for keys that implement Comparable) but it can also create maps that sort keys based on a Comparator provided by the caller. When providing your own Comparator class be extremely careful to ensure that your Comparator is immutable and repeatable. The map will share your Comparator across all child instances of the map. If your Comparator is mutable and is somehow modified that could easily break the map and cause undefined behavior. Similarly if your Comparator can be affected by any outside entity that would change its comparisons of keys over time that also could break any maps that use it. If you haven't already done so be sure to read the section on Comparator in the JDK's javadoc and also the advice on how to write a well behaved Comparator in Effective Java.

Comparative Performance
---

Let's start off by stating the obvious.  Mutable collections are faster than immutable ones.  That's a basic fact of life.  Consider a hash table for example.  A mutable hash map can simply replace a value in an internal array in response to a put() call while an immutable one has to create a number of new objects to build a new version of the map reflecting the change.  So, yes, mutable collections are faster.

The real questions are: how much faster are mutable collections and will you really notice the difference.  Based on benchmark runs a JImmutableHashMap is about 2-3 times slower than a HashMap but is about 1.5x faster than a TreeMap.  Unless your application spends most of its time CPU bound updating collections you probably won't notice much of a difference using an immutable collection.

Here is a sample run using the org.javimmutable.collections.hash.TimingComparison benchmark program that comes with JImmutable Collections.  The program uses a random number generator to create sequences of puts, gets, and deletes.  The program can be tweaked in a variety of ways but the primary setting is the number of loops (operations) to perform.  The program repeats the same series of random operations on a TreeMap<Inetger, Integer>, a JImmutables.map(), and a JImmutables.array().  These test runs were performed on a MacBook Pro with heap settings -Xms384m -Xmx512m.

````
    // subset of results for 250k ops run using TreeMap averages include many other runs
    java map adds 74847 removes 24925 gets 150228 size 74814 elapsed 80
    jimm map adds 74847 removes 24925 gets 150228 size 74814 elapsed 57
    jimm ary adds 74847 removes 24925 gets 150228 size 74814 elapsed 50

    java map adds 75044 removes 24846 gets 150110 size 75013 elapsed 80
    jimm map adds 75044 removes 24846 gets 150110 size 75013 elapsed 55
    jimm ary adds 75044 removes 24846 gets 150110 size 75013 elapsed 50

    java map adds 75093 removes 24995 gets 149912 size 75050 elapsed 81
    jimm map adds 75093 removes 24995 gets 149912 size 75050 elapsed 55
    jimm ary adds 75093 removes 24995 gets 149912 size 75050 elapsed 51

    java avg: 79.6  hash avg: 55.6  array avg: 50.0
````

As you can see the TreeMap completed 250,000 operations in about 80 milliseconds on average winding up with a map containing 75k elements.  JImmutables.map() completed the same operations in 55 ms and JImmutables.array() in 50 ms.  Using the same program a HashMap averages around 21 ms.

Increasing the number of operations to 500k (double) the average times are 52 ms for HashMap, 200 ms for TreeMap, 128 ms for JImmutables.map() and 118 ms for JImmutables.array().  The resulting collections contain approximately 150k elements.  For the same run JImmutables.sortedMap() takes approximately 375 ms.

Cranking the number of operations up some more (1.5 million) we get these times: HashMap 262 ms, JImmutables.map() 530 ms, JImmutables.array() 476 ms.  The final collection contained 448k elements.  For these I used heap settings -Xms384m -Xmx768m.

````
    // subset of results for 1.5 million ops run using HashMap averages include many other runs
    java map adds 449849 removes 149741 gets 900410 size 448534 elapsed 269
    jimm map adds 449849 removes 149741 gets 900410 size 448534 elapsed 521
    jimm ary adds 449849 removes 149741 gets 900410 size 448534 elapsed 466

    java map adds 449889 removes 149428 gets 900683 size 448581 elapsed 267
    jimm map adds 449889 removes 149428 gets 900683 size 448581 elapsed 517
    jimm ary adds 449889 removes 149428 gets 900683 size 448581 elapsed 465

    java map adds 450106 removes 149794 gets 900100 size 448768 elapsed 265
    jimm map adds 450106 removes 149794 gets 900100 size 448768 elapsed 522
    jimm ary adds 450106 removes 149794 gets 900100 size 448768 elapsed 462

    java avg: 260.0  hash avg: 505.9  array avg: 456.2
````

From these test runs with maps it's clear that java.util.HashMap is wicked fast as would be expected but the fully immutable alternatives are still within a factor of 2-3 of it even for collections as large as 448k elements.  Saying something is 2-3 times slower than something else sounds bad but keep in mind that this test performed one and a half million operations, wound up with a collection of 448k elements, and the difference in execution time between the best mutable and immutable versions was only about 245 milliseconds!  In exchange for that quarter second your program would have all the benefits of immutability including:

- elimination of the need for locking or any potential for lock contention impacting performance
- elimination of the need for defensive copying or any potential for different parts of the program using the same map changing the data unexpectedly and breaking other parts of the program
- improvements in maintainability from ability to reason about how and when collections might change


If your program is CPU bound and works with enormous data structures it might require the use of mutable data structures.  However for most programs switching to fully immutable data structures would have no noticeable effect on performance but would provide major benefits in the design and maintainability of the system.

Collections
---

JImmutable Collections contains a variety of fully immutable collections designed to suit different needs in an application. This page describes the various collections (as of version 1.5) and their general performance characteristics. For each collection the summary lists the JImmutables factory method used to create an instance, the general algorithmic complexity of the collection, and a description of the collection's uses and characteristics.

The complexity is expressed in big-oh notation which means "on the order of" or "within some constant multiple of" the value in parentheses. logX(n) means log to the base X of the number of elements in the collection. To give an idea of scale log32(100000) is 3.3 while log2(100000) is 16.6. "Within some constant factor" means that there will be fixed costs associated with the implementation so one algorithm with a given big-oh complexity might be 2-3 times faster than another with the same complexity.

Generally speaking ArrayList will be much faster than JImmutableList since it simply updates an array but for most algorithms the difference will not be significant to overall run time. JImmutableMaps are generally comparable in performance (i.e. within acceptable limits for most algorithms) to java.util.Maps. See the [Comparative Performance] page for an idea of how the immutable maps and arrays compare to java.util.Maps.

Factory|Complexity|Description
---|---|---
JImmutables.array()|O(log32(n))|A sparse array allowing any integer (positive or negative) as index. Indexes do not need to be consecutive and the array allocates memory only for indexes actually inserted into the array. Implemented internally as an integer trie with 32-way branching. Cursors visit elements in order by index with negative indexes visited before positive indexes. No direct java.util analog but similar to a TreeMap but with better performance.
JImmutables.map()|O(log32(n))|A hash map using hashCode() and equals() methods of keys. Keys and values are stored in hash mapped array tries using linked lists or 2-3 trees for collision handling (two keys having same hash code). Performance scales roughly linearly with size of the collection since depth of the trie never exceeds 7 levels. Intended as a replacement for java.util.HashMap. Cursors visit elements in an unspecified order.
JImmutables.sortedMap()|O(log2(n))|A tree map using a Comparator object to sort and compare keys. Keys and values are stored in 2-3 trees. Intended as a replacement for java.util.TreeMap. Cursors visit elements in sort order of keys as specified by the Comparator.
JImmutables.insertOrderMap()|O(2 * log32(n))|A hash map using hashCode() and equals() methods of keys. Keys and values are stored in hash mapped array tries using linked lists or 2-3 trees for collision handling (two keys having same hash code). A second integer trie is also used to govern cursor order. Performance scales roughly linearly with size of the collection since depth of the trie never exceeds 7 levels. Perhaps as much as twice the overhead of a map() since it maintains two data structures internally. Intended as a replacement for java.util.LinkedHashMap. Cursors visit elements in the order they were originally inserted into the map.
JImmutables.stack()|O(1) inserts/deletes at head, O(n) searches|A singly linked linear list of elements maintained in reverse insertion order. Extremely fast inserts and deletes from the head but searches require looping through all elements to find a match. Does not support insertion or deletion inside the list. Useful as a stack or a fast temporary list of items where LIFO order is acceptable. Cursors visit elements in LIFO (last in first out) order.
JImmutables.list()|O(log32(n))|A "list" implemented internally as a 32-way tree. Allows elements to be inserted or removed only at either end of the list. Allows elements to be replaced anywhere within the list. Intended as a replacement for java.util.List. Cursors visit elements in order by index.
JImmutables.ralist()|O(log2(n))|A "list" implemented internally as a B-tree. Allows elements to be inserted, removed, and updated anywhere within the list. Intended as a replacement for java.util.List in algorithms that require insertion or removal from the middle of the list. Otherwise use list(). Cursors visit elements in order by index.
JImmutables.set()|O(log32(n))|A set implemented internally using a hash map. Keys are stored in hash mapped array tries using hashCode() and equals() methods to compare keys. Intended as a replacement for HashSet. Cursors visit elements in an unspecified order.
JImmutables.sortedSet()|O(log2(n))|A set implemented internally using a 2-3 tree. Keys are compared using a Comparator. Intended as a replacement for TreeSet. Cursors visit elements in sort order of keys as specified by the Comparator.
JImmutables.insertOrderSet()|O(2 * log32(n))|A set implemented internally using an integer trie for sort order and a hash mapped trie for searching. Performance scales roughly linearly with size of the collection since depth of the trie never exceeds 7 levels. Perhaps as much as twice the overhead of a set() since it maintains two data structures internally. Intended as a replacement for java.util.LinkedHashSet. Cursors visit elements in the order they were originally inserted into the map.
JImmutables.listMap()|O(log32(n))|A hash map mapping keys to JImmutableLists. Performance similar to JImmutables.map(). Cursors visit elements in an unspecified order.
JImmutables.sortedListMap()|O(log2(n))|A sorted map mapping keys to JImmutableLists. Performance similar to JImmutables.sortedMap(). Cursors visit elements in sort order of keys as specified by the Comparator.
JImmutables.insertOrderListMap()|O(2 * log32(n))|A sorted map mapping keys to JImmutableLists. Performance similar to JImmutables.insertOrderMap(). Cursors visit elements in the order they were originally inserted into the map.

Hash Keys
---

JImmutable Collections includes two basic types of maps ([Map Tutorial](https://github.com/brianburton/java-immutable-collections/wiki/Map-Tutorial)): sorted and unsorted.  A third type, insert order maps, use an unsorted map for their implementation internally so they won't be addressed here.  Sorted maps are implemented using balanced 2-3 trees.  Unsorted maps are implemented using hash array mapped tries (HAMT).

Most hash map implementations use an array sufficiently large to hold all of the elements in the map plus some extra capacity to allow for growth and minimize hash collisions.  These implementations compute an integer hash code for each key (in Java the hashCode() method is used for this purpose) and then take the modulus of this code and the size of the array to produce an index into the array.  Since hash codes are 32-bit integers the arrays themselves are tiny in comparison to the 4 billion potential hash codes.  When two different hash codes map to the same array index this is called a "hash collision".  Allowing extra space in the array and using a prime number for its size can help to reduce the number of collisions.

In contrast a HAMT is a shallow tree-like structure which maps every possible hash code to a unique location in the trie.  The trie's potential capacity is exactly equal to the number of possible hash codes so two hash codes can never "collide" due to limited capacity of the structure.  In that sense a HAMT is an ideal structure for hashing so it would seem that hash collisions would be impossible.

However there is another potential source of hash collisions.  Specifically a poorly implemented hash function can generate the same hash code for many keys.  An obvious example would be one that hashes strings by simply adding the UTF-32 integer values of each character.  This would obviously produce exactly the same hash code for the strings "ABCD", "DCBA", and "ADBC".

Since hash collisions are always possible hash map implementations have to adopt strategies for dealing with collisions.

**Collision Handling Strategies**

Versions of JImmutable Collections prior to 1.6 relied on the hash functions producing very few collisions.  Based on that simplifying assumption they adopted the simple strategy of using linked lists of key/value pairs to resolve collisions at any given location in the HAMT.  For example if 9 different keys stored in the hash map have exactly the same hash code the map would store all nine elements in a linked list at the HAMT node corresponding to that hash code.  Whenever the get() method is called for one of those keys the map would find the node with the appropriate hash code in the HAMT and then walk the linked list looking for a matching key.  Obviously this implementations performance would degrade in the presence of poor hashCode() methods that generate frequent collisions.

A second hash collision strategy has been available beginning with version 1.6.  This second strategy uses balanced 2-3 trees instead of linked lists at each node.  In the example above with 9 keys in the same HAMT node a linked list could require up to 9 list nodes to be visited searching for a key but with a 2-3 tree at most 3 tree nodes would have to be visited.  With 128 collisions in a single node the list would require visiting up to 128 list nodes but the 2-3 tree would require at most 7 tree nodes.

Obviously the 2-3 tree strategy offers superior performance.  However since it relies on the ability to keep keys sorted in the tree it can only be used for certain types of keys.  Specifically keys must implement the Comparable interface.  Since the various factory methods, JImmutables.map(), do not require that keys implement Comparable each map waits to decide which strategy to use until the first key is added to the map.  On the first call to insert() the map determines if the key is Comparable.  If it is the map uses the 2-3 tree strategy.  Otherwise it falls back on the linked list strategy.

**Best Practices for Unsorted Map Keys**

All of this background boils down to a few simple best practices to follow when deciding which objects to use as keys in your unsorted maps.

1. Ensure that all keys in the map are implementations of a single class.
2. Ensure the key class is immutable.
3. Ensure the key class has an excellent hashCode() method.
4. Ensure the key class `K` implements `Comparable<K>`.

Restricting your keys to a single class ensures that all of the keys will always be comparable to one another.  It also ensures that all keys either implement Comparable or do not.  For example if you have a class A which is not Comparable and a subclass B of A which is Comparable then an unsorted map will encounter exceptions if the first key inserted is of class B but a later key is of class A.  In that case the map will choose to use the 2-3 strategy when the B key is inserted but when the map tries to cast the A key to Comparable an exception will be thrown.  All keys in a given map **must** either implement Comparable or not implement Comparable.  You cannot have a mix of the two in a single map.  The easiest way to prevent this problem from happening is to use homogeneous hash keys.

Restricting your keys to be immutable is required not just in the JImmutable maps but also in the java.util maps.  If a key is mutable it would be possible for its hashCode() to change after the key has already been added to the map.  If that were to happen later attempts to delete or move the key could corrupt the map.

Having an excellent hashCode() will make it highly unlikely that any collisions will happen in a HAMT.  Integer is an obvious example of a class whose hash codes never collide.  (note: if your keys are Integers though you could use a JImmutableArray for better efficiency)  There is always a trade off of performance vs collision avoidance.  For example computing an MD5 digest and using the first 32 bits would probably yield a fantastic hash code but would be extremely slow.  Do some reading on how to implement efficient hashCode() methods.  The time expended will pay off in better performance for your program.

Restricting your keys to those classes which implement Comparable will allow the map to use a 2-3 tree for maximum performance if collisions do happen.  Imagine the worst case scenario of a hashCode() that always generates the same number.  In that case a map using the 2-3 tree strategy will have O(log(N)) performance while a map using linked list strategy will have O(N) performance.  So the small amount of time needed to add a well written compareTo() method can pay off in better performance if your hashCode() method proves to be less than stellar.

Of course the easiest strategy is to simply use one of Java's built in value types as keys.  String, Integer, Double, etc all implement Comparable and have good hashCode() implementations.

Tutorials
---

**Array Tutorial**

JImmutable Collections provides a sparse array implementation.  A sparse array is an immutable collection similar to a map except that:

- it implements JImmutableArray instead of JImmutableMap
- its keys are ints (not Integers) so no boxing/unboxing is needed for them
- its cursors iterate in sorted order by key using natural integer ordering (negative indexes then positive indexes)

Any valid 32-bit integer can be used as an index to a sparse array.  Like a map the array efficiently manages memory so an array with widely dispersed keys will use approximately the same amount of memory as one with contiguous keys.

Like all of the other jimmutable containers you should create sparse array instances using the static factory methods in the util.JImmutables class.  Using these methods instead of instantiating objects directly is preferred since it isolates the client from future changes in the underlying implementation.

JImmutableArrays are immutable.  The assign() and delete() methods leave the original array intact and return a new modified array containing the requested change.  The old and new arrays share almost all of their structure in common so very little copying is performed.

````
    JImmutableArray<Integer> array = JImmutables.array();
    array = array.assign(-50000, "able");
    array = array.assign(25000, "charlie");
    array = array.assign(0, "baker");
    assertEquals("baker", array.get(0));
````

The example creates an empty array and then assigns three values.  Notice that the indexes are not contiguous and that negative indexes are perfectly acceptable.  Arrays iterate over their values in order of their keys so for the sample array valuesCursor() would return the values in the order "able" then "baker" then "charlie".  The keysCursor() would return -50000 then 0 then 25000.  Cursors skip "missing" indexes.  The standard cursor() method returns JImmutableMap.Entry objects that contain both the index and the value for each entry in the array.

Since arrays are not contiguous there is no concept of "insertion", only assignment.  If you need a collection that manages indexes for you use a JImmutableList.  However if your algorithm provides a natural way to manage its own indexes a JImmutableArray might be a better option.

**List Tutorial**

JImmutable Collections provides two interfaces with signatures similar to java.util.List.  Both of these interfaces provide access to elements in the List using an integer index.  As with List valid indexes are always in the range zero through size() - 1.  JImmutable Collections uses assign() instead of set(), insert() instead of add(), and delete() instead of remove() so that when converting code from java.util.List to JImmutableList the compiler will find all of the places that you need to replace a simple method call with an assignment.

The simpler interface, JImmutableList, provides indexed access to elements within the list but restricts addition and removal of values to the end of the list.  Values added to the list are always stored in the same order that they were added.  Cursors also traverse the values in the same order as they were added to the list.  The current implementation uses a 32-way tree which provides O(log32(n)) performance for all operations. (see [Comparative Performance](https://github.com/brianburton/java-immutable-collections/wiki/Comparative-Performance))

As with all of the immutable collection classes any method that modifies the list actually creates a new list instance and returns it as the method's result.  This result can be assigned to the original list variable or to a new variable as needed.  The original, unmodified, version of the list remains in memory until garbage collected.  The lists reuse as much structure as possible so adding and removing elements requires very little copying and only a small amount of additional memory.

In the example below notice that changed's third value is now 45 and list's third value is still 30.  The two lists internally contain shared copies of the common values to minimize memory consumption and improve performance but as a user of the list you don't need to worry about that.

````
    JImmutableList<Integer> list = JImmutables.list();
    list = list.insert(10).insert(20).insert(30);
    assertEquals(10, list.get(0));
    assertEquals(20, list.get(1));
    assertEquals(30, list.get(2));

    JImmutableList<Integer> changed = list.deleteLast().insert(45);
    assertEquals(10, list.get(0));
    assertEquals(20, list.get(1));
    assertEquals(30, list.get(2));
    assertEquals(10, changed.get(0));
    assertEquals(20, changed.get(1));
    assertEquals(45, changed.get(2));
````

The JImmutableList interfaces provide a getList() method that returns an object implementing java.util.List that uses the original list as its data source.  The actual data is not copied so this method has very low overhead.  You can use this any time you need to pass a JImmutableList to code that needs a java.util.List. The resulting List is unmodifiable so set, remove, etc methods all throw UnsupportedOperationExceptions.

````
    assertEquals(Arrays.asList(10, 20, 30), list.getList());
    assertEquals(Arrays.asList(10, 20, 45), changed.getList());
````

The JImmutableRandomAccessList interface provides all of the same methods as JImmutableList but also allows insertion and removal of values from anywhere in the list.  The current implementation uses a 2-3 tree which provides O(log2(n)) performance for all operations.

The JImmutables.ralist() factory method can be used to create new JImmutableRandomAccessList.  In the example below a new JImmutableRandomAccessList is created and values are inserted to place its elements in the same order as list from the first example.

````
    JImmutableRandomAccessList<Integer> ralist = JImmutables.ralist();
    ralist = ralist.insert(30).insert(0, 20).insert(0, 10);
    assertEquals(10, ralist.get(0));
    assertEquals(20, ralist.get(1));
    assertEquals(30, ralist.get(2));
    JImmutableRandomAccessList<Integer> ralist2 = ralist;
    ralist2 = ralist2.delete(1).insert(1, 87);
    assertEquals(10, ralist.get(0));
    assertEquals(20, ralist.get(1));
    assertEquals(30, ralist.get(2));
    assertEquals(10, ralist2.get(0));
    assertEquals(87, ralist2.get(1));
    assertEquals(30, ralist2.get(2));
    assertEquals(Arrays.asList(10, 20, 30), ralist.getList());
    assertEquals(Arrays.asList(10, 87, 30), ralist2.getList());
````

**Map Tutorial**

The JImmutable library provides a JImmutableMap interface that is very similar to java.util.Map.  Like other immutable interfaces all of the methods that modify the map return a new map as their result.  The old and new maps share almost all of their structure in common to minimize memory and CPU overhead.  Two implementations are provided.  Nulls are not permitted as keys but can be used as values.  JImmutable Collections uses assign() instead of put() and delete() instead of remove() so that when converting code from java.util.Map to JImmutableMap the compiler will find all of the places that you need to replace a simple method call with an assignment.

JImmutables.map() uses a hash mapped integer trie to store its values.  This provides O(log32(n)) performance for all operations.  (see [Comparative Performance](https://github.com/brianburton/java-immutable-collections/wiki/Comparative-Performance))  Values within the map are stored in an ordering based on the hash codes of the keys so no guarantee is made about what order a Cursor will return entries. (see [Hash Keys](https://github.com/brianburton/java-immutable-collections/wiki/Hash-Keys) for advice on selecting keys for maps)

JImmutables.sortedMap() uses a 2-3 tree with a Comparator object to store its values.  This provides O(log2(n)) performance for all operations.  Values within the map are stored in sorted order based on the Comparator used by the tree.  Usually you will use objects which implement the Comparable interface as keys and when you do so the keys will be stored in their natural ordering.  When you need to use keys that do not implement Comparable or if you need to use a different ordering you can create the tree with your own Comparable class.  Care must be taken to write robust and correct implementations of Comparable to ensure the tree operates as expected.

````
    JImmutableMap<Integer, Integer> hmap = JImmutables.map();
    hmap = hmap.assign(10, 11).assign(20, 21).assign(30, 31).assign(20, 19);

    JImmutableMap<Integer, Integer> hmap2 = hmap.delete(20).assign(18,19);

    assertEquals(11, hmap.get(10));
    assertEquals(19, hmap.get(20));
    assertEquals(31, hmap.get(30));

    assertEquals(11, hmap2.get(10));
    assertEquals(19, hmap2.get(18));
    assertEquals(null, hmap2.get(20));
    assertEquals(31, hmap2.get(30));
````

The get() method operates in the same manner as java.util.Map.get().  If the map does not contain a value for the specified key null is returned.  Since JImmutableMaps allow nulls as values the get() method's result can be ambiguous.  JImmutableMap provides a find() method which is similar to get() but always returns a non-null Holder object that can be used to determine unambiguously whether or not a value was found matching the key.

````
    hmap2 = hmap2.assign(80, null);
    assertEquals(null, hmap2.get(20));
    assertEquals(true, hmap2.find(20).isEmpty());
    // hmap2.find(20).getValue() would throw since the Holder is empty

    assertEquals(null, hmap2.get(80));
    assertEquals(false, hmap2.find(80).isEmpty());
    assertEquals(null, hmap2.find(80).getValue());
````

JImmutableMap includes a getMap() method that returns an object implementing java.util.Map.  The returned Map is immutable (set, remove, etc throw UnsupportedOperationException) and uses the original JImmutableMap to access values.  getMap() has very low overhead since contents of the JImmutableMap are not copied when creating the Map.  Use this method when you want to share the JImmutableMap's contents with code that only understands java.util.Map.

Sorted maps work exactly the same way as hash maps but their cursors provide access to entries in sorted order based on their keys.  In the example below the keySet() and values() methods provide their contents sorted based on the order of the corresponding keys in the map.

````
    JImmutableMap<Integer, Integer> smap = JImmutables.sortedMap();
    smap = smap.assign(10, 80).assign(20, 21).assign(30, 31).assign(20, 19);
    assertEquals(Arrays.asList(10, 20, 30), new ArrayList<Integer>(smap.getMap().keySet()));
    assertEquals(Arrays.asList(80, 19, 31), new ArrayList<Integer>(smap.getMap().values()));
````
