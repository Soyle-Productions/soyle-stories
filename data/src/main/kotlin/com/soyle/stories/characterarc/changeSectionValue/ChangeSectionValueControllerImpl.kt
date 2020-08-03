package com.soyle.stories.characterarc.changeSectionValue

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.changeCharacterArcSectionValue.ChangeCharacterDesire
import com.soyle.stories.theme.usecases.changeCharacterArcSectionValue.ChangeCharacterMoralWeakness
import com.soyle.stories.theme.usecases.changeCharacterArcSectionValue.ChangeCharacterPsychologicalWeakness
import java.util.*

class ChangeSectionValueControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val changeCharacterDesire: ChangeCharacterDesire,
    private val changeCharacterDesireOutputPort: ChangeCharacterDesire.OutputPort,
    private val changeCharacterPsychologicalWeakness: ChangeCharacterPsychologicalWeakness,
    private val changeCharacterPsychologicalWeaknessOutputPort: ChangeCharacterPsychologicalWeakness.OutputPort,
    private val changeCharacterMoralWeakness: ChangeCharacterMoralWeakness,
    private val changeCharacterMoralWeaknessOutputPort: ChangeCharacterMoralWeakness.OutputPort
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
}