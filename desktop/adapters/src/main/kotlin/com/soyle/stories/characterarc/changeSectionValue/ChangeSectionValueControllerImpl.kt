package com.soyle.stories.characterarc.changeSectionValue

import com.soyle.stories.usecase.character.changeCharacterArcSectionValue.ChangeCharacterArcSectionValue
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.character.changeCharacterArcSectionValue.ChangeCharacterDesire
import com.soyle.stories.usecase.character.changeCharacterArcSectionValue.ChangeCharacterMoralWeakness
import com.soyle.stories.usecase.character.changeCharacterArcSectionValue.ChangeCharacterPsychologicalWeakness
import com.soyle.stories.usecase.scene.coverCharacterArcSectionsInScene.ChangeCharacterArcSectionValueAndCoverInScene
import java.util.*

class ChangeSectionValueControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val changeCharacterDesire: ChangeCharacterDesire,
    private val changeCharacterDesireOutputPort: ChangeCharacterDesire.OutputPort,
    private val changeCharacterPsychologicalWeakness: ChangeCharacterPsychologicalWeakness,
    private val changeCharacterPsychologicalWeaknessOutputPort: ChangeCharacterPsychologicalWeakness.OutputPort,
    private val changeCharacterMoralWeakness: ChangeCharacterMoralWeakness,
    private val changeCharacterMoralWeaknessOutputPort: ChangeCharacterMoralWeakness.OutputPort,
    private val changeCharacterArcSectionValue: ChangeCharacterArcSectionValue,
    private val changeCharacterArcSectionValueOutputPort: ChangeCharacterArcSectionValue.OutputPort,
    private val changeCharacterArcSectionValueAndCoverInScene: ChangeCharacterArcSectionValueAndCoverInScene,
    private val changeCharacterArcSectionValueAndCoverInSceneOutputPort: ChangeCharacterArcSectionValueAndCoverInScene.OutputPort
) : ChangeSectionValueController {

    override fun changeDesire(themeId: String, characterId: String, desire: String) {
        val request = ChangeCharacterDesire.RequestModel(
            UUID.fromString(themeId),
            UUID.fromString(characterId),
            desire
        )
        threadTransformer.async {
            changeCharacterDesire.invoke(
                request, changeCharacterDesireOutputPort
            )
        }
    }

    override fun setMoralWeakness(themeId: String, characterId: String, weakness: String) {
        val request = ChangeCharacterMoralWeakness.RequestModel(
            UUID.fromString(themeId),
            UUID.fromString(characterId),
            weakness
        )
        threadTransformer.async {
            changeCharacterMoralWeakness.invoke(
                request, changeCharacterMoralWeaknessOutputPort
            )
        }
    }

    override fun setPsychologicalWeakness(themeId: String, characterId: String, weakness: String) {
        val request = ChangeCharacterPsychologicalWeakness.RequestModel(
            UUID.fromString(themeId),
            UUID.fromString(characterId),
            weakness
        )
        threadTransformer.async {
            changeCharacterPsychologicalWeakness.invoke(
                request, changeCharacterPsychologicalWeaknessOutputPort
            )
        }

    }

    override fun changeValueOfArcSection(themeId: String, characterId: String, arcSectionId: String, value: String) {
        val request = ChangeCharacterArcSectionValue.RequestModel(
            UUID.fromString(themeId),
            UUID.fromString(characterId),
            UUID.fromString(arcSectionId),
            value
        )
        threadTransformer.async {
            changeCharacterArcSectionValue.invoke(
                request, changeCharacterArcSectionValueOutputPort
            )
        }
    }

    override fun changeValueOfArcSectionAndCoverInScene(
        themeId: String,
        characterId: String,
        arcSectionId: String,
        value: String,
        sceneId: String
    ) {
        val request = ChangeCharacterArcSectionValueAndCoverInScene.RequestModel(
            UUID.fromString(themeId),
            UUID.fromString(characterId),
            UUID.fromString(arcSectionId),
            UUID.fromString(sceneId),
            value
        )
        threadTransformer.async {
            changeCharacterArcSectionValueAndCoverInScene.invoke(
                request, changeCharacterArcSectionValueAndCoverInSceneOutputPort
            )
        }
    }
}