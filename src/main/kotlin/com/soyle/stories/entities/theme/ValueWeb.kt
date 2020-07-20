package com.soyle.stories.entities.theme

import com.soyle.stories.common.Entity
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.CharacterAlreadyRepresentationValueInValueWeb
import com.soyle.stories.theme.OppositionValueDoesNotExist
import java.util.*

class ValueWeb(
    override val id: Id,
    val themeId: Theme.Id,
    val name: String,
    val oppositions: List<OppositionValue>
) : Entity<ValueWeb.Id> {

    constructor(themeId: Theme.Id, name: String) : this(Id(), themeId, name, listOf(OppositionValue(name)))

    private fun copy(
        name: String = this.name,
        oppositions: List<OppositionValue> = this.oppositions
    ) = ValueWeb(
        id,
        themeId,
        name,
        oppositions
    )

    fun withName(name: String) = copy(name = name)
    fun withOpposition(opposition: OppositionValue) = copy(oppositions = oppositions + opposition)
    fun withoutOpposition(oppositionId: OppositionValue.Id) = copy(oppositions = oppositions.filterNot { it.id == oppositionId })

    fun withRepresentationOf(representation: SymbolicRepresentation, oppositionId: OppositionValue.Id): ValueWeb
    {
        val opposition = getOppositionValue(oppositionId)
        preventDuplicateRepresentations(representation, oppositionId)
        return withoutOpposition(oppositionId)
            .withOpposition(opposition.withRepresentation(representation))
    }
    fun withoutRepresentation(representation: SymbolicRepresentation): ValueWeb
    {
        val opposition = oppositionWithRepresentation.getValue(representation.entityUUID)
            .withoutRepresentation(representation)
        return withoutOpposition(opposition.id).withOpposition(opposition)
    }
    fun hasRepresentation(representation: SymbolicRepresentation): Boolean
    {
        return oppositionWithRepresentation[representation.entityUUID] != null
    }

    private val oppositionsById by lazy {
        oppositions.associateBy { it.id }
    }
    private fun getOppositionValue(oppositionId: OppositionValue.Id): OppositionValue
    {
        return oppositionsById[oppositionId]
            ?: throw OppositionValueDoesNotExist(oppositionId.uuid)
    }

    private val oppositionWithRepresentation by lazy {
        oppositions.flatMap { op ->
            op.representations.map { it.entityUUID to op }
        }.toMap()
    }
    private fun preventDuplicateRepresentations(
        representation: SymbolicRepresentation,
        oppositionId: OppositionValue.Id
    ) {
        val oppositionWithRepresentation = oppositionWithRepresentation[representation.entityUUID]
        if (oppositionWithRepresentation != null) {
            throw CharacterAlreadyRepresentationValueInValueWeb(
                themeId.uuid,
                id.uuid,
                oppositionWithRepresentation.id.uuid,
                oppositionId.uuid,
                representation.entityUUID
            )
        }
    }

    data class Id(val uuid: UUID = UUID.randomUUID())

}