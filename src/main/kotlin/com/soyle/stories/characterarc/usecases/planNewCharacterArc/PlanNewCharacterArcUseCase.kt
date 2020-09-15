package com.soyle.stories.characterarc.usecases.planNewCharacterArc

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.entities.*
import com.soyle.stories.theme.repositories.CharacterArcRepository
import com.soyle.stories.theme.usecases.createTheme.CreatedTheme
import com.soyle.stories.translators.asCharacterArcSection
import java.util.*

class PlanNewCharacterArcUseCase(
    private val characterRepository: com.soyle.stories.character.repositories.CharacterRepository,
    private val themeRepository: com.soyle.stories.characterarc.repositories.ThemeRepository,
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

        themeRepository.addNewTheme(themeWithCharacter)
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
        return theme.withCharacterIncluded(character.id, character.name, character.media)
            .withCharacterPromoted(character.id)
    }

    private suspend fun getCharacter(characterId: UUID) =
        characterRepository.getCharacterById(Character.Id(characterId))
            ?: throw CharacterDoesNotExist(characterId)
}