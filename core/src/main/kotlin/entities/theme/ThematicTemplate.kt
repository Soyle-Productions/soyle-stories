package com.soyle.stories.entities.theme

import com.soyle.stories.common.*
import com.soyle.stories.translators.asThematicTemplateSection

/**
 * Created by Brendan
 * Date: 2/22/2020
 * Time: 4:06 PM
 */
class ThematicTemplate(
    val sections: List<ThematicTemplateSection>
) {

    val ids
        get() = sections.map { it.characterArcTemplateSectionId }.toSet()

    companion object {
        private val defaultTemplate by lazy {
            ThematicTemplate(
                listOf(
                    PsychologicalWeakness,
                    MoralWeakness,
                    PsychologicalNeed,
                    MoralNeed,
                    Desire,
                    ValuesOrBeliefs
                ).map {
                    it.asThematicTemplateSection()
                }
            )
        }

        fun default() = defaultTemplate
    }

}