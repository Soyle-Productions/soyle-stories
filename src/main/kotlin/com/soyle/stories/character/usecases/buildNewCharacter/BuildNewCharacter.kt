package com.soyle.stories.character.usecases.buildNewCharacter

import com.soyle.stories.character.CharacterException
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import java.util.*

interface BuildNewCharacter {

    suspend operator fun invoke(projectId: UUID, name: String, outputPort: OutputPort)
    suspend fun createAndIncludeInTheme(name: String, themeId: UUID, outputPort: OutputPort)

    interface OutputPort {
        fun receiveBuildNewCharacterFailure(failure: CharacterException)
        fun receiveBuildNewCharacterResponse(response: CharacterItem)
        suspend fun characterIncludedInTheme(response: CharacterIncludedInTheme)
    }
}