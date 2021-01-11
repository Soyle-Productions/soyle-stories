package com.soyle.stories.scene.sceneDetails.includedCharacter

import com.soyle.stories.character.renameCharacter.CharacterRenamedReceiver
import com.soyle.stories.entities.CharacterRenamed
import com.soyle.stories.gui.View
import com.soyle.stories.scene.coverArcSectionsInScene.CharacterArcSectionUncoveredInSceneReceiver
import com.soyle.stories.scene.coverArcSectionsInScene.CharacterArcSectionsCoveredBySceneReceiver
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.AvailableCharacterArcSectionsForCharacterInScene
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.CharacterArcSectionCoveredByScene
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.CharacterArcSectionUncoveredInScene
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.GetAvailableCharacterArcsForCharacterInScene
import com.soyle.stories.scene.usecases.setMotivationForCharacterInScene.SetMotivationForCharacterInScene

class IncludedCharacterInScenePresenter(
    private val sceneId: String,
    private val characterId: String,
    private val view: View.Nullable<IncludedCharacterInSceneViewModel>
) : CharacterRenamedReceiver, GetAvailableCharacterArcsForCharacterInScene.OutputPort,
    SetMotivationForCharacterInScene.OutputPort, CharacterArcSectionsCoveredBySceneReceiver,
    CharacterArcSectionUncoveredInSceneReceiver {

    override suspend fun availableCharacterArcSectionsForCharacterInSceneListed(response: AvailableCharacterArcSectionsForCharacterInScene) {
        if (
            response.characterId.toString() != characterId ||
            response.sceneId.toString() != sceneId
        ) return
        view.updateOrInvalidated {
            copy(
                availableCharacterArcSections = response.map { arc ->
                    AvailableCharacterArcViewModel(
                        arc.characterArcId.toString(),
                        arc.themeId.toString(),
                        arc.characterArcName,
                        arc.filter { it.usedInScene }.size,
                        arc.all { it.usedInScene },
                        arc.map {
                            AvailableArcSectionViewModel(
                                it.arcSectionId.toString(),
                                arcSectionListedLabel(it.templateName, it.sectionValue, it.isMultiTemplate),
                                it.usedInScene,
                                arcSectionDisplayLabel(arc.characterArcName, it.templateName, it.sectionValue, it.isMultiTemplate)
                            )
                        }
                    )
                }
            )
        }
    }

    override suspend fun receiveCharacterRenamed(characterRenamed: CharacterRenamed) {
        if (characterRenamed.characterId.toString() != characterId) return
        view.updateOrInvalidated {
            copy(characterName = characterRenamed.newName)
        }
    }

    override fun motivationSetForCharacterInScene(response: SetMotivationForCharacterInScene.ResponseModel) {
        if (
            response.sceneId.toString() != sceneId ||
            response.characterId.toString() != characterId
        ) return
        view.updateOrInvalidated {
            copy(
                motivation = response.motivation ?: "",
                motivationCanBeReset = response.motivation != null
            )
        }
    }

    override suspend fun receiveCharacterArcSectionsCoveredByScene(sections: List<CharacterArcSectionCoveredByScene>) {
        val applicableSections =
            sections.filter { it.sceneId.toString() == sceneId && it.characterId.toString() == characterId }
        if (applicableSections.isNotEmpty()) {
            view.updateOrInvalidated {
                copy(
                    coveredArcSections = coveredArcSections + applicableSections.map {
                        CoveredArcSectionViewModel(
                            it.characterArcSectionId.toString(),
                            it.characterArcId.toString(),
                            // TODO add [isMultiTemplate] property to [CharacterArcSectionCoveredByScene]
                            arcSectionDisplayLabel(it.characterArcName, it.characterArcSectionName, it.characterArcSectionValue, false)
                        )
                    }
                )
            }
        }
    }

    override suspend fun receiveCharacterArcSectionUncoveredInScene(events: List<CharacterArcSectionUncoveredInScene>) {
        val applicableSections = events.filter { it.sceneId.toString() == sceneId && it.characterId.toString() == characterId }
            .map { it.characterArcSectionId.toString() }.toSet()
        if (applicableSections.isNotEmpty()) {
            view.updateOrInvalidated {
                copy(
                    coveredArcSections = coveredArcSections.filterNot {
                        it.arcSectionId in applicableSections
                    }
                )
            }
        }
    }


    override fun failedToSetMotivationForCharacterInScene(failure: Exception) {}

    companion object {
        fun arcSectionListedLabel(arcSectionTemplateName: String, arcSectionValue: String, isMultiTemplate: Boolean): String
                = arcSectionTemplateName + if (isMultiTemplate) " - \"$arcSectionValue\"" else ""

        fun arcSectionDisplayLabel(characterArcName: String, arcSectionTemplateName: String, arcSectionValue: String, isMultiTemplate: Boolean): String
                = "$characterArcName - ${arcSectionListedLabel(arcSectionTemplateName, arcSectionValue, isMultiTemplate)}"
    }

}