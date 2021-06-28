package com.soyle.stories.characterarc.createArcSectionDialog

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.character.CharacterArcTemplateSection
import com.soyle.stories.domain.theme.Theme

interface CreateArcSectionDialogViewListener {

    fun getValidState(themeId: Theme.Id, characterId: Character.Id)
    fun createArcSection(characterId: Character.Id, themeId: Theme.Id, sectionTemplateId: CharacterArcTemplateSection.Id, description: String)
    fun modifyArcSection(characterId: Character.Id, themeId: Theme.Id, arcSectionId: CharacterArcSection.Id, description: String)

}