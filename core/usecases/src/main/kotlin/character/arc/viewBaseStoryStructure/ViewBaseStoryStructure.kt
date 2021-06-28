package com.soyle.stories.usecase.character.arc.viewBaseStoryStructure

import java.util.*

/**
 * Created by Brendan
 * Date: 2/9/2020
 * Time: 10:47 AM
 */
interface ViewBaseStoryStructure {

    suspend operator fun invoke(
        characterId: UUID,
        themeId: UUID,
        outputPort: OutputPort
    )

    class ResponseModel(
        val themeId: UUID,
        val characterId: UUID,
        val sections: List<StoryStructureSection>
    )

    class StoryStructureSection(
        val arcSectionId: UUID,
        val value: String,
        val templateName: String,
        val subSections: Map<String, String>,
        val linkedLocation: UUID?
    )

    interface OutputPort {
        fun receiveViewBaseStoryStructureFailure(failure: Exception)
        fun receiveViewBaseStoryStructureResponse(response: ResponseModel)
    }

}