package com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene

import com.soyle.stories.usecase.scene.character.coverCharacterArcSectionsInScene.CoverCharacterArcSectionsInScene

class CoverCharacterArcSectionsInSceneOutputPort(
    private val coverCharacterArcSectionsInSceneReceiver: CharacterArcSectionsCoveredBySceneReceiver,
    private val characterArcSectionUncoveredInSceneReceiver: CharacterArcSectionUncoveredInSceneReceiver
) : CoverCharacterArcSectionsInScene.OutputPort {

    override suspend fun characterArcSectionsCoveredInScene(response: CoverCharacterArcSectionsInScene.ResponseModel) {
        if (response.sectionsCoveredByScene.isNotEmpty()) {
            coverCharacterArcSectionsInSceneReceiver.receiveCharacterArcSectionsCoveredByScene(response.sectionsCoveredByScene)
        }
        if (response.sectionsUncovered.isNotEmpty()) {
            characterArcSectionUncoveredInSceneReceiver.receiveCharacterArcSectionUncoveredInScene(response.sectionsUncovered)
        }
    }

}