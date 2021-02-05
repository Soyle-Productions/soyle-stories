/**
 * Created by Brendan
 * Date: 3/6/2020
 * Time: 1:25 PM
 */
package com.soyle.stories.characterarc.usecases.deleteCharacterArc

import com.soyle.stories.theme.usecases.demoteMajorCharacter.DemoteMajorCharacter
import java.util.*

class DeleteCharacterArcUseCase(private val demoteMajorCharacter: DemoteMajorCharacter) : DeleteCharacterArc {
    override suspend fun invoke(themeId: UUID, characterId: UUID, output: DemoteMajorCharacter.OutputPort) =
        demoteMajorCharacter.invoke(themeId, characterId, output)
}