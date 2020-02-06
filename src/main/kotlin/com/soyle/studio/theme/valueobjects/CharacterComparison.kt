package com.soyle.studio.theme.valueobjects

import com.soyle.studio.theme.entities.CharacterArcSection

/**
 * Created by Brendan
 * Date: 2/6/2020
 * Time: 2:53 PM
 */
class CharacterComparison(
	override val sections: List<CharacterArcSection>
) : CharacterThematicValueSet {

	fun createCharacterArc() = CharacterArc(sections)



}