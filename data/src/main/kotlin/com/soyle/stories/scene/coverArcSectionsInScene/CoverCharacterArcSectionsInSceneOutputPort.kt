package com.soyle.stories.scene.coverArcSectionsInScene

import com.soyle.stories.character.createArcSection.CreatedCharacterArcSectionReceiver
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.*

class CoverCharacterArcSectionsInSceneOutputPort(
    private val coverCharacterArcSectionsInSceneReceiver: CharacterArcSectionsCoveredBySceneReceiver,
    private val characterArcSectionUncoveredInSceneReceiver: CharacterArcSectionUncoveredInSceneReceiver,
    private val createdCharacterArcSectionReceiver: CreatedCharacterArcSectionReceiver
) :
    CoverCharacterArcSectionsInScene.OutputPort,
    ChangeCharacterArcSectionValueAndCoverInScene.OutputPort,
    CreateCharacterArcSectionAndCoverInScene.OutputPort
{

    override suspend fun characterArcSectionsCoveredInScene(response: CoverCharacterArcSectionsInScene.ResponseModel) {
        if (response.sectionsCoveredByScene.isNotEmpty()) {
            coverCharacterArcSectionsInSceneReceiver.receiveCharacterArcSectionsCoveredByScene(response.sectionsCoveredByScene)
        }
        if (response.sectionsUncovered.isNotEmpty()) {
            characterArcSectionUncoveredInSceneReceiver.receiveCharacterArcSectionUncoveredInScene(response.sectionsUncovered)
        }
    }

    override suspend fun characterArcSectionValueChangedAndAddedToScene(response: ChangeCharacterArcSectionValueAndCoverInScene.ResponseModel) {
        coverCharacterArcSectionsInSceneReceiver.receiveCharacterArcSectionsCoveredByScene(
            listOf(response.characterArcSectionCoveredByScene))
    }

    override suspend fun characterArcCreatedAndCoveredInScene(response: CreateCharacterArcSectionAndCoverInScene.ResponseModel) {
        createdCharacterArcSectionReceiver.receiveCreatedCharacterArcSection(
            response.createdCharacterArcSection
        )
        coverCharacterArcSectionsInSceneReceiver.receiveCharacterArcSectionsCoveredByScene(
            listOf(response.characterArcSectionCoveredByScene))
    }

}