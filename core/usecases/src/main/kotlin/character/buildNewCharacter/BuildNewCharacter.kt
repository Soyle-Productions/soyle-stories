package com.soyle.stories.usecase.character.buildNewCharacter

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.arc.listAllCharacterArcs.CharacterItem
import com.soyle.stories.usecase.theme.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.usecase.theme.useCharacterAsOpponent.CharacterUsedAsOpponent
import java.util.*

interface BuildNewCharacter {

    suspend operator fun invoke(projectId: Project.Id, name: NonBlankString, output: OutputPort): Result<Character.Id>

    fun interface OutputPort {
        suspend fun characterCreated(response: CharacterCreated)
    }
}