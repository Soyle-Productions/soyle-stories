package com.soyle.stories.layout.entities

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 10:38 PM
 */
enum class ToolType(val canHaveNullData: Boolean) {
    CharacterList(true),
    BaseStoryStructure(false),
    CharacterComparison(false),
    LocationList(true),
    LocationDetails(false)/*,
    Timeline(true),
    NoteList(true),
    SceneList(true),
    PlotPointList(true),
    Properties(true),
    SceneWeave(false),
    ContinuityErrors(true),
    CharacterDevelopment(true),
    LocationTracking(true)*/
}

