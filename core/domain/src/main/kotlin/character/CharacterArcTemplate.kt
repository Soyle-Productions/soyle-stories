package com.soyle.stories.domain.character

class CharacterArcTemplate(
    val sections: List<CharacterArcTemplateSection>
) {

    private val sectionsById by lazy {
        sections.associateBy { it.id }
    }

    fun hasSection(sectionId: CharacterArcTemplateSection.Id): Boolean = sectionsById.containsKey(sectionId)
    fun getSectionById(sectionId: CharacterArcTemplateSection.Id): CharacterArcTemplateSection? = sectionsById[sectionId]

    companion object {

        private val defaultTemplate by lazy {
            CharacterArcTemplate(
                listOf(
                    PsychologicalWeakness,
                    PsychologicalNeed,
                    MoralWeakness,
                    MoralNeed,
                    Desire,
                    ImmoralAction,
                    Opponent,
                    Plan,
                    Drive,
                    AttackByAlly,
                    Battle,
                    FinalActionAgainstOpponent,
                    MoralDecision,
                    PsychologicalSelfRevelation,
                    MoralSelfRevelation,
                    NewEquilibrium
                )
            )
        }

        fun default() = defaultTemplate
    }

}