package com.soyle.stories.characterarc.characterComparison

/**
 * Created by Brendan
 * Date: 2/10/2020
 * Time: 9:06 AM
 */
data class CharacterComparisonViewModel(
  internal val focusedCharacterId: String? = null,
  val focusedCharacter: CharacterItemViewModel? = null,
  internal val majorCharacterIds: List<String> = emptyList(),
  val focusCharacterOptions: List<CharacterItemViewModel> = emptyList(),
  val subTools: List<SubToolViewModel> = emptyList(),
  internal val characters: List<CharacterItemViewModel> = emptyList(),
  val availableCharactersToAdd: List<CharacterItemViewModel> = emptyList(),
  val isInvalid: Boolean = false
)

sealed class SubToolViewModel {
	abstract val label: String
	abstract val sections: List<String>
	abstract val items: List<ComparisonItem>
}

data class CompSubToolViewModel(
  override val label: String,
  val storyFunctionSectionLabel: String,
  override val sections: List<String>,
  override val items: List<ComparisonItem>,
  val storyFunctionOptions: List<StoryFunctionOption>
) : SubToolViewModel()

data class StoryFunctionOption(val label: String, val storyFunction: String) {
	constructor(label: String) : this(label, label)
}

data class MoralProblemSubToolViewModel(
  override val label: String,
  val centralMoralQuestion: String,
  override val sections: List<String>,
  override val items: List<ComparisonItem>
) : SubToolViewModel()

data class CharacterChangeSubToolViewModel(
  override val label: String,
  val psychWeakness: SectionValue,
  val moralWeakness: SectionValue,
  val change: String,
  val desire: SectionValue,
  override val sections: List<String>,
  override val items: List<ComparisonItem>
) : SubToolViewModel()

data class CharacterItemViewModel(val characterId: String, val characterName: String)
data class ComparisonItem(val characterId: String, val characterName: String, val isMajorCharacter: Boolean, val compSections: Map<String, SectionValue>, val storyFunctions: Set<String>)

sealed class SectionValue {
	abstract val value: String
}

data class PropertyValue(val propertyName: String, override val value: String, val isShared: Boolean) : SectionValue()
data class CharacterArcSectionValue(val sectionId: String, override val value: String) : SectionValue()
