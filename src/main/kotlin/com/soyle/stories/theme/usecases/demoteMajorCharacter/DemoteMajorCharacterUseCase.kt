/**
 * Created by Brendan
 * Date: 3/5/2020
 * Time: 3:42 PM
 */
package com.soyle.stories.theme.usecases.demoteMajorCharacter

import arrow.core.identity
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.characterInTheme.MajorCharacter
import com.soyle.stories.theme.*
import com.soyle.stories.theme.repositories.CharacterArcRepository
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
        val removedCharacterArc = removeCharacterArc(updatedTheme.id, character.id)
        saveTheme(updatedTheme)
        return MajorCharacterDemotionResult(updatedTheme, character, removedCharacterArc.arcSections.map { it.id })
    }

    private suspend fun saveTheme(
        updatedTheme: Theme
    ) {/*
        if (updatedTheme.characters.filterIsInstance<MajorCharacter>().isEmpty()) {
            context.themeRepository.deleteTheme(updatedTheme)
        } else {*/
            updateTheme(updatedTheme)
        //}
    }

    private fun Theme.tryToDemoteCharacter(character: MajorCharacter): Theme {
        return demoteCharacter(character).fold(
            { throw it },
            ::identity
        )
    }

    private suspend fun removeCharacterArc(themeId: Theme.Id, characterId: Character.Id): CharacterArc {
        val arc = context.characterArcRepository.getCharacterArcByCharacterAndThemeId(characterId, themeId)!!
        context.characterArcRepository.removeCharacterArcs(arc)
        return arc
    }

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