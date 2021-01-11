package com.soyle.stories.desktop.view.character.characterList

import com.soyle.stories.characterarc.characterList.CharacterList
import com.soyle.stories.entities.Character
import org.junit.jupiter.api.Assertions.assertEquals

class CharacterListAssertions private constructor(private val driver: CharacterListDriver){
    companion object {
        fun assertThat(characterList: CharacterList, assertions: CharacterListAssertions.() -> Unit) {
            CharacterListAssertions(CharacterListDriver(characterList)).assertions()
        }
    }

    fun characterHasName(characterId: Character.Id, expectedName: String)
    {
        assertEquals(expectedName, driver.getCharacterItemOrError(characterId).value!!.name)
    }
}