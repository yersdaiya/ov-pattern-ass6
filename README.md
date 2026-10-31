# Airport Operations (Assignment 6: Observer + Visitor)

Variant 8. Java 17, JUnit 5, no other libraries.

## Design decisions
- Observer: push model (event carries its data). _TODO: justify_
- Registry: CopyOnWriteArrayList per event type in a ConcurrentHashMap. _TODO: explain_
