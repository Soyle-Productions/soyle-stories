package com.soyle.stories.entities.theme

import com.soyle.stories.entities.Character

/**
 * Created by Brendan
 * Date: 2/20/2020
 * Time: 4:43 PM
 */
class CharacterPerspective(
    val storyFunctions: Map<Character.Id, List<StoryFunction>>,
    val attacks: Map<Character.Id, String>
) {

    fun perceiveCharacter(characterId: Character.Id) =
        CharacterPerspective(
            storyFunctions + (characterId to listOf()),
            attacks + (characterId to "")
        )

    fun applyStoryFunction(characterId: Character.Id, function: StoryFunction) =
        CharacterPerspective(
            storyFunctions.minus(characterId).plus(characterId to storyFunctions.getValue(characterId) + function),
            attacks
        )

    fun clearStoryFunctions(characterId: Character.Id) =
        CharacterPerspective(
            storyFunctions.minus(characterId).plus(characterId to emptyList()),
            attacks
        )

    fun changeAttack(characterId: Character.Id, attack: String) =
        CharacterPerspective(
            storyFunctions,
            attacks.minus(characterId).plus(characterId to attack)
        )
}