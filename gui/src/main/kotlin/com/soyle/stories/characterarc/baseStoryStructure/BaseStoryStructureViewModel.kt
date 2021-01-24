package com.soyle.stories.characterarc.baseStoryStructure

import com.soyle.stories.location.items.LocationItemViewModel

sealed class BaseStoryStructureViewModel {
	abstract val sections: List<StoryStructureSectionViewModel>
	abstract val availableLocations: List<LocationItemViewModel>

	abstract fun withSections(sections: List<StoryStructureSectionViewModel>): BaseStoryStructureViewModel
	abstract fun withLocations(availableLocations: List<LocationItemViewModel>): BaseStoryStructureViewModel
}

internal class PartialWithSections(
  override val sections: List<StoryStructureSectionViewModel>,
  private val sectionToLocationIds: Map<String, String?>
) : BaseStoryStructureViewModel() {
	override val availableLocations: List<LocationItemViewModel>
		get() = emptyList()

	override fun withSections(sections: List<StoryStructureSectionViewModel>): BaseStoryStructureViewModel = PartialWithSections(sections, sectionToLocationIds)
	override fun withLocations(availableLocations: List<LocationItemViewModel>): BaseStoryStructureViewModel {
		val locationMap = availableLocations.associateBy { it.id }
		return Full(sections.map {
			StoryStructureSectionViewModel(it.sectionTemplateName, it.sectionId, it.sectionValue, it.subsections, sectionToLocationIds[it.sectionId]?.let { locationMap[it] })
		}, availableLocations)
	}
}

internal class PartialWithLocations(
  override val availableLocations: List<LocationItemViewModel>
) : BaseStoryStructureViewModel() {
	override val sections: List<StoryStructureSectionViewModel>
		get() = emptyList()

	override fun withSections(sections: List<StoryStructureSectionViewModel>): BaseStoryStructureViewModel =
	  Full(sections, availableLocations)

	override fun withLocations(availableLocations: List<LocationItemViewModel>): BaseStoryStructureViewModel = PartialWithLocations(availableLocations)
}

internal data class Full(
  override val sections: List<StoryStructureSectionViewModel>,
  override val availableLocations: List<LocationItemViewModel>
) : BaseStoryStructureViewModel() {
	override fun withSections(sections: List<StoryStructureSectionViewModel>): BaseStoryStructureViewModel = copy(sections = sections)
	override fun withLocations(availableLocations: List<LocationItemViewModel>): BaseStoryStructureViewModel {
		val locationsById = availableLocations.associateBy { it.id }
		return copy(
			sections.map {
				if (it.linkedLocation != null  && it.linkedLocation.name != locationsById[it.linkedLocation.id]?.name) {
					StoryStructureSectionViewModel(
						it.sectionTemplateName,
						it.sectionId,
						it.sectionValue,
						it.subsections,
						LocationItemViewModel(it.linkedLocation.id, locationsById[it.linkedLocation.id]?.name ?: "")
					)
				} else it
			},
			availableLocations = availableLocations
		)
	}
}

class StoryStructureSectionViewModel(val sectionTemplateName: String, val sectionId: String, val sectionValue: String, val subsections: List<SubSectionViewModel>, val linkedLocation: LocationItemViewModel?)
class SubSectionViewModel(val sectionTemplateName: String, val sectionId: String, val subSectionName: String, val value: String)