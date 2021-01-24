package com.soyle.stories.entities.theme

import com.soyle.stories.common.Entity
import java.util.*

class Symbol(
    override val id: Id,
    val name: String
) : Entity<Symbol.Id> {

    constructor(name: String) : this(Id(), name)

    private fun copy(
        name: String = this.name
    ) = Symbol(
        id,
        name
    )

    fun withName(name: String) = copy(name = name)


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Symbol

        if (id != other.id) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString(): String {
        return "Symbol(id=$id, name='$name')"
    }

    data class Id(val uuid: UUID = UUID.randomUUID()) {
        override fun toString(): String = "Symbol($uuid)"
    }

}