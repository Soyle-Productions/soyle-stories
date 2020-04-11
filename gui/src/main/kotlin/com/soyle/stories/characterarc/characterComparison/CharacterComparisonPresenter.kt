package com.soyle.stories.characterarc.characterComparison

import com.soyle.stories.characterarc.characterComparison.presenters.*
import com.soyle.stories.characterarc.eventbus.CharacterArcEvents
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcs
import com.soyle.stories.eventbus.listensTo
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.compareCharacters.CompareCharacters
import java.util.*

/**
 * Created by Brendan
 * Date: 2/10/2020
 * Time: 9:13 AM
 */
class CharacterComparisonPresenter(
  private val view: CharacterComparisonView,
  themeId: String,
  characterArcEvents: CharacterArcEvents
) : CompareCharacters.OutputPort, ListAllCharacterArcs.OutputPort {

	private val themeId: UUID = UUID.fromString(themeId)

	/*
	hold them in a property because notifiers use weak references
	to listeners and these presenters should be held to the life
	cycle of this character comparison presenter.
	 */
	private val subPresenters: List<Any>

	init {
		val themeUUID = this.themeId
		with(characterArcEvents) {
			subPresenters = listOf(
			  BuildNewCharacterPresenter(view) listensTo buildNewCharacter,
			  IncludeCharacterInComparisonPresenter(themeUUID, view) listensTo includeCharacterInComparison,
			  PromoteMinorCharacterPresenter(themeUUID, view) listensTo promoteMinorCharacter,
			  DeleteLocalCharacterArcPresenter(themeUUID, view) listensTo deleteLocalCharacterArc,
			  RemoveCharacterFromLocalStoryPresenter(themeUUID, view) listensTo removeCharacterFromStory,
			  ChangeStoryFunctionPresenter(themeUUID, view) listensTo changeStoryFunction,
			  ChangeThematicSectionValuePresenter(view) listensTo changeThematicSectionValue,
			  ChangeCentralMoralQuestionPresenter(themeUUID, view) listensTo changeCentralMoralQuestion,
			  ChangeCharacterPropertyValuePresenter(themeUUID, view) listensTo changeCharacterPropertyValue,
			  ChangeCharacterPerspectivePropertyValuePresenter(themeUUID, view) listensTo changeCharacterPerspectivePropertyValue,
			  RemoveCharacterFromLocalComparisonPresenter(themeUUID, view) listensTo removeCharacterFromLocalComparison,
			  RenameCharacterPresenter(themeUUID, view) listensTo renameCharacter
			)
		}
	}

	private var allCharacters: List<CharacterItem> = emptyList()

	override fun receiveCharacterComparison(response: CompareCharacters.ResponseModel) {
		view.update {
			copy(
			  focusedCharacter = createFocusedCharacter(response),
			  focusCharacterOptions = collectMajorCharacters(response),
			  subTools = createSubTools(response),
			  availableCharactersToAdd = collectCharactersNotAlreadyIncluded(response),
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

	private fun createFocusedCharacter(response: CompareCharacters.ResponseModel): FocusCharacterOption? {
		return response.characterSummaries
		  .forceGetById(response.focusedCharacterId)
		  .toFocusCharacterOption()
	}

	private fun collectMajorCharacters(response: CompareCharacters.ResponseModel): List<FocusCharacterOption> {
		return response.majorCharacterIds
		  .asSequence()
		  .map(response.characterSummaries::forceGetById)
		  .map { it.toFocusCharacterOption() }
		  .toList()
	}

	private fun collectCharactersNotAlreadyIncluded(response: CompareCharacters.ResponseModel): List<CharacterItemViewModel> {
		val includedCharacterIds = response.characterSummaries.ids.map { it.toString() }.toSet()

		return allCharacters
		  .filterNot { it.characterId.toString() in includedCharacterIds }
		  .map { CharacterItemViewModel(it.characterId.toString(), it.characterName) }
	}

	private fun createSubTools(response: CompareCharacters.ResponseModel): List<SubToolViewModel> {
		val comparisonItems = response.characterSummaries.values
		  .let(::sortSummariesByStoryFunction)
		  .let { mapCharacterSummariesToComparisonItems(it, response) }

		return listOf(
		  createComparisonSubTool(response, comparisonItems),
		  createMoralProblemSubTool(response, comparisonItems),
		  createCharacterChangeSubTool(response, comparisonItems)
		)
	}

	private fun sortSummariesByStoryFunction(summaries: Collection<CompareCharacters.CharacterComparisonSummary>): List<CompareCharacters.CharacterComparisonSummary>
	{
		return summaries.sortedBy {
			when (it.storyFunctions.firstOrNull()) {
				CompareCharacters.StoryFunction.Hero -> 0
				CompareCharacters.StoryFunction.Antagonist -> 1
				CompareCharacters.StoryFunction.Ally -> 2
				CompareCharacters.StoryFunction.FakeAllyAntagonist -> 3
				CompareCharacters.StoryFunction.FakeAntagonistAlly -> 3
				CompareCharacters.StoryFunction.Subplot -> 3
				null -> 4
			}
		}
	}

	private fun mapCharacterSummariesToComparisonItems(summaries: List<CompareCharacters.CharacterComparisonSummary>, response: CompareCharacters.ResponseModel): List<ComparisonItem> {
		val majorCharacterIds = response.majorCharacterIds.toSet()
		return summaries.map {
			convertCharacterCompSummaryToViewModel(it, majorCharacterIds, response.comparisonSections)
		}
	}

	private fun convertCharacterCompSummaryToViewModel(summary: CompareCharacters.CharacterComparisonSummary, majorCharacterIds: Set<UUID>, comparisonSections: List<String>): ComparisonItem {
		val uuid = summary.id
		return ComparisonItem(
		  uuid.toString(),
		  summary.name,
		  uuid in majorCharacterIds,
		  getComparisonSections(summary, comparisonSections),
		  summary.storyFunctions.map(::getStoryFunctionLabel).toSet()
		)
	}

	private fun getStoryFunctionLabel(it: CompareCharacters.StoryFunction): String {
		return when (it) {
			CompareCharacters.StoryFunction.Hero -> "Hero"
			CompareCharacters.StoryFunction.Antagonist -> "Antagonist"
			CompareCharacters.StoryFunction.FakeAllyAntagonist -> "Fake-Ally Antagonist"
			CompareCharacters.StoryFunction.FakeAntagonistAlly -> "Fake-Antagonist Ally"
			CompareCharacters.StoryFunction.Ally -> "Ally"
			CompareCharacters.StoryFunction.Subplot -> "Subplot Character"
		}
	}

	private fun getComparisonSections(summary: CompareCharacters.CharacterComparisonSummary, comparisonSections: List<String>): Map<String, SectionValue> {
		return mapOf(
		  "Archetype(s)" to PropertyValue("Archetype", summary.archetypes, false),
		  *comparisonSections.withIndex().map {
			  it.value to summary.comparisonSections[it.index].let {
				  CharacterArcSectionValue(
					it.first.toString(),
					it.second
				  )
			  }
		  }.toTypedArray(),
		  "Variation on Moral" to PropertyValue("VariationOnMoral", summary.variationOnMoral, false),
		  "Similarities to Hero" to PropertyValue("Similarities", summary.similaritiesToHero, true),
		  "Opponent Attack on Hero's Weakness" to PropertyValue("Attack", summary.attackAgainstHero, true)
		)
	}

	private fun createComparisonSubTool(response: CompareCharacters.ResponseModel, comparisonItems: List<ComparisonItem>): CompSubToolViewModel {
		return CompSubToolViewModel(
		  label = "Comparisons",
		  storyFunctionSectionLabel = "Story Function",
		  sections = listOf("Archetype(s)") + response.comparisonSections,
		  items = comparisonItems,
		  storyFunctionOptions = listOf(
			StoryFunctionOption("Antagonist"),
			StoryFunctionOption("Ally"),
			StoryFunctionOption("Fake-Ally Antagonist", "FakeAllyAntagonist"),
			StoryFunctionOption("Fake-Antagonist Ally", "FakeAntagonistAlly")
		  )
		)
	}

	private fun createMoralProblemSubTool(response: CompareCharacters.ResponseModel, comparisonItems: List<ComparisonItem>): MoralProblemSubToolViewModel {
		return MoralProblemSubToolViewModel(
		  label = "Moral Problem",
		  centralMoralQuestion = response.centralQuestion,
		  sections = listOf("Variation on Moral"),
		  items = comparisonItems
		)
	}

	private fun createCharacterChangeSubTool(response: CompareCharacters.ResponseModel, comparisonItems: List<ComparisonItem>): CharacterChangeSubToolViewModel {
		val focusedItem = comparisonItems.find { it.characterId == response.focusedCharacterId.toString() }!!

		return CharacterChangeSubToolViewModel(
		  label = "Character Change",
		  psychWeakness = focusedItem.compSections.getValue("Psychological Weakness"),
		  moralWeakness = focusedItem.compSections.getValue("Moral Weakness"),
		  change = "",
		  desire = focusedItem.compSections.getValue("Desire"),
		  sections = listOf(
			"Opponent Attack on Hero's Weakness",
			"Values or Beliefs",
			"Similarities to Hero"
		  ),
		  items = comparisonItems.filter { it.storyFunctions.contains("Antagonist") }
		)
	}

	private fun CompareCharacters.CharacterComparisonSummary.toFocusCharacterOption(): FocusCharacterOption {
		return FocusCharacterOption(id.toString(), name)
	}

	override fun receiveCompareCharactersFailure(error: ThemeException) {
		throw error
	}

}