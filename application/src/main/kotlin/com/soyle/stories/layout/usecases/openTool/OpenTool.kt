/**
 * Created by Brendan
 * Date: 3/2/2020
 * Time: 11:17 AM
 */
package com.soyle.stories.layout.usecases.openTool

import com.soyle.stories.layout.usecases.OpenToolGroup
import java.util.*

interface OpenTool {

    /**
     * Expected system states:
     * - Layout exists and does not contain the tool
     * - Layout exists and contains a tool with the requested data
     *
     * Unexpected system states:
     * - Layout doesn't exist
     * - Layout exists, but does not contain the tool nor have a primary tool group
     *
     * Input parameters:
     * - data UUID's
     * - tool type id
     *
     * Happy Path:
     * - System retrieves layout
     * - System creates a Tool of specified type with provided data
     * - System adds created Tool to primary tool group in layout
     * - System focuses on created Tool in primary tool group
     * - System outputs primary tool group
     *
     * Alternative paths:
     * - Layout does not have primary tool group:
     *      - System creates primary window with primary tool group
     *      - System adds new window to layout
     *      - System adds new tool to new primary tool group
     *      - System outputs new window
     * - Layout already had tool of specified type with provided data:
     *      - Tool is not open:
     *          - System opens tool
     *          - System focuses on tool in its parent tool group
     *          - System outputs parent tool group
     *      - Tool is open but not in focus:
     *          - System focuses on tool in its parent tool group
     *          - System outputs parent tool group
     *      - Tool is open and in focus:
     *          - System outputs nothing
     * - Layout does not exist
     *      - System outputs error
     *
     */
    suspend operator fun invoke(requestModel: RequestModel, output: OutputPort)

    sealed class RequestModel {

        class BaseStoryStructure(val characterId: UUID, val themeId: UUID) : RequestModel()
        class CharacterComparison(val characterId: UUID, val themeId: UUID) : RequestModel()
        class LocationDetails(val locationId: UUID) : RequestModel()

    }

    class ResponseModel(val affectedToolGroup: OpenToolGroup, val affectedGroupSplitterIds: List<UUID>, val affectedWindowId: UUID?) {



    }

    interface OutputPort {
        fun receiveOpenToolFailure(failure: Exception)
        fun receiveOpenToolResponse(response: ResponseModel)
    }

}