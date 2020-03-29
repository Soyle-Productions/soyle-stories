package com.soyle.stories.characterarc.usecases.listAllCharacterArcs

/**
 * Created by Brendan
 * Date: 2/23/2020
 * Time: 11:56 AM
 */
interface ListAllCharacterArcs {

    suspend operator fun invoke(outputPort: OutputPort)

    class ResponseModel(val characters: Map<CharacterItem, List<CharacterArcItem>>)

    interface OutputPort {
        fun receiveCharacterArcList(response: ResponseModel)
    }

}