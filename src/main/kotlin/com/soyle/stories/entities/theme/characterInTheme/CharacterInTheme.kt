package com.soyle.stories.entities.theme.characterInTheme

import com.soyle.stories.entities.*
import com.soyle.stories.entities.theme.ThematicSection
import com.soyle.stories.entities.theme.ThematicTemplate
import com.soyle.stories.translators.asThematicTemplateSection
import java.util.*

/**
 * Created by Brendan
 * Date: 2/19/2020
 * Time: 9:17 AM
 */
sealed class CharacterInTheme {
    abstract val id: Character.Id
    abstract val name: String
    abstract val archetype: String
    abstract val variationOnMoral: String
    abstract val position: String
    abstract val thematicSections: List<ThematicSection>

    abstract fun changeName(name: String): CharacterInTheme
    abstract fun changeArchetype(archetype: String): CharacterInTheme
    abstract fun changeVariationOnMoral(variationOnMoral: String): CharacterInTheme
    abstract fun changePosition(position: String): CharacterInTheme

}

class MinorCharacter(
    override val id: Character.Id,
    override val name: String,
    override val archetype: String,
    override val variationOnMoral: String,
    override val position: String,
    override val thematicSections: List<ThematicSection>
) : CharacterInTheme() {

    constructor(themeId: Theme.Id, characterId: Character.Id, name: String, thematicTemplate: ThematicTemplate) : this(
        characterId,
        name,
        "",
        "",
        "",
        thematicTemplate.sections.map {
            ThematicSection(
                CharacterArcSection.Id(UUID.randomUUID()),
                characterId,
                themeId,
                it
            )
        }
    )

    private fun copy(
        name: String = this.name,
        archetype: String = this.archetype,
        variationOnMoral: String = this.variationOnMoral,
        thematicSections: List<ThematicSection> = this.thematicSections,
        position: String = this.position
    ) = MinorCharacter(
        id,
        name,
        archetype,
        variationOnMoral,
        position,
        thematicSections
    )

    override fun changeName(name: String): MinorCharacter {
        return copy(name = name)
    }

    override fun changeArchetype(archetype: String): MinorCharacter {
        return copy(archetype = archetype)
    }

    override fun changeVariationOnMoral(variationOnMoral: String): MinorCharacter {
        return copy(variationOnMoral = variationOnMoral)
    }

    override fun changePosition(position: String): MinorCharacter {
        return copy(position = position)
    }

    fun promote(
        otherCharacters: List<Character.Id>,
        themeId: Theme.Id,
        themeName: String,
        characterArcTemplate: CharacterArcTemplate
    ): MajorCharacter = MajorCharacter(
        id,
        name,
        archetype,
        variationOnMoral,
        position,
        thematicSections,
        otherCharacters,
        themeId,
        themeName,
        characterArcTemplate
    )

}

class MajorCharacter(
    override val id: Character.Id,
    override val name: String,
    override val archetype: String,
    override val variationOnMoral: String,
    override val position: String,
    override val thematicSections: List<ThematicSection>,
    private val perspective: CharacterPerspective,
    val characterArc: CharacterArc,
    val characterChange: String
) : CharacterInTheme() {

    constructor(
        id: Character.Id,
        name: String,
        archetype: String,
        variationOnMoral: String,
        position: String,
        thematicSections: List<ThematicSection>,
        otherCharacters: List<Character.Id>,
        themeId: Theme.Id,
        themeName: String,
        characterArcTemplate: CharacterArcTemplate
    ) : this(
        id,
        name,
        archetype,
        variationOnMoral,
        position,
        thematicSections.let {
            val thematicSectionsIds = it.map { it.template.characterArcTemplateSectionId }.toSet()
            thematicSections + characterArcTemplate.sections.filterNot {
                it.id in thematicSectionsIds
            }.map {
                ThematicSection(CharacterArcSection.Id(UUID.randomUUID()), id, themeId, it.asThematicTemplateSection())
            }
        },
        CharacterPerspective(otherCharacters),
        CharacterArc.planNewCharacterArc(id, themeId, themeName, characterArcTemplate),
        ""
    )

    private fun copy(
        name: String = this.name,
        archetype: String = this.archetype,
        variationOnMoral: String = this.variationOnMoral,
        position: String = this.position,
        thematicSections: List<ThematicSection> = this.thematicSections,
        perspective: CharacterPerspective = this.perspective,
        characterArc: CharacterArc = this.characterArc,
        characterChange: String = this.characterChange
    ) = MajorCharacter(
        id,
        name,
        archetype,
        variationOnMoral,
        position,
        thematicSections,
        perspective,
        characterArc,
        characterChange
    )

    override fun changeName(name: String): MajorCharacter =
        copy(name = name)

    fun perceiveCharacter(characterId: Character.Id): MajorCharacter =
        copy(perspective = perspective.perceiveCharacter(characterId))

    internal fun ignoreCharacter(characterId: Character.Id): MajorCharacter =
        copy(perspective = perspective.ignoreCharacter(characterId))

    override fun changePosition(position: String): MajorCharacter {
        return copy(position = position)
    }

    fun getStoryFunctionsForCharacter(characterId: Character.Id) =
        perspective.storyFunctions[characterId]

    fun getOpponents() =
        perspective.storyFunctions.filter {
            it.value == StoryFunction.Antagonist || it.value == StoryFunction.FakeAllyAntagonist || it.value == StoryFunction.MainAntagonist
        }

    fun hasStoryFunctionForTargetCharacter(function: StoryFunction, characterId: Character.Id) =
        getStoryFunctionsForCharacter(characterId) == function

    fun applyStoryFunction(characterId: Character.Id, function: StoryFunction): MajorCharacter =
        copy(perspective = perspective.applyStoryFunction(characterId, function))

    fun clearStoryFunctions(characterId: Character.Id): MajorCharacter =
        copy(perspective = perspective.clearStoryFunctions(characterId))

    override fun changeArchetype(archetype: String): MajorCharacter =
        copy(archetype = archetype)

    override fun changeVariationOnMoral(variationOnMoral: String): MajorCharacter =
        copy(variationOnMoral = variationOnMoral)

    fun changeAttack(characterId: Character.Id, attack: String): MajorCharacter =
        copy(perspective = perspective.changeAttack(characterId, attack))

    fun getAttacksByCharacter(characterId: Character.Id) =
        perspective.attacks[characterId]

    fun withCharacterChangeAs(change: String): MajorCharacter =
        copy(characterChange = change)

    fun withCharacterArcRenamed(name: String): MajorCharacter =
        copy(characterArc = characterArc.withNewName(name))

}