Overview
---
The JImmutable Collections library provides a useful set of immutable/persistent collection classes designed with performance and ease of integration in mind.  These collections are intended to replace the java.util collection classes to provide the thread safety and other benefits of immutability.

Immutability and persistence are terms which people tend to interpret in different ways.  The JImmutable classes are immutable in the sense that once once a given collection has been created it cannot be modified.  This means that it can be safely shared throughout a program without the need for synchronization or defensive copying.

However the collections are designed to allow themselves to be easily updated as well.  Each collection provides methods for adding and removing elements.  Each of these methods creates a new collection of the same type while leaving the original collection intact (i.e. the original persists).  The data structures used to implement the collections (linked lists, 2-3 trees, and integer tries) allow for almost all of the structure of the original collection to be shared by the new collection.  Since all objects within each collection are immutable this sharing is completely safe.  The collections are persistent in the functional programming sense.  The collections are **not** persistent in the database sense.  All contents are stored in memory at all times.

Each collection class provides adapter methods to create java.util style unmodifiable collections backed by the immutable collection.  Unlike the [Guava](https://github.com/google/guava) immutable collection classes these adapters do not create defensive copies of all elements from the original collections.  They simply access elements within the original collection.  If you have code that needs a java.util.Map to do its work you can still use a JImmutables.map() and simply call it's getMap() method when you need to pass a java.util.Map to your older code.

The library uses a Cursor class to allow iteration over the collection elements.  Cursor is similar to Iterator but is immutable and allows for lazy evaluation.  An adapter is provided to easily turn a Cursor into an Iterator for easier integration with standard java classes.  All collections implement the Iterable interface so you can use them in foreach loops.

The library is designed to have no dependencies on other libraries but it should interact well with others.  Standard java interfaces are used where appropriate.  Class names were chosen so as not to conflict with Guava's immutable container class names or Hibernate's persistent container class names.

[JImmutables Factory Methods](https://github.com/brianburton/java-immutable-collections/wiki/JImmutables-Factory-Methods)  
[Collections Overview](https://github.com/brianburton/java-immutable-collections/wiki/Collections-Overview)  
[List Tutorial](https://github.com/brianburton/java-immutable-collections/wiki/List-Tutorial)  
[Map Tutorial](https://github.com/brianburton/java-immutable-collections/wiki/Map-Tutorial)  
[Array Tutorial](https://github.com/brianburton/java-immutable-collections/wiki/Array-Tutorial)  
[Comparative Performance](https://github.com/brianburton/java-immutable-collections/wiki/Comparative-Performance)  
[Hash Keys](https://github.com/brianburton/java-immutable-collections/wiki/Hash-Keys)  
[Project Javadoc](http://brianburton.github.io/java-immutable-collections/apidocs/index.html)  

Note: Keep in mind that while the JImmutables themselves are immutable the values you store in them might not be.  Always use immutable objects as keys and if you use mutable objects as values be aware that they could change between when you add them to a JImmutable and when you retrieve them later.


Project Status
---
The collections have junit tests and I try to perform rigorous testing.  Still, I always recommend that you evaluate the collections for yourself and perform your own tests before using the collections in production systems.

The 1.8 release has been uploaded to the releases section on GitHub and is also available via Maven in [Maven Central](http://search.maven.org/#artifactdetails%7Corg.javimmutable%7Cjavimmutable-collections%7C1.8%7Cjar).  You can add JImmutable Collections to your Maven project by adding this dependency to your pom.xml

    <dependency>
        <groupId>org.javimmutable</groupId>
        <artifactId>javimmutable-collections</artifactId>
        <version>1.8</version>
    </dependency>

**Project Members:**  
[Brian Burton](https://github.com/brianburton) (admin)
[Angela Burton](https://github.com/anjbur)
