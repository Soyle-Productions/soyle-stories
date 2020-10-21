package com.soyle.stories.entities

import com.soyle.stories.characterarc.CharacterArcAlreadyContainsMaximumNumberOfTemplateSection
import com.soyle.stories.characterarc.TemplateSectionIsNotPartOfArcTemplate
import com.soyle.stories.common.Entity
import java.util.*

class CharacterArc private constructor(
    override val id: Id,
    val characterId: Character.Id,
    val template: CharacterArcTemplate,
    val themeId: Theme.Id,
    val name: String,
    val arcSections: List<CharacterArcSection>,
    private val moralArgumentSectionOrder: Map<CharacterArcSection.Id, Int>,

    defaultConstructorMarker: Unit
) : Entity<CharacterArc.Id> {

    constructor(
        id: Id,
        characterId: Character.Id,
        template: CharacterArcTemplate,
        themeId: Theme.Id,
        name: String,
        arcSections: List<CharacterArcSection>,
        moralArgumentSectionOrder: Map<CharacterArcSection.Id, Int>,
    ) : this(id, characterId, template, themeId, name, arcSections, moralArgumentSectionOrder, defaultConstructorMarker = Unit) {
        if (moralArgumentSectionOrder.values.toSet().size != moralArgumentSectionOrder.size) error("Not all indices are unique in moral argument order")
        moralArgumentSectionOrder.values.forEach { if (it !in 0..moralArgumentSectionOrder.size) error("Moral argument index is out of bounds") }
        val moralSectionIds = arcSections.filter { it.template.isMoral }.map { it.id }.toSet()
        if (moralSectionIds != moralArgumentSectionOrder.keys) error("Not all moral argument sections have an ordered index")
    }

    private fun copy(
        name: String = this.name,
        arcSections: List<CharacterArcSection> = this.arcSections,
        moralArgumentSectionOrder: Map<CharacterArcSection.Id, Int> = this.moralArgumentSectionOrder,
    ) = CharacterArc(id, characterId, template, themeId, name, arcSections, moralArgumentSectionOrder, defaultConstructorMarker = Unit)

    fun withNewName(name: String) = copy(name = name)

    fun withArcSection(templateSection: CharacterArcTemplateSection, linkedLocation: Location.Id? = null, value: String = ""): CharacterArc
    {
        if (template.sections.none { it isSameEntityAs templateSection}) {
            throw TemplateSectionIsNotPartOfArcTemplate(
                id.uuid, characterId.uuid, themeId.uuid, templateSection.id.uuid
            )
        }
        if (! templateSection.allowsMultiple && arcSections.any { it.template isSameEntityAs templateSection }) {
            throw CharacterArcAlreadyContainsMaximumNumberOfTemplateSection(
                id.uuid, characterId.uuid, themeId.uuid, templateSection.id.uuid
            )
        }
        val newSection = CharacterArcSection(
            CharacterArcSection.Id(UUID.randomUUID()),
            characterId,
            themeId,
            templateSection,
            linkedLocation,
            value
        )

        return copy(
            arcSections = arcSections + newSection,
            moralArgumentSectionOrder = if (templateSection.isMoral) {
                moralArgumentSectionOrder + (newSection.id to moralArgumentSectionOrder.size)
            } else {
                moralArgumentSectionOrder
            }
        )
    }
    fun withArcSection(arcSection: CharacterArcSection): CharacterArc
    {
        if (template.sections.none { it isSameEntityAs arcSection.template}) {
            throw TemplateSectionIsNotPartOfArcTemplate(
                id.uuid, characterId.uuid, themeId.uuid, arcSection.template.id.uuid
            )
        }
        if (! arcSection.template.allowsMultiple && arcSections.any { it.template isSameEntityAs arcSection.template }) {
            throw CharacterArcAlreadyContainsMaximumNumberOfTemplateSection(
                id.uuid, characterId.uuid, themeId.uuid, arcSection.template.id.uuid
            )
        }
        return copy(
            arcSections = arcSections + arcSection,
            moralArgumentSectionOrder = if (arcSection.template.isMoral) {
                moralArgumentSectionOrder + (arcSection.id to moralArgumentSectionOrder.size)
            } else {
                moralArgumentSectionOrder
            }
        )
    }
    fun withoutArcSection(arcSectionId: CharacterArcSection.Id) = withoutArcSections(setOf(arcSectionId))
    fun withoutArcSections(arcSectionIds: Set<CharacterArcSection.Id>): CharacterArc
    {
        return copy(
            arcSections = arcSections.filterNot { it.id in arcSectionIds },
            moralArgumentSectionOrder = (moralArgumentSectionOrder - arcSectionIds).entries.sortedBy { it.value }.withIndex().associate {
                it.value.key to it.index
            }
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

    fun moralArgument() = arcSections.filter { it.template.isMoral }.sortedBy { moralArgumentSectionOrder.getValue(it.id) }

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

            val initialSections = template.sections.asSequence().filter { it.isRequired }
                .map { CharacterArcSection.planNewCharacterArcSection(characterId, themeId, it) }.toList()

            return CharacterArc(
                Id(),
                characterId,
                template,
                themeId,
                name,
                initialSections,
                initialSections.filter { it.template.isMoral }.withIndex().associate { it.value.id to it.index }
            )
        }
    }

}