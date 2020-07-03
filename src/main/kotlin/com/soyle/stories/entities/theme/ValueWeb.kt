package com.soyle.stories.entities.theme

import com.soyle.stories.common.Entity
import java.util.*

class ValueWeb(
    override val id: Id,
    val name: String,
    val oppositions: List<OppositionValue>
) : Entity<ValueWeb.Id> {

    constructor(name: String) : this(Id(), name, listOf(OppositionValue(name)))

    private fun copy(
        name: String = this.name,
        oppositions: List<OppositionValue> = this.oppositions
    ) = ValueWeb(
        id,
        name,
        oppositions
    )

    fun withName(name: String) = copy(name = name)
    fun withOpposition(opposition: OppositionValue) = copy(oppositions = oppositions + opposition)
    fun withoutOpposition(oppositionId: OppositionValue.Id) = copy(oppositions = oppositions.filterNot { it.id == oppositionId })

    data class Id(val uuid: UUID = UUID.randomUUID())

}