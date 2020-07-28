package com.soyle.stories.entities.theme

import com.soyle.stories.entities.Character

/**
 * Created by Brendan
 * Date: 2/20/2020
 * Time: 4:43 PM
 */
class CharacterPerspective(
    val storyFunctions: Map<Character.Id, StoryFunction?>,
    val attacks: Map<Character.Id, String>
) {

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