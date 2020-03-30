package com.soyle.stories.characterarc.usecases.deleteLocalCharacterArc

import com.soyle.stories.characterarc.LocalCharacterArcException
import java.util.*

interface DeleteLocalCharacterArc {
    suspend operator fun invoke(themeId: UUID, characterId: UUID, output: OutputPort)

    class ResponseModel(val themeId: UUID, val characterId: UUID, val removedCharacterArcSections: List<UUID>, val removedTools: List<UUID>, val themeRemoved: Boolean)

    interface OutputPort {
        fun receiveDeleteLocalCharacterArcFailure(failure: LocalCharacterArcException)
        fun receiveDeleteLocalCharacterArcResponse(response: ResponseModel)
    }
}