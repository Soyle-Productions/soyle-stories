package com.soyle.stories.entities.theme

import com.soyle.stories.common.Entity
import java.util.*

class OppositionValue(
    override val id: Id,
    val name: String,
    val representations: List<SymbolicRepresentation>
) : Entity<OppositionValue.Id> {

    constructor(name: String) : this(Id(), name, listOf())

    private fun copy(
        name: String = this.name,
        representations: List<SymbolicRepresentation> = this.representations
    ) = OppositionValue(
        id,
        name,
        representations
    )

    fun withName(name: String) = copy(name = name)

    data class Id(val uuid: UUID = UUID.randomUUID())

}