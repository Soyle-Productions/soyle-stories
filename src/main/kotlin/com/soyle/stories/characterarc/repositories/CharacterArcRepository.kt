package com.soyle.stories.characterarc.repositories

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme

/**
 * Created by Brendan
 * Date: 2/25/2020
 * Time: 10:47 PM
 */
interface CharacterArcRepository {
    suspend fun listAllCharacterArcsInProject(projectId: Project.Id): List<CharacterArc>
    suspend fun addNewCharacterArc(characterArc: CharacterArc)
    suspend fun getCharacterArcByCharacterAndThemeId(characterId: Character.Id, themeId: Theme.Id): CharacterArc?
}