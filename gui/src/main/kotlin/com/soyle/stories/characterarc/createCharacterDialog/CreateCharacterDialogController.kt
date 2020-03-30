/**
 * Created by Brendan
 * Date: 3/1/2020
 * Time: 4:31 PM
 */
package com.soyle.stories.characterarc.createCharacterDialog

import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacter

class CreateCharacterDialogController(
    private val buildNewCharacter: BuildNewCharacter,
    private val buildNewCharacterOutputPort: BuildNewCharacter.OutputPort
) : CreateCharacterDialogViewListener {

    override suspend fun createCharacter(name: String) {
        buildNewCharacter.invoke(name, buildNewCharacterOutputPort)
    }
}