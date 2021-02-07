package com.soyle.stories.usecase.theme.listAvailableOppositionValuesForCharacterInTheme

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.CharacterNotInTheme
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.characterInTheme.CharacterInTheme
import com.soyle.stories.usecase.theme.ThemeDoesNotExist
import com.soyle.stories.usecase.theme.ThemeRepository
import com.soyle.stories.usecase.theme.listOppositionsInValueWeb.OppositionValueItem
import java.util.*

class ListAvailableOppositionValuesForCharacterInThemeUseCase(
    private val themeRepository: ThemeRepository
) : ListAvailableOppositionValuesForCharacterInTheme {

    override suspend fun invoke(
        themeId: UUID,
        characterId: UUID,
        output: ListAvailableOppositionValuesForCharacterInTheme.OutputPort
    ) {
        val theme = getTheme(themeId)
        val characterInTheme = getCharacterInTheme(theme, characterId)
        output.availableOppositionValuesListedForCharacterInTheme(
            getAvailableOppositionValuesForCharacterInTheme(theme, characterInTheme)
        )
    }

    private suspend fun getTheme(themeId: UUID): Theme {
        return themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId)
    }

    private fun getCharacterInTheme(theme: Theme, characterId: UUID): CharacterInTheme {
        return theme.getIncludedCharacterById(Character.Id(characterId))
            ?: throw CharacterNotInTheme(theme.id.uuid, characterId)
    }

    private fun getAvailableOppositionValuesForCharacterInTheme(
        theme: Theme,
        characterInTheme: CharacterInTheme
    ): OppositionValuesAvailableForCharacterInTheme {
        return OppositionValuesAvailableForCharacterInTheme(
            theme.id.uuid,
            characterInTheme.id.uuid,
            collectAllValueWebsInTheme(theme, characterInTheme.id.uuid)
        )
    }

    private fun collectAllValueWebsInTheme(
        theme: Theme,
        characterId: UUID
    ): List<AvailableValueWebForCharacterInTheme> {
        return theme.valueWebs.map {
            AvailableValueWebForCharacterInTheme(
                it.id.uuid,
                it.name.value,
                it.oppositions.find { it.hasEntityAsRepresentation(characterId) }
                    ?.let { OppositionValueItem(it.id.uuid, it.name.value) },
                it.oppositions.asSequence()
                    .filterNot { it.hasEntityAsRepresentation(characterId) }
                    .map {
                        AvailableOppositionValueForCharacterInTheme(
                            it.id.uuid,
                            it.name.value
                        )
                    }
                    .toList()
            )
        }
    }
}