package com.soyle.stories.entities

import com.soyle.stories.common.Entity
import com.soyle.stories.prose.ProseCreated
import java.util.*

class Prose private constructor(
    override val id: Id,

    defaultConstructorMarker: Unit
) : Entity<Prose.Id> {

    companion object {
        fun create(): Pair<Prose, ProseCreated>
        {
            val prose = Prose(Id(), defaultConstructorMarker = Unit)
            return prose to ProseCreated()
        }
    }

    data class Id(val uuid: UUID = UUID.randomUUID()) {
        override fun toString(): String = "Prose($uuid)"
    }
}