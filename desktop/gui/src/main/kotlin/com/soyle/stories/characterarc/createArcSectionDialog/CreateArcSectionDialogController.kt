package com.soyle.stories.characterarc.createArcSectionDialog

import com.soyle.stories.character.createArcSection.CreateArcSectionController
import com.soyle.stories.characterarc.changeSectionValue.ChangeSectionValueController
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.scene.coverCharacterArcSectionsInScene.GetAvailableCharacterArcSectionTypesForCharacterArc
import java.util.*

class CreateArcSectionDialogController(
    private val threadTransformer: ThreadTransformer,
    private val getAvailableCharacterArcSectionTypesForCharacterArc: GetAvailableCharacterArcSectionTypesForCharacterArc,
    private val getAvailableCharacterArcSectionTypesForCharacterArcOutputPort: GetAvailableCharacterArcSectionTypesForCharacterArc.OutputPort,
    private val createArcSectionController: CreateArcSectionController,
    private val changeArcSectionController: ChangeSectionValueController
) : CreateArcSectionDialogViewListener {

    override fun getValidState(themeUUID: String, characterUUID: String) {
        threadTransformer.async {
            getAvailableCharacterArcSectionTypesForCharacterArc.invoke(
                UUID.fromString(themeUUID),
                UUID.fromString(characterUUID),
                getAvailableCharacterArcSectionTypesForCharacterArcOutputPort
            )
        }
    }

    override fun createArcSection(characterId: String, themeId: String, sectionTemplateId: String, sceneId: String, description: String) {
        createArcSectionController.createArcSectionAndCoverInScene(characterId, themeId, sectionTemplateId, description, sceneId)
    }

    override fun modifyArcSection(characterId: String, themeId: String, arcSectionId: String, sceneId: String, description: String) {
        changeArcSectionController.changeValueOfArcSectionAndCoverInScene(
            themeId, characterId, arcSectionId, description, sceneId
        )
    }

}