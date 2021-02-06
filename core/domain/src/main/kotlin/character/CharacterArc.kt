package com.soyle.stories.domain.character

import com.soyle.stories.domain.entities.Entity
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.theme.Theme
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
        val newSection = CharacterArcSection(
            CharacterArcSection.Id(UUID.randomUUID()),
            characterId,
            themeId,
            templateSection,
            linkedLocation,
            value
        )

        return withArcSection(newSection)
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

    private val arcSectionsById: Map<CharacterArcSection.Id, CharacterArcSection> by lazy { arcSections.associateBy { it.id } }
    fun getArcSection(arcSectionId: CharacterArcSection.Id): CharacterArcSection? = arcSectionsById[arcSectionId]

    private val moralArgument by lazy { MoralArgument(arcSections.filter { it.template.isMoral }.sortedBy { moralArgumentSectionOrder.getValue(it.id) }) }
    fun moralArgument(): MoralArgument = moralArgument
    fun indexInMoralArgument(sectionId: CharacterArcSection.Id): Int? = moralArgumentSectionOrder[sectionId]

    inner class MoralArgument(
        val arcSections: List<CharacterArcSection>
    ) {

        fun withArcSection(
            templateSection: CharacterArcTemplateSection,
            linkedLocation: Location.Id? = null,
            value: String = "",
            index: Int? = null
        ): CharacterArc
        {
            val newSection = CharacterArcSection(
                CharacterArcSection.Id(UUID.randomUUID()),
                characterId,
                themeId,
                templateSection,
                linkedLocation,
                value
            )

            return withArcSection(newSection, index)
        }
        fun withArcSection(arcSection: CharacterArcSection, index: Int? = null): CharacterArc
        {

            if (! arcSection.template.isMoral) throw ArcTemplateSectionIsNotMoral(
                id.uuid,
                characterId.uuid,
                themeId.uuid,
                arcSection.template.id.uuid
            )

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
                moralArgumentSectionOrder = if (index == null) {
                    moralArgumentSectionOrder + (arcSection.id to moralArgumentSectionOrder.size)
                } else {
                    if (index < 0 || index > moralArgumentSectionOrder.size)
                        throw IndexOutOfBoundsException("Moral Argument index $index is not in the range of 0 to ${moralArgumentSectionOrder.size}.")

                    moralArgumentSectionOrder.mapValues {
                        if (it.value >= index) it.value + 1
                        else it.value
                    } + (arcSection.id to index)
                }
            )
        }

        fun withSectionMovedTo(arcSectionId: CharacterArcSection.Id, index: Int): CharacterArc
        {
            val currentIndex = indexInMoralArgument(arcSectionId)
                ?: throw CharacterArcSectionNotInMoralArgument(arcSectionId.uuid,characterId.uuid,themeId.uuid,id.uuid)

            return copy(
                moralArgumentSectionOrder = arcSections.toMutableList().apply {
                    add(index, removeAt(currentIndex))
                }.withIndex().associate { it.value.id to it.index }
            )
        }

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CharacterArc

        if (id != other.id) return false
        if (characterId != other.characterId) return false
        if (template != other.template) return false
        if (themeId != other.themeId) return false
        if (name != other.name) return false
        if (arcSections != other.arcSections) return false
        if (moralArgumentSectionOrder != other.moralArgumentSectionOrder) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + characterId.hashCode()
        result = 31 * result + template.hashCode()
        result = 31 * result + themeId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + arcSections.hashCode()
        result = 31 * result + moralArgumentSectionOrder.hashCode()
        return result
    }

    override fun toString(): String {
        return "CharacterArc(id=$id, characterId=$characterId, template=$template, themeId=$themeId, name='$name', arcSections=$arcSections, moralArgumentSectionOrder=$moralArgumentSectionOrder)"
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