package com.soyle.stories.entities.theme

import com.soyle.stories.common.Entity
import java.util.*

class Symbol(
    override val id: Id,
    val name: String
) : Entity<Symbol.Id> {

    constructor(name: String) : this(Id(), name)

    data class Id(val uuid: UUID = UUID.randomUUID()) {
        override fun toString(): String = "Symbol($uuid)"
    }
}