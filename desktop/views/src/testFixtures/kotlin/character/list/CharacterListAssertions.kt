package com.soyle.stories.desktop.view.character.list

import com.soyle.stories.character.list.CharacterListView
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.theme.Theme
import org.junit.jupiter.api.Assertions.assertEquals

class CharacterListAssertions private constructor(private val access: CharacterListViewAccess){
    companion object {
        fun assertThat(characterList: CharacterListView, assertions: CharacterListAssertions.() -> Unit) {
            CharacterListAssertions(CharacterListViewAccess(characterList)).assertions()
        }
    }

    fun characterHasName(characterId: Character.Id, expectedName: String)
    {
        assertEquals(expectedName, access.getCharacterItemOrError(characterId).characterName)
    }

    fun characterArcHasName(characterId: Character.Id, themeId: Theme.Id, arcId: CharacterArc.Id, expectedName: String)
    {
        assertEquals(expectedName, access.getArcItem(characterId, themeId)!!.name)
    }
}