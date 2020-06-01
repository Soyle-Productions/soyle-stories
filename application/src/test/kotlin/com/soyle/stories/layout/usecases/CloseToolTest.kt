package com.soyle.stories.layout.usecases

import com.soyle.stories.entities.Project
import com.soyle.stories.layout.LayoutException
import com.soyle.stories.layout.ToolDoesNotExist
import com.soyle.stories.layout.assertLayoutDoesNotExist
import com.soyle.stories.layout.assertResponseModel
import com.soyle.stories.layout.doubles.LayoutRepositoryDouble
import com.soyle.stories.layout.entities.Layout
import com.soyle.stories.layout.entities.Tool
import com.soyle.stories.layout.repositories.OpenToolContext
import com.soyle.stories.layout.tools.fixed.FixedTool
import com.soyle.stories.layout.tools.temporary.TemporaryTool
import com.soyle.stories.layout.usecases.closeTool.CloseTool
import com.soyle.stories.layout.usecases.closeTool.CloseToolUseCase
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class CloseToolTest {

    private val toolId = Tool.Id().uuid
    private val projectId = Project.Id().uuid
    private val layoutId = Layout.Id().uuid

    @Test
    fun `layout doesn't exist`() {
        whenToolIsClosed()
        assertLayoutDoesNotExist().invoke(result)
    }

    @Test
    fun `tool not in layout`() {
        givenLayoutExists()
        whenToolIsClosed()
        assertToolDoesNotExist().invoke(result)
    }

    @Test
    fun `tool already closed`() {
        givenLayoutExists()
        givenToolInLayout(toolId, isOpen = false)
        whenToolIsClosed()
        assertNull(savedLayout)
        assertResponseModel(layoutRepository.layout!!).invoke(result)
    }

    @Test
    fun `tool open`() {
        givenLayoutExists()
        givenToolInLayout(toolId, isOpen = true)
        whenToolIsClosed()
        assertSavedLayout().invoke(savedLayout)
        assertResponseModel(savedLayout!!).invoke(result)
    }

    @Test
    fun `remove temporary tools`() {
        givenLayoutExists()
        givenToolInLayout(toolId, isOpen = true, isTemporary = true)
        whenToolIsClosed()
        assertSavedLayout(removed = true).invoke(savedLayout)
        assertResponseModel(savedLayout!!).invoke(result)
    }

    private var savedLayout: Layout? = null
    private val layoutRepository = LayoutRepositoryDouble(onSaveLayout = {
        savedLayout = it
    })

    private fun givenLayoutExists() {
        layoutRepository.layout = Layout(Project.Id(projectId)).let {
            Layout(Layout.Id(layoutId), it.projectId, it.windows)
        }
    }

    private fun givenToolInLayout(toolId: UUID, isOpen: Boolean, isTemporary: Boolean = false) {
        val testType = if (isTemporary) object : TemporaryTool() {
            override val isTemporary: Boolean
                get() = true

            override fun identifiedWithId(id: UUID): Boolean = false
            override suspend fun validate(context: OpenToolContext) {}
        } else FixedTool.CharacterList
        layoutRepository.layout = layoutRepository.layout!!.let {
            it.withToolAddedToStack(Tool(Tool.Id(toolId), testType, isOpen), it.primaryStack.id)
        }
    }

    private var result: Any? = null

    private fun whenToolIsClosed() {
        val useCase: CloseTool = CloseToolUseCase(projectId, layoutRepository)
        val output = object : CloseTool.OutputPort {
            override fun receiveCloseToolResponse(response: GetSavedLayout.ResponseModel) {
                result = response
            }

            override fun receiveCloseToolFailure(failure: LayoutException) {
                result = failure
            }
        }
        runBlocking {
            useCase.invoke(toolId, output)
        }
    }

    private fun assertToolDoesNotExist(): (Any?) -> Unit = { actual ->
        actual as ToolDoesNotExist
        assertEquals(toolId, actual.toolId)
    }

    private fun assertSavedLayout(removed: Boolean = false): (Any?) -> Unit = { actual ->
        actual as Layout
        if (removed) {
            assertFalse(actual.hasTool(Tool.Id(toolId)))
        } else {
            assertFalse(actual.isToolOpen(Tool.Id(toolId)))
        }
    }
}