package com.soyle.studio.theme.valueobjects

import com.soyle.studio.theme.entities.CharacterArcSection
import java.util.*

/**
 * Created by Brendan
 * Date: 2/5/2020
 * Time: 3:50 PM
 */
class CharacterArc(
        val explicitlyCreated: Boolean,
        val sections: List<CharacterArcSection>
) {

    fun markCreated(): CharacterArc =
        CharacterArc(true, sections)
    fun markImplicit(): CharacterArc =
        CharacterArc(false, sections)
    fun addSections(sections: List<CharacterArcSection>) = CharacterArc(explicitlyCreated, this.sections + sections)
}