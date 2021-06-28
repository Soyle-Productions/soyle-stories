/**
 * Created by Brendan
 * Date: 3/14/2020
 * Time: 10:25 AM
 */
package com.soyle.stories.usecase.theme.changeStoryFunction

import java.util.*

interface ChangeStoryFunction {

    class RequestModel(
        val themeId: UUID,
        val perspectiveCharacterId: UUID,
        val targetCharacterId: UUID,
        val storyFunction: StoryFunction
    )

    enum class StoryFunction {
        Antagonist, Ally, FakeAllyAntagonist, FakeAntagonistAlly
    }

    suspend operator fun invoke(request: RequestModel, outputPort: OutputPort)

    class ResponseModel(
        val themeId: UUID,
        val perspectiveCharacterId: UUID,
        val targetCharacterId: UUID,
        val storyFunction: String
    )

    interface OutputPort {
        fun receiveChangeStoryFunctionFailure(failure: Exception)
        fun receiveChangeStoryFunctionResponse(response: ResponseModel)
    }

}