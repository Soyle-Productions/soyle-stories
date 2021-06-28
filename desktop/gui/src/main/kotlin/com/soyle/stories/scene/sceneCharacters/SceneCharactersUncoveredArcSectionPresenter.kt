package com.soyle.stories.scene.sceneCharacters

import com.soyle.stories.gui.View
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.CharacterArcSectionUncoveredInSceneReceiver
import com.soyle.stories.usecase.scene.character.coverCharacterArcSectionsInScene.CharacterArcSectionUncoveredInScene

class SceneCharactersUncoveredArcSectionPresenter(
    private val view: View.Nullable<SceneCharactersViewModel>
) : CharacterArcSectionUncoveredInSceneReceiver {

    override suspend fun receiveCharacterArcSectionUncoveredInScene(events: List<CharacterArcSectionUncoveredInScene>) {
        val sceneUUID = view.viewModel?.targetSceneId?.uuid ?: return
        val removedSections = events.filter { it.sceneId == sceneUUID }
        if (removedSections.isEmpty()) return
        val removedSectionsByCharacter = removedSections.groupBy { it.characterId }
        println("sections uncovered ${removedSections}")
        view.updateOrInvalidated {
            copy(
                includedCharacters = includedCharacters?.map { includedCharacter ->
                    val removedSectionsForCharacter = removedSectionsByCharacter[includedCharacter.id.uuid]?.map { it.characterArcSectionId }
                    if (removedSectionsForCharacter == null) includedCharacter
                    else {
                        includedCharacter.copy(
                            coveredArcSections = includedCharacter.coveredArcSections.filterNot {
                                it.arcSectionId.uuid in removedSectionsForCharacter
                            }
                        )
                    }

                }
            )
        }
    }
}