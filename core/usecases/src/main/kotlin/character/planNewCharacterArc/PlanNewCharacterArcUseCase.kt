package com.soyle.stories.usecase.character.planNewCharacterArc

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.theme.ThemeRepository
import com.soyle.stories.usecase.theme.createTheme.CreatedTheme
import java.util.*

class PlanNewCharacterArcUseCase(
    private val characterRepository: CharacterRepository,
    private val themeRepository: ThemeRepository,
    private val characterArcRepository: CharacterArcRepository
) : PlanNewCharacterArc {

    override suspend fun invoke(
        characterId: UUID,
        name: String,
        outputPort: PlanNewCharacterArc.OutputPort
    ) {
        val character = getCharacter(characterId)
        val theme = Theme(character.projectId, name, emptyList(), "")

        val themeWithCharacter = addArcToCharacter(character, theme)
        val newArc = CharacterArc.planNewCharacterArc(character.id, theme.id, theme.name)

        themeRepository.addTheme(themeWithCharacter)
        characterArcRepository.addNewCharacterArc(newArc)

        outputPort.characterArcPlanned(
            PlanNewCharacterArc.ResponseModel(
                CreatedCharacterArc(theme.id.uuid, characterId, name),
                CreatedTheme(theme.projectId.uuid, theme.id.uuid, theme.name)
            )
        )

    }

    private fun addArcToCharacter(
        character: Character,
        theme: Theme
    ): Theme {
        return theme.withCharacterIncluded(character.id, character.name.value, character.media)
            .withCharacterPromoted(character.id)
    }

    private suspend fun getCharacter(characterId: UUID) =
        characterRepository.getCharacterById(Character.Id(characterId))
            ?: throw CharacterDoesNotExist(characterId)
}