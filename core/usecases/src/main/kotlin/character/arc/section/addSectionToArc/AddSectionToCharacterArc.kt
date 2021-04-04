package com.soyle.stories.usecase.character.arc.section.addSectionToArc

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArcTemplateSection
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.usecase.character.arc.section.addCharacterArcSectionToMoralArgument.ArcSectionAddedToCharacterArc

interface AddSectionToCharacterArc {

    class RequestModel(
        val characterId: Character.Id, val themeId: Theme.Id, val sectionTemplateId: CharacterArcTemplateSection.Id
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(
        val sectionAddedToCharacterArc: ArcSectionAddedToCharacterArc,
    )

    interface OutputPort {
        suspend fun receiveAddSectionToCharacterArcResponse(response: ResponseModel)
    }

}