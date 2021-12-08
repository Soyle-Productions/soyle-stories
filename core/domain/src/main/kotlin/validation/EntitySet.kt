package com.soyle.stories.domain.validation

import com.soyle.stories.domain.entities.Entity

class EntitySet<E : Entity<*>> private constructor(private val _backingMap: Map<*, E>) : Collection<E> {

    companion object {
        fun  <E : Entity<Id>, Id> fromCollection(entities: Collection<E>) = EntitySet(entities.associateBy { it.id })
        fun  <E : Entity<Id>, Id> fromMap(map: Map<Id, E>): EntitySet<E>?
        {
            if (map.any { it.value.id != it.key }) return null
            return EntitySet(map)
        }
    }

    override fun toString(): String = "EntitySet${_backingMap.values}"

    override fun hashCode(): Int = _backingMap.hashCode()

    override fun equals(other: Any?): Boolean {
        return (other as? EntitySet<*>)?._backingMap?.equals(_backingMap) ?: false
    }

    override fun containsAll(elements: Collection<E>): Boolean = elements.all(this::contains)

    override fun iterator(): Iterator<E> = _backingMap.values.iterator()

    override val size: Int
        get() = _backingMap.size

    fun getEntityById(maybeId: Any) = _backingMap[maybeId]

    override fun contains(element: E): Boolean = _backingMap[element.id] == element

    fun containsEntityWithId(maybeId: Any): Boolean = _backingMap.containsKey(maybeId)

    override fun isEmpty(): Boolean = _backingMap.isEmpty()

    operator fun plus(entity: E) = EntitySet(_backingMap.plus(entity.id to entity))

    operator fun minus(entity: E) = EntitySet(_backingMap.minus(entity.id))

    operator fun minus(maybeId: Any): EntitySet<E> {
        return if (_backingMap.containsKey(maybeId)) {
            EntitySet(_backingMap.minus(maybeId))
        } else this
    }

    operator fun minus(entities: Collection<E>): EntitySet<E> {
        val update = _backingMap.minus(entities.map { it.id }.toSet())
        if (update.size == _backingMap.size) return this // nothing removed
        return EntitySet(update)
    }

    operator fun plus(collection: Collection<E>) = EntitySet(_backingMap.plus(collection.map { it.id to it }))

}

fun <E : Entity<Id>, Id> entitySetOfNotNull(vararg entities: E?) = EntitySet.fromCollection(entities.asSequence().filterNotNull().toList())

fun <E : Entity<Id>, Id> entitySetOf(vararg entities: E) = EntitySet.fromCollection(entities.toList())

fun <Id, E : Entity<Id>> noEntities(): EntitySet<E> = EntitySet.fromMap(emptyMap<Id, E>())!!

fun <E: Entity<Id>, Id> Collection<E>.toEntitySet() = EntitySet.fromCollection(this)