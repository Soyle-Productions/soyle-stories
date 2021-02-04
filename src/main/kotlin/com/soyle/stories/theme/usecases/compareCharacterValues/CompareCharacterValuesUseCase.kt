package com.soyle.stories.theme.usecases.compareCharacterValues

import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.characterInTheme.MajorCharacter
import com.soyle.stories.entities.theme.oppositionValue.OppositionValue
import com.soyle.stories.entities.theme.valueWeb.ValueWeb
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.repositories.ThemeRepository
import java.util.*

class CompareCharacterValuesUseCase(
    private val themeRepository: ThemeRepository
) : CompareCharacterValues {

    override suspend fun invoke(themeId: UUID, output: CompareCharacterValues.OutputPort) {
        val theme = getTheme(themeId)

        val allOppositions = collectOppositionsInTheme(theme)
        val charactersWithValues = organizeOppositionsByCharactersThatRepresentThemInTheme(allOppositions, theme)

        output.charactersCompared(CharacterValueComparison(themeId, charactersWithValues))
    }

    private suspend fun getTheme(themeId: UUID) = (themeRepository.getThemeById(Theme.Id(themeId))
        ?: throw ThemeDoesNotExist(themeId))

    private fun collectOppositionsInTheme(theme: Theme): Sequence<Pair<OppositionValue, ValueWeb>> {
        return theme.valueWebs.asSequence().flatMap { web ->
            web.oppositions.asSequence().map { it to web }
        }
    }

    private fun organizeOppositionsByCharactersThatRepresentThemInTheme(
        oppositions: Sequence<Pair<OppositionValue, ValueWeb>>,
        theme: Theme
    ): List<CharacterComparedWithValues> {
        return theme.characters.map {
            CharacterComparedWithValues(
                it.id.uuid,
                it.name,
                it.archetype,
                oppositions
                    .filter { (op, _) -> op.hasEntityAsRepresentation(it.id.uuid) }
                    .map { (op, web) -> CharacterValue(web.id.uuid, web.name, op.id.uuid, op.name) }
                    .toList(),
                it is MajorCharacter
            )
        }
    }

}