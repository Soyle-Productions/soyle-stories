package com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.PsychologicalWeakness
import com.soyle.stories.domain.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.domain.theme.CharacterNotInTheme
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.characterInTheme.CharacterInTheme
import com.soyle.stories.domain.theme.characterInTheme.MajorCharacter
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.character.arc.section.addCharacterArcSectionToMoralArgument.ArcSectionAddedToCharacterArc
import com.soyle.stories.usecase.theme.ThemeRepository
import java.util.*

class ChangeCharacterPsychologicalWeaknessUseCase(
    private val themeRepository: ThemeRepository,
    private val characterArcRepository: CharacterArcRepository
) : ChangeCharacterPsychologicalWeakness {

    private val logic = ChangeOptionalCharacterArcSectionValue(
        themeRepository,
        characterArcRepository,
        PsychologicalWeakness,
        ArcSectionType.PsychologicalWeakness
    )

    override suspend fun invoke(
        request: ChangeCharacterPsychologicalWeakness.RequestModel,
        output: ChangeCharacterPsychologicalWeakness.OutputPort
    ) {

        val event = logic.changeOptionalSectionValue(
            Theme.Id(request.themeId),
            Character.Id(request.characterId),
            request.psychologicalWeakness
        ).event

        output.characterPsychologicalWeaknessChanged(
            ChangeCharacterPsychologicalWeakness.ResponseModel(
                event as? ArcSectionAddedToCharacterArc,
                event as? ChangedCharacterArcSectionValue
            )
        )
    }
}