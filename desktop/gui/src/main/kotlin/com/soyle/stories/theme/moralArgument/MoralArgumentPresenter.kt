package com.soyle.stories.theme.moralArgument

import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.characterarc.moveCharacterArcSectionInMoralArgument.CharacterArcSectionMovedInMoralArgumentReceiver
import com.soyle.stories.characterarc.removeCharacterArcSectionFromMoralArgument.CharacterArcSectionRemovedReceiver
import com.soyle.stories.domain.character.Character
import com.soyle.stories.usecase.character.arc.section.addCharacterArcSectionToMoralArgument.ArcSectionAddedToCharacterArc
import com.soyle.stories.usecase.character.arc.section.addCharacterArcSectionToMoralArgument.ListAvailableArcSectionTypesToAddToMoralArgument
import com.soyle.stories.usecase.character.arc.section.moveCharacterArcSectionInMoralArgument.CharacterArcSectionMovedInMoralArgument
import com.soyle.stories.usecase.character.arc.section.removeCharacterArcSectionFromMoralArgument.CharacterArcSectionRemoved
import com.soyle.stories.gui.View
import com.soyle.stories.theme.addCharacterArcSectionToMoralArgument.ArcSectionAddedToCharacterArcReceiver
import com.soyle.stories.theme.characterConflict.AvailablePerspectiveCharacterViewModel
import com.soyle.stories.usecase.theme.listAvailablePerspectiveCharacters.AvailablePerspectiveCharacters
import com.soyle.stories.usecase.theme.listAvailablePerspectiveCharacters.ListAvailablePerspectiveCharacters
import com.soyle.stories.usecase.theme.outlineMoralArgument.GetMoralArgumentFrame
import com.soyle.stories.usecase.theme.outlineMoralArgument.OutlineMoralArgumentForCharacterInTheme
import java.util.*

class MoralArgumentPresenter(
    themeId: String,
    private val view: View.Nullable<MoralArgumentViewModel>
) :
    GetMoralArgumentFrame.OutputPort,
    OutlineMoralArgumentForCharacterInTheme.OutputPort,
    ListAvailableArcSectionTypesToAddToMoralArgument.OutputPort,
    ListAvailablePerspectiveCharacters.OutputPort,

    ArcSectionAddedToCharacterArcReceiver,
    CharacterArcSectionMovedInMoralArgumentReceiver,
    CharacterArcSectionRemovedReceiver {

    private val themeId = UUID.fromString(themeId)

    override suspend fun receiveMoralArgumentFrame(response: GetMoralArgumentFrame.ResponseModel) {
        view.update {
            MoralArgumentViewModel(
                moralProblemLabel = "Moral Problem",
                moralProblemValue = response.moralProblem,
                themeLineLabel = "Theme Line",
                themeLineValue = response.themeLine,
                thematicRevelationLabel = "Thematic Revelation",
                thematicRevelationValue = response.thematicRevelation,
                perspectiveCharacterLabel = "Perspective Character",
                noPerspectiveCharacterLabel = "-Select Perspective Character-",
                selectedPerspectiveCharacter = null,
                availablePerspectiveCharacters = null,
                loadingPerspectiveCharactersLabel = "Loading ...",
                loadingSectionTypesLabel = "Loading ...",
                createCharacterLabel = "Create Character",
                unavailableCharacterMessage = {
                    "${it.characterName} is a minor character.  By selecting them, they will " +
                            "gain a character arc for this theme and become a major character."
                },
                unavailableSectionTypeMessage = {
                    "${it.sectionTypeName} has already been used.  By selecting this, the section " +
                            "of this type will be moved to this position."
                },
                removeSectionButtonLabel = "Remove",
                sections = null,
                availableSectionTypes = null
            )
        }
    }

    override suspend fun receiveAvailablePerspectiveCharacters(response: AvailablePerspectiveCharacters) {
        view.updateOrInvalidated {
            copy(
                availablePerspectiveCharacters = response.map {
                    AvailablePerspectiveCharacterViewModel(
                        it.characterId.toString(),
                        it.characterName,
                        it.isMajorCharacter
                    )
                }
            )
        }
    }

    override suspend fun receiveMoralArgumentOutlineForCharacterInTheme(response: OutlineMoralArgumentForCharacterInTheme.ResponseModel) {
        view.updateOrInvalidated {
            copy(
                selectedPerspectiveCharacter = CharacterItemViewModel(
                    Character.Id(response.characterId),
                    response.characterName,
                    ""
                ),
                sections = response.characterArcSections.map {
                    MoralArgumentSectionViewModel(
                        it.arcSectionId.toString(),
                        it.sectionTemplateName,
                        it.arcSectionValue,
                        !it.sectionTemplateIsRequired
                    )
                }
            )
        }
    }

    override suspend fun receiveAvailableArcSectionTypesToAddToMoralArgument(response: ListAvailableArcSectionTypesToAddToMoralArgument.ResponseModel) {
        if (response.themeId != themeId) return
        view.updateOrInvalidated {
            if (selectedPerspectiveCharacter?.characterId != Character.Id(response.characterId))
                return@updateOrInvalidated this
            copy(
                availableSectionTypes = response.map {
                    MoralArgumentSectionTypeViewModel(
                        it.sectionTemplateId.toString(),
                        it.sectionTemplateName,
                        it.canBeCreated,
                        it.existingSectionId?.toString()
                    )
                }
            )
        }
    }

    override suspend fun receiveArcSectionAddedToCharacterArc(event: ArcSectionAddedToCharacterArc) {
        if (event.themeId != themeId) return
        if (event.indexInMoralArgument == null) return
        view.updateOrInvalidated {
            if (selectedPerspectiveCharacter?.characterId != Character.Id(event.characterId))
                return@updateOrInvalidated this
            copy(
                sections = sections?.toMutableList()?.apply {
                    add(
                        event.indexInMoralArgument!!,
                        MoralArgumentSectionViewModel(
                            event.characterArcSectionId.toString(),
                            event.templateSectionName,
                            event.value,
                            true // if added, then it wasn't required.
                        )
                    )
                }
            )
        }
    }

    override suspend fun receiveCharacterArcSectionsMovedInMoralArgument(events: List<CharacterArcSectionMovedInMoralArgument>) {
        view.updateOrInvalidated {
            val listedSections = this.sections?.map { it.arcSectionId } ?: return@updateOrInvalidated this
            val modifiedSections = events.filter { it.arcSectionId.toString() in listedSections }
                .associateBy { it.arcSectionId.toString() }
            if (modifiedSections.isEmpty()) return@updateOrInvalidated this

            copy(
                sections = sections.withIndex().sortedBy { (i, it) ->
                    if (it.arcSectionId !in modifiedSections) i
                    else modifiedSections.getValue(it.arcSectionId).newIndex
                }.map { it.value }
            )
        }
    }

    override suspend fun receiveCharacterArcSectionRemoved(event: CharacterArcSectionRemoved) {
        if (event.themeId != themeId) return
        val arcSectionId = event.arcSectionId.toString()
        view.updateOrInvalidated {
            if (Character.Id(event.characterId) != selectedPerspectiveCharacter?.characterId) {
                return@updateOrInvalidated this
            }

            copy(
                sections = sections?.filterNot { it.arcSectionId == arcSectionId }
            )
        }
    }

}