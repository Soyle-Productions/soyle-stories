package com.soyle.stories.characterarc.usecases.viewBaseStoryStructure

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.CharacterInTheme
import com.soyle.stories.entities.theme.MajorCharacter
import com.soyle.stories.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.theme.CharacterNotInTheme
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.repositories.CharacterArcSectionRepository
import java.util.*

/**
 * Created by Brendan
 * Date: 2/9/2020
 * Time: 10:49 AM
 */
class ViewBaseStoryStructureUseCase(
    private val themeRepository: com.soyle.stories.characterarc.repositories.ThemeRepository,
    private val characterArcSectionRepository: CharacterArcSectionRepository
) : ViewBaseStoryStructure {

    override suspend fun invoke(
        characterId: UUID,
        themeId: UUID,
        outputPort: ViewBaseStoryStructure.OutputPort
    ) {
        val theme = getThemeById(themeId, outputPort) ?: return
        val characterInTheme = theme.getCharacterInTheme(characterId, outputPort) ?: return

        if (characterInTheme !is MajorCharacter) return outputPort.receiveViewBaseStoryStructureFailure(
            CharacterIsNotMajorCharacterInTheme(characterId, themeId)
        )

        val sections =
            characterArcSectionRepository.getCharacterArcSectionsForCharacterInTheme(characterInTheme.id, theme.id)

        ViewBaseStoryStructure.ResponseModel(
            themeId,
            characterId,
            sections.filter { it.template.isRequired }.map {
                ViewBaseStoryStructure.StoryStructureSection(
                    it.id.uuid,
                    it.value,
                    it.template.name,
                    mapOf()
                )
            }
        ).let(outputPort::receiveViewBaseStoryStructureResponse)
    }

    private suspend fun getThemeById(
        themeId: UUID,
        outputPort: ViewBaseStoryStructure.OutputPort
    ): Theme? {
        val theme = themeRepository.getThemeById(Theme.Id(themeId))
        if (theme == null) {
            outputPort.receiveViewBaseStoryStructureFailure(
                ThemeDoesNotExist(themeId)
            )
        }
        return theme
    }

    private fun Theme.getCharacterInTheme(
        characterId: UUID,
        outputPort: ViewBaseStoryStructure.OutputPort
    ): CharacterInTheme? {
        val characterInTheme = getIncludedCharacterById(Character.Id(characterId))
        if (characterInTheme == null) {
            CharacterNotInTheme(id.uuid, characterId).let(outputPort::receiveViewBaseStoryStructureFailure)
        }
        return characterInTheme
    }

}