package com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.MoralWeakness
import com.soyle.stories.domain.character.PsychologicalWeakness
import com.soyle.stories.domain.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.domain.theme.CharacterNotInTheme
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.characterInTheme.CharacterInTheme
import com.soyle.stories.domain.theme.characterInTheme.MajorCharacter
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.character.arc.section.addCharacterArcSectionToMoralArgument.ArcSectionAddedToCharacterArc
import com.soyle.stories.usecase.theme.ThemeRepository


class ChangeCharacterMoralWeaknessUseCase(
    private val themeRepository: ThemeRepository,
    private val characterArcRepository: CharacterArcRepository
) : ChangeCharacterMoralWeakness {


    private val logic = ChangeOptionalCharacterArcSectionValue(
        themeRepository,
        characterArcRepository,
        MoralWeakness,
        ArcSectionType.MoralWeakness
    )

    override suspend fun invoke(
        request: ChangeCharacterMoralWeakness.RequestModel,
        output: ChangeCharacterMoralWeakness.OutputPort
    ) {

        val event = logic.changeOptionalSectionValue(
            Theme.Id(request.themeId),
            Character.Id(request.characterId),
            request.moralWeakness
        ).event

        output.characterMoralWeaknessChanged(
            ChangeCharacterMoralWeakness.ResponseModel(
                event as? ArcSectionAddedToCharacterArc,
                event as? ChangedCharacterArcSectionValue
            )
        )
    }
}