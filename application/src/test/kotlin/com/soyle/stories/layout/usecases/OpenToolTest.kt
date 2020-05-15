/**
 * Created by Brendan
 * Date: 3/2/2020
 * Time: 9:02 AM
 */
package com.soyle.stories.layout.usecases

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.layout.LayoutDoesNotExist
import com.soyle.stories.layout.TestContext
import com.soyle.stories.layout.entities.*
import com.soyle.stories.layout.usecases.openTool.OpenTool
import com.soyle.stories.layout.usecases.openTool.OpenToolUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.reflect.KClass

class OpenToolTest {

    val projectId = Project.Id(UUID.randomUUID())

    private fun given(
        layouts: List<Layout>,
        saveLayout: (Layout) -> Unit = {}
    ): (OpenTool.RequestModel) -> Either<*, *> {
        val repo = TestContext(initialLayouts = layouts, saveLayout = saveLayout).layoutRepository
        val useCase: OpenTool = OpenToolUseCase(projectId, repo)
        val output = object : OpenTool.OutputPort {
            var result: Either<*, *>? = null
            override fun receiveOpenToolFailure(failure: Exception) {
                result = failure.left()
            }

            override fun receiveOpenToolResponse(response: OpenTool.ResponseModel) {
                result = response.right()
            }
        }
        return {
            runBlocking {
                useCase.invoke(it, output)
            }
            output.result ?: null.right()
        }
    }

    val characterUUID = UUID.randomUUID()
    val themeUUID = UUID.randomUUID()
    val request = OpenTool.RequestModel.BaseStoryStructure(characterUUID, themeUUID)

    /**
     *
     */
    @Nested
    inner class `When layout does not exist` {

        val useCase = given(emptyList())

        @Test
        fun `should output error`() {
            val (result) = useCase.invoke(request) as Either.Left
            result as LayoutDoesNotExist
        }

    }

    @Nested
    inner class `When layout exists` {

        val layout: Layout = layout(projectId, Layout.Id(UUID.randomUUID())) {
            window {
                primaryStack {}
            }
        }

        @Nested
        inner class `And does not contain tool` {

            @Test
            fun `should create new tool`() {
                var savedLayout: Layout? = null
                given(layouts = listOf(layout), saveLayout = {
                    savedLayout = it
                }).invoke(request)
                assert((savedLayout!!.windows.first().child as ToolStack).tools.first().isOpen)
            }

            @Test
            fun `new tool should be in output`() {
                var savedLayout: Layout? = null
                val (result) = given(layouts = listOf(layout), saveLayout = {
                    savedLayout = it
                }).invoke(request) as Either.Right
                result as OpenTool.ResponseModel
                result.affectedToolGroup.run {
                    (savedLayout!!.windows.first().child as ToolStack).tools.first().id
                }
            }

        }

        @Nested
        inner class `And contains tool` {

            val parentGroup: ToolStack
            val toolId: Tool.Id = Tool.Id(UUID.randomUUID())
            val layout: Layout

            init {
                val tool = Tool.BaseStoryStructure(toolId, Theme.Id(themeUUID), Character.Id(characterUUID), true)
                layout = (this@`When layout exists`.layout.addToolToPrimaryStack(tool) as Either.Right).b
                parentGroup = layout.getParentToolGroup(toolId)!!
            }

            @Test
            fun `tool should be opened`() {
                var savedLayout: Layout? = null
                given(layouts = listOf(layout), saveLayout = {
                    savedLayout = it
                }).invoke(request)
                assert((savedLayout!!.windows.first().child as ToolStack).tools.first().isOpen)
            }

            fun assertReceivedParentToolGroup(result: Any?) {
                result as OpenTool.ResponseModel
                result.affectedToolGroup.run {
                    assertEquals(parentGroup.id.uuid, groupId)
                }
            }

            @Test
            fun `should output parent tool group`() {
                val (result) = given(layouts = listOf(layout)).invoke(request) as Either.Right
                assertReceivedParentToolGroup(result)
            }

            @Nested
            inner class `And Tool is open` {

                val layout = (this@`And contains tool`.layout.openTool(toolId) as Either.Right).b

                @Test
                fun `tool should be focused`() {
                    var savedLayout: Layout? = null
                    given(layouts = listOf(layout), saveLayout = {
                        savedLayout = it
                    }).invoke(request)
                    assertEquals(
                        toolId,
                        (savedLayout!!.windows.first().child as ToolStack).focusedTool
                    )
                }

                @Test
                fun `should output parent tool group`() {
                    val (result) = given(layouts = listOf(layout)).invoke(request) as Either.Right
                    assertReceivedParentToolGroup(result)
                }

                @Nested
                inner class `And tool group is focused` {

                    val layout: Layout

                    init {
                        val layoutWithOpenTool = this@`And Tool is open`.layout
                        layout = Layout(layoutWithOpenTool.id, projectId, layoutWithOpenTool.windows.map {
                            if (it.isPrimary) {
                                val toolStack = (it.child as ToolStack)
                                val stackWithFocus = ToolStack(
                                    toolStack.id,
                                    toolStack.layoutId,
                                    toolStack.tools,
                                    toolStack.isPrimary,
                                    toolId
                                )
                                Window(it.id, it.layoutId, stackWithFocus)
                            } else it
                        })
                    }

                    @Test
                    fun `should receive no output`() {
                        val (result) = given(layouts = listOf(layout)).invoke(request) as Either.Right
                        assert(result == null)
                    }

                }

            }

        }

    }

    @Nested
    inner class SupportedToolTypes {

        val layout: Layout = layout(projectId, Layout.Id(UUID.randomUUID())) {
            window {
                primaryStack {}
            }
        }

        fun <T : Tool<*>, R : com.soyle.stories.layout.usecases.OpenTool> testType(request: OpenTool.RequestModel, toolClass: KClass<T>, findType: (T) -> Boolean, activeClass: KClass<R>, findActive: (R) -> Boolean) {
            var savedLayout: Layout? = null
            val (result) = given(layouts = listOf(layout), saveLayout = {
                savedLayout = it
            }).invoke(request) as Either.Right
            val openedTool = (savedLayout!!.tools
              .filter { it.isOpen }
              .filter { toolClass.isInstance(it) } as List<T>)
              .find(findType)
            assertNotNull(openedTool)
            result as OpenTool.ResponseModel
            val toolsMatchingSaved = result.affectedToolGroup.tools.filter { it.toolId == openedTool!!.id.uuid }
            assertTrue(toolsMatchingSaved.isNotEmpty(), "No active tools matching tool id")
            val toolsAsActiveClass = (toolsMatchingSaved.filter { activeClass.isInstance(it) } as List<R>)
            assertTrue(toolsAsActiveClass.isNotEmpty(), "No active tools of expected type $activeClass")
            val activeTool = toolsAsActiveClass.find(findActive)
            assertNotNull(activeTool)
        }

        @Test
        fun `base story structure`() {
            val request = OpenTool.RequestModel.BaseStoryStructure(UUID.randomUUID(), UUID.randomUUID())
            testType(
              request,
              Tool.BaseStoryStructure::class,
              {
                  it.themeId == Theme.Id(request.themeId) && it.characterId == Character.Id(request.characterId)
              },
              BaseStoryStructureTool::class,
              {
                  it.characterId == request.characterId && it.themeId == request.themeId
              }
            )
        }

        @Test
        fun `character comparison`() {
            val request = OpenTool.RequestModel.CharacterComparison(UUID.randomUUID(), UUID.randomUUID())
            testType(
              request,
              Tool.CharacterComparison::class,
              {
                  it.identifyingData == Theme.Id(request.themeId) && it.associatedData == Character.Id(request.characterId)
              },
              CharacterComparisonTool::class,
              {
                  it.characterId == request.characterId && it.themeId == request.themeId
              }
            )
        }

        @Test
        fun `location details`() {
            val request = OpenTool.RequestModel.LocationDetails(UUID.randomUUID())
            testType(
              request,
              Tool.LocationDetails::class,
              { it.identifyingData == Location.Id(request.locationId) },
              LocationDetailsTool::class,
              { it.locationId == request.locationId }
            )
        }

    }

    @Nested
    inner class `When windows and splitters are also affected` {

        var ancestorWindowId: Window.Id? = null
        val ancestorSplitters = mutableListOf<StackSplitter>()
        val layout: Layout = layout(projectId, Layout.Id(UUID.randomUUID())) {
            window { primaryStack {  } }
            window {
                verticalStackSplitter {
                    stackSplitter(1) {
                        stackSplitter(1) {
                            stack(1) {
                                tool(Tool.BaseStoryStructure(Tool.Id(), Theme.Id(themeUUID), Character.Id(characterUUID), false))
                            }
                        }.also { ancestorSplitters.add(it) }
                    }.also { ancestorSplitters.add(it) }
                }.also { ancestorSplitters.add(it) }
            }.also { ancestorWindowId = it.id }
        }

        @Test
        fun `should output all affected ancestors`() {
            val (result) = given(layouts = listOf(layout)).invoke(request) as Either.Right
            result as OpenTool.ResponseModel
            result.affectedGroupSplitterIds.run {
                assertEquals(ancestorSplitters.size, size)
                assertEquals(ancestorSplitters.map { it.id.uuid }.toSet(), toSet())
            }
            assertEquals(ancestorWindowId!!.uuid, result.affectedWindowId)
        }
    }

}