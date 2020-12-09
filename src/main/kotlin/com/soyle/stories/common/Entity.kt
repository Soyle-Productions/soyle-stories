package com.soyle.stories.common

import kotlin.reflect.KClass

/**
 * Created by Brendan
 * Date: 2/5/2020
 * Time: 3:30 PM
 */
interface Entity<Id> {
    val id: Id
    infix fun isSameEntityAs(other: Entity<*>): Boolean =
        other::class.java == this::class.java && other.id == this.id && this.id != null
}

class EntityId<Id> private constructor(val id: Id)
{
    companion object {
        fun <E : Entity<Id>, Id> of(kClass: KClass<E>): EntityIdGetter<E, Id> = EntityIdGetter()
    }

    class EntityIdGetter<E : Entity<Id>, Id> internal constructor()
    {
        fun id(id: Id): EntityId<Id> = EntityId(id)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EntityId<*>

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "EntityId($id)"
    }


}