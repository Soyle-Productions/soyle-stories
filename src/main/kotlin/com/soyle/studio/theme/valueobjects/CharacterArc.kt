package com.soyle.studio.theme.valueobjects

import com.soyle.studio.theme.entities.CharacterArcSection

/**
 * Created by Brendan
 * Date: 2/5/2020
 * Time: 3:50 PM
 */
class CharacterArc(
	override val sections: List<CharacterArcSection>
) : CharacterThematicValueSet {

	fun removeCharacterArc() = CharacterComparison(sections)

	fun addSections(sections: List<CharacterArcSection>) = CharacterArc(this.sections + sections)
}