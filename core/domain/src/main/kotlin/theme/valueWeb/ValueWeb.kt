package com.soyle.stories.domain.theme.valueWeb

import com.soyle.stories.domain.entities.Entity
import com.soyle.stories.domain.theme.OppositionValueDoesNotExist
import com.soyle.stories.domain.theme.SymbolicRepresentation
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.oppositionValue.OppositionValue
import com.soyle.stories.domain.validation.NonBlankString
import java.util.*

class ValueWeb private constructor(
    override val id: Id,
    val themeId: Theme.Id,
    val name: NonBlankString,
    val oppositions: List<OppositionValue>,
    @Suppress("UNUSED_PARAMETER") defaultConstructorMarker: Unit
) : Entity<ValueWeb.Id> {

    constructor(themeId: Theme.Id, name: NonBlankString) : this(Id(), themeId, name, mutableListOf(), Unit){
        (oppositions as MutableList).add(OppositionValue(name))
    }

    constructor(
        id: Id,
        themeId: Theme.Id,
        name: NonBlankString,
        oppositions: List<OppositionValue>
    ) : this(id, themeId, name, oppositions, Unit) {
        preventDuplicateOppositionValues(oppositions)
        oppositions.flatMap {
            it.representations
        }.groupBy { it.entityUUID }.onEach { it.value.single() }
    }

    private fun copy(
        name: NonBlankString = this.name,
        oppositions: List<OppositionValue> = this.oppositions
    ) = ValueWeb(
        id,
        themeId,
        name,
        oppositions,
        Unit
    )

    fun withName(name: NonBlankString): ValueWeb {
        return copy(name = name)
    }

    fun withOpposition(name: NonBlankString? = null): ValueWeb {
        return copy(oppositions = oppositions + OppositionValue(name ?: NonBlankString.create(this.name.value + " ${oppositions.size + 1}")!!))
    }

    fun withoutOpposition(oppositionId: OppositionValue.Id): ValueWeb {
        if (oppositions.none { it.id == oppositionId }) {
            throw ValueWebDoesNotContainOppositionValue(
                id.uuid,
                oppositionId.uuid
            )
        }
        return copy(oppositions = oppositions.filterNot { it.id == oppositionId })
    }

    private fun replaceOpposition(opposition: OppositionValue): ValueWeb {
        return withoutOpposition(opposition.id).let {
            it.copy(oppositions = it.oppositions + opposition)
        }
    }

    fun withOppositionRenamedTo(oppositionId: OppositionValue.Id, newName: NonBlankString): ValueWeb {
        val opposition = getOppositionValue(oppositionId)
        return replaceOpposition(opposition.withName(newName))
    }

    fun withRepresentationOf(representation: SymbolicRepresentation, oppositionId: OppositionValue.Id): ValueWeb {
        val opposition = getOppositionValue(oppositionId)
        preventDuplicateRepresentations(representation, oppositionId)
        return replaceOpposition(opposition.withRepresentation(representation))
    }

    fun withoutRepresentation(entityId: UUID): ValueWeb {
        val opposition = oppositionWithRepresentation.getValue(entityId)
            .withoutRepresentation(entityId)
        return replaceOpposition(opposition)
    }

    fun withoutRepresentationIn(entityId: UUID, oppositionId: OppositionValue.Id): ValueWeb {
        val opposition = getOppositionValue(oppositionId)
        return replaceOpposition(opposition.withoutRepresentation(entityId))
    }

    fun withRepresentationRenamedTo(entityId: UUID, newName: String): ValueWeb {
        val oppositionWithRepresentation = oppositionWithRepresentation.getValue(entityId)
        return replaceOpposition(
            oppositionWithRepresentation.withoutRepresentation(entityId).withRepresentation(
                SymbolicRepresentation(entityId, newName)
            )
        )
    }


    fun hasRepresentation(entityId: UUID): Boolean {
        return oppositionWithRepresentation[entityId] != null
    }

    private val oppositionsById by lazy {
        oppositions.associateBy { it.id }
    }

    private fun getOppositionValue(oppositionId: OppositionValue.Id): OppositionValue {
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

    private fun preventDuplicateOppositionValues(oppositions: List<OppositionValue>) {
        oppositions.groupBy { it.id }.onEach {
            if (it.value.size > 1) {
                throw DuplicateOppositionValuesInValueWeb(id.uuid, it.key.uuid, it.value.size)
            }
        }
    }

    data class Id(val uuid: UUID = UUID.randomUUID())

}