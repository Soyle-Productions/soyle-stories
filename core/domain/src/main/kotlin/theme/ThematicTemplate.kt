package com.soyle.stories.domain.theme

import com.soyle.stories.domain.character.*

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
                    ThematicTemplateSection(
                        it.id,
                        it.name,
                        it.isRequired,
                        it.allowsMultiple,
                        it.isMoral
                    )
                }
            )
        }

        fun default() = defaultTemplate
    }

}