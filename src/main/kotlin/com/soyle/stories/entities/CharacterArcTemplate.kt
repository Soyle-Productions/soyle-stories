package com.soyle.stories.entities

import com.soyle.stories.common.*

/**
 * Created by Brendan
 * Date: 2/22/2020
 * Time: 2:39 PM
 */
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
                    Opponent,
                    Plan,
                    Battle,
                    PsychologicalSelfRevelation,
                    MoralSelfRevelation,
                    NewEquilibrium
                )
            )
        }

        fun default() = defaultTemplate
    }

}