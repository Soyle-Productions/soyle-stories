package com.soyle.stories.domain.character

class CharacterArcTemplate(
    val sections: List<CharacterArcTemplateSection>
) {

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