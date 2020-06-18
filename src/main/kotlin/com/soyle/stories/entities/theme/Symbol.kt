package com.soyle.stories.entities.theme

import com.soyle.stories.common.Entity
import java.util.*

class Symbol(
    override val id: Id,
    val name: String
) : Entity<Symbol.Id> {

    data class Id(val uuid: UUID) {
        override fun toString(): String = "Symbol($uuid)"
    }
}