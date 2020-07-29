package com.soyle.stories.characterarc.usecases.planNewCharacterArc

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterArcItem
import com.soyle.stories.entities.*
import com.soyle.stories.theme.repositories.CharacterArcSectionRepository
import com.soyle.stories.theme.usecases.ThemeItem
import com.soyle.stories.theme.usecases.createTheme.CreatedTheme
import com.soyle.stories.translators.asCharacterArcSection
import java.util.*

class PlanNewCharacterArcUseCase(
    private val characterRepository: com.soyle.stories.character.repositories.CharacterRepository,
    private val themeRepository: com.soyle.stories.characterarc.repositories.ThemeRepository,
    private val characterArcSectionRepository: CharacterArcSectionRepository
) : PlanNewCharacterArc {

    override suspend fun invoke(
        characterId: UUID,
        name: String,
        outputPort: PlanNewCharacterArc.OutputPort
    ) {
        val character = getCharacter(characterId)
        val theme = Theme(character.projectId, name, emptyList(), "")

        val (characterWithArc, themeWithCharacter) = addArcToCharacter(character, name, theme)

        val initialSections = themeWithCharacter.getMajorCharacterById(character.id)!!.thematicSections

        characterRepository.updateCharacter(characterWithArc)
        themeRepository.addNewTheme(themeWithCharacter)
        characterArcSectionRepository.addNewCharacterArcSections(initialSections.map { it.asCharacterArcSection() })

        outputPort.characterArcPlanned(
            PlanNewCharacterArc.ResponseModel(
                CreatedCharacterArc(theme.id.uuid, characterId, name),
                CreatedTheme(theme.projectId.uuid, theme.id.uuid, theme.name)
            )
        )

    }

    private fun addArcToCharacter(
        character: Character,
        name: String,
        theme: Theme
    ): Pair<Character, Theme> {
        val characterWithArc = character.withCharacterArc(name, theme.id)
        val themeWithCharacter = theme.withCharacterIncluded(character.id, character.name, character.media)
            .withCharacterPromoted(character.id)
        return Pair(characterWithArc, themeWithCharacter)
    }

    private suspend fun getCharacter(characterId: UUID) =
        characterRepository.getCharacterById(Character.Id(characterId))
            ?: throw CharacterDoesNotExist(characterId)
}