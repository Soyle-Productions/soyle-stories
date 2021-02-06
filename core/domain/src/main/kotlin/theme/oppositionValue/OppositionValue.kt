package com.soyle.stories.domain.theme.oppositionValue

import com.soyle.stories.domain.entities.Entity
import com.soyle.stories.domain.theme.SymbolicRepresentation
import com.soyle.stories.domain.validation.NonBlankString
import java.util.*

class OppositionValue private constructor(
    override val id: Id,
    val name: NonBlankString,
    val representations: List<SymbolicRepresentation>,
    @Suppress("UNUSED_PARAMETER") defaultConstructorMarker: Unit
) : Entity<OppositionValue.Id> {

    constructor(name: NonBlankString) : this(Id(), name, listOf())

    constructor(
        id: Id,
        name: NonBlankString,
        representations: List<SymbolicRepresentation>
    ) : this(id, name, representations, Unit)
    {
        representations.groupBy { it.entityUUID }.forEach { it.value.single() }
    }

    private fun copy(
        name: NonBlankString = this.name,
        representations: List<SymbolicRepresentation> = this.representations
    ) = OppositionValue(
        id,
        name,
        representations,
        Unit
    )

    fun withName(name: NonBlankString) = copy(name = name)
    fun withRepresentation(representation: SymbolicRepresentation): OppositionValue {
        if (hasEntityAsRepresentation(representation.entityUUID)) {
            throw OppositionValueAlreadyHasEntity(representation.entityUUID, representation.name, id.uuid)
        }
        return copy(representations = representations + representation)
    }
    fun withoutRepresentation(entityId: UUID) = copy(representations = representations.filterNot { it.entityUUID == entityId })

    private val representationIds by lazy { representations.map { it.entityUUID }.toSet() }

    fun hasEntityAsRepresentation(entityId: UUID) = representationIds.contains(entityId)

    data class Id(val uuid: UUID = UUID.randomUUID())

}