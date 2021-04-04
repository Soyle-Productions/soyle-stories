package com.soyle.stories.character.createArcSection

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArcTemplateSection
import com.soyle.stories.domain.theme.Theme
import kotlinx.coroutines.Job

interface CreateArcSectionController {

    fun createArcSection(
        characterId: Character.Id,
        themeId: Theme.Id,
        sectionTemplateId: CharacterArcTemplateSection.Id,
        value: String
    ): Job

    fun createArcSectionAndCoverInScene(
        characterId: String,
        themeId: String,
        sectionTemplateId: String,
        value: String,
        sceneId: String
    )

}