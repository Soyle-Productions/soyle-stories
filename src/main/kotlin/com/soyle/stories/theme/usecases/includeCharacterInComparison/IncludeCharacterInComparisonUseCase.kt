package com.soyle.stories.theme.usecases.includeCharacterInComparison

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.character.CharacterException
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.repositories.CharacterArcSectionRepository
import com.soyle.stories.theme.repositories.CharacterRepository
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.translators.asCharacterArcTemplateSection
import java.util.*

class IncludeCharacterInComparisonUseCase(
    private val characterRepository: CharacterRepository,
    private val themeRepository: ThemeRepository,
    private val characterArcSectionRepository: CharacterArcSectionRepository
) : IncludeCharacterInComparison {

    override suspend fun invoke(characterId: UUID, themeId: UUID, output: IncludeCharacterInComparison.OutputPort) {
        val response = try {
            includeCharacterInComparison(characterId, themeId)
        } catch (c: CharacterException) {
            return output.receiveIncludeCharacterInComparisonFailure(c)
        } catch (c: com.soyle.stories.characterarc.CharacterArcException) {
            return output.receiveIncludeCharacterInComparisonFailure(c)
        } catch (t: ThemeException) {
            return output.receiveIncludeCharacterInComparisonFailure(t)
        }
        output.receiveIncludeCharacterInComparisonResponse(response)
    }

    private suspend fun includeCharacterInComparison(
        characterId: UUID,
        themeId: UUID
    ): CharacterIncludedInTheme {
        val character = getCharacterById(characterId)
        val theme = getThemeById(themeId)
        val initialSections = createCharacterArcSectionsForCharacterInTheme(character, theme)

        return theme.includeCharacter(character, initialSections).fold(
            { throw it },
            {
                characterArcSectionRepository.addNewCharacterArcSections(initialSections)
                themeRepository.updateTheme(it)
                CharacterIncludedInTheme(
                    themeId,
                    characterId,
                    it.characters.map {
                        CharacterItem(
                            it.id.uuid,
                            it.name,
                            null
                        )
                    }
                )
            }
        )
    }

    private suspend fun getCharacterById(characterId: UUID) =
        characterRepository.getCharacterById(Character.Id(characterId))
            ?: throw CharacterDoesNotExist(characterId)

    private suspend fun getThemeById(themeId: UUID) =
        themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId)

    private fun createCharacterArcSectionsForCharacterInTheme(
        character: Character,
        theme: Theme
    ): List<CharacterArcSection> =
        theme.thematicTemplate.sections.map {
            CharacterArcSection(
                CharacterArcSection.Id(UUID.randomUUID()),
                character.id, theme.id, it.asCharacterArcTemplateSection(),
              null,
                ""
            )
        }
}