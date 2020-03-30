/**
 * Created by Brendan
 * Date: 3/1/2020
 * Time: 5:12 PM
 */
package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterArcItem
import com.soyle.stories.characterarc.usecases.planNewCharacterArc.PlanNewCharacterArc
import com.soyle.stories.eventbus.Notifier

class PlanNewCharacterArcNotifier : PlanNewCharacterArc.OutputPort, Notifier<PlanNewCharacterArc.OutputPort>() {
    override fun receivePlanNewCharacterArcFailure(failure: Exception) {
        notifyAll { it.receivePlanNewCharacterArcFailure(failure) }
    }

    override fun receivePlanNewCharacterArcResponse(response: CharacterArcItem) {
        notifyAll { it.receivePlanNewCharacterArcResponse(response) }
    }
}