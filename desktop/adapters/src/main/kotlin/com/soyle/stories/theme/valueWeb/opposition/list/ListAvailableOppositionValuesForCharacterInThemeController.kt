package com.soyle.stories.theme.valueWeb.opposition.list

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.usecase.theme.listAvailableOppositionValuesForCharacterInTheme.ListAvailableOppositionValuesForCharacterInTheme
import kotlinx.coroutines.Job

interface ListAvailableOppositionValuesForCharacterInThemeController {
    companion object {

        operator fun invoke(
            threadTransformer: ThreadTransformer,
            getAvailableOppositionValuesForCharacterInTheme: ListAvailableOppositionValuesForCharacterInTheme
        ): ListAvailableOppositionValuesForCharacterInThemeController =
            object : ListAvailableOppositionValuesForCharacterInThemeController {
                override fun listAvailableOppositionValuesForCharacter(
                    themeId: Theme.Id,
                    characterId: Character.Id,
                    output: ListAvailableOppositionValuesForCharacterInTheme.OutputPort
                ): Job {
                    return threadTransformer.async {
                        getAvailableOppositionValuesForCharacterInTheme(
                            themeId.uuid,
                            characterId.uuid,
                            output
                        )
                    }
                }
            }
    }

    fun listAvailableOppositionValuesForCharacter(
        themeId: Theme.Id,
        characterId: Character.Id,
        output: ListAvailableOppositionValuesForCharacterInTheme.OutputPort
    ): Job
}