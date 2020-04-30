package com.soyle.stories.characterarc.baseStoryStructure

import com.soyle.stories.location.items.LocationItemViewModel

data class BaseStoryStructureViewModel(
    val sections: List<StoryStructureSectionViewModel> = emptyList(),
	val availableLocations: List<LocationItemViewModel> = emptyList()
)

class StoryStructureSectionViewModel(val sectionTemplateName: String, val sectionId: String, val sectionValue: String, val subsections: List<SubSectionViewModel>)
class SubSectionViewModel(val sectionTemplateName: String, val sectionId: String, val subSectionName: String, val value: String)