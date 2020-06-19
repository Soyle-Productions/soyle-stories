package com.soyle.stories.characterarc.characterComparison

import com.soyle.stories.character.characterList.CharacterListListener
import com.soyle.stories.character.characterList.LiveCharacterList
import com.soyle.stories.characterarc.characterComparison.presenters.*
import com.soyle.stories.characterarc.eventbus.CharacterArcEvents
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.common.listensTo
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
  characterArcEvents: CharacterArcEvents,
  liveCharacterList: LiveCharacterList
) : CompareCharacters.OutputPort, CharacterListListener {

	private val themeId: UUID = UUID.fromString(themeId)

	private val subPresenters: List<Any>

	init {
		val themeUUID = this.themeId
		with(characterArcEvents) {
			subPresenters = listOf(
			  IncludeCharacterInComparisonPresenter(themeUUID, view) listensTo includeCharacterInComparison,
			  PromoteMinorCharacterPresenter(themeUUID, view) listensTo promoteMinorCharacter,
			  DeleteLocalCharacterArcPresenter(themeUUID, view) listensTo deleteLocalCharacterArc,
			  ChangeStoryFunctionPresenter(themeUUID, view) listensTo changeStoryFunction,
			  ChangeThematicSectionValuePresenter(view) listensTo changeThematicSectionValue,
			  ChangeCentralMoralQuestionPresenter(themeUUID, view) listensTo changeCentralMoralQuestion,
			  ChangeCharacterPropertyValuePresenter(themeUUID, view) listensTo changeCharacterPropertyValue,
			  ChangeCharacterPerspectivePropertyValuePresenter(themeUUID, view) listensTo changeCharacterPerspectivePropertyValue,
			  RemoveCharacterFromLocalComparisonPresenter(themeUUID, view) listensTo removeCharacterFromLocalComparison
			)
		}
		liveCharacterList.addListener(this)
	}

	override fun receiveCharacterComparison(response: CompareCharacters.ResponseModel) {
		val focusCharacterId = response.focusedCharacterId?.toString()
		view.update {

			val charactersById = characters.associateBy { it.characterId }

			val includedIds = response.characterSummaries.ids.map(UUID::toString).toSet()

			copy(
			  focusedCharacterId = focusCharacterId,
			  focusedCharacter = focusCharacterId?.let { charactersById[it] },
			  majorCharacterIds = response.majorCharacterIds.map(UUID::toString),
			  focusCharacterOptions = response.majorCharacterIds.mapNotNull { charactersById[it.toString()] },
			  subTools = createSubTools(response),
			  availableCharactersToAdd = characters.filterNot { it.characterId in includedIds },
			  isInvalid = false
			)
		}
	}

	override fun receiveCharacterListUpdate(characters: List<CharacterItem>) {
		view.update {

			val characterViewModels = characters.map {
				CharacterItemViewModel(it.characterId.toString(), it.characterName)
			}

			val charactersById = characterViewModels.associateBy { it.characterId }

			val includedIds = subTools.getOrNull(0)?.items?.map { it.characterId }?.toSet() ?: setOf()
			copy(
			  focusedCharacter = focusedCharacterId?.let { charactersById[it] },
			  focusCharacterOptions = majorCharacterIds.mapNotNull { charactersById[it] },
			  characters = characterViewModels,
			  availableCharactersToAdd = characterViewModels.filterNot { it.characterId in includedIds }
			)
		}
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
		val focusedCharacterId = response.focusedCharacterId?.toString()
		val focusedItem = focusedCharacterId ?.let {
			comparisonItems.find { it.characterId == focusedCharacterId }!!
		}

		return CharacterChangeSubToolViewModel(
		  label = "Character Change",
		  psychWeakness = focusedItem?.compSections?.getValue("Psychological Weakness"),
		  moralWeakness = focusedItem?.compSections?.getValue("Moral Weakness"),
		  change = "",
		  desire = focusedItem?.compSections?.getValue("Desire"),
		  sections = listOf(
			"Opponent Attack on Hero's Weakness",
			"Values or Beliefs",
			"Similarities to Hero"
		  ),
		  items = comparisonItems.filter { it.storyFunctions.contains("Antagonist") }
		)
	}

	private fun CompareCharacters.CharacterComparisonSummary.toFocusCharacterOption(): CharacterItemViewModel {
		return CharacterItemViewModel(id.toString(), name)
	}

	override fun receiveCompareCharactersFailure(error: ThemeException) {
		throw error
	}
}