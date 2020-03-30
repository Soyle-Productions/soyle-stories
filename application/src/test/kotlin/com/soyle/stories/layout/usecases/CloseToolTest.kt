package com.soyle.stories.layout.usecases

import com.soyle.stories.entities.Project
import com.soyle.stories.layout.LayoutException
import com.soyle.stories.layout.TestContext
import com.soyle.stories.layout.ToolDoesNotExist
import com.soyle.stories.layout.entities.*
import com.soyle.stories.layout.usecases.closeTool.CloseTool
import com.soyle.stories.layout.usecases.closeTool.CloseToolUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.util.*
import java.util.logging.Logger

class CloseToolTest {

    private val projectId = UUID.randomUUID()
    private val toolId = tool().id

    @Test
    fun `fail when tool does not exist`() {
        listOf(
            given(NoTools),
            given(List(5) { tool() })
        ).whenExecutedAssertEach {
            output as ToolDoesNotExist
            output.toolId.mustEqual(toolId) { "Output tool id does not equal toolId" }

            assertNotPersisted()
        }
    }

    @Test
    fun `pretend to succeed but don't persist when tool already closed`() {
        listOf(
            given(List(1) { closedTool(toolId) }),
            given(List(5) { openTool() } + closedTool(toolId))
        ).whenExecutedAssertEach {
            output as CloseTool.ResponseModel
            output.toolId.mustEqual(toolId) { "Output tool id does not equal toolId" }

            assertNotPersisted()
        }
    }

    @Test
    fun `output tool id and persist layout when tool exists and is open`() {
        listOf(
            given(List(1) { openTool(toolId) }),
            given(List(5) { closedTool() } + openTool(toolId))
        ).whenExecutedAssertEach {
            output as CloseTool.ResponseModel
            output.toolId.mustEqual(toolId) { "Output tool id does not equal toolId" }

            assertPersistedLayout {
                tools.find { it.id.uuid == toolId }!!.isOpen.mustEqual(false) { "Tool was not properly closed" }
            }
        }
    }

    @Test
    fun `modified ancestors should be in output`() {
        listOf(
            given(window(nestedSplitters = List(2) { stackSplitter() }, leaf = stack(openTool(toolId))))
        ).whenExecutedAssertEach {
            output as CloseTool.ResponseModel
            output.closedStackId.mustNotBeNull { "Output stack id is null" }
            output.closedSplitterIds.size.mustEqual(2) { "Output does not contain required number of splitter ids" }
            output.closedWindowId.mustNotBeNull { "Output window id is null" }
        }
    }

    @Test
    fun `unmodified ancestors should not be in output`() {
        // difference between this test and the one before is that
        // the stack now has two open tools.  Thus, no ancestors will be closed
        listOf(
            given(window(nestedSplitters = List(2) { stackSplitter() }, leaf = stack(openTool(toolId), openTool())))
        ).whenExecutedAssertEach {
            output as CloseTool.ResponseModel
            output.closedStackId.mustBeNull { "Output stack id is not null" }
            output.closedSplitterIds.size.mustEqual(0) { "Output splitter ids is not empty" }
            output.closedWindowId.mustBeNull { "Output window id is not null" }
        }
    }

    private val NoTools
        get() = emptyList<Nothing>()

    private fun tool() = openTool()
    private fun closedTool(id: UUID? = null) = ToolRepresentation(id ?: UUID.randomUUID(), false)
    private fun openTool(id: UUID? = null) = ToolRepresentation(id ?: UUID.randomUUID(), true)

    private fun window(
        nestedSplitters: List<StackSplitterRepresentation>,
        leaf: StackRepresentation
    ): WindowRepresentation = WindowRepresentation(nestedSplitters, leaf)

    private fun stackSplitter(): StackSplitterRepresentation = StackSplitterRepresentation()
    private fun stack(vararg tools: ToolRepresentation): StackRepresentation =
        StackRepresentation(false, tools.toList())

    private fun given(tools: List<ToolRepresentation>): Setup {
        return Setup(tools)
    }

    private fun given(window: WindowRepresentation): Setup {
        return Setup(emptyList(), window)
    }

    private inline fun List<Setup>.whenExecutedAssertEach(assertions: CloseToolAssertions.() -> Unit) {
        forEachIndexed { index, setup ->
            try {
                setup.execute().assertions()
            } catch (t: Throwable) {
                Logger.getGlobal().severe("Failed on [$index]")
                t.printStackTrace()
                fail(t)
            }
        }
    }

    private class WindowRepresentation(
        val nestedSplitters: List<StackSplitterRepresentation>,
        val leaf: StackRepresentation
    )

    private class StackSplitterRepresentation
    private class StackRepresentation(
        val isPrimary: Boolean = false,
        val tools: List<ToolRepresentation>
    )

    private class ToolRepresentation(
        val id: UUID = UUID.randomUUID(),
        val isOpen: Boolean
    )

    private inner class Setup(
        tools: List<ToolRepresentation>,
        window: WindowRepresentation? = null
    ) {
        private val initialLayout: Layout =
            if (window == null) layout(Project.Id(projectId), Layout.Id(UUID.randomUUID())) {
                window {
                    primaryStack {
                        this += tools.map {
                            CharacterListTool(Tool.Id(it.id), Project.Id(projectId), it.isOpen)
                        }
                    }
                }
            }
            else {
                val layoutId = Layout.Id(UUID.randomUUID())
                fun makeStack(): ToolStack {
                    return ToolStack(ToolStack.Id(UUID.randomUUID()), layoutId, window.leaf.tools.map {
                        CharacterListTool(Tool.Id(it.id), Project.Id(projectId), it.isOpen)
                    }, window.leaf.isPrimary, null)
                }
                Layout(
                    layoutId, Project.Id(projectId), listOfNotNull(
                        Window(Window.Id(UUID.randomUUID()), layoutId,
                            if (window.nestedSplitters.isEmpty()) {
                                makeStack()
                            } else {
                                var previousNode: Window.WindowChild = makeStack()
                                window.nestedSplitters.reversed().forEachIndexed { index, rep ->
                                    previousNode = StackSplitter(StackSplitter.Id(UUID.randomUUID()), index %2 == 0, layoutId, listOf(
                                        1 to previousNode
                                    ))
                                }
                                previousNode
                            }
                        ),
                        if (! window.leaf.isPrimary) {
                            Window(Window.Id(UUID.randomUUID()), layoutId, ToolStack(ToolStack.Id(UUID.randomUUID()), layoutId, emptyList(), true, null))
                        } else null
                    )
                )
            }

        private val context = TestContext(
            initialLayouts = listOf(initialLayout)
        )
        private val useCase: CloseTool = CloseToolUseCase(context, projectId)

        fun execute(): CloseToolAssertions {
            val output = object : CloseTool.OutputPort {
                var result: Any? = null
                override fun receiveCloseToolFailure(failure: LayoutException) {
                    result = failure
                }

                override fun receiveCloseToolResponse(response: CloseTool.ResponseModel) {
                    result = response
                }
            }
            runBlocking {
                useCase.invoke(toolId, output)
            }
            return CloseToolAssertions(initialLayout, output.result, context.persistedItems)
        }
    }

    private class CloseToolAssertions(
        private val initialLayout: Layout? = null,
        val output: Any?,
        private val persistedData: List<TestContext.PersistenceLog>
    ) {

        fun Any?.mustEqual(value: Any?, message: () -> String = { "" }) =
            Assertions.assertEquals(value, this) { message() }
        fun Any?.mustNotBeNull(message: () -> String = { "" }) =
            Assertions.assertNotNull(this) { message() }
        fun Any?.mustBeNull(message: () -> String = { "" }) =
            Assertions.assertNull(this) { message() }

        fun assertNotPersisted() {
            assert(persistedData.isEmpty()) { "No items should have been persisted.  $persistedData" }
        }

        fun assertPersistedLayout(assertions: Layout.() -> Unit = {}) {
            val persistedLayout = persistedData.asSequence().map { it.data }.filterIsInstance<Layout>().single()
            persistedLayout.id.mustEqual(initialLayout!!.id) { "Persisted layout was not the correct layout." }
            persistedLayout.assertions()
        }
    }
}