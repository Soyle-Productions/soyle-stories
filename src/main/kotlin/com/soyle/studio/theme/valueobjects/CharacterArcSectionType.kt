package com.soyle.studio.theme.valueobjects

/**
 * Created by Brendan
 * Date: 2/5/2020
 * Time: 3:58 PM
 */
data class CharacterArcSectionType(
	val name: String,
	val isRequired: Boolean,
	val usedInCharacterComp: Boolean,
	val isMoral: Boolean,
	val isPsychological: Boolean,
	val canHaveMultiple: Boolean,
	val subSections: List<String>
)