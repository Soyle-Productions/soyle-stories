/**
 * Created by Brendan
 * Date: 3/5/2020
 * Time: 3:42 PM
 */
package com.soyle.stories.theme.usecases.demoteMajorCharacter

import arrow.core.identity
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.MajorCharacter
import com.soyle.stories.theme.*
import java.util.*

class DemoteMajorCharacterUseCase(
    private val context: Context
) : DemoteMajorCharacter {

    override suspend fun invoke(themeId: UUID, characterId: UUID, output: DemoteMajorCharacter.OutputPort) {
        val response = try {
            demoteMajorCharacter(themeId, characterId)
        } catch (t: ThemeException) {
            return output.receiveDemoteMajorCharacterFailure(t)
        }
        output.receiveDemoteMajorCharacterResponse(response)
    }

    private suspend fun demoteMajorCharacter(themeId: UUID, characterId: UUID): DemoteMajorCharacter.ResponseModel {
        val theme = getThemeById(themeId)
        val character = theme.getCharacterById(characterId)
        val update = theme.demoteMajorCharacter(character)
        return respondWith(update)
    }

    private suspend fun getThemeById(themeId: UUID) =
        context.themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId)

    private fun Theme.getCharacterById(characterId: UUID): MajorCharacter {
        val characterInTheme = getIncludedCharacterById(Character.Id(characterId))
            ?: throw CharacterNotInTheme(id.uuid, characterId)
        if (characterInTheme !is MajorCharacter) throw CharacterIsNotMajorCharacterInTheme(characterId, id.uuid)
        return characterInTheme
    }

    private suspend fun Theme.demoteMajorCharacter(character: MajorCharacter): MajorCharacterDemotionResult {
        val updatedTheme = tryToDemoteCharacter(character)
        val thematicSectionIds = removeThematicSectionsForThemeAndCharacter(updatedTheme, character)
        removeCharacterArc(updatedTheme.id, character.id)
        saveTheme(updatedTheme)
        return MajorCharacterDemotionResult(updatedTheme, character, thematicSectionIds)
    }

    private suspend fun saveTheme(
        updatedTheme: Theme
    ) {
        if (updatedTheme.characters.filterIsInstance<MajorCharacter>().isEmpty()) {
            context.themeRepository.deleteTheme(updatedTheme)
        } else {
            updateTheme(updatedTheme)
        }
    }

    private suspend fun removeThematicSectionsForThemeAndCharacter(
        receiver: Theme,
        character: MajorCharacter
    ): List<CharacterArcSection.Id> {
        val thematicSectionIds = receiver.collectThematicSectionIdsFrom(character)
        removeArcSections(thematicSectionIds)
        return thematicSectionIds
    }

    private fun Theme.tryToDemoteCharacter(character: MajorCharacter): Theme {
        return demoteCharacter(character).fold(
            { throw it },
            ::identity
        )
    }

    private fun Theme.collectThematicSectionIdsFrom(character: MajorCharacter): List<CharacterArcSection.Id> {
        val defaultThematicTemplateIds = thematicTemplate.ids
        return character.thematicSections.asSequence()
            .filterNot { it.template.characterArcTemplateSectionId in defaultThematicTemplateIds }
            .map { it.characterArcSectionId }.toList()
    }

    private suspend fun removeArcSections(arcSectionIds: List<CharacterArcSection.Id>) {
        val sectionsToRemove = context.characterArcSectionRepository.getCharacterArcSectionsById(arcSectionIds.toSet())
        context.characterArcSectionRepository.removeArcSections(sectionsToRemove)
    }

    private suspend fun removeCharacterArc(themeId: Theme.Id, characterId: Character.Id) =
        context.characterArcRepository.removeCharacterArc(themeId, characterId)

    private suspend fun updateTheme(updatedTheme: Theme) {
        context.themeRepository.updateTheme(updatedTheme)
    }

    private fun respondWith(update: MajorCharacterDemotionResult): DemoteMajorCharacter.ResponseModel {
        return DemoteMajorCharacter.ResponseModel(
            update.updatedTheme.id.uuid,
            update.demotedCharacter.id.uuid,
            update.removedArcSections.map { it.uuid },
            update.updatedTheme.characters.filterIsInstance<MajorCharacter>().isEmpty()
        )
    }
}