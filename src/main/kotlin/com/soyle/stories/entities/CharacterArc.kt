package com.soyle.stories.entities

import arrow.core.Either
import arrow.core.right
import com.soyle.stories.common.Entity
import java.util.*

class CharacterArc private constructor(
    override val id: Id,
    val characterId: Character.Id,
    val template: CharacterArcTemplate,
    val themeId: Theme.Id,
    val name: String,
    val arcSections: List<CharacterArcSection>,


    defaultConstructorMarker: Unit
) : Entity<CharacterArc.Id> {

    constructor(
        id: Id,
        characterId: Character.Id,
        template: CharacterArcTemplate,
        themeId: Theme.Id,
        name: String,
        arcSections: List<CharacterArcSection>,
    ) : this(id, characterId, template, themeId, name, arcSections, defaultConstructorMarker = Unit) {


    }

    private fun copy(
        name: String = this.name
    ) = CharacterArc(id, characterId, template, themeId, name, arcSections)

    fun withNewName(name: String) = copy(name = name)

    data class Id(val uuid: UUID = UUID.randomUUID()) {
        override fun toString(): String = "CharacterArc($uuid)"
    }

    companion object {

        fun planNewCharacterArc(
            characterId: Character.Id,
            themeId: Theme.Id,
            name: String,
            template: CharacterArcTemplate = CharacterArcTemplate.default()
        ): CharacterArc {
            return CharacterArc(
                Id(),
                characterId,
                template,
                themeId,
                name,
                template.sections.asSequence().filter { it.isRequired }
                    .map { CharacterArcSection.planNewCharacterArcSection(characterId, themeId, it) }.toList()
            )
        }
    }

}