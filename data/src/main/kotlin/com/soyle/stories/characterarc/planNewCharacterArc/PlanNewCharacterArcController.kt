package com.soyle.stories.characterarc.planNewCharacterArc

interface PlanNewCharacterArcController {

    fun planCharacterArc(characterId: String, name: String, onError: (Throwable) -> Unit)

}