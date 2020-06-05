/**
 * Created by Brendan
 * Date: 3/2/2020
 * Time: 9:02 AM
 */
package com.soyle.stories.layout.usecases

import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.layout.assertLayoutDoesNotExist
import com.soyle.stories.layout.assertResponseModel
import com.soyle.stories.layout.doubles.LayoutRepositoryDouble
import com.soyle.stories.layout.doubles.LocaleDouble
import com.soyle.stories.layout.doubles.OpenToolContextDouble
import com.soyle.stories.layout.entities.Layout
import com.soyle.stories.layout.entities.Tool
import com.soyle.stories.layout.entities.ToolStack
import com.soyle.stories.layout.entities.layout
import com.soyle.stories.layout.tools.dynamic.DynamicTool
import com.soyle.stories.layout.tools.dynamic.LocationDetails
import com.soyle.stories.layout.tools.temporary.Ramifications
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout
import com.soyle.stories.layout.usecases.openTool.OpenTool
import com.soyle.stories.layout.usecases.openTool.OpenToolUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class OpenToolTest {

    private val dynamicTool = LocationDetails(Location.Id().uuid)
    private val temporaryTool = Ramifications.DeleteSceneRamifications(UUID.randomUUID(), LocaleDouble())

    private var result: Any? = null

    @Test
    fun `open a new tool in a layout`() {
        givenALayoutExistsForProject()
        givenDataForDynamicToolExists()
        whenAToolIsOpened(dynamicTool)
        savedLayoutShouldHaveToolInPrimaryStack(dynamicTool)
        assertResponseModel(savedLayout!!).invoke(result)
    }

    @Test
    fun `layout does not exist`() {
        whenAToolIsOpened(dynamicTool)
        layoutShouldNotHaveBeenSaved()
        assertLayoutDoesNotExist().invoke(result)
    }

    @Test
    fun `data for dynamic tool doesn't exist`() {
        givenALayoutExistsForProject()
        whenADynamicToolIsOpened()
        layoutShouldNotHaveBeenSaved()
        result as Exception
    }

    @Test
    fun `reopen existing tool`() {
        givenALayoutExistsForProject()
        givenDynamicToolExistsInLayout()
        givenDataForDynamicToolExists()
        whenAToolIsOpened(dynamicTool)
        savedLayoutShouldHaveToolInSameToolStack(dynamicTool)
        assertResponseModel(savedLayout!!).invoke(result)
    }

    @Test
    fun `temporary tool`() {
        givenALayoutExistsForProject()
        givenTemporaryToolMarkerExistsInLayout()
        givenDataForTemporaryToolExists()
        whenAToolIsOpened(temporaryTool)
        savedLayoutShouldHaveToolInMarkedToolStack(temporaryTool)
        assertResponseModel(savedLayout!!).invoke(result)
    }

    private val projectId = Project.Id().uuid
    private val layoutId = Layout.Id().uuid

    private var savedLayout: Layout? = null

    private val layoutRepository = LayoutRepositoryDouble(onSaveLayout = {
        savedLayout = it
    })
    private val context = OpenToolContextDouble()

    private fun givenALayoutExistsForProject() {
        layoutRepository.layout = Layout(Project.Id(projectId)).let {
            Layout(Layout.Id(layoutId), it.projectId, it.windows)
        }
    }

    private var initialParentStackId: ToolStack.Id? = null
    private var initialToolId: Tool.Id? = null

    private fun givenDynamicToolExistsInLayout() {
        layoutRepository.layout = layout(Project.Id(projectId), Layout.Id(layoutId)) {
            window {
                verticalStackSplitter {
                    primaryStack(1) {}
                    stack(1) {
                        initialParentStackId = id
                        val tool = Tool(dynamicTool)
                        initialToolId = tool.id
                        tool(tool)
                    }
                }
            }
        }
    }

    private var markedToolStackId: ToolStack.Id? = null

    private fun givenTemporaryToolMarkerExistsInLayout() {
        layoutRepository.layout = layout(Project.Id(projectId), Layout.Id(layoutId)) {
            window {
                verticalStackSplitter {
                    primaryStack(1) {}
                    stack(1) {
                        markedToolStackId = id
                        marker(Ramifications.DeleteSceneRamifications::class)
                    }
                }
            }
        }
    }

    private fun givenDataForDynamicToolExists() {
        Location(Location.Id(dynamicTool.locationId), Project.Id(projectId), "", "").let {
            context.locationRepository.locations[it.id] = it
        }
    }

    private fun givenDataForTemporaryToolExists() {
        Scene(Scene.Id(temporaryTool.sceneId), Project.Id(projectId), "", StoryEvent.Id()).let {
            context.sceneRepository.scenes[it.id] = it
            context.sceneRepository.sceneOrder[it.projectId] = listOf(it.id)
        }
    }

    private fun whenAToolIsOpened(type: DynamicTool) {
        val useCase: OpenTool = OpenToolUseCase(projectId, layoutRepository, context)
        val output = object : OpenTool.OutputPort {
            override fun receiveOpenToolResponse(response: GetSavedLayout.ResponseModel) {
                result = response
            }

            override fun receiveOpenToolFailure(failure: Exception) {
                result = failure
            }
        }
        runBlocking {
            useCase.invoke(type, output)
        }
    }

    private fun whenADynamicToolIsOpened() = whenAToolIsOpened(dynamicTool)
    private fun whenATemporaryToolIsOpened() = whenAToolIsOpened(temporaryTool)

    private fun layoutShouldNotHaveBeenSaved() {
        assertNull(savedLayout)
    }

    private fun savedLayoutShouldContainOpenTool(toolType: DynamicTool): Tool {
        val savedLayout = savedLayout!!
        assertEquals(layoutId, savedLayout.id.uuid)
        val stack = savedLayout.getToolStackForToolType(toolType)!!
        val tool = stack.tools.find { it.type == toolType }!!
        assertTrue(tool.isOpen)
        return tool
    }

    private fun savedLayoutShouldHaveToolInPrimaryStack(toolType: DynamicTool): Tool {
        val tool = savedLayoutShouldContainOpenTool(toolType)
        val primaryStack = savedLayout!!.getToolStackForToolType(toolType)!!
        assertTrue(primaryStack.isPrimary)
        return tool
    }

    private fun savedLayoutShouldHaveToolInSameToolStack(toolType: DynamicTool): Tool {
        val tool = savedLayoutShouldContainOpenTool(toolType)
        val stack = savedLayout!!.getToolStackForToolType(toolType)!!
        assertEquals(initialParentStackId!!, stack.id)
        assertEquals(initialToolId!!, tool.id)
        return tool
    }

    private fun savedLayoutShouldHaveToolInMarkedToolStack(toolType: DynamicTool): Tool {
        val tool = savedLayoutShouldContainOpenTool(toolType)
        val stack = savedLayout!!.getToolStackForToolType(toolType)!!
        assertEquals(markedToolStackId!!, stack.id)
        return tool
    }

}