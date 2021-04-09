package com.soyle.stories.scene.sceneCharacters

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.scene.RoleInScene
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.gui.View
import com.soyle.stories.scene.charactersInScene.RenamedCharacterInSceneReceiver
import com.soyle.stories.scene.charactersInScene.assignRole.CharacterRoleInSceneChangedReceiver
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.CharacterArcSectionUncoveredInSceneReceiver
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.CharacterArcSectionsCoveredBySceneReceiver
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.IncludedCharacterInSceneReceiver
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemovedCharacterFromSceneReceiver
import com.soyle.stories.scene.charactersInScene.setDesire.CharacterDesireInSceneChangedReceiver
import com.soyle.stories.usecase.scene.character.coverCharacterArcSectionsInScene.AvailableCharacterArcSectionsForCharacterInScene
import com.soyle.stories.usecase.scene.character.coverCharacterArcSectionsInScene.GetAvailableCharacterArcsForCharacterInScene
import com.soyle.stories.usecase.scene.character.listAvailableCharacters.AvailableCharactersToAddToScene
import com.soyle.stories.usecase.scene.character.listAvailableCharacters.ListAvailableCharactersToIncludeInScene
import com.soyle.stories.usecase.scene.character.listIncluded.ListCharactersInScene
import com.soyle.stories.usecase.scene.common.IncludedCharacterInScene

class SceneCharactersPresenter(
    private val view: View.Nullable<SceneCharactersViewModel>
) :
    ListCharactersInScene.OutputPort,
    ListAvailableCharactersToIncludeInScene.OutputPort,
    GetAvailableCharacterArcsForCharacterInScene.OutputPort,
    IncludedCharacterInSceneReceiver by SceneCharactersIncludedCharacterPresenter(view),
    RemovedCharacterFromSceneReceiver by SceneCharactersRemovedCharacterPresenter(view),
    RenamedCharacterInSceneReceiver by SceneCharactersRenamedCharacterPresenter(view),
    CharacterArcSectionsCoveredBySceneReceiver by SceneCharactersCoveredArcSectionsPresenter(view),
    CharacterArcSectionUncoveredInSceneReceiver by SceneCharactersUncoveredArcSectionPresenter(view),
    CharacterRoleInSceneChangedReceiver by SceneCharactersRoleInSceneChangedPresenter(view),
    CharacterDesireInSceneChangedReceiver by SceneCharactersDesireChangedPresenter(view)
{

    override suspend fun receiveCharactersInScene(response: ListCharactersInScene.ResponseModel) {
        view.update {
            SceneCharactersViewModel(
                response.sceneId,
                null,
                response.charactersInScene.map(::includedCharacterViewModel)
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

    companion object {
        internal fun includedCharacterViewModel(includedCharacter: IncludedCharacterInScene): IncludedCharacterViewModel {
            return IncludedCharacterViewModel(
                includedCharacter.characterId,
                includedCharacter.characterName,
                "",
                includedCharacter.roleInScene.toRoleInSceneViewModel(),
                includedCharacter.desire,
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

        internal fun RoleInScene?.toRoleInSceneViewModel(): CharacterRoleInScene? =
            when (this) {
                RoleInScene.IncitingCharacter -> CharacterRoleInScene.IncitingCharacter
                RoleInScene.OpponentCharacter -> CharacterRoleInScene.OpponentToIncitingCharacter
                null -> null
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