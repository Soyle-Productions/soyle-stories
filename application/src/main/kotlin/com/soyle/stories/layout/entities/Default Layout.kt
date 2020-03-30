package com.soyle.stories.layout.entities

import com.soyle.stories.entities.Project

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 12:17 AM
 */

fun defaultLayout(projectId: Project.Id, layoutId: Layout.Id): Layout = layout(projectId, layoutId) {
    window {
        stackSplitter(false) {
            stackSplitter(4) {
                stackSplitter(2) {
                    stack(1) {
                        //openTool(ToolType.Properties)
                        //openTool(ToolType.CharacterDevelopment)
                        //openTool(ToolType.LocationTracking)
                    }
                    stack(1) {
                        //openTool(ToolType.SceneList)
                        //openTool(ToolType.PlotPointList)
                        openTool(ToolType.CharacterList, mapOf("projectId" to projectId))
                    }
                }
                primaryStack(6) {}
                stack(2) {
                    //openTool(ToolType.NoteList)
                }
            }
            stack(2) {
                //openTool(ToolType.Timeline)
                //openTool(ToolType.ContinuityErrors)
            }
        }
    }
}