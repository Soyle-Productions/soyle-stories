package com.soyle.stories.usecase.character.arc.viewBaseStoryStructure

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.domain.theme.CharacterNotInTheme
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.characterInTheme.CharacterInTheme
import com.soyle.stories.domain.theme.characterInTheme.MajorCharacter
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.theme.ThemeDoesNotExist
import com.soyle.stories.usecase.theme.ThemeRepository
import java.util.*

class ViewBaseStoryStructureUseCase(
    private val themeRepository: ThemeRepository,
    private val characterArcRepository: CharacterArcRepository
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

        val characterArc =
            characterArcRepository.getCharacterArcByCharacterAndThemeId(characterInTheme.id, theme.id)

        ViewBaseStoryStructure.ResponseModel(
            themeId,
            characterId,
            (characterArc?.arcSections ?: listOf()).filter { it.template.isRequired }.map {
                ViewBaseStoryStructure.StoryStructureSection(
                    it.id.uuid,
                    it.value,
                    it.template.name,
                    mapOf(),
                    it.linkedLocation?.uuid
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