package com.soyle.stories.scene.sceneCharacters

import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.gui.View
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.CharacterArcSectionsCoveredBySceneReceiver
import com.soyle.stories.usecase.scene.charactersInScene.coverCharacterArcSectionsInScene.CharacterArcSectionCoveredByScene

class SceneCharactersCoveredArcSectionsPresenter(
    private val view: View.Nullable<SceneCharactersViewModel>
) : CharacterArcSectionsCoveredBySceneReceiver {

    override suspend fun receiveCharacterArcSectionsCoveredByScene(sections: List<CharacterArcSectionCoveredByScene>) {
        val sceneUUID = view.viewModel?.targetSceneId?.uuid ?: return
        val newSections = sections.filter { it.sceneId == sceneUUID }
        if (newSections.isEmpty()) return
        val newSectionsByCharacter = newSections.groupBy { it.characterId }
        println("new section covered ${newSections}")
        view.updateOrInvalidated {
            copy(
                includedCharacters = includedCharacters?.map { includedCharacter ->
                    val newSectionsForCharacter = newSectionsByCharacter[includedCharacter.id.uuid]
                    if (newSectionsForCharacter == null) includedCharacter
                    else {
                        includedCharacter.copy(
                            coveredArcSections = includedCharacter.coveredArcSections + newSectionsForCharacter.map {
                                CoveredArcSectionViewModel(
                                    CharacterArcSection.Id(it.characterArcSectionId),
                                    CharacterArc.Id(it.characterArcId),
                                    "${it.characterArcName} - ${it.characterArcSectionName}"
                                )
                            }
                        )
                    }

                }
            )
        }
    }
}