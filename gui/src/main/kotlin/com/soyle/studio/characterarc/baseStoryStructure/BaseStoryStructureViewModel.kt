package com.soyle.studio.characterarc.baseStoryStructure

/**
 * Created by Brendan
 * Date: 2/9/2020
 * Time: 11:15 PM
 */
class BaseStoryStructureViewModel(
    val themeId: String,
    val characterId: String,
    val sections: List<StoryStructureSectionViewModel>
)

class StoryStructureSectionViewModel(val sectionTemplateName: String, val sectionId: String, val sectionValue: String, val subsections: List<SubSectionViewModel>)
class SubSectionViewModel(val sectionTemplateName: String, val sectionId: String, val subSectionName: String, val value: String)