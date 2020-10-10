package com.soyle.stories.scene.coverArcSectionsInScene

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.CoverCharacterArcSectionsInScene
import java.util.*

class CoverArcSectionsInSceneControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val coverCharacterArcSectionsInScene: CoverCharacterArcSectionsInScene,
    private val coverCharacterArcSectionsInSceneOutput: CoverCharacterArcSectionsInScene.OutputPort
) : CoverArcSectionsInSceneController {

    override fun coverCharacterArcSectionInScene(
        sceneId: String,
        characterId: String,
        characterArcSectionIds: List<String>,
        sectionsToUnCover: List<String>
    ) {
        val request = CoverCharacterArcSectionsInScene.RequestModel(
            UUID.fromString(sceneId),
            UUID.fromString(characterId),
            sectionsToUnCover.map(UUID::fromString),
            *characterArcSectionIds.map(UUID::fromString).toTypedArray()
        )
        threadTransformer.async {
            coverCharacterArcSectionsInScene.invoke(
                request, coverCharacterArcSectionsInSceneOutput
            )
        }
    }

}