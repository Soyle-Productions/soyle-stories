package com.soyle.stories.character.createArcSection

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.CreateCharacterArcSectionAndCoverInScene
import kotlinx.coroutines.runBlocking
import java.util.*

class CreateArcSectionControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val createCharacterArcSectionAndCoverInScene: CreateCharacterArcSectionAndCoverInScene,
    private val createCharacterArcSectionAndCoverInSceneOutputPort: CreateCharacterArcSectionAndCoverInScene.OutputPort
) : CreateArcSectionController {

    override fun createArcSectionAndCoverInScene(
        characterId: String,
        themeId: String,
        sectionTemplateId: String,
        value: String,
        sceneId: String
    ) {
        threadTransformer.async {
            createCharacterArcSectionAndCoverInScene.invoke(
                CreateCharacterArcSectionAndCoverInScene.RequestModel(
                    UUID.fromString(themeId),
                    UUID.fromString(characterId),
                    UUID.fromString(sceneId),
                    UUID.fromString(sectionTemplateId),
                    value
                ),
                createCharacterArcSectionAndCoverInSceneOutputPort
            )
        }
    }

}