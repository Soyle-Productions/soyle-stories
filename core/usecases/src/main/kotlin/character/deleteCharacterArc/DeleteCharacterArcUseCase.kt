package com.soyle.stories.usecase.character.deleteCharacterArc

import com.soyle.stories.usecase.theme.demoteMajorCharacter.DemoteMajorCharacter
import java.util.*

class DeleteCharacterArcUseCase(private val demoteMajorCharacter: DemoteMajorCharacter) : DeleteCharacterArc {
    override suspend fun invoke(themeId: UUID, characterId: UUID, output: DemoteMajorCharacter.OutputPort) =
        demoteMajorCharacter.invoke(themeId, characterId, output)
}