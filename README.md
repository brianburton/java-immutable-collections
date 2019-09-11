Overview
---
The JImmutable Collections library provides a powerful selection of immutable/persistent collection classes designed with performance and ease of integration in mind. These collections are intended to replace the java.util collection classes to provide the thread safety and other benefits of immutability.

The JImmutable classes are immutable so once a particular collection object has been created it cannot be modified. This means that it can be safely shared throughout a program without the need for synchronization or defensive copying. In fact structure sharing is a theme throughout the library. For example you never actually "create" a JImmutableList instance. The factory method always returns the single, shared, empty list instance when it's called.

However a collection that you can't add or delete from has fairly limited utility. Instead of locking you in once you've created a collection, the JImmutable collections are designed to allow themselves to be easily updated as well. Each collection provides methods for adding and removing elements. These methods create a new collection of the same type while leaving the original collection intact (i.e. the original persists). The data structures used to implement the collections (linked lists, balanced trees, and integer tries) allow for almost all of the structure of the original collection to be shared by the new collection. Since all objects within each collection are immutable this sharing is completely safe. The collections are persistent in the functional programming sense. The collections are not persistent in the database sense. All contents are stored in memory at all times.

Each collection class provides adapter methods to create java.util style unmodifiable collections backed by the immutable collection. Unlike the [Guava](https://github.com/google/guava) immutable collection classes these adapters do not create defensive copies of all elements from the original collections. They simply access elements within the original collection. If you have code that needs a java.util.Map to do its work you can still use a `JImmutables.map()` and simply call it's `getMap()` method when you need to pass a java.util.Map to your older code.

The collections fully support Java 8 lambdas and parallel processing using streams.  Collectors and builders are provided easily create and populate new collections.  All collections also implement the Iterable interface so you can use them in foreach loops directly.

The library is designed to have no dependencies on other libraries but it should interact well with others.  Standard java interfaces are used where appropriate.  Class names were chosen so as not to conflict with Guava's immutable container class names or Hibernate's persistent container class names.

**Note:** Keep in mind that while the JImmutables themselves are immutable the values you choose to store in them might not be.  Always [use immutable objects as keys](https://github.com/brianburton/java-immutable-collections/wiki/Hash-Keys) and if you use mutable objects as values be aware that your code could mutate them between when you add them to a JImmutable and when you retrieve them later.

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
All production releases undergo stress testing and pass all junit tests.  Of course you should evaluate the collections for yourself and perform your own tests before deploying the collections to production systems.

All releases are uploaded to the [releases section](https://github.com/brianburton/java-immutable-collections/releases) on GitHub and are also available via Maven in [Maven Central](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.javimmutable%22%20AND%20a%3A%22javimmutable-collections%22).  You can add JImmutable Collections to your Maven project by adding a dependency like this to your pom.xml.  The maven releases include source jars for easy reference in your IDE.

    <dependency>
        <groupId>org.javimmutable</groupId>
        <artifactId>javimmutable-collections</artifactId>
        <version>insert-desired-version</version>
    </dependency>

**Project Members:**  

- [Brian Burton](https://github.com/brianburton) (admin)
- [Angela Burton](https://github.com/anjbur)
