package com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene

import java.util.*

interface CoverCharacterArcSectionsInScene {

    class RequestModel( val sceneId: UUID, val characterId: UUID, val removeSections: List<UUID>, vararg sections: UUID) {
        val sections = sections.toList()
    }

    suspend fun listAvailableCharacterArcsForCharacterInScene(sceneId: UUID, characterId: UUID, output: OutputPort)
    suspend operator fun invoke(request: RequestModel, output: OutputPort)


    class ResponseModel(
        val sectionsCoveredByScene: List<CharacterArcSectionCoveredByScene>,
        val sectionsUncovered: List<CharacterArcSectionUncoveredInScene>
    )

    interface OutputPort {
        suspend fun availableCharacterArcSectionsForCharacterInSceneListed(response: AvailableCharacterArcSectionsForCharacterInScene)

        suspend fun characterArcSectionsCoveredInScene(response: ResponseModel)
    }

}