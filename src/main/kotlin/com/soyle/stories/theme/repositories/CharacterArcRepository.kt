/**
 * Created by Brendan
 * Date: 3/4/2020
 * Time: 11:26 PM
 */
package com.soyle.stories.theme.repositories

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.Theme

interface CharacterArcRepository {
    suspend fun getCharacterArcByCharacterAndThemeId(characterId: Character.Id, themeId: Theme.Id): CharacterArc?
    suspend fun listCharacterArcsForTheme(themeId: Theme.Id): List<CharacterArc>
    suspend fun addNewCharacterArc(characterArc: CharacterArc)
    suspend fun removeCharacterArc(themeId: Theme.Id, characterId: Character.Id)
}