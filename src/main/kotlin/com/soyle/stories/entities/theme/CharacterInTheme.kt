package com.soyle.stories.entities.theme

import com.soyle.stories.entities.Character

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
    abstract val thematicSections: List<ThematicSection>

    abstract fun changeName(name: String): CharacterInTheme
    abstract fun changeArchetype(archetype: String): CharacterInTheme
    abstract fun changeVariationOnMoral(variationOnMoral: String): CharacterInTheme

}

class MinorCharacter(
    override val id: Character.Id,
    override val name: String,
    override val archetype: String,
    override val variationOnMoral: String,
    override val thematicSections: List<ThematicSection>
) : CharacterInTheme() {

    private fun copy(
        name: String = this.name,
        archetype: String = this.archetype,
        variationOnMoral: String = this.variationOnMoral,
        thematicSections: List<ThematicSection> = this.thematicSections
    ) = MinorCharacter(id, name, archetype, variationOnMoral, thematicSections)

    override fun changeName(name: String): MinorCharacter {
        return copy(name = name)
    }

    override fun changeArchetype(archetype: String): MinorCharacter {
        return copy(archetype = archetype)
    }

    override fun changeVariationOnMoral(variationOnMoral: String): MinorCharacter {
        return copy(variationOnMoral = variationOnMoral)
    }

}

class MajorCharacter(
    override val id: Character.Id,
    override val name: String,
    override val archetype: String,
    override val variationOnMoral: String,
    override val thematicSections: List<ThematicSection>,
    private val perspective: CharacterPerspective,
    val characterChange: String
) : CharacterInTheme() {

    private fun copy(
        name: String = this.name,
        archetype: String = this.archetype,
        variationOnMoral: String = this.variationOnMoral,
        thematicSections: List<ThematicSection> = this.thematicSections,
        perspective: CharacterPerspective = this.perspective,
        characterChange: String = this.characterChange
    ) = MajorCharacter(id, name, archetype, variationOnMoral, thematicSections, perspective, characterChange)

    override fun changeName(name: String): MajorCharacter =
      copy(name = name)

    fun perceiveCharacter(characterId: Character.Id): MajorCharacter =
        copy(perspective = perspective.perceiveCharacter(characterId))

    internal fun ignoreCharacter(characterId: Character.Id): MajorCharacter =
        copy(perspective = perspective.ignoreCharacter(characterId))

    fun getStoryFunctionsForCharacter(characterId: Character.Id) =
        perspective.storyFunctions[characterId]

    fun hasStoryFunctionForTargetCharacter(function: StoryFunction, characterId: Character.Id) =
        getStoryFunctionsForCharacter(characterId)?.contains(function) == true

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

}