package com.soyle.stories.usecase.theme.compareCharacterValues

import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.characterInTheme.MajorCharacter
import com.soyle.stories.domain.theme.oppositionValue.OppositionValue
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.usecase.theme.ThemeDoesNotExist
import com.soyle.stories.usecase.theme.ThemeRepository
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
                    .map { (op, web) -> CharacterValue(web.id.uuid, web.name.value, op.id.uuid, op.name.value) }
                    .toList(),
                it is MajorCharacter
            )
        }
    }

}