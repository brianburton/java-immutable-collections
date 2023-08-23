# Immutable Collections For Java

Overview
---

The immutable collections for Java library (JImmutable Collections) is a bundle of high performance immutable
collections intended to replace or supplement the standard `java.util` collections. Functional replacements are provided
for each of the most commonly used collections:

| Java Class    | JImmutable Interface | Factory Methods                             |
|---------------|----------------------|---------------------------------------------|
| ArrayList     | IList                | `ILists.of()`, `ILists.allOf()`             |
| LinkedList    | IList                | `ILists.of()`, `ILists.allOf()`             |
| HashMap       | IMap                 | `IMaps.hashed()`                            |
| TreeMap       | IMap                 | `IMaps.sorted()` `IMaps.sorted(Comparator)` |
| LinkedHashMap | IMap                 | `IMaps.ordered()`                           |
| HashSet       | ISet                 | `ISets.hashed()`                            |
| TreeSet       | ISet                 | `ISets.sorted()` `ISets.sorted(Comparator)` |
| LinkedHashSet | ISet                 | `ISets.ordered()`                           |

There are also a number of highly useful collections with no equivalent in the standard Java library.

| Description                                                  | JImmutable Interface | Factory Method                                                                                      |
|--------------------------------------------------------------|----------------------|-----------------------------------------------------------------------------------------------------|
| Similar to a list but only add and delete at front or back.  | IDeque               | `IDeques.of()` `IDeques.allOf()`                                                                    |
| Map of lists of items related by a key.                      | IListMap             | `IListMaps.hashed()` `IListMaps.sorted()`  `IListMaps.sorted(Comparator)`  `IListMaps.ordered()`    |
| Map of sets of items related by a key.                       | ISetMap              | `ISetMaps.hashed()` `ISetMaps.sorted()`  `ISetMaps.sorted(Comparator)`  `ISetMaps.ordered()`        |
| Set that tracks number of times any given element was added. | IMultiset            | `IMultisets.hashed()`  `IMultisets.sorted()` `IMultisets.sorted(Comparator)` `IMultisets.ordered()` |
| Sparse array of elements indexed by an Integer.              | IArray               | `IArrays.of()` `IArrays.allOf()`                                                                    |

The collections support these standard Java features:

- All are fully `Serializable` to facilitate storing to disk or sending over a network (i.e. in an Apache Spark
  application)
- All allow creation of Streams (parallel or sequential) over their contents. Maps support streams over their keys and
  values separately or both at the same time.
- All are `Iterable`. Maps support iterators over their keys and values separately or both at the same time.
- Where appropriate they provide views that can be passed to code that requires a standard collection interface.  (
  e.g. `IMap` has a `getMap()` method to create a view that implements `Map`)
- Most provide collectors for use with Streams to create new collections in `collect()` method call. (see `ICollectors`)
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
create an empty IList instance. The `ILists.of()` factory method always returns a single, shared, empty
list instance. The other factory methods work the same way.

The collections are still highly dynamic and fully support addition, deletion, and replacement of elements via efficient
creation of modified versions of themselves. This sets them apart from the static immutable collections in
the [Guava](https://github.com/google/guava) collections library.

**Note:** Keep in mind that while the collections themselves are immutable the values you choose to store in them might
not be. Always [use immutable objects as keys](https://github.com/brianburton/java-immutable-collections/wiki/Hash-Keys)
and if you use mutable objects as values be aware that your code could mutate them between when you add them to a
collection and when you retrieve them later.

Dependencies
---

The library is designed to have no dependencies on other libraries, but it should interact well with others. Standard
java interfaces are used where appropriate.

# Examples

The examples in this section highlight some features of the library. All of them use a static import of the factory
methods in the `JImmutables` utility class:

````
import static org.javimmutable.collections.util.JImmutables.*;
````

Factory Methods
---

Static factory methods make it easy to create new collections. Here are various ways to
create the same basic list. Similar factory methods exist for the other collections.

````
        List<String> sourceList = Arrays.asList("these", "are", "some", "strings");
        IList<String> empty = ILists.of();
        IList<String> aList = empty
            .insert("these")
            .insert("are")
            .insert("some")
            .insert("strings");
        IList<String> literal = ILists.of("these", "are", "some", "strings");
        IList<String> fromJavaList = ILists.allOf(sourceList);
        IList<String> fromBuilder = ILists.<String>builder()
            .add("these")
            .add("are")
            .addAll("some", "strings")
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
    private IList<Integer> factorsOf(int number)
    {
        final int maxPossibleFactor = (int)Math.sqrt(number);
        return IntStream.range(2, maxPossibleFactor + 1).boxed()
            .filter(candidate -> number % candidate == 0)
            .collect(ICollectors.toList());
    }
````

This code creates a lookup table of all the factors of the first 1000 integers into an `IMap`.

````
        IMap<Integer, IList<Integer>> factorMap =
            IntStream.range(2, 100).boxed()
                .map(i -> IMapEntry.of(i, factorsOf(i)))
                .collect(ICollectors.toMap());
````

This code shows how the lookup table could be used to get a list of the prime numbers in the map:

````
        IList<Integer> primes = factorMap.stream()
            .filter(e -> e.getValue().isEmpty())
            .map(e -> e.getKey())
            .collect(ICollectors.toList());
        assertThat(primes)
            .isEqualTo(ILists.of(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 
                                 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97));
````

Iteration
----

In addition to fully supporting Streams and Iterators the collections also provide their own iteration methods that
operate in a more functional style. For example the `forEach()` method takes a lambda and invokes it for each element of
the collection:

````
        ISet<Integer> numbers = IntStream.range(1, 20).boxed().collect(ICollectors.toSet());
        numbers.forEach(i -> System.out.println(i));
````

Methods are also provided to iterate over an entire collection to produce a new one by applying a predicate or
transformation. All of these operations can be done with Stream/map/filter/collect as well of course, but these
light-weight versions are faster when a single thread is sufficient for the job.

````
        ISet<Integer> numbers = IntStream.range(1, 20).boxed().collect(ICollectors.toSet());
        ISet<Integer> changed = numbers.reject(i -> i % 3 != 2);
        assertThat(changed).isEqualTo(ISets.hashed(2, 5, 8, 11, 14, 17));
        
        changed = numbers.select(i -> i % 3 == 1);
        assertThat(changed).isEqualTo(ISets.hashed(1, 4, 7, 10, 13, 16, 19));

        IDeque<Integer> transformed = changed.stream().collect(ICollectors.toDeque());
        assertThat(transformed).isEqualTo(IDeques.of(1, 4, 7, 10, 13, 16, 19));
````

Slicing and Dicing Lists
----

Lists allow elements (and even whole lists) to be added or deleted at any index. They also support grabbing sub-lists
from anywhere within themselves. This example shows how various sub-lists can be extracted from a list and then inserted
into the middle of another.

````
        IList<Integer> numbers = IntStream.range(1, 21).boxed().collect(ICollectors.toList());
        IList<Integer> changed = numbers.prefix(6);
        assertThat(changed).isEqualTo(ILists.of(1, 2, 3, 4, 5, 6));
        
        changed = numbers.suffix(16);
        assertThat(changed).isEqualTo(ILists.of(17, 18, 19, 20));
        
        changed = changed.insertAll(2, numbers.prefix(3).insertAllLast(numbers.middle(9, 12)));
        assertThat(changed).isEqualTo(ILists.of(17, 18, 1, 2, 3, 10, 11, 12, 19, 20));
````

Inserting entire lists will always reuse structure from both lists as much as possible. Likewise, removing sub-lists
from within a large list will produce a new list that shares most of its structure with the original list. This means
building a large list by successively appending other lists to it can be faster than inserting the individual values
into a builder.

Maps of Sets and Lists
---

The `ISetMap` makes it easy to index values or accumulate values related to a key. The `IListMap`
works similarly but accumulates lists of values by key so it can preserve the order in which they are added and track
duplicates.

The example below shows a trivial example of indexing a sequence of sentences by the words they contain.

````
        IList<String> source = ILists.of("Now is our time.",
                                         "Our moment has arrived.",
                                         "Shall we embrace immutable collections?",
                                         "Or tread in dangerous synchronized waters forever?");
        ISetMap<String, String> index = source
            .stream()
            .flatMap(line -> Stream.of(line
                                           .toLowerCase()
                                           .replace(".", "")
                                           .replace("?", "")
                                           .split(" "))
                .map(word -> MapEntry.entry(word, line)))
            .collect(ICollectors.toSetMap());
        assertThat(index.get("our")).isEqualTo(ISets.hashed("Now is our time.", "Our moment has arrived."));
````

These classes offer a variety of methods for adding elements individually or in groups as well as iterating over all the
values for a given key as well as over the entire collection.

````
        IListMap<String, Integer> index = IListMaps.<String, Integer>sorted()
            .insert("c", 2)
            .insert("a", 1)
            .insert("d", 640)
            .insert("b", 3)
            .insert("d", 512)
            .insertAll("a", ILists.of(-4, 40, 18)); // could be any Iterable not just list
        // keys are sorted in the map
        assertThat(ILists.allOf(index.keys())).isEqualTo(ILists.of("a", "b", "c", "d"));
        // values appear in the list in order they are added
        assertThat(index.getList("a")).isEqualTo(ILists.of(1, -4, 40, 18));
        assertThat(index.getList("d")).isEqualTo(ILists.of(640, 512));
        assertThat(index.getList("x")).isEqualTo(ILists.of());
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

        IMap<Integer, Integer> myMap = IntStream.range(1, 11).boxed().map(i -> IMapEntry.of(i, i)).collect(ICollectors.toMap());
        for (IMapEntry<Integer, Integer> entry : myMap) {
            myMap = myMap.assign(2 * entry.getKey(), 2 * entry.getValue());
        }
        assertThat(ILists.allOf(myMap.keys())).isEqualTo(ILists.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 14, 16, 18, 20));
        assertThat(ILists.allOf(myMap.values())).isEqualTo(ILists.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 14, 16, 18, 20));
````

The static collector factory methods create collectors that add elements from the stream to an empty collection.
Instances of the collection classes also provide an instance method to create a collector based on that instance (rather
than an empty instance). This can be used with a Stream to add entries to the collection. The example below adds entries
to the map. Some keys update existing entries while others are new keys to be added to the collection.

````
        myMap = IntStream.range(1, 11).boxed().map(i -> IMapEntry.of(i, i)).collect(ICollectors.toMap());
        IMap<Integer, Integer> changed = myMap.stream()
            .map(entry -> IMapEntry.of(5 + entry.getKey(), 10 + entry.getValue()))
            .collect(myMap.mapCollector());
        // 6-10 were updated, 11-15 were added
        assertThat(ILists.allOf(changed.keys())).isEqualTo(ILists.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
        assertThat(ILists.allOf(changed.values())).isEqualTo(ILists.of(1, 2, 3, 4, 5, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20));
        // original map is unchanged 
        assertThat(ILists.allOf(myMap.keys())).isEqualTo(ILists.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
````

Maybe - Avoiding null
---

The use of null has been controversial. The JImmutable collections are mostly indifferent to null. Nulls are not
permitted as keys to maps or values in sets. However, they can be stored as values in lists and maps. Holders returned
by the `find()` method permit nulls as well.

There are many disadvantages to nulls though. In particular, they cannot be used in call chains. The `Maybe` class
provides an alternative to null that can be easily chained in a functional style.  `Maybe` is similar to `Holder` but
does not allow nulls and provides more monadic functionality. It is meant to be used in sequences of method calls.

There are two possible states for a `Maybe` object:

- `None` indicates no value is stored within the `Maybe`. The `unsafeGet` methods cannot be called on these objects but
  all others can be called safely.
- `Some` indicates a non-null value is stored within the `Maybe`. All methods can be called on these objects.

Maybe should be used when a value might not exist. For example as the result of a database query for a single object.
Once you have a Maybe value you can call the `map` method with a lambda that transforms the value (if one exists). The
transformed value can be of the same or another type. If your lambda returns another Maybe you should use the
`flatMap` method to "unwrap" the resulting value.

````
// simplified class for illustration - normally you'd use getters
class Person
{
  final String emailAddress;
  final Maybe<PhoneNumber> homePhone;
  final Maybe<PhoneNumber> mobilePhone;
}

Maybe<Person> customer = customers.lookupCustomerByName("Jones", "Patrick");
Maybe<String> email = customer.map(c -> c.emailAddress);

// get the area code from the home phone number if we have one, "" otherwise
String areaCode = customer.flatMap(c -> c.homePhone)
          .map(phone -> phone.getAreaCode())
          .get("");

// another way to do the same - using match
areaCode = customer.flatMap(c -> c.homePhone)
                   .match("", phone -> phone.getAreaCode());
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
