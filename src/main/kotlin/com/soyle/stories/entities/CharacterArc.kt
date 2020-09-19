package com.soyle.stories.entities

import arrow.core.Either
import arrow.core.right
import com.soyle.stories.characterarc.CharacterArcAlreadyContainsMaximumNumberOfTemplateSection
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
        name: String = this.name,
        arcSections: List<CharacterArcSection> = this.arcSections
    ) = CharacterArc(id, characterId, template, themeId, name, arcSections, defaultConstructorMarker = Unit)

    fun withNewName(name: String) = copy(name = name)

    fun withArcSection(templateSection: CharacterArcTemplateSection, linkedLocation: Location.Id? = null, value: String = ""): CharacterArc
    {
        if (! templateSection.allowsMultiple && arcSections.any { it.template isSameEntityAs templateSection }) {
            throw CharacterArcAlreadyContainsMaximumNumberOfTemplateSection(
                id.uuid, characterId.uuid, themeId.uuid, templateSection.id.uuid
            )
        }
        return copy(
            arcSections = arcSections + CharacterArcSection(
                CharacterArcSection.Id(UUID.randomUUID()),
                characterId,
                themeId,
                templateSection,
                linkedLocation,
                value
            )
        )
    }
    fun withArcSection(arcSection: CharacterArcSection): CharacterArc
    {
        if (! arcSection.template.allowsMultiple && arcSections.any { it.template isSameEntityAs arcSection.template }) {
            throw CharacterArcAlreadyContainsMaximumNumberOfTemplateSection(
                id.uuid, characterId.uuid, themeId.uuid, arcSection.template.id.uuid
            )
        }
        return copy(
            arcSections = arcSections + arcSection
        )
    }
    fun withoutArcSection(arcSectionId: CharacterArcSection.Id) = withoutArcSections(setOf(arcSectionId))
    fun withoutArcSections(arcSectionIds: Set<CharacterArcSection.Id>): CharacterArc
    {
        return copy(
            arcSections = arcSections.filterNot { it.id in arcSectionIds }
        )
    }

    fun withArcSectionsMapped(mapping: (CharacterArcSection) -> CharacterArcSection): CharacterArc
    {
        val arcSectionsMapped = arcSections.map(mapping)

        // any validation on changes

        return copy(
            arcSections = arcSectionsMapped
        )
    }

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