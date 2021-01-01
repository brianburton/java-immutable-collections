# Immutable Collections For Java

Overview
---

The immutable collections for Java library (JImmutable Collections) is a bundle of high performance immutable
collections intended to replace or supplement the standard `java.util` collections. Functional replacements are provided
for each of the most commonly used collections:

Java Class | JImmutable Interface | Factory Method
--- | --- | ---
ArrayList | JImmutableList | `JImmutables.list()`
LinkedList | JImmutableList | `JImmutables.list()`
HashMap | JImmutableMap | `JImmutables.map()`
TreeMap | JImmutableMap | `JImmutables.sortedMap()` `JImmutables.sortedMap(Comparator)`
LinkedHashMap | JImmutableMap | `JImmutables.insertOrderMap()`
HashSet | JImmutableSet | `JImmutables.set()`
TreeSet | JImmutableSet | `JImmutables.sortedSet()` `JImmutables.sortedSet(Comparator)`
LinkedHashSet | JImmutableSet | `JImmutables.insertOrderSet()`

There are also a number of highly useful collections with no equivalent in the standard Java library.

Description | JImmutable Interface | Factory Method
--- | --- | ---
Map of lists of items related by a key. | JImmutableListMap | `JImmutables.listMap()` `JImmutables.sortedListMap()`  `JImmutables.sortedListMap(Comparator)`  `JImmutables.insertOrderListMap()`
Map of sets of items related by a key. | JImmutableSetMap | `JImmutables.setMap()` `JImmutables.sortedSetMap()`  `JImmutables.sortedSetMap(Comparator)`  `JImmutables.insertOrderSetMap()`
Set that tracks number of times any given element was added. | JImmutableMultiset | `JImmutables.multiset()`  `JImmutables.sortedMultiset()` `JImmutables.sortedMultiset(Comparator)` `JImmutables.insertOrderMultiset()`
Sparse array of elements indexed by an Integer. | JImmutableArray | `JImmutables.array()`
Stack implemented using a Lisp style head/tail list. | JImmutableStack | `JImmutables.stack()`

The collections support these standard Java features:

- All are fully `Serializable` to facilitate storing to disk or sending over a network (i.e. in an Apache Spark
  application)
- All allow creation of Streams (parallel or sequential) over their contents. Maps support streams over their keys and
  values separately or both at the same time.
- All are `Iterable`. Maps support iterators over their keys and values separately or both at the same time.
- Where appropriate they provide views that can be passed to code that requires a standard collection interface.  (
  e.g. `JImmutableMap` has a `getMap()` method to create a view that mplements `Map`)
- Most provide collectors for use with Streams to create new collections in `collect()` method call.
- Most provide efficient builder classes for constructing new collections in imperative fashion.

Immutability/Persistence
---

The collections are all [immutable](https://en.wikipedia.org/wiki/Immutable_object)
and [persistent](https://en.wikipedia.org/wiki/Persistent_data_structure). Any method that adds or removes an item in a
collection actually creates a new collection. The old and new collections share almost all of their structure in common
with only the minimum number of new objects needed to implement the change in the new version. The process of creating a
new collection from an old one is extremely fast.

Since the collections are immutable they can be safely shared throughout a program without the need for synchronization
or defensive copying. In fact structure sharing is a theme throughout the library. For example, you never actually
create an empty JImmutableList instance. The `JImmutables.list()` factory method always returns a single, shared, empty
list instance. The other factory methods work the same way.

The collections are still highly dynamic and fully support addition, deletion, and replacement of elements via efficient
creation of modified versions of themselves. This sets them apart from the static immutable collections in
the [Guava](https://github.com/google/guava) collections library.

**Note:** Keep in mind that while the JImmutables themselves are immutable the values you choose to store in them might
not be. Always [use immutable objects as keys](https://github.com/brianburton/java-immutable-collections/wiki/Hash-Keys)
and if you use mutable objects as values be aware that your code could mutate them between when you add them to a
JImmutable and when you retrieve them later.

Dependencies
---

The library is designed to have no dependencies on other libraries, but it should interact well with others. Standard
java interfaces are used where appropriate. Class names were chosen so as not to conflict with Guava's immutable
container class names or Hibernate's persistent container class names.

# Examples

The examples in this section highlight some features of the library. All of them use a static import of the factory
methods in the `JImmutables` utility class:

````
import static org.javimmutable.collections.util.JImmutables.*;
````

Factory Methods
---

The `JImmutables` class has static factory methods to make it easy to create new instances. Here are various ways to
create the same basic list. Similar factory methods exist for the other collections.

````
        List<String> sourceList = Arrays.asList("these", "are", "some", "strings");
        JImmutableList<String> empty = list();
        JImmutableList<String> aList = empty
            .insert("these")
            .insert("are")
            .insert("some")
            .insert("strings");
        JImmutableList<String> literal = list("these", "are", "some", "strings");
        JImmutableList<String> fromJavaList = list(sourceList);
        JImmutableList<String> fromBuilder = JImmutables.<String>listBuilder()
            .add("these")
            .add("are")
            .add("some", "strings")
            .build();
        assertThat(aList).isEqualTo(literal);
        assertThat(fromJavaList).isEqualTo(literal);
        assertThat(fromBuilder).isEqualTo(literal);
````

Iterators
---

The collections are all `Iterable` so they can be used in standard `for` loops.

````
        int eWordCount = 0;
        for (String word : fromBuilder) {
            if (word.contains("e")) {
                eWordCount += 1;
            }
        }
        assertThat(eWordCount).isEqualTo(3);
````

Streams and Collectors
---

Streams can be used along with the provided collector methods to easily create new collections. For example this
function creates a list of the integer factors (other than 1) of an integer.

````
    private JImmutableList<Integer> factorsOf(int number)
    {
        final int maxPossibleFactor = (int)Math.sqrt(number);
        return IntStream.range(2, maxPossibleFactor + 1).boxed()
            .filter(candidate -> number % candidate == 0)
            .collect(listCollector());
    }
````

This code creates a lookup table of all the factors of the first 1000 integers into a `JImmutableMap`.

````
    JImmutableMap<Integer, JImmutableList<Integer>> factorMap =
        IntStream.range(2, 1000).boxed()
            .map(i -> MapEntry.of(i, factorsOf(i)))
            .collect(mapCollector());
````

This code shows how the lookup table could be used to get a list of the prime numbers in the map:

````
        JImmutableList<Integer> primes = factorMap.stream()
            .filter(e -> e.getValue().isEmpty())
            .map(e -> e.getKey())
            .collect(listCollector());
````

Iteration
----

In addition to fully supporting Streams and Iterators the collections also provide their own iteration methods that
operate in a more functional style. For example the `forEach()` method takes a lamba and invokes it for each element of
the collection:

````
        JImmutableSet<Integer> numbers = IntStream.range(1,20).boxed().collect(setCollector());
        numbers.forEach(i -> System.out.println(i));
````

Methods are also provided to iterate over an entire collection to produce a new one by applying a predicate or
transformation. All of these operations can be done with Stream/map/filter/collect as well of course, but these
light-weight versions are faster when a single thread is sufficient for the job.

````
        JImmutableSet<Integer> numbers = IntStream.range(1, 20).boxed().collect(setCollector());
        JImmutableSet<Integer> changed = numbers.reject(i -> i % 3 != 2);
        assertThat(changed).isEqualTo(set(2, 5, 8, 11, 14, 17));
        
        changed = numbers.select(i -> i % 3 == 1);
        assertThat(changed).isEqualTo(set(1, 4, 7, 10, 13, 16, 19));
        
        JImmutableList<Integer> transformed = changed.collect(list());
        assertThat(transformed).isEqualTo(list(1, 4, 7, 10, 13, 16, 19));
````

Slicing and Dicing Lists
----

Lists allow elements (and even whole lists) to be added or deleted at any index. They also support grabbing sub-lists
from anywhere within themselves. This example shows how various sub-lists can be extracted from a list and then inserted
into the middle of another.

````
        JImmutableList<Integer> numbers = IntStream.range(1, 21).boxed().collect(listCollector());
        JImmutableList<Integer> changed = numbers.prefix(6);
        assertThat(changed).isEqualTo(list(1, 2, 3, 4, 5, 6));
        
        changed = numbers.suffix(16);
        assertThat(changed).isEqualTo(list(17, 18, 19, 20));
        
        changed = changed.insertAll(2, numbers.prefix(3).insertAllLast(numbers.middle(9,12)));
        assertThat(changed).isEqualTo(list(17,18,1,2,3,10,11,12,19,20));
````

Inserting entire lists will always reuse structure from both lists as much as possible. Likewise, removing sub-lists
from within a large list will produce a new list that shares most of its structure with the original list. This means
building a large list by successively appending other lists to it can be faster than inserting the individual values
into a builder.

Maps of Sets and Lists
---

The `JImmutableSetMap` makes it easy to index values or accumulate values related to a key. The `JImmutableListMap`
works similarly but accumulates lists of values by key so it can preserve the order in which they are added and track
duplicates.

The example below shows a trivial example of indexing a sequence of sentences by the words they contain.

````
        JImmutableList<String> source = list("Now is our time.",
                                             "Our moment has arrived.",
                                             "Shall we embrace immutable collections?",
                                             "Or tread in dangerous synchronized bogs forever?");
        JImmutableSetMap<String, String> index = source
            .stream()
            .flatMap(line -> Stream.of(line
                                           .toLowerCase()
                                           .replace(".", "")
                                           .replace("?", "")
                                           .split(" "))
                .map(word -> MapEntry.entry(word, line)))
            .collect(setMapCollector());
        assertThat(index.get("our")).isEqualTo(set("Now is our time.", "Our moment has arrived."));
````

These classes offer a variety of methods for adding elements individually or in groups as well as iterating over all the
values for a given key as well as over the entire collection.

````
        JImmutableListMap<String, Integer> index = JImmutables.<String, Integer>sortedListMap()
            .insert("c", 2)
            .insert("a", 1)
            .insert("d", 640)
            .insert("b", 3)
            .insert("d", 512)
            .insertAll("a", list(-4, 40, 18)); // could be any Iterable not just list
        // keys are sorted in the map
        assertThat(list(index.keys())).isEqualTo(list("a", "b", "c", "d"));
        // values appear in the list in order they are added
        assertThat(index.getList("a")).isEqualTo(list(1, -4, 40, 18));
        assertThat(index.getList("d")).isEqualTo(list(640, 512));
        assertThat(index.getList("x")).isEqualTo(list());
````

ConcurrentModificationException
---

Immutable collections never throw these. The example below is contrived, but it illustrates the problem of updating a
mutable collection while iterating over its contents. Since immutable collections are persistent you are always
modifying a different version of the collection, and the iterator doesn't become confused.

````
        assertThatThrownBy(() -> {
            Map<Integer, Integer> ints = IntStream.range(1, 11).boxed().collect(Collectors.toMap(i -> i, i -> i));
            for (Map.Entry<Integer, Integer> entry : ints.entrySet()) {
                ints.put(2 * entry.getKey(), 2 * entry.getValue());
            }
        }).isInstanceOf(ConcurrentModificationException.class);

        JImmutableMap<Integer, Integer> myMap = IntStream.range(1, 11).boxed().map(i -> MapEntry.of(i, i)).collect(mapCollector());
        for (JImmutableMap.Entry<Integer, Integer> entry : myMap) {
            myMap = myMap.assign(2 * entry.getKey(), 2 * entry.getValue());
        }
        assertThat(list(myMap.values())).isEqualTo(list(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 14, 16, 18, 20));
````

The static collector factory methods create collectors that add elements from the stream to an empty collection.
Instances of the collection classes also provide an instance method to create a collector based on that instance (rather
than an empty instance). This can be used with a Stream to add entries to the collection. The example below adds entries
to the map. Some keys update existing entries while others are new keys to be added to the collection.

````
        myMap = IntStream.range(1, 11).boxed().map(i -> MapEntry.of(i, i)).collect(mapCollector());  // uses empty map collector
        JImmutableMap<Integer, Integer> changed = myMap.stream()
            .map(entry -> MapEntry.of(5 + entry.getKey(), 10 + entry.getValue()))
            .collect(myMap.mapCollector());   // uses an instance based collector
        // 6-10 were updated, 11-15 were added
        assertThat(list(changed.keys())).isEqualTo(list(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
        assertThat(list(changed.values())).isEqualTo(list(1, 2, 3, 4, 5, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20));
        // original map is unchanged 
        assertThat(list(myMap.keys())).isEqualTo(list(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
````

# Resources

Wiki Pages
---

[JImmutables Factory Methods](https://github.com/brianburton/java-immutable-collections/wiki/JImmutables-Factory-Methods)  
[Collections Overview](https://github.com/brianburton/java-immutable-collections/wiki/Collections-Overview)  
[List Tutorial](https://github.com/brianburton/java-immutable-collections/wiki/List-Tutorial)  
[Map Tutorial](https://github.com/brianburton/java-immutable-collections/wiki/Map-Tutorial)  
[Array Tutorial](https://github.com/brianburton/java-immutable-collections/wiki/Array-Tutorial)  
[Streams and Lambdas](https://github.com/brianburton/java-immutable-collections/wiki/Streams-and-Lambdas)  
[Comparative Performance](https://github.com/brianburton/java-immutable-collections/wiki/Comparative-Performance)  
[Hash Keys](https://github.com/brianburton/java-immutable-collections/wiki/Hash-Keys)  
[Project Javadoc](http://brianburton.github.io/java-immutable-collections/apidocs/index.html)  
[Jackson Module for JSON Support](https://github.com/brianburton/javimmutable-jackson)


Project Status
---
All production releases undergo stress testing and pass all junit tests. Of course, you should evaluate the collections
for yourself and perform your own tests before deploying the collections to production systems.

All releases are uploaded to the [releases section](https://github.com/brianburton/java-immutable-collections/releases)
on GitHub and are also available via Maven
in [Maven Central](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.javimmutable%22%20AND%20a%3A%22javimmutable-collections%22)
. You can add JImmutable Collections to your Maven project by adding a dependency like this to your pom.xml. The maven
releases include source jars for easy reference in your IDE.

````
    <dependency>
        <groupId>org.javimmutable</groupId>
        <artifactId>javimmutable-collections</artifactId>
        <version>insert-desired-version</version>
    </dependency>
````

Project Members:
---

- [Brian Burton](https://github.com/brianburton) (admin)
- [Angela Burton](https://github.com/anjbur)
