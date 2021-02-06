package com.soyle.stories.domain.theme.characterInTheme

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArcTemplate
import com.soyle.stories.domain.theme.Theme

sealed class CharacterInTheme {
    abstract val id: Character.Id
    abstract val name: String
    abstract val archetype: String
    abstract val variationOnMoral: String
    abstract val position: String

    abstract fun changeName(name: String): CharacterInTheme
    abstract fun changeArchetype(archetype: String): CharacterInTheme
    abstract fun changeVariationOnMoral(variationOnMoral: String): CharacterInTheme
    abstract fun changePosition(position: String): CharacterInTheme

    fun isAntagonisticTowards(majorCharacter: MajorCharacter): Boolean {
        val storyFunction = majorCharacter.getStoryFunctionsForCharacter(id)
        return storyFunction == StoryFunction.MainAntagonist || storyFunction == StoryFunction.Antagonist
    }

}

class MinorCharacter(
    override val id: Character.Id,
    override val name: String,
    override val archetype: String,
    override val variationOnMoral: String,
    override val position: String
) : CharacterInTheme() {

    constructor(themeId: Theme.Id, characterId: Character.Id, name: String) : this(
        characterId,
        name,
        "",
        "",
        ""
    )

    private fun copy(
        name: String = this.name,
        archetype: String = this.archetype,
        variationOnMoral: String = this.variationOnMoral,
        position: String = this.position
    ) = MinorCharacter(
        id,
        name,
        archetype,
        variationOnMoral,
        position
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
    private val perspective: CharacterPerspective,
    val characterChange: String
) : CharacterInTheme() {

    constructor(
        id: Character.Id,
        name: String,
        archetype: String,
        variationOnMoral: String,
        position: String,
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
        CharacterPerspective(otherCharacters),
        ""
    )

    private fun copy(
        name: String = this.name,
        archetype: String = this.archetype,
        variationOnMoral: String = this.variationOnMoral,
        position: String = this.position,
        perspective: CharacterPerspective = this.perspective,
        characterChange: String = this.characterChange
    ) = MajorCharacter(
        id,
        name,
        archetype,
        variationOnMoral,
        position,
        perspective,
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

}