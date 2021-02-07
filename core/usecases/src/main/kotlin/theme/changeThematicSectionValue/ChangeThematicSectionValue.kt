/**
 * Created by Brendan
 * Date: 2/27/2020
 * Time: 4:47 PM
 */
package com.soyle.stories.usecase.theme.changeThematicSectionValue

import java.util.*

interface ChangeThematicSectionValue {
    suspend operator fun invoke(thematicSectionId: UUID, value: String, output: OutputPort)

    class ResponseModel(val thematicSectionId: UUID, val newValue: String)

    interface OutputPort {
        fun receiveChangeThematicSectionValueFailure(failure: Exception)
        fun receiveChangeThematicSectionValueResponse(response: ResponseModel)
    }
}