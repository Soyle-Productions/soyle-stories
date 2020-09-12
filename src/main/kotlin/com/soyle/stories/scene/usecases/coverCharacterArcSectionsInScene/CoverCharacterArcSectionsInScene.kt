package com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene

import java.util.*

interface CoverCharacterArcSectionsInScene {

    sealed class RequestModel {
        abstract val sceneId: UUID
        abstract val characterId: UUID

        class ListAvailableArcs(override val sceneId: UUID, override val characterId: UUID) : RequestModel()
        class CoverSections(override val sceneId: UUID, override val characterId: UUID, vararg sections: UUID) : RequestModel()
        {
            val sections = sections.toList()
        }
    }



    suspend fun listAvailableCharacterArcsForCharacterInScene(sceneId: UUID, characterId: UUID, output: OutputPort)
    suspend fun coverSectionsInScene(request: RequestModel.CoverSections, output: OutputPort)

    class ResponseModel(
        val sectionsCoveredByScene: List<CharacterArcSectionCoveredByScene>
    )

    interface OutputPort {
        suspend fun availableCharacterArcSectionsForCharacterInSceneListed(response: AvailableCharacterArcSectionsForCharacterInScene)

        suspend fun characterArcSectionsCoveredInScene(response: ResponseModel)
    }

}