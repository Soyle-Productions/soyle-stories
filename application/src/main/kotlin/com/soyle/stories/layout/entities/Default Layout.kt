package com.soyle.stories.layout.entities

import com.soyle.stories.entities.Project
import com.soyle.stories.layout.tools.fixed.FixedTool

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 12:17 AM
 */

fun defaultLayout(projectId: Project.Id, layoutId: Layout.Id): Layout = layout(projectId, layoutId) {
    window {
        horizontalStackSplitter {
            stackSplitter(4) {
                stackSplitter(2) {
                    stack(1) {
                        //openTool(ToolType.Properties)
                        //openTool(ToolType.CharacterDevelopment)
                        //openTool(ToolType.LocationTracking)
                    }
                    stack(1) {
                        tool(Tool(FixedTool.StoryEventList))
                        tool(Tool(FixedTool.SceneList))
                        tool(Tool(FixedTool.LocationList))
                        tool(Tool(FixedTool.CharacterList))
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