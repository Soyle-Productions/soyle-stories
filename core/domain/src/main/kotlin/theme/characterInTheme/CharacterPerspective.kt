package com.soyle.stories.domain.theme.characterInTheme

import com.soyle.stories.domain.character.Character

class CharacterPerspective(
    val storyFunctions: Map<Character.Id, StoryFunction?>,
    val attacks: Map<Character.Id, String>
) {

    constructor(characterIds: List<Character.Id>) : this(
        characterIds.associateWith { null },
        characterIds.associateWith { "" }
    )

    fun perceiveCharacter(characterId: Character.Id) =
        CharacterPerspective(
            storyFunctions + (characterId to null),
            attacks + (characterId to "")
        )

    internal fun ignoreCharacter(characterId: Character.Id) =
        CharacterPerspective(
            storyFunctions - characterId,
            attacks - characterId
        )

    fun applyStoryFunction(characterId: Character.Id, function: StoryFunction) =
        CharacterPerspective(
            storyFunctions.minus(characterId).plus(characterId to function),
            attacks
        )

    fun clearStoryFunctions(characterId: Character.Id) =
        CharacterPerspective(
            storyFunctions.minus(characterId).plus(characterId to null),
            attacks
        )

    fun changeAttack(characterId: Character.Id, attack: String) =
        CharacterPerspective(
            storyFunctions,
            attacks.minus(characterId).plus(characterId to attack)
        )
}