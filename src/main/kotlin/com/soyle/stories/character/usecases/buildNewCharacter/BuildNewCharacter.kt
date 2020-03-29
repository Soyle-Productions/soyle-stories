package com.soyle.stories.character.usecases.buildNewCharacter

import com.soyle.stories.character.CharacterException
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem

/**
 * Created by Brendan
 * Date: 2/26/2020
 * Time: 1:02 PM
 */
interface BuildNewCharacter {
    suspend operator fun invoke(name: String, outputPort: OutputPort)

    interface OutputPort {
        fun receiveBuildNewCharacterFailure(failure: CharacterException)
        fun receiveBuildNewCharacterResponse(response: CharacterItem)
    }
}