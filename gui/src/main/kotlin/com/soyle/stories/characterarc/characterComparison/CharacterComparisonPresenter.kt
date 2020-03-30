package com.soyle.stories.characterarc.characterComparison

import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.character.usecases.removeCharacterFromLocalStory.RemoveCharacterFromLocalStory
import com.soyle.stories.characterarc.LocalCharacterArcException
import com.soyle.stories.characterarc.eventbus.EventBus
import com.soyle.stories.characterarc.usecases.deleteLocalCharacterArc.DeleteLocalCharacterArc
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcs
import com.soyle.stories.theme.LocalThemeException
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.changeCentralMoralQuestion.ChangeCentralMoralQuestion
import com.soyle.stories.theme.usecases.changeCharacterPerspectivePropertyValue.ChangeCharacterPerspectivePropertyValue
import com.soyle.stories.theme.usecases.changeCharacterPropertyValue.ChangeCharacterPropertyValue
import com.soyle.stories.theme.usecases.changeStoryFunction.ChangeStoryFunction
import com.soyle.stories.theme.usecases.changeThematicSectionValue.ChangeThematicSectionValue
import com.soyle.stories.theme.usecases.compareCharacters.CompareCharacters
import com.soyle.stories.theme.usecases.includeCharacterInComparison.IncludeCharacterInComparison
import com.soyle.stories.theme.usecases.promoteMinorCharacter.PromoteMinorCharacter
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromLocalComparison
import java.util.*

/**
 * Created by Brendan
 * Date: 2/10/2020
 * Time: 9:13 AM
 */
class CharacterComparisonPresenter(
    private val view: CharacterComparisonView,
    private val themeId: String,
    eventBus: EventBus
) : CompareCharacters.OutputPort, ListAllCharacterArcs.OutputPort, BuildNewCharacter.OutputPort,
    ChangeStoryFunction.OutputPort,
    IncludeCharacterInComparison.OutputPort, PromoteMinorCharacter.OutputPort, DeleteLocalCharacterArc.OutputPort,
    ChangeThematicSectionValue.OutputPort, RemoveCharacterFromLocalStory.OutputPort,
    ChangeCentralMoralQuestion.OutputPort,
    ChangeCharacterPropertyValue.OutputPort, ChangeCharacterPerspectivePropertyValue.OutputPort,
    RemoveCharacterFromLocalComparison.OutputPort {

    init {
        eventBus.buildNewCharacter.addListener(this)
        eventBus.includeCharacterInComparison.addListener(this)
        eventBus.promoteMinorCharacter.addListener(this)
        eventBus.deleteLocalCharacterArc.addListener(this)
        eventBus.removeCharacterFromStory.addListener(this)
        eventBus.changeStoryFunction.addListener(this)
        eventBus.changeThematicSectionValue.addListener(this)
        eventBus.changeCentralMoralQuestion.addListener(this)
        eventBus.changeCharacterPropertyValue.addListener(this)
        eventBus.changeCharacterPerspectivePropertyValue.addListener(this)
        eventBus.removeCharacterFromLocalComparison.addListener(this)
    }

    private var allCharacters: List<CharacterItem> = emptyList()

    override fun receiveCharacterComparison(response: CompareCharacters.ResponseModel) {
        val majorCharacterIds = response.majorCharacterIds.toSet()
        val includedCharacterIds = response.characterSummaries.ids.map { it.toString() }.toSet()
        val comparisonItems = response.characterSummaries.values.map { summary ->
            val uuid = summary.id
            ComparisonItem(uuid.toString(), summary.name, uuid in majorCharacterIds, mapOf(
                "Archetype(s)" to PropertyValue("Archetype", summary.archetypes, false)
            ) + response.comparisonSections.withIndex().associate {
                it.value to summary.comparisonSections[it.index].let {
                    CharacterArcSectionValue(
                        it.first.toString(),
                        it.second
                    )
                }
            } + mapOf(
                "Variation on Moral" to PropertyValue("VariationOnMoral", summary.variationOnMoral, false),
                "Similarities to Hero" to PropertyValue("Similarities", summary.similaritiesToHero, true),
                "Opponent Attack on Hero's Weakness" to PropertyValue("Attack", summary.attackAgainstHero, true)
            ), summary.storyFunctions.map { it.name }.toSet()
            )
        }.sortedBy {
            val firstFunction = it.storyFunctions.firstOrNull()
            when (firstFunction) {
                "Hero" -> 0
                "Antagonist" -> 1
                "Ally" -> 2
                null -> 4
                else -> 3
            }
        }
        val focusedItem = comparisonItems.find { it.characterId == response.focusedCharacterId.toString() }!!
        view.update {
            copy(
                focusedCharacter = response.characterSummaries.forceGetById(response.focusedCharacterId)
                    .toFocusCharacterOption(),
                focusCharacterOptions = response.majorCharacterIds.map {
                    response.characterSummaries.forceGetById(it).toFocusCharacterOption()
                },
                subTools = listOf(
                    CompSubToolViewModel(
                        "Comparisons",
                        "Story Function",
                        listOf("Archetype(s)") + response.comparisonSections,
                        comparisonItems
                    ),
                    MoralProblemSubToolViewModel(
                        "Moral Problem",
                        response.centralQuestion,
                        listOf("Variation on Moral"),
                        comparisonItems
                    ),
                    CharacterChangeSubToolViewModel("Character Change",
                        focusedItem.compSections.getValue("Psychological Weakness"),
                        focusedItem.compSections.getValue("Moral Weakness"),
                        "",
                        focusedItem.compSections.getValue("Desire"),
                        listOf(
                            "Opponent Attack on Hero's Weakness",
                            "Values or Beliefs",
                            "Similarities to Hero"
                        ),
                        comparisonItems.filter { it.storyFunctions.contains("Antagonist") }
                    )
                ),
                availableCharactersToAdd = allCharacters.filterNot { it.characterId.toString() in includedCharacterIds }.map { CharacterItemViewModel(it.characterId.toString(), it.characterName) },
                isInvalid = false
            )
        }
    }

    override fun receiveCharacterArcList(response: ListAllCharacterArcs.ResponseModel) {
        view.update {
            allCharacters = response.characters.keys.toList()
            val includedIds = subTools.getOrNull(0)?.items?.map { it.characterId }?.toSet() ?: setOf()
            copy(
                availableCharactersToAdd = response.characters.keys.filterNot { it.characterId.toString() in includedIds }
                    .map {
                        CharacterItemViewModel(it.characterId.toString(), it.characterName)
                    }
            )
        }
    }

    override fun receiveBuildNewCharacterFailure(failure: CharacterException) {}
    override fun receiveBuildNewCharacterResponse(response: CharacterItem) {
        view.update {
            if (subTools.getOrNull(0)?.items?.find { it.characterId == response.characterId.toString() } != null) return@update this
            copy(
                availableCharactersToAdd = availableCharactersToAdd + CharacterItemViewModel(
                    response.characterId.toString(),
                    response.characterName
                )
            )
        }
    }

    override fun receiveIncludeCharacterInComparisonFailure(failure: Exception) {}
    override fun receiveIncludeCharacterInComparisonResponse(response: IncludeCharacterInComparison.ResponseModel) {
        if (response.themeId.toString() != themeId) return
        view.update {
            copy(
                isInvalid = true
            )
        }
    }

    override fun receivePromoteMinorCharacterFailure(failure: ThemeException) {
        if (failure.themeId.toString() == themeId) {
            view.update { this }
            throw failure
        }
    }

    override fun receivePromoteMinorCharacterResponse(response: PromoteMinorCharacter.ResponseModel) {
        view.update {
            copy(
                isInvalid = true
            )
        }
    }

    override fun receiveDeleteLocalCharacterArcFailure(failure: LocalCharacterArcException) {}

    override fun receiveDeleteLocalCharacterArcResponse(response: DeleteLocalCharacterArc.ResponseModel) {
        if (response.themeId.toString() == themeId && !response.themeRemoved) {
            view.update {
                copy(
                    isInvalid = true
                )
            }
        }
    }

    private fun CompareCharacters.CharacterComparisonSummary.toFocusCharacterOption(): FocusCharacterOption {
        return FocusCharacterOption(id.toString(), name)
    }

    override fun receiveCompareCharactersFailure(error: ThemeException) {
        throw error
    }

    override fun receiveChangeThematicSectionValueFailure(failure: Exception) {
    }

    override fun receiveChangeThematicSectionValueResponse(response: ChangeThematicSectionValue.ResponseModel) {
        view.update {
            copy(
                isInvalid = true
            )
        }

    }

    override fun receiveRemoveCharacterFromLocalStoryFailure(failure: CharacterException) {
    }

    override fun receiveRemoveCharacterFromLocalStoryResponse(response: RemoveCharacterFromLocalStory.ResponseModel) {
        if (UUID.fromString(themeId) !in response.updatedThemes) return
        view.update {
            val focusOptions = focusCharacterOptions.filterNot { it.characterId == response.characterId.toString() }
            val focusedCharacter = focusedCharacter?.takeUnless { it.characterId == response.characterId.toString() }
                ?: focusOptions.firstOrNull()
            copy(
                focusedCharacter = focusedCharacter,
                focusCharacterOptions = focusOptions,
                isInvalid = true
            )
        }
    }

    override fun receiveChangeStoryFunctionFailure(failure: Exception) {

    }

    override fun receiveChangeStoryFunctionResponse(response: ChangeStoryFunction.ResponseModel) {
        if (UUID.fromString(themeId) != response.themeId) return
        view.update {
            copy(
                isInvalid = true
            )
        }
    }

    override fun receiveChangeCentralMoralQuestionResponse(response: ChangeCentralMoralQuestion.ResponseModel) {
        if (themeId != response.themeId.toString()) return
        view.update {
            copy(
                isInvalid = true
            )
        }
    }

    override fun receiveChangeCentralMoralQuestionFailure(failure: ThemeException) {

    }

    override fun receiveChangeCharacterPerspectivePropertyValueResponse(response: ChangeCharacterPerspectivePropertyValue.ResponseModel) {
        if (themeId != response.themeId.toString()) return
        view.update {
            copy(
                isInvalid = true
            )
        }
    }

    override fun receiveChangeCharacterPerspectivePropertyValueFailure(failure: ThemeException) {
    }

    override fun receiveChangeCharacterPropertyValueResponse(response: ChangeCharacterPropertyValue.ResponseModel) {
        if (themeId != response.themeId.toString()) return
        view.update {
            copy(
                isInvalid = true
            )
        }
    }

    override fun receiveChangeCharacterPropertyValueFailure(failure: ThemeException) {
    }

    override fun receiveRemoveCharacterFromLocalComparisonResponse(response: RemoveCharacterFromLocalComparison.ResponseModel) {
        if (themeId != response.themeId.toString() || response.themeRemoved) return
        view.update {
            copy(
                isInvalid = true
            )
        }
    }

    override fun receiveRemoveCharacterFromLocalComparisonFailure(failure: LocalThemeException) {

    }
}