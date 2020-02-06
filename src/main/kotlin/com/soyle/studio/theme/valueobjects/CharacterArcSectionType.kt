package com.soyle.studio.theme.valueobjects

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.soyle.studio.theme.ArcSectionTypesUsedInCharacterCompCannotAllowMultiple
import com.soyle.studio.theme.RequiredArcSectionTypesCannotAllowMultiple

/**
 * Created by Brendan
 * Date: 2/5/2020
 * Time: 3:58 PM
 */
class CharacterArcSectionType private constructor(
	val name: String,
	val isRequired: Boolean,
	val usedInCharacterComp: Boolean,
	val isMoral: Boolean,
	val isPsychological: Boolean,
	val canHaveMultiple: Boolean,
	val subSections: List<String>
) {

	companion object {
		fun define(
			name: String,
			isRequired: Boolean,
			usedInCharacterComp: Boolean,
			isMoral: Boolean,
			isPsychological: Boolean,
			canHaveMultiple: Boolean,
			subSections: List<String>
		): Either<*, CharacterArcSectionType> {
			if (canHaveMultiple) {
				if (isRequired) return RequiredArcSectionTypesCannotAllowMultiple.left()
				if (usedInCharacterComp) return ArcSectionTypesUsedInCharacterCompCannotAllowMultiple.left()
			}
			return CharacterArcSectionType(name, isRequired, usedInCharacterComp, isMoral, isPsychological, canHaveMultiple, subSections).right()
		}
	}

}