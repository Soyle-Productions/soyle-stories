package com.soyle.stories.core.definitions.storyevent.character

import com.soyle.stories.core.framework.storyevent.`Story Event Steps`
import com.soyle.stories.domain.character.Character
import com.soyle.stories.usecase.storyevent.character.involve.AvailableCharactersToInvolveInStoryEvent
import org.amshove.kluent.should

class `Characters in Story Event Assertions`(
    private val availableCharacters: AvailableCharactersToInvolveInStoryEvent
) : `Story Event Steps`.Then.AvailableCharactersStateAssertions {
    override fun `should have an item for the`(characterId: Character.Id) {
        availableCharacters.should("""
                        Available Characters should have an item for the $characterId
                            available elements: ${availableCharacters.allAvailableElements}
                    """.trimIndent()) {
            allAvailableElements.any { it.entityId.id == characterId }
        }
    }

    override fun `should not have an item for the`(characterId: Character.Id) {
        availableCharacters.should("""
                        Available Characters should not have an item for the $characterId
                            available elements: ${availableCharacters.allAvailableElements}
                    """.trimIndent()) {
            allAvailableElements.none { it.entityId.id == characterId }
        }
    }
}