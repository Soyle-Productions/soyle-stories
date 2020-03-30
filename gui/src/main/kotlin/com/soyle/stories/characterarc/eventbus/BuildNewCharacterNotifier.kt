/**
 * Created by Brendan
 * Date: 3/1/2020
 * Time: 4:36 PM
 */
package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.eventbus.Notifier

class BuildNewCharacterNotifier : BuildNewCharacter.OutputPort, Notifier<BuildNewCharacter.OutputPort>() {
    override fun receiveBuildNewCharacterFailure(failure: CharacterException) {
        notifyAll { it.receiveBuildNewCharacterFailure(failure) }
    }

    override fun receiveBuildNewCharacterResponse(response: CharacterItem) {
        notifyAll { it.receiveBuildNewCharacterResponse(response) }
    }
}