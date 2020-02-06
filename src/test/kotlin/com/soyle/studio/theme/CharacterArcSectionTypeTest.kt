package com.soyle.studio.theme

import arrow.core.right
import com.soyle.studio.common.`when`
import com.soyle.studio.common.thenFailWith
import com.soyle.studio.theme.valueobjects.CharacterArcSectionType
import org.junit.jupiter.api.Test

/**
 * Created by Brendan
 * Date: 2/6/2020
 * Time: 10:35 AM
 */
class CharacterArcSectionTypeTest {

	@Test
	fun requiredSectionTypesCannotAllowMultiple() {
		`when` {
			CharacterArcSectionType.define(
				"Failure",
				isRequired = true,
				usedInCharacterComp = false,
				isMoral = false,
				isPsychological = false,
				canHaveMultiple = true,
				subSections = emptyList()
			)
		} thenFailWith {
			RequiredArcSectionTypesCannotAllowMultiple
		}
	}

	@Test
	fun sectionTypesUsedInCharacterCompCannotAllowMultiple() {
		`when` {
			CharacterArcSectionType.define(
				"Failure",
				isRequired = false,
				usedInCharacterComp = true,
				isMoral = false,
				isPsychological = false,
				canHaveMultiple = true,
				subSections = emptyList()
			)
		} thenFailWith {
			ArcSectionTypesUsedInCharacterCompCannotAllowMultiple
		}
	}
}