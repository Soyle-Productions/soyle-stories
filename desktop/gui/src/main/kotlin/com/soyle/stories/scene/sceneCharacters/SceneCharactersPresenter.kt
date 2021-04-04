package com.soyle.stories.scene.sceneCharacters

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.gui.View
import com.soyle.stories.scene.charactersInScene.RenamedCharacterInSceneReceiver
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.CharacterArcSectionUncoveredInSceneReceiver
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.CharacterArcSectionsCoveredBySceneReceiver
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.IncludedCharacterInSceneReceiver
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemovedCharacterFromSceneReceiver
import com.soyle.stories.usecase.scene.charactersInScene.coverCharacterArcSectionsInScene.AvailableCharacterArcSectionsForCharacterInScene
import com.soyle.stories.usecase.scene.charactersInScene.coverCharacterArcSectionsInScene.GetAvailableCharacterArcsForCharacterInScene
import com.soyle.stories.usecase.scene.charactersInScene.listAvailableCharacters.AvailableCharactersToAddToScene
import com.soyle.stories.usecase.scene.charactersInScene.listAvailableCharacters.ListAvailableCharactersToIncludeInScene
import com.soyle.stories.usecase.scene.common.IncludedCharacterInScene
import com.soyle.stories.usecase.scene.getSceneDetails.GetSceneDetails

class SceneCharactersPresenter(
    private val view: View.Nullable<SceneCharactersViewModel>
) :
    GetSceneDetails.OutputPort,
    ListAvailableCharactersToIncludeInScene.OutputPort,
    GetAvailableCharacterArcsForCharacterInScene.OutputPort,
    IncludedCharacterInSceneReceiver by SceneCharactersIncludedCharacterPresenter(view),
    RemovedCharacterFromSceneReceiver by SceneCharactersRemovedCharacterPresenter(view),
    RenamedCharacterInSceneReceiver by SceneCharactersRenamedCharacterPresenter(view),
    CharacterArcSectionsCoveredBySceneReceiver by SceneCharactersCoveredArcSectionsPresenter(view),
    CharacterArcSectionUncoveredInSceneReceiver by SceneCharactersUncoveredArcSectionPresenter(view) {

    override fun sceneDetailsRetrieved(response: GetSceneDetails.ResponseModel) {
        view.update {
            SceneCharactersViewModel(
                Scene.Id(response.sceneId),
                null,
                response.characters.map(::includedCharacterViewModel)
            )
        }
    }


    override suspend fun receiveAvailableCharactersToAddToScene(response: AvailableCharactersToAddToScene) {
        view.updateOrInvalidated {
            copy(
                availableCharacters = response.map {
                    AvailableCharacterToAddToSceneViewModel(
                        Character.Id(it.characterId),
                        it.characterName,
                        it.mediaId?.toString() ?: ""
                    )
                }
            )
        }
    }

    override suspend fun availableCharacterArcSectionsForCharacterInSceneListed(response: AvailableCharacterArcSectionsForCharacterInScene) {
        view.updateOrInvalidated {
            copy(
                includedCharacters = this.includedCharacters?.map { includedCharacter ->
                    if (response.characterId == includedCharacter.id.uuid) {
                        includedCharacter.copy(
                            availableCharacterArcSections = response.map { arcUsedInScene ->
                                AvailableCharacterArcViewModel(
                                    CharacterArc.Id(arcUsedInScene.characterArcId),
                                    Theme.Id(arcUsedInScene.themeId),
                                    arcUsedInScene.characterArcName,
                                    arcUsedInScene.count { it.usedInScene },
                                    arcUsedInScene.all { it.usedInScene },
                                    arcUsedInScene.map {
                                        AvailableArcSectionViewModel(
                                            CharacterArcSection.Id(it.arcSectionId),
                                            arcSectionListedLabel(it.templateName, it.sectionValue, it.isMultiTemplate),
                                            it.usedInScene,
                                            arcSectionDisplayLabel(
                                                arcUsedInScene.characterArcName,
                                                it.templateName,
                                                it.sectionValue,
                                                it.isMultiTemplate
                                            )
                                        )
                                    }
                                )
                            }
                        )
                    } else includedCharacter
                }
            )
        }
    }

    override fun failedToGetSceneDetails(failure: Exception) {

    }

    companion object {
        internal fun includedCharacterViewModel(includedCharacter: IncludedCharacterInScene): IncludedCharacterViewModel {
            return IncludedCharacterViewModel(
                Character.Id(includedCharacter.characterId),
                includedCharacter.characterName,
                "",
                null,
                "",
                includedCharacter.motivation,
                includedCharacter.motivation != null,
                includedCharacter.inheritedMotivation?.let { inherited ->
                    PreviousMotivation(
                        inherited.motivation,
                        Scene.Id(inherited.sceneId),
                        inherited.sceneName
                    )
                },
                includedCharacter.coveredArcSections.map {
                    CoveredArcSectionViewModel(
                        CharacterArcSection.Id(it.arcSectionId),
                        CharacterArc.Id(it.characterArcId),
                        arcSectionDisplayLabel(
                            it.characterArcName,
                            it.arcSectionTemplateName,
                            it.arcSectionValue,
                            it.arcSectionTemplateAllowsMultiple
                        )
                    )
                },
                null
            )
        }

        private fun arcSectionListedLabel(
            arcSectionTemplateName: String,
            arcSectionValue: String,
            isMultiTemplate: Boolean
        ): String = arcSectionTemplateName + if (isMultiTemplate) " - \"$arcSectionValue\"" else ""

        private fun arcSectionDisplayLabel(
            characterArcName: String,
            arcSectionTemplateName: String,
            arcSectionValue: String,
            isMultiTemplate: Boolean
        ): String =
            "$characterArcName - ${arcSectionListedLabel(arcSectionTemplateName, arcSectionValue, isMultiTemplate)}"
    }

}