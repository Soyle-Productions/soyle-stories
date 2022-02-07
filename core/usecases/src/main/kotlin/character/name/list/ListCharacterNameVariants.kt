package com.soyle.stories.usecase.character.name.list

import com.soyle.stories.domain.character.Character

interface ListCharacterNameVariants {

    suspend operator fun invoke(characterId: Character.Id, output: OutputPort)

    class ResponseModel(list: List<String>) : List<String> by list

    fun interface OutputPort
    {
        suspend fun receiveCharacterNameVariants(response: ResponseModel)
    }

}