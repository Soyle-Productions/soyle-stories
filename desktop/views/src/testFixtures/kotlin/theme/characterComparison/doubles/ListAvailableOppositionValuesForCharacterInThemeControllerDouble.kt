package com.soyle.stories.desktop.view.theme.characterComparison.doubles

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.theme.valueWeb.opposition.list.ListAvailableOppositionValuesForCharacterInThemeController
import com.soyle.stories.usecase.theme.listAvailableOppositionValuesForCharacterInTheme.ListAvailableOppositionValuesForCharacterInTheme
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job

class ListAvailableOppositionValuesForCharacterInThemeControllerDouble(
    val onInvoke: (Theme.Id, Character.Id, ListAvailableOppositionValuesForCharacterInTheme.OutputPort) -> Unit = { _, _, _ -> },
    var job: CompletableJob = Job()
) : ListAvailableOppositionValuesForCharacterInThemeController {

    override fun listAvailableOppositionValuesForCharacter(
        themeId: Theme.Id,
        characterId: Character.Id,
        output: ListAvailableOppositionValuesForCharacterInTheme.OutputPort
    ): Job {
        onInvoke(themeId, characterId, output)
        return job
    }
}