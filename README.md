# Soyle Stories
Soyle Stories is a working title for an application to help writers organize and think through their stories.

## Designing Principles
1. Enable writers to finish their stories without worrying about how to keep track of everything.
2. Writers have many different ways of thinking about their stories, we can't enforce one way of thinking and instead have to allow for as many different workflows as possible.
3. No input field should be included just for the sake of input.  If it's just a box, it can be put into the description of the thing.

## Architecture
At the very core of the project, we use Domain-Driven Design and functional programming.  Charaters are distinct from Locations or Scenes.
Surrounding the core domain, we use Clean Architecture Use Cases to describe what users or external actors can do with the system.  Many use cases map directly to a method on an entity.
In the Adapters layer of the system, we include the implementations of the outputs of use cases which typically distribute the domain events to receivers/notifiers that broadcast a change to interested parties (usually some GUI component).  We also implement the persistence code, but currently, it only resides in memory and does not actually save to the file system.
In the View layer, we usually have tornadofx components and the dependency injection interface.
In the src folder for the desktop module, we have all the definitions for the dependency injection (some DI definitions are still waiting to be migrated from Views into this folder).

## Current Focuses
1. World Building features
2. Scene Formatting and additional mentions
3. Migrating old, core API's to return events in Update objects
4. Migrating old use case API's to output events and throw errors (instead of outputting the errors)
5. Persistence to file system
