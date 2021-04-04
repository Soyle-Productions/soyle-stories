package com.soyle.stories.characterarc.planNewCharacterArc

import kotlinx.coroutines.Job

interface PlanNewCharacterArcController {

    fun planCharacterArc(characterId: String, name: String): Job

}