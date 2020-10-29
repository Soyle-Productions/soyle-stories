package com.soyle.stories.theme.usecases.outlineMoralArgument

import java.util.*

interface OutlineMoralArgumentForCharacterInTheme {

    suspend operator fun invoke(themeId: UUID, characterId: UUID, output: OutputPort)

    class ResponseModel(
        val characterId: UUID,
        val characterName: String,
        val characterArcSections: List<CharacterArcSectionInMoralArgument>
    )

    class CharacterArcSectionInMoralArgument(
        val arcSectionId: UUID,
        val arcSectionValue: String,
        val sectionTemplateName: String,
        val sectionTemplateIsRequired: Boolean
    )

    interface OutputPort {
        suspend fun receiveMoralArgumentOutlineForCharacterInTheme(response: ResponseModel)
    }

}